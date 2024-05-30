package Cubo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import Cubo.comandos.ComandoDice;
import Cubo.comandos.ComandoDrillDown;
import Cubo.comandos.ComandoRollUp;
import Cubo.comandos.ComandoSlice;
import Cubo.excepciones.excepciones_dimension.ClaveNoPresenteException;
import Cubo.excepciones.excepciones_dimension.DimensionNoPresenteException;
import Cubo.excepciones.excepciones_dimension.NivelNoPresenteException;
import Cubo.excepciones.excepciones_hechos.HechoNoPresenteException;
import Cubo.excepciones.excepciones_operacion.AgregacionNoSoportadaException;
import Cubo.excepciones.excepciones_tabla.ColumnaNoPresenteException;
import Cubo.excepciones.excepciones_tabla.FilaFueraDeRangoException;
import Cubo.excepciones.excepciones_tabla.TablaException;
import Cubo.exportacion_archivos.EstrategiaExportarArchivo;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;
import Cubo.tablas.Tabla;
import Cubo.utils.Visualizable;

/**
 * Esta clase representa un cubo OLAP.
 * Proporciona métodos para realizar operaciones de roll-up, drill-down, slice, dice y visualización.
 * Implementa la interfaz {@link Visualizable}.
 */
public class CuboOLAP implements Visualizable {

    // Defino atributos comunes del cubo
    private final List<Dimension> dimensiones;
    private final Hecho hecho;
    private final String nombre;
    private Hecho tabla_operacion;
    private Hecho tabla_base;

    // Defino los historiales internos de operaciones sobre el cubo
    private List<ComandoRollUp> historial_rollUp;
    private List<ComandoDice> historial_dice;
    private List<ComandoSlice> historial_slice;

    /**
     * Constructor para la clase CuboOLAP.
     *
     * @param nombre El nombre del cubo.
     * @param hecho La tabla de hechos para el cubo.
     * @param dimensiones La lista de dimensiones para el cubo.
     * @throws ClaveNoPresenteException Si alguna clave primaria de una dimensión no está presente en la tabla de hechos.
     * @throws HechoNoPresenteException 
     */
    public CuboOLAP(String nombre, Hecho hecho, List<Dimension> dimensiones) throws ClaveNoPresenteException, ColumnaNoPresenteException, HechoNoPresenteException{

        // Verifico que en la tabla de hechos estén todas las claves primarias de las dimensiones
        for (Dimension dimension : dimensiones){
            if (!hecho.getHeaders().contains(dimension.getPrimaryKey())){
                throw new ClaveNoPresenteException("La clave primaria " + dimension.getPrimaryKey() + " no está en la tabla de hechos " + hecho.getNombre());
            }
        }

        // Una vez verificado asigno los valores
        this.dimensiones = new ArrayList<>(dimensiones);
        this.hecho = hecho;
        this.nombre = nombre;

        // Inicializo los historiales de operaciones de Dice y RollUp
        this.historial_rollUp = new ArrayList<>();
        this.historial_dice = new ArrayList<>();

        // Y ahora en 'tabla_operacion' guardo una gran tabla resultado 
        // de hacer merge a la tabla de hechos por cada dimensión,
        // esto servirá para realizar las operaciones
        Hecho hechos_merged = hecho.getHechoCopy();
        for (Dimension dimension : dimensiones) {
            Tabla.merge(hechos_merged, dimension, dimension.getPrimaryKey());
        }
        this.tabla_operacion = hechos_merged;

        // Elimino las columnas primaryKey de las dimensiones de 'tabla_operacion'
        // ya que una vez toda la información junta, estas no sirven más
        for (Dimension dimension : this.dimensiones){
            this.tabla_operacion.eliminarColumna(dimension.getPrimaryKey());
        }

        // Finalmente guardo una 'tabla_base' que servirá para volver al estado original del cubo
        this.tabla_base = this.tabla_operacion.getHechoCopy();

    }

    /**
     * Realiza una operación de roll-up en el cubo.
     *
     * @param criterios_reduccion Un mapa de criterios que contiene como clave la dimensión sobre la cual se quiere aplicar reducción 
     *                            y como valor el nivel que se toma como criterio para aplicar la misma.
     * @param hechos_seleccionados La lista de hechos a incluir en la operación de roll-up.
     * @param agregacion La operación de agregación a realizar. Solo son soportadas: "sum", "max", "min", "count" (Escribir tal cual al pasar el argumento!).
     * @throws AgregacionNoSoportadaException Si la operación de agregación seleccionada no está entre las disponibles.
     * @throws DimensionNoPresenteException Si alguna dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si algún nivel especificado no está presente en alguna dimensión.
     * @throws HechoNoPresenteException Si algún hecho seleccionado no está presente en la tabla de hechos.
     * @throws TablaException Si ocurre un error inesperado al invocar el comando.
     */
    public void rollUp(Map<Dimension, String> criterios_reduccion, List<String> hechos_seleccionados, String agregacion) throws TablaException, AgregacionNoSoportadaException, NivelNoPresenteException, DimensionNoPresenteException, HechoNoPresenteException{

        // Formateo el string de la operación de agregacion
        String agregacion_parsed = agregacion.toLowerCase().trim();

        // Armo una lista que contiene los nombres exactos de las operaciones de agregación soportadas
        List<String> operaciones_soportadas = new ArrayList<>(Arrays.asList("sum", "max", "min", "count"));

        // Verifico que la operación de agregacion seleccionada esté presente en dicha lista
        if (!operaciones_soportadas.contains(agregacion_parsed)){
            throw new AgregacionNoSoportadaException("La operación de agregación " + agregacion_parsed + " no está soportada.");
        }

        // Verifico que estén presentes todas las dimensiones y niveles pasadas en el mapa 'criterios_reduccion'
        for (Dimension dimension : criterios_reduccion.keySet()){
            if (!this.dimensiones.contains(dimension)){
                throw new DimensionNoPresenteException("La dimensión " + dimension.getNombre() + " no está presente en el cubo " + this.nombre);
            }
            if (!dimension.getNiveles().containsKey(criterios_reduccion.get(dimension))){
                throw new NivelNoPresenteException("El nivel " + criterios_reduccion.get(dimension) + " no está presente en la dimensión " + dimension.getNombre());
            }
        }

        // Verifico que estén presentes todos los hechos seleccionados para la operación
        for (String hecho: hechos_seleccionados){
            if (!tabla_operacion.getHeaders().contains(hecho)){
                throw new HechoNoPresenteException("El hecho " + hecho + " no está presente en la tabla de hechos " + this.hecho.getNombre());
            }
        }

        // Genero una instancia de RollUp
        ComandoRollUp comando = new ComandoRollUp(this.tabla_operacion, criterios_reduccion, hechos_seleccionados, 
                                                  agregacion_parsed, this.historial_rollUp);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historial_rollUp = comando.getHistorial();

        // Modifico el estado del cubo
        this.tabla_operacion = comando.getResultado();
    }

    public void drillDown(String dimension) throws TablaException{
        new ComandoDrillDown(dimension).ejecutar();
    }

    /**
     * Realiza una operación de slice en el cubo.
     *
     * @param dimension La dimensión en la que realizar la operación de slice.
     * @param nivel El nivel en la dimensión en el que realizar la operación de slice.
     * @param valor_corte El valor de corte para la operación de slice.
     * @throws DimensionNoPresenteException Si la dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si el nivel especificado no está presente en la dimensión.
     * @throws NivelNoPresenteException Si el valor de corte no está presente en el nivel seleccionado de la dimensión.
     * @throws TablaException Si ocurre un error inesperado al invocar el comando.
     */
    public void slice(Dimension dimension, String nivel, String valor_corte) throws TablaException,DimensionNoPresenteException, NivelNoPresenteException{

        // Verifico que la dimensión pasada como argumento esté en la lista de dimensiones
        if (!this.dimensiones.contains(dimension)){
            throw new DimensionNoPresenteException("La dimensión " + dimension.getNombre() + " no está presente en el cubo.");
        }

        // Verifico que el nivel pasado como argumento esté presente en la dimensión
        if (!dimension.getNiveles().containsKey(nivel)){
            throw new NivelNoPresenteException("El nivel " + nivel + " no está presente en la dimensión " + dimension.getNombre());
        }

        // Verifico que el valor de corte pasado como argumento esté presente en el nivel
        if (!dimension.getNiveles().get(nivel).contains(valor_corte)){
            throw new DimensionNoPresenteException("El valor de corte " + valor_corte + " no está presente en el nivel " + nivel + " de la dimensión " + dimension.getNombre());
        }

        // Genero una instancia de Slice
        ComandoSlice comando = new ComandoSlice(this.tabla_operacion, dimension, nivel, valor_corte, this.historial_slice);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historial_slice = comando.getHistorial();

        // Modifico el estado del cubo
        this.tabla_operacion = comando.getResultado();

    }

    /**
     * Realiza una operación de dice en el cubo.
     *
     * @param criterios Un mapa que contiene las dimensiones, niveles y valores a incluir en la operación de "dice".
     * @throws TablaException Si se produce un error inesperado al ejecutar el comando.
     * @throws DimensionNoPresenteException Si la dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si el nivel especificado no está presente en la dimensión.
     * @throws NivelNoPresenteException Si el valor de corte no está presente en el nivel seleccionado de la dimensión.
     */
    public void dice(Map<Dimension, Map<String, List<String>>> criterios) throws TablaException, DimensionNoPresenteException, NivelNoPresenteException{
        
        //Primero hago las verificaciones pertinentes de la existencia de dimension, niveles y valores especificados
        for (Map.Entry<Dimension, Map<String, List<String>>> criterioDimension : criterios.entrySet()){
            // Verifico que la dimensión pasada como argumento esté en la lista de dimensiones
            if (!this.dimensiones.contains(criterioDimension.getKey())){
                throw new DimensionNoPresenteException("La dimensión " + criterioDimension.getKey().getNombre() + " no está presente en el cubo.");
            }
            // Verifico que el nivel pasado como argumento esté presente en la dimensión
            for (Map.Entry<String, List<String>> criterioNivel : criterioDimension.getValue().entrySet()){
                if (!criterioDimension.getKey().getNiveles().containsKey(criterioNivel.getKey())){
                    throw new NivelNoPresenteException("El nivel " + criterioNivel.getKey() + " no está presente en la dimensión " + criterioDimension.getKey().getNombre());
                }
                // Finalmente verifico que los valores del nivel elegidos estén en el mismo
                // nivel
                for (String valor : criterioNivel.getValue()){
                    if (!criterioDimension.getKey().getNiveles().get(criterioNivel.getKey()).contains(valor)){
                        throw new NivelNoPresenteException("El valor " + valor + " no está presente en el nivel " + criterioNivel.getKey() + " de la dimensión " + criterioDimension.getKey().getNombre());
                    }
                }
             }
        }   
    
        // Genero una instancia de ComandoDice
        ComandoDice comando = new ComandoDice(this.tabla_operacion, criterios, this.historial_dice);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historial_dice = comando.getHistorial();

        // Modifico el estado del cubo
        this.tabla_operacion = comando.getResultado();

    }
    
    /**
     * Muestra una parte seleccionada de los datos del cubo en un formato tabular.
     *
     * @param n_filas El número de filas a mostrar.
     * @param columnas La lista de nombres de columnas a mostrar.
     * @throws ColumnaNoPresenteException Si una columna solicitada no está presente en los datos del objeto.
     * @throws FilaFueraDeRangoException Si el número solicitado de filas está fuera del rango de datos del objeto.
     */
    @Override
    public void ver(int n_filas, List<String> columnas) throws ColumnaNoPresenteException, FilaFueraDeRangoException{
        this.tabla_operacion.ver(n_filas, columnas);
    }

    /**
     * Exporta los datos del cubo a un archivo utilizando una estrategia específica de exportación.
     *
     * @param ruta_guardado La ruta de destino donde se guardará el archivo.
     * @param estrategia_exportar La estrategia de exportación a utilizar.
     * @throws IOException Si ocurre un error de entrada/salida al exportar los datos.
     */
    public void exportar(String ruta_guardado, EstrategiaExportarArchivo estrategia_exportar) throws IOException{
        
        // Para exportar primero debo juntar los encabezados con los datos
        // para eso armo una lista vacía que los junte
        List<List<String>> datos_a_exportar = new ArrayList<>();

        //Añado los headers
        datos_a_exportar.add(this.tabla_operacion.getHeaders());

        // Añado ahora el resto de información debajo
        for (List<String> fila : this.tabla_operacion.getData()){
            datos_a_exportar.add(fila);
        }

        // Ahora si exporto el cubo
        estrategia_exportar.exportarArchivo(ruta_guardado, datos_a_exportar);

    }

    /**
     * Reinicia el cubo a su estado original, restaurando 'tabla_operacion' al estado en que se creó
     * y limpiando los historiales de operaciones.
     *
     * @throws TablaException Si ocurre un error al intentar restaurar el cubo a su estado original.
     */
    public void resetear() throws TablaException{
        this.tabla_operacion = tabla_base.getHechoCopy();
        this.historial_dice = new ArrayList<>();
        this.historial_rollUp = new ArrayList<>();
        this.historial_slice = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Muestro el nombre del cubo
        sb.append("CuboOLAP: ").append(this.nombre).append("\n");
        
        // Muestro la información de las dimensiones
        sb.append("Dimensiones: ").append(this.dimensiones.size()).append("\n");
        for (Dimension dimension : this.dimensiones) {
            sb.append(" - ").append(dimension.toString()).append("\n");
        }
        
        // Muestro la información de la tabla de hechos
        sb.append(this.hecho.toString());
        
        return sb.toString();
    }

}

