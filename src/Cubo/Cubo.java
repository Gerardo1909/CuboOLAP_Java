package Cubo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Cubo.ImplementacionCubo.*;
import Cubo.excepciones.excepcionesCubo.*;
import Cubo.excepciones.excepcionesDimension.*;
import Cubo.excepciones.excepcionesHechos.*;
import Cubo.excepciones.excepcionesOperacion.*;
import Cubo.excepciones.excepcionesTabla.*;
import Cubo.exportacionArchivos.EstrategiaExportarArchivo;
import Cubo.tablasCubo.*;

/**
 * Esta clase representa un cubo que provee operaciones OLAP.
 * Proporciona métodos para realizar operaciones de roll-up, drill-down, slice y dice.
 * Permite la proyección de los datos del cubo.
 */
public class Cubo{

    // Defino atributos comunes del cubo
    private final String nombre;
    private final List<Dimension> dimensiones;
    private final Hecho hecho;
    private CuerpoCubo tablaOperacion;
    private CuerpoCubo tablaBase;

    // Defino los historiales internos de operaciones sobre el cubo
    private List<ComandoRollUp> historialRollUp;
    private List<ComandoDice> historialDice;
    private List<ComandoSlice> historialSlice;
    private List<ComandoDrillDown> historialDrillDown;

    public static Cubo crearCuboOLAP(String nombreCubo, Hecho tablaHechos, List<Dimension> tablasDimensiones) throws ClaveNoPresenteException{
        
        // Verifico que en la tabla de hechos estén todas las claves primarias de las dimensiones
        for (Dimension dimension : tablasDimensiones){
            if (!tablaHechos.getHeaders().contains(dimension.getPrimaryKey())){
                throw new ClaveNoPresenteException("La clave primaria " + dimension.getPrimaryKey() + " no está en la tabla de hechos " + tablaHechos.getNombre());
            }
        }
        
        // Retorno un nuevo Cubo OLAP 
        return new Cubo(nombreCubo, tablaHechos, tablasDimensiones);

    }

    /**
     * Constructor para la clase Cubo.
     *
     * @param nombre El nombre del cubo.
     * @param hecho La tabla de hechos para el cubo.
     * @param dimensiones La lista de dimensiones para el cubo.
     */
    private Cubo(String nombre, Hecho hecho, List<Dimension> dimensiones) throws ClaveNoPresenteException{

        // Inicializo los atributos del cubo
        this.dimensiones = new ArrayList<>(dimensiones);
        this.hecho = hecho;
        this.nombre = nombre;

        // Inicializo los historiales dde todos los métodos 
        this.historialDrillDown = new ArrayList<>();
        this.historialRollUp = new ArrayList<>();
        this.historialDice = new ArrayList<>();
        this.historialSlice = new ArrayList<>();

        // Configuro el cuerpo interno del cubo
        this.tablaOperacion = CuerpoCubo.configurarCubo(hecho, dimensiones);

        // Finalmente guardo una 'tablaBase' que servirá para volver al estado original del cubo
        this.tablaBase = this.tablaOperacion.getCuerpoCopy();
    }

    /**
     * Realiza una operación de roll-up en el cubo.
     *
     * @param criteriosAgregacion Un mapa de criterios que contiene como clave la dimensión sobre la cual se quiere aplicar reducción 
     *                            y como valor el nivel que se toma como criterio para aplicar la misma.
     * @param hechosSeleccionados La lista de hechos a incluir en la operación de roll-up.
     * @param agregacion La operación de agregación a realizar. Solo son soportadas: "sum", "max", "min", "count" (Escribir tal cual al pasar el argumento!).
     * @throws AgregacionNoSoportadaException Si la operación de agregación seleccionada no está entre las disponibles.
     * @throws DimensionNoPresenteException Si alguna dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si algún nivel especificado no está presente en alguna dimensión.
     * @throws HechoNoPresenteException Si algún hecho seleccionado no está presente en la tabla de hechos.
     * @throws ArgumentosInoperablesException Si el mapa de criterios de reducción está vacío.
     */
    public void rollUp(Map<Dimension, String> criteriosAgregacion, List<String> hechosSeleccionados, String agregacion) throws ArgumentosInoperablesException, AgregacionNoSoportadaException, NivelNoPresenteException, DimensionNoPresenteException, HechoNoPresenteException{

        // Verifico que 'criteriosAgregacion' no sea un mapa vacío
        if (criteriosAgregacion.isEmpty()){
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

        // Itero sobre cada entrada del mapa 'criteriosAgregacion' para verificar que los niveles y dimensiones seleccionados estén presentes
        // en el cubo
        for (Map.Entry<Dimension, String> criterio : criteriosAgregacion.entrySet()){
            // Verifico que la dimensión pasada como argumento esté presente en el cubo
            if (!this.dimensiones.contains(criterio.getKey())){
                throw new DimensionNoPresenteException("La dimensión " + criterio.getKey().getNombre() + " no está presente en el cubo <" + this.getNombre() + ">.");
            }
            // Verifico que el nivel pasado como argumento esté presente en el cubo
            if (!this.tablaOperacion.getHeaders().contains(criterio.getValue())){
                throw new NivelNoPresenteException("El nivel " + criterio.getValue() + " no está presente en el cubo <" + this.getNombre() + ">.");
            }
        }

        // Verifico que estén presentes todos los hechos seleccionados para la operación
        for (String hecho: hechosSeleccionados){
            if (!this.tablaOperacion.getHeaders().contains(hecho)){
                throw new HechoNoPresenteException("El hecho " + hecho + " no está presente en el cubo <" + this.getNombre() + ">.");
            }
        }

        // Genero una instancia de RollUp
        ComandoRollUp comando = new ComandoRollUp(this.tablaOperacion, criteriosAgregacion, hechosSeleccionados, 
                                                  operacion, this.historialRollUp);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historialRollUp = comando.getHistorial();

        // Modifico el estado del cubo
        this.tablaOperacion = comando.getResultado();
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
            if (this.tablaOperacion.getHeaders().contains(criterio.getValue())){
                throw new NivelDesagregadoException("El nivel " + criterio.getValue() + " ya esta desagregado en el cubo <" + this.getNombre() + ">.");
            }
        }

        // Genero una instancia de DrillDown
        ComandoDrillDown comando = new ComandoDrillDown(criterios_desagregacion, this.tablaBase.getCuerpoCopy(), this.historialRollUp, 
                                                        this.historialDice, this.historialSlice, this.historialDrillDown);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historialDrillDown = comando.getHistorial();

        // Modifico el estado del cubo
        this.tablaOperacion = comando.getResultado();
    }

    /**
     * Realiza una operación de slice en el cubo.
     *
     * @param dimension La dimensión en la que realizar la operación de slice.
     * @param nivel El nivel en la dimensión en el que realizar la operación de slice.
     * @param valorCorte El valor de corte para la operación de slice.
     * @throws SliceException Si la dimensión especificada ya ha sido filtrada anteriormente en una operación de slice.
     * @throws DimensionNoPresenteException Si la dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si el nivel especificado no está presente en el cubo.
     * @throws ValorNoPresenteException Si el valor de corte no está presente en el cubo para el nivel seleccionado de la dimensión.
     */
    public void slice(Dimension dimension, String nivel, String valorCorte) throws SliceException, DimensionNoPresenteException, NivelNoPresenteException, ValorNoPresenteException{

        // Verifico que la dimensión pasada como argumento no se haya visto involucrada en 
        // esta operación anteriormente
        if (this.historialSlice.size() > 0){
            for (ComandoSlice comando : this.historialSlice){
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
        if (!this.tablaOperacion.getHeaders().contains(nivel)){
            throw new NivelNoPresenteException("El nivel " + nivel + " no esta presente en el cubo <" + this.getNombre() + ">.");
        }

        // Verifico que el valor de corte pasado como argumento esté presente en el nivel de la dimensión a la hora de ejecutar el método
        try{
            if (!this.tablaOperacion.getColumna(nivel).contains(valorCorte)){
                throw new ValorNoPresenteException("El valor " + valorCorte + " del nivel "+ nivel + " no esta presente en el cubo <" + this.getNombre() + ">.");
            }
        } catch (ColumnaNoPresenteException e){
            // Esta excepción no debería ocurrir ya que anteriormente se verificó que el nivel estuviese presente
            // en el cubo
            e.printStackTrace();
        }
        
        // Genero una instancia de Slice
        ComandoSlice comando = new ComandoSlice(this.tablaOperacion, dimension, nivel, valorCorte, this.historialSlice);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historialSlice = comando.getHistorial();

        // Modifico el estado del cubo
        this.tablaOperacion = comando.getResultado();

    }

    /**
     * Realiza una operación de dice en el cubo.
     *
     * @param criteriosDice Un mapa que contiene las dimensiones, niveles y valores a incluir en la operación Dice.
     * @throws DimensionNoPresenteException Si la dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si el nivel especificado no está presente en el cubo.
     * @throws ValorNoPresenteException Si el valor de corte no está presente en el cubo para el nivel seleccionado de la dimensión.
     * @throws ArgumentosInoperablesException Si el mapa de criterios de filtrado está vacío.
     */
    public void dice(Map<Dimension, Map<String, List<String>>> criteriosDice) throws ArgumentosInoperablesException, DimensionNoPresenteException, NivelNoPresenteException, ValorNoPresenteException{
        
        // Verifico que 'criterios' no sea un mapa vacío
        if (criteriosDice.isEmpty()){
            throw new ArgumentosInoperablesException("El mapa de criterios de filtrado para la operación dice no puede estar vacío.");
        }

        //Primero hago las verificaciones pertinentes de la existencia de dimension, niveles y valores especificados
        for (Map.Entry<Dimension, Map<String, List<String>>> criterioDimension : criteriosDice.entrySet()){
            // Verifico que la dimensión pasada como argumento esté en la lista de dimensiones
            if (!this.dimensiones.contains(criterioDimension.getKey())){
                throw new DimensionNoPresenteException("La dimensión " + criterioDimension.getKey().getNombre() + " no está presente en el cubo <" + this.getNombre() + ">.");
            }
            // Verifico que el nivel pasado como argumento esté presente en la dimensión
            for (Map.Entry<String, List<String>> criterioNivel : criterioDimension.getValue().entrySet()){
                if (!this.tablaOperacion.getHeaders().contains(criterioNivel.getKey())){
                    throw new NivelNoPresenteException("El nivel " + criterioNivel.getKey() + " no está presente en el cubo <" + this.getNombre() + ">.");
                }
                // Finalmente verifico que los valores del nivel elegidos estén en el mismo
                // nivel
                for (String valor : criterioNivel.getValue()){
                    try {
                        if (!this.tablaOperacion.getColumna(criterioNivel.getKey()).contains(valor)){
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
        ComandoDice comando = new ComandoDice(this.tablaOperacion, criteriosDice, this.historialDice);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historialDice = comando.getHistorial();

        // Modifico el estado del cubo
        this.tablaOperacion = comando.getResultado();

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
        this.tablaOperacion.ver(n_filas, columnas);
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
        datos_a_exportar.add(this.tablaOperacion.getHeaders());

        // Añado ahora el resto de información debajo
        for (List<String> fila : this.tablaOperacion.getData()){
            datos_a_exportar.add(fila);
        }

        // Ahora si exporto el cubo
        estrategia_exportar.exportarArchivo(ruta_guardado, datos_a_exportar);

    }

    /**
     * Reinicia el cubo a su estado original, restaurando 'tablaOperacion' al estado en que se creó
     * y limpiando los historiales de operaciones.
     */
    public void reiniciar(){
        this.tablaOperacion = tablaBase.getCuerpoCopy();
        this.historialDice = new ArrayList<>();
        this.historialRollUp = new ArrayList<>();
        this.historialSlice = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Muestro el nombre del cubo
        sb.append("Cubo: ").append(this.nombre).append("\n");
        
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
        return this.nombre;
    }
}

