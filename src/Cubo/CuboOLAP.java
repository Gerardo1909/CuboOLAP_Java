package Cubo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Cubo.comandos.ComandoDice;
import Cubo.comandos.ComandoDrillDown;
import Cubo.comandos.ComandoRollUp;
import Cubo.comandos.ComandoSlice;
import Cubo.cubo_utils.OperacionAgregacion;
import Cubo.excepciones.excepciones_cubo.SliceException;
import Cubo.excepciones.excepciones_dimension.ClaveNoPresenteException;
import Cubo.excepciones.excepciones_dimension.DimensionNoPresenteException;
import Cubo.excepciones.excepciones_dimension.NivelNoPresenteException;
import Cubo.excepciones.excepciones_dimension.ValorNoPresenteException;
import Cubo.excepciones.excepciones_hechos.HechoNoPresenteException;
import Cubo.excepciones.excepciones_operacion.AgregacionNoSoportadaException;
import Cubo.excepciones.excepciones_operacion.ArgumentosInoperablesException;
import Cubo.excepciones.excepciones_operacion.NivelDesagregadoException;
import Cubo.excepciones.excepciones_tabla.ColumnaNoPresenteException;
import Cubo.excepciones.excepciones_tabla.FilaFueraDeRangoException;
import Cubo.exportacion_archivos.EstrategiaExportarArchivo;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;
import Cubo.tablas.Tabla;

/**
 * Esta clase representa un cubo OLAP.
 * Proporciona métodos para realizar operaciones de roll-up, drill-down, slice y dice.
 * Permite la proyección de los datos del cubo.
 */
public class CuboOLAP{

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
    private List<ComandoDrillDown> historial_drillDown;

    /**
     * Constructor para la clase CuboOLAP.
     *
     * @param nombre El nombre del cubo.
     * @param hecho La tabla de hechos para el cubo.
     * @param dimensiones La lista de dimensiones para el cubo.
     * @throws ClaveNoPresenteException Si alguna clave primaria de una dimensión no está presente en la tabla de hechos.
     */
    public CuboOLAP(String nombre, Hecho hecho, List<Dimension> dimensiones) throws ClaveNoPresenteException, ColumnaNoPresenteException{

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

        // Inicializo los historiales dde todos los métodos 
        this.historial_drillDown = new ArrayList<>();
        this.historial_rollUp = new ArrayList<>();
        this.historial_dice = new ArrayList<>();
        this.historial_slice = new ArrayList<>();

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
     * @throws ArgumentosInoperablesException Si el mapa de criterios de reducción está vacío.
     */
    public void rollUp(Map<Dimension, String> criterios_reduccion, List<String> hechos_seleccionados, String agregacion) throws ArgumentosInoperablesException, AgregacionNoSoportadaException, NivelNoPresenteException, DimensionNoPresenteException, HechoNoPresenteException{

        // Verifico que 'criterios_reduccion' no sea un mapa vacío
        if (criterios_reduccion.isEmpty()){
            throw new ArgumentosInoperablesException("El mapa de criterios de reducción no puede estar vacío.");
        }

        // Verifico que la operación de agregación sea una de las soportadas por el método 
        String agregacion_parsed = agregacion.toLowerCase().trim();
        OperacionAgregacion operacion;
        if (OperacionAgregacion.esOperacionValida(agregacion_parsed)){
            // Si la operación indicada en el String es válida, uso esa constante del enum
            operacion = OperacionAgregacion.valueOf(agregacion_parsed.toUpperCase());
        } else{
            throw new AgregacionNoSoportadaException("La operación de agregación " + agregacion + " no está soportada por el método RollUp.");
        }

        // Itero sobre cada entrada del mapa 'criterios_reduccion' para verificar que los niveles y dimensiones seleccionados estén presentes
        // en el cubo
        for (Map.Entry<Dimension, String> criterio : criterios_reduccion.entrySet()){
            // Verifico que la dimensión pasada como argumento esté presente en el cubo
            if (!this.dimensiones.contains(criterio.getKey())){
                throw new DimensionNoPresenteException("La dimensión " + criterio.getKey().getNombre() + " no está presente en el cubo <" + this.getNombre() + ">.");
            }
            // Verifico que el nivel pasado como argumento esté presente en el cubo
            if (!this.tabla_operacion.getHeaders().contains(criterio.getValue())){
                throw new NivelNoPresenteException("El nivel " + criterio.getValue() + " no está presente en el cubo <" + this.getNombre() + ">.");
            }
        }

        // Verifico que estén presentes todos los hechos seleccionados para la operación
        for (String hecho: hechos_seleccionados){
            if (!this.tabla_operacion.getHeaders().contains(hecho)){
                throw new HechoNoPresenteException("El hecho " + hecho + " no está presente en el cubo <" + this.getNombre() + ">.");
            }
        }

        // Genero una instancia de RollUp
        ComandoRollUp comando = new ComandoRollUp(this.tabla_operacion, criterios_reduccion, hechos_seleccionados, 
                                                  operacion, this.historial_rollUp);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historial_rollUp = comando.getHistorial();

        // Modifico el estado del cubo
        this.tabla_operacion = comando.getResultado();
    }

    public void drillDown(Map<Dimension, String> criterios_desagregacion) throws ArgumentosInoperablesException, DimensionNoPresenteException, NivelNoPresenteException, NivelDesagregadoException{
        
        // Verifico que 'criterios_desagregacion' no sea un mapa vacío
        if (criterios_desagregacion.isEmpty()){
            throw new ArgumentosInoperablesException("El mapa de criterios de desagregacion no puede estar vacio.");
        }

        // Por cada entrada del mapa verifico los criterios seleccionados 
        for (Map.Entry<Dimension, String> criterio : criterios_desagregacion.entrySet()){
            // Verifico que la dimensión pasada como argumento esté en la lista de dimensiones
            if (!this.dimensiones.contains(criterio.getKey())){
                throw new DimensionNoPresenteException("La dimension " + criterio.getKey().getNombre() + " no esta presente en el cubo <" + this.getNombre() + ">.");
            }
            // Verifico que el nivel pasado como argumento esté presente en la dimensión
            if (!criterio.getKey().getNiveles().containsKey(criterio.getValue())){
                throw new NivelNoPresenteException("El nivel " + criterio.getValue() + " no esta presente en el cubo <" + this.getNombre() + ">.");
            }
            // Verifico que el nivel pasado como argumento no esté desagregado
            if (this.tabla_operacion.getHeaders().contains(criterio.getValue())){
                throw new NivelDesagregadoException("El nivel " + criterio.getValue() + " ya esta desagregado en el cubo <" + this.getNombre() + ">.");
            }
        }

        // Genero una instancia de DrillDown
        ComandoDrillDown comando = new ComandoDrillDown(criterios_desagregacion, this.tabla_base.getHechoCopy(), this.historial_rollUp, 
                                                        this.historial_dice, this.historial_slice, this.historial_drillDown);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historial_drillDown = comando.getHistorial();

        // Modifico el estado del cubo
        this.tabla_operacion = comando.getResultado();
    }

    /**
     * Realiza una operación de slice en el cubo.
     *
     * @param dimension La dimensión en la que realizar la operación de slice.
     * @param nivel El nivel en la dimensión en el que realizar la operación de slice.
     * @param valor_corte El valor de corte para la operación de slice.
     * @throws SliceException Si la dimensión especificada ya ha sido filtrada anteriormente en una operación de slice.
     * @throws DimensionNoPresenteException Si la dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si el nivel especificado no está presente en el cubo.
     * @throws ValorNoPresenteException Si el valor de corte no está presente en el cubo para el nivel seleccionado de la dimensión.
     */
    public void slice(Dimension dimension, String nivel, String valor_corte) throws SliceException, DimensionNoPresenteException, NivelNoPresenteException, ValorNoPresenteException{

        // Verifico que la dimensión pasada como argumento no se haya visto involucrada en 
        // esta operación anteriormente
        if (this.historial_slice.size() > 0){
            for (ComandoSlice comando : this.historial_slice){
                if (comando.getDimension().equals(dimension)){
                    throw new SliceException("La dimension " + dimension.getNombre() + " ya ha sido filtrada anteriormente en una operacion de slice en el cubo <" + this.getNombre() + ">.");
                }
            }
        }

        // Verifico que la dimensión pasada como argumento esté en la lista de dimensiones
        if (!this.dimensiones.contains(dimension)){
            throw new DimensionNoPresenteException("La dimension " + dimension.getNombre() + " no esta presente en el cubo <" + this.getNombre() + ">.");
        }

        // Verifico que el nivel pasado como argumento esté presente en el cubo a la hora de ejecutar el método
        if (!this.tabla_operacion.getHeaders().contains(nivel)){
            throw new NivelNoPresenteException("El nivel " + nivel + " no esta presente en el cubo <" + this.getNombre() + ">.");
        }

        // Verifico que el valor de corte pasado como argumento esté presente en el nivel de la dimensión a la hora de ejecutar el método
        try{
            if (!this.tabla_operacion.getColumna(nivel).contains(valor_corte)){
                throw new ValorNoPresenteException("El valor " + valor_corte + " del nivel "+ nivel + " no esta presente en el cubo <" + this.getNombre() + ">.");
            }
        } catch (ColumnaNoPresenteException e){
            // Esta excepción no debería ocurrir ya que anteriormente se verificó que el nivel estuviese presente
            // en el cubo
            e.printStackTrace();
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
     * @throws DimensionNoPresenteException Si la dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si el nivel especificado no está presente en el cubo.
     * @throws ValorNoPresenteException Si el valor de corte no está presente en el cubo para el nivel seleccionado de la dimensión.
     * @throws ArgumentosInoperablesException Si el mapa de criterios de filtrado está vacío.
     */
    public void dice(Map<Dimension, Map<String, List<String>>> criterios) throws ArgumentosInoperablesException, DimensionNoPresenteException, NivelNoPresenteException, ValorNoPresenteException{
        
        // Verifico que 'criterios' no sea un mapa vacío
        if (criterios.isEmpty()){
            throw new ArgumentosInoperablesException("El mapa de criterios de filtrado para la operación dice no puede estar vacío.");
        }

        //Primero hago las verificaciones pertinentes de la existencia de dimension, niveles y valores especificados
        for (Map.Entry<Dimension, Map<String, List<String>>> criterioDimension : criterios.entrySet()){
            // Verifico que la dimensión pasada como argumento esté en la lista de dimensiones
            if (!this.dimensiones.contains(criterioDimension.getKey())){
                throw new DimensionNoPresenteException("La dimensión " + criterioDimension.getKey().getNombre() + " no está presente en el cubo <" + this.getNombre() + ">.");
            }
            // Verifico que el nivel pasado como argumento esté presente en la dimensión
            for (Map.Entry<String, List<String>> criterioNivel : criterioDimension.getValue().entrySet()){
                if (!this.tabla_operacion.getHeaders().contains(criterioNivel.getKey())){
                    throw new NivelNoPresenteException("El nivel " + criterioNivel.getKey() + " no está presente en el cubo <" + this.getNombre() + ">.");
                }
                // Finalmente verifico que los valores del nivel elegidos estén en el mismo
                // nivel
                for (String valor : criterioNivel.getValue()){
                    try {
                        if (!this.tabla_operacion.getColumna(criterioNivel.getKey()).contains(valor)){
                            throw new ValorNoPresenteException("El valor " + valor + " del nivel " + criterioNivel.getKey() + " no está presente en el cubo <" + this.getNombre() + ">.");
                        }
                    } catch (ColumnaNoPresenteException e){
                        // Esta excepción no debería ocurrir ya que anteriormente se verificó que el nivel estuviese presente
                        // en el cubo
                        e.printStackTrace();
                    }
                }
             }
        }   
    
        // Genero una instancia de Dice
        ComandoDice comando = new ComandoDice(this.tabla_operacion, criterios, this.historial_dice);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historial_dice = comando.getHistorial();

        // Modifico el estado del cubo
        this.tabla_operacion = comando.getResultado();

    }
    
    /**
     * Proyecta una parte seleccionada de los datos del cubo en un formato tabular.
     *
     * @param n_filas El número de filas a mostrar.
     * @param columnas La lista de nombres de columnas a mostrar.
     * @throws ColumnaNoPresenteException Si una columna solicitada no está presente en los datos del objeto.
     * @throws FilaFueraDeRangoException Si el número solicitado de filas está fuera del rango de datos del objeto.
     */
    public void proyectar(int n_filas, List<String> columnas) throws ColumnaNoPresenteException, FilaFueraDeRangoException{
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
     */
    public void reiniciar(){
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

    public String getNombre() {
        return nombre;
    }
}

