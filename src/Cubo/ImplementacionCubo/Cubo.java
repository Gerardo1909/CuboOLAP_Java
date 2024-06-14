package Cubo.ImplementacionCubo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Cubo.excepciones.excepcionesCubo.*;
import Cubo.excepciones.excepcionesDimension.*;
import Cubo.excepciones.excepcionesHechos.*;
import Cubo.excepciones.excepcionesOperacion.*;
import Cubo.excepciones.excepcionesTabla.*;
import Cubo.exportacionArchivos.EstrategiaExportarArchivo;
import Cubo.tablasCubo.*;

/**
 * <p>
 * Esta clase representa un cubo que provee operaciones OLAP.
 * </p>
 * 
 * <p>
 * Proporciona métodos para realizar operaciones OLAP tales como: <b> roll-up, drill-down, slice y dice </b>.
 * </p>
 * 
 * <p>
 * Permite la exportación del cubo para analizar su información en otros entornos de trabajo.
 * </p>
 * 
 * <p>
 * Además de estas operaciones, la clase Cubo también permite la proyección de los datos del cubo, 
 * seleccionando dimensiones y hechos específicos para mostrar en un formato tabular.
 * </p>
 */
public class Cubo{

    // Atributos de la clase Cubo
    private final String nombre;
    private final List<Dimension> tablasDimensiones;
    private final Hecho tablaHechos;
    private CuerpoCubo tablaOperacion;
    private CuerpoCubo tablaBase;

    // Historiales que llevan registro de las operaciones realizadas 
    // sobre una instancia de esta clase
    private List<ComandoRollUp> historialRollUp;
    private List<ComandoDice> historialDice;
    private List<ComandoSlice> historialSlice;
    private List<ComandoDrillDown> historialDrillDown;


    // Métodos para la creación de instancias de la clase Cubo

    /**
     * </p>
     * Método para crear un <b>cubo OLAP</b>, el cual es instancia de la clase Cubo.
     * </p>
     * 
     * </p>
     * Crea un nuevo cubo OLAP con un nombre, una tabla de hechos y una lista de tablas de dimensiones.
     * </p>
     * 
     * @param nombreCubo El nombre del cubo.
     * @param tablaHechos La tabla de hechos asociada a las dimensiones del cubo.
     * @param tablasDimensiones Una lista que contiene todas las dimensiones asociadas al cubo.
     *
     * @throws ClaveNoPresenteException Si alguna clave primaria de las dimensiones no está presente en la tabla de hechos.
     * 
     * @return Una nueva instancia de la clase Cubo, la cual representa un cubo OLAP.
     */
    public static Cubo crearCuboOLAP(String nombreCubo, Hecho tablaHechos, List<Dimension> tablasDimensiones){
        
        // Verifico que en la tabla de hechos estén todas las claves primarias de las dimensiones
        for (Dimension dimension : tablasDimensiones){
            if (!tablaHechos.getHeaders().contains(dimension.getPrimaryKey())){
                throw new ClaveNoPresenteException("La clave primaria '" + dimension.getPrimaryKey() + "' no está en la tabla de hechos " + tablaHechos.getNombre());
            }
        }
        
        // Retorno un nuevo Cubo OLAP 
        return new Cubo(nombreCubo, tablaHechos, tablasDimensiones);

    }

    /**
     * <p>
     * <b>Constructor para la clase Cubo.</b>
     * </p>
     * 
     * <p>
     * Se encarga de inicializar un nuevo cubo con un nombre, tablas de dimensiones y una tabla de hechos asociadas a las mismas.
     * </p>
     *
     * @param nombre El nombre del cubo.
     * @param tablaHechos La tabla de hechos asociada a las dimensiones del cubo.
     * @param tablasDimensiones Una lista que contiene todas las dimensiones asociadas al cubo.
     */
    private Cubo(String nombre, Hecho tablaHechos, List<Dimension> tablasDimensiones){

        // Inicializo los atributos del cubo
        this.tablasDimensiones = new ArrayList<>(tablasDimensiones);
        this.tablaHechos = tablaHechos;
        this.nombre = nombre;

        // Inicializo los historiales dde todos los métodos 
        this.historialDrillDown = new ArrayList<>();
        this.historialRollUp = new ArrayList<>();
        this.historialDice = new ArrayList<>();
        this.historialSlice = new ArrayList<>();

        // Configuro el cuerpo interno del cubo
        this.tablaOperacion = CuerpoCubo.configurarCubo(tablaHechos, tablasDimensiones, tablaHechos.getClavesForaneasDims());

        // Finalmente guardo una 'tablaBase' que servirá para volver al estado original del cubo
        this.tablaBase = this.tablaOperacion.getCuerpoCopy();
    }


    // Métodos para la manipulación de instancias de la clase Cubo

    /**
     * <p>
     * Este método realiza una operación "Roll-Up" sobre este cubo, modificando la estructura interna de dimensiones 
     * y hechos del mismo. 
     * </p>
     * 
     * <p>
     * Se aplica una operación de agregación a los hechos seleccionados, las disponibles son:
     * <b> "sum", "max", "min", "count". (Escribir tal cual al indicarla en el argumento) </b>
     * </p>
     * 
     * @param criteriosAgregacion Un mapa de criterios que contiene como clave la dimensión sobre la cual se quiere aplicar la operación 
     *                            y como valor el nivel de la dimensión a la cual se quiere agrupar la información.
     * @param hechosSeleccionados La lista de hechos a incluir en la operación de roll-up.
     * @param agregacion La operación de agregación a aplicar sobre los hechos del cubo. Solo son soportadas: "sum", "max", "min", "count" (Escribir tal cual al pasar el argumento).
     * 
     * @throws AgregacionNoSoportadaException Si la operación de agregación seleccionada no está entre las disponibles.
     * @throws DimensionNoPresenteException Si alguna dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si algún nivel especificado no está presente en alguna dimensión.
     * @throws HechoNoPresenteException Si algún hecho seleccionado no está presente en la tabla de hechos.
     * @throws ArgumentosInoperablesException Si el mapa de criterios de reducción está vacío.
     */
    public void rollUp(Map<Dimension, String> criteriosAgregacion, List<String> hechosSeleccionados, String agregacion){

        // Verifico que 'criteriosAgregacion' no sea un mapa vacío
        if (criteriosAgregacion.isEmpty()){
            throw new ArgumentosInoperablesException("El mapa de criterios de reducción no puede estar vacío.");
        }

        // Verifico que la operación de agregación sea una de las soportadas por el método 
        String agregacion_parsed = agregacion.toLowerCase().trim();
        OperacionAgregacion operacion;
        if (OperacionAgregacion.esOperacionValida(agregacion_parsed)){
            operacion = OperacionAgregacion.valueOf(agregacion_parsed.toUpperCase());
        } else{
            throw new AgregacionNoSoportadaException("La operacion de agregacion '" + agregacion + "' no esta soportada por el metodo RollUp.");
        }

        // Itero sobre cada entrada del mapa 'criteriosAgregacion' para verificar que los niveles y dimensiones seleccionados estén presentes
        // en el cubo
        for (Map.Entry<Dimension, String> criterio : criteriosAgregacion.entrySet()){
            if (!this.tablasDimensiones.contains(criterio.getKey())){
                throw new DimensionNoPresenteException("La dimension '" + criterio.getKey().getNombre() + "' no esta presente en el cubo <" + this.nombre + ">.");
            }
            if (!this.tablaOperacion.getHeaders().contains(criterio.getValue())){
                throw new NivelNoPresenteException("El nivel '" + criterio.getValue() + "' no esta presente en el cubo <" + this.nombre + ">.");
            }
        }

        // Verifico que estén presentes todos los hechos seleccionados para la operación
        for (String hecho: hechosSeleccionados){
            if (!this.tablaOperacion.getHeaders().contains(hecho)){
                throw new HechoNoPresenteException("El hecho '" + hecho + "' no esta presente en el cubo <" + this.nombre + ">.");
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

    /**
     * <p>
     * Realiza una operación "Drill-Down" sobre este cubo, modificando la estructura interna de dimensiones 
     * y hechos del mismo.
     * </p>
     * 
     * <p>
     * Para realizar esta operación es necesario haber agrupado las dimensiones que se quieren desagrupar 
     * anteriormente.
     * </p>
     * 
     * <p>
     * Del mismo modo esta operación solo desagrega los hechos que estuvieron implicados anteriormente en una operación rollUp.
     * </p>
     *
     * @param criteriosDesagregacion Un mapa de criterios que contiene como clave la dimensión sobre la cual se quiere aplicar la operación 
     *                              y como valor el nivel a desagregar.
     * 
     * @throws ArgumentosInoperablesException Si el mapa de criterios de desagregación está vacío.
     * @throws DimensionNoPresenteException Si alguna dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si algún nivel especificado no está presente en alguna dimensión.
     * @throws NivelDesagregadoException Si algún nivel especificado ya está desagregado en el cubo.
    */
    public void drillDown(Map<Dimension, String> criteriosDesagregacion){
        
        // Verifico que 'criteriosDesagregacion' no sea un mapa vacío
        if (criteriosDesagregacion.isEmpty()){
            throw new ArgumentosInoperablesException("El mapa de criterios de desagregacion no puede estar vacio.");
        }

        // Itero sobre cada entrada del mapa 'criteriosDesagregacion' para verificar que los niveles y dimensiones seleccionados estén presentes
        // en el cubo
        for (Map.Entry<Dimension, String> criterio : criteriosDesagregacion.entrySet()){
            if (!this.tablasDimensiones.contains(criterio.getKey())){
                throw new DimensionNoPresenteException("La dimension '" + criterio.getKey().getNombre() + "' no esta presente en el cubo <" + this.nombre + ">.");
            }
            if (!criterio.getKey().getNiveles().containsKey(criterio.getValue())){
                throw new NivelNoPresenteException("El nivel '" + criterio.getValue() + "' no esta presente en el cubo <" + this.nombre + ">.");
            }
            if (this.tablaOperacion.getHeaders().contains(criterio.getValue())){
                throw new NivelDesagregadoException("El nivel '" + criterio.getValue() + "' ya esta desagregado en el cubo <" + this.nombre + ">.");
            }
        }

        // Genero una instancia de DrillDown
        ComandoDrillDown comando = new ComandoDrillDown(criteriosDesagregacion, this.tablaBase.getCuerpoCopy(), this.historialRollUp, 
                                                        this.historialDice, this.historialSlice, this.historialDrillDown);

        // Ejecuto la operación
        comando.ejecutar();

        // Guardo en el historial la operación realizada
        this.historialDrillDown = comando.getHistorial();

        // Modifico el estado del cubo
        this.tablaOperacion = comando.getResultado();
    }

    /**
     * <p>
     * Realiza una operación "Slice" sobre este cubo, modificando la estructura interna de dimensiones 
     * y hechos del mismo.
     * </p>
     *
     * <p>
     * Fija una dimensión de este cubo a valor de un nivel específico, eliminando dicha dimensión pero filtrando 
     * al resto de dimensiones de este cubo según dicho valor.
     * </p>
     *
     * @param dimension La dimensión sobre la cual se realizará la operación "Slice".
     * @param nivel El nivel de la dimensión sobre la cual se realizará la operación "Slice".
     * @param valorCorte El valor al cual se fijará la dimensión.
     * 
     * @throws SliceException Si la dimensión especificada ya ha sido filtrada anteriormente en una operación de slice.
     * @throws DimensionNoPresenteException Si la dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si el nivel especificado no está presente en el cubo.
     * @throws ValorNoPresenteException Si el valor de corte no está presente en el cubo para el nivel seleccionado de la dimensión.
     */
    public void slice(Dimension dimension, String nivel, String valorCorte){

        // Verifico que la dimensión pasada como argumento no se haya visto involucrada en 
        // esta operación anteriormente
        if (this.historialSlice.size() > 0){
            for (ComandoSlice comando : this.historialSlice){
                if (comando.getDimension().equals(dimension)){
                    throw new SliceException("La dimension " + dimension.getNombre() + " ya ha sido filtrada anteriormente en una operacion de slice en el cubo <" + this.nombre + ">.");
                }
            }
        }

        // Verifico que la dimensión pasada como argumento esté en la lista de dimensiones
        if (!this.tablasDimensiones.contains(dimension)){
            throw new DimensionNoPresenteException("La dimension " + dimension.getNombre() + " no esta presente en el cubo <" + this.nombre + ">.");
        }

        // Verifico que el nivel pasado como argumento esté presente en el cubo a la hora de ejecutar el método
        if (!this.tablaOperacion.getHeaders().contains(nivel)){
            throw new NivelNoPresenteException("El nivel " + nivel + " no esta presente en el cubo <" + this.nombre + ">.");
        }

        // Verifico que el valor de corte pasado como argumento esté presente en el nivel de la dimensión a la hora de ejecutar el método
        if (!this.tablaOperacion.getColumna(nivel).contains(valorCorte)){
            throw new ValorNoPresenteException("El valor " + valorCorte + " del nivel "+ nivel + " no esta presente en el cubo <" + this.nombre + ">.");
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
     * <p>
     * Realiza una operación "Dice" sobre este cubo, modificando la estructura interna de dimensiones 
     * y hechos del mismo.
     * </p>
     *
     * <p>
     * Filtra las dimensiones y hechos de este cubo, reduciendo el tamaño del mismo.
     * </p>
     *
     * @param criteriosDice Un mapa que contiene como clave la dimensión sobre la cual se aplica el filtrado y como 
     *                      valor otro mapa que contiene como clave el nivel de la dimensión que se filtra y como valor
     *                      una lista con los nombres de los valores que se quieren permitir en ese nivel.
     * 
     * @throws DimensionNoPresenteException Si alguna dimensión especificada no está presente en el cubo.
     * @throws NivelNoPresenteException Si algún nivel especificado no está presente en el cubo.
     * @throws ValorNoPresenteException Si algún valor de corte no está presente en el cubo para el nivel seleccionado de la dimensión.
     * @throws ArgumentosInoperablesException Si el mapa de criterios de filtrado está vacío.
     */
    public void dice(Map<Dimension, Map<String, List<String>>> criteriosDice){
        
        // Verifico que 'criteriosDice' no sea un mapa vacío
        if (criteriosDice.isEmpty()){
            throw new ArgumentosInoperablesException("El mapa de criterios para la operación Dice no puede estar vacío.");
        }

        // Itero sobre cada entrada del mapa 'criteriosDice' para verificar que los niveles, dimensiones y valores seleccionados estén presentes
        // en el cubo
        for (Map.Entry<Dimension, Map<String, List<String>>> criterioDimension : criteriosDice.entrySet()){
            if (!this.tablasDimensiones.contains(criterioDimension.getKey())){
                throw new DimensionNoPresenteException("La dimension '" + criterioDimension.getKey().getNombre() + "' no esta presente en el cubo <" + this.nombre + ">.");
            }
            for (Map.Entry<String, List<String>> criterioNivel : criterioDimension.getValue().entrySet()){
                if (!this.tablaOperacion.getHeaders().contains(criterioNivel.getKey())){
                    throw new NivelNoPresenteException("El nivel '" + criterioNivel.getKey() + "' no esta presente en el cubo <" + this.nombre + ">.");
                }
                for (String valor : criterioNivel.getValue()){
                    if (!this.tablaOperacion.getColumna(criterioNivel.getKey()).contains(valor)){
                        throw new ValorNoPresenteException("El valor '" + valor + "' del nivel '" + criterioNivel.getKey() + "' no esta presente en el cubo <" + this.nombre + ">.");
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
     * Realiza una proyección de una parte seleccionada de los datos del cubo en un formato tabular.
     *
     * @param cantFilas El número de filas a mostrar.
     * @param nombresColumnas La lista de nombres de las columnas que están contenidas en este cubo.
     * 
     * @throws ColumnaNoPresenteException Si una columna solicitada no está presente en este cubo.
     * @throws FilaFueraDeRangoException Si el número solicitado de filas está fuera del rango de datos de este cubo.
     */
    public void proyectar(int cantFilas, List<String> nombresColumnas){
        this.tablaOperacion.ver(cantFilas, nombresColumnas);
    }

    /**
     * Exporta la información de este cubo, descargando los datos del cubo a un archivo utilizando una estrategia específica de exportación.
     *
     * @param rutaGuardadoArchivo La ruta de destino donde se guardará el archivo.
     * @param estrategiaExportacion La estrategia de exportación a utilizar.
     * 
     * @throws IOException Si ocurre un error de entrada/salida al exportar los datos del cubo.
     */
    public void exportar(String rutaGuardadoArchivo, EstrategiaExportarArchivo estrategiaExportacion) throws IOException{
        
        // Junto los encabezados del cubo con los datos asociados 
        // a los mismos para exportar su información correctamente
        List<List<String>> datosExportar = new ArrayList<>();
        datosExportar.add(this.tablaOperacion.getHeaders());
        for (List<String> fila : this.tablaOperacion.getDatosTabla()){
            datosExportar.add(fila);
        }

        // Exporto la información del cubo
        estrategiaExportacion.exportarArchivo(rutaGuardadoArchivo, datosExportar);

    }

    /**
     * Restaura la información de este cubo al momento en que se inicializó, agregando de vuelta
     * la información de las dimensiones y tabla de hechos asociadas a este cubo.
     */    
    public void reiniciar(){
        this.tablaOperacion = tablaBase.getCuerpoCopy();
        this.historialDice = new ArrayList<>();
        this.historialRollUp = new ArrayList<>();
        this.historialSlice = new ArrayList<>();
    }

    /**
     * <p>
     * Devuelve una representación en forma de cadena de texto de este cubo.
     * </p>
     * 
     * <p>
     * La representación incluye el nombre del cubo, la cantidad de registros en la tabla de operaciones,
     * la tabla de hechos y las dimensiones dentro del mismo.
     * </p>
     * 
     * @return La representación en forma de cadena de texto de este cubo.
     */
    @Override
    public String toString() {
        String informacionCubo = "\nCUBO <" + this.nombre + "> / (CANTIDAD DE REGISTROS: " + this.tablaOperacion.getDatosTabla().size() + ")\n";
        informacionCubo += "---------------------------\n";
        informacionCubo += this.tablaHechos.toString();
        informacionCubo += "---------------------------\n";
        informacionCubo += "DIMENSIONES\n" + "\n";	
        for (Dimension dimension : this.tablasDimensiones){
            informacionCubo += " " + dimension.toString() + "\n";
        }
        informacionCubo += "---------------------------";
        return informacionCubo;
    }

}

