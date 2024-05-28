package Cubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Cubo.comandos.ComandoDice;
import Cubo.comandos.ComandoDrillDown;
import Cubo.comandos.ComandoRollUp;
import Cubo.excepciones.excepciones_dimension.ClaveNoPresenteException;
import Cubo.excepciones.excepciones_dimension.DimensionNoPresenteException;
import Cubo.excepciones.excepciones_dimension.NivelNoPresenteException;
import Cubo.excepciones.excepciones_hechos.HechoNoPresenteException;
import Cubo.excepciones.excepciones_operacion.AgregacionNoSoportadaException;
import Cubo.excepciones.excepciones_operacion.NivelDesagregadoException;
import Cubo.excepciones.excepciones_tabla.ColumnaNoPresenteException;
import Cubo.excepciones.excepciones_tabla.TablaException;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;
import Cubo.tablas.Tabla;

/**
 * Esta clase representa un cubo OLAP.
 * Proporciona métodos para realizar operaciones de roll-up, drill-down, slice, dice y visualización.
 */
public class CuboOLAP{

    private final List<Dimension> dimensiones;
    private final Hecho hecho;
    private final String nombre;
    private Hecho tabla_base;
    private Hecho tabla_operacion;
    private Map<List<String>, List<List<String>>> proyeccion_cubo;

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

        // En 'tabla_operacion' guardo una tabla que contiene a todas las dimensiones junto con la 
        // tabla de hechos mergeadas, esta será a la cual se le apliquen operaciones
        Hecho hechos_merged = hecho.getHechoCopy();
        for (Dimension dimension : dimensiones) {
            Tabla.merge(hechos_merged, dimension, dimension.getPrimaryKey());
        }
        this.tabla_operacion = hechos_merged;

        // Guardo una tabla base que servirá para volver al estado original del cubo
        this.tabla_base = hechos_merged.getHechoCopy();
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
    public void rollUp(Map<Dimension, String> criterios_reduccion, String agregacion) throws TablaException, AgregacionNoSoportadaException, NivelNoPresenteException, DimensionNoPresenteException, HechoNoPresenteException{

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

        // Ahora por cada entrada del mapa, voy agrupando
        for (Map.Entry<Dimension, String> entry : criterios_reduccion.entrySet()) {

            // Tomo la clave y valor del mapo
            Dimension dimension_reduccion = entry.getKey();
            String nivel_reduccion = entry.getValue();

            // Genero una instancia de RollUp
            ComandoRollUp comando = new ComandoRollUp(this.tabla_operacion, dimension_reduccion, nivel_reduccion, agregacion_parsed);

            // Ejecuto la operación
            comando.ejecutar();

            // Modifico el estado del cubo
            this.tabla_operacion = comando.getResultado();

        }

    }

    public void drillDown(Map<Dimension, String> criterios_expansion) throws TablaException, DimensionNoPresenteException, NivelNoPresenteException, NivelDesagregadoException{

        // Verifico que los argumentos pasados estén bien
        for (Map.Entry<Dimension, String> criterio : criterios_expansion.entrySet()){

            // Obtengo clave y valor
            Dimension dimension = criterio.getKey();
            String nivel = criterio.getValue();

            // Verifico que estén presentes todas las dimensiones y niveles pasadas en el mapa 'criterios_expansion'
            if (!this.dimensiones.contains(dimension)){
                throw new DimensionNoPresenteException("La dimensión " + dimension.getNombre() + " no está presente en el cubo " + this.nombre);
            }
            if (!dimension.getNiveles().containsKey(criterios_expansion.get(dimension))){
                throw new NivelNoPresenteException("El nivel " + criterios_expansion.get(dimension) + " no está presente en la dimensión " + dimension.getNombre());
            }

            // Verifico también que no se quiera desagrupar a un nivel que ya está desagrupado
            if (this.tabla_operacion.getHeaders().contains(nivel)){
                throw new NivelDesagregadoException("La dimension " + dimension.getNombre() + " ya está desagrupada al nivel " + nivel);
            }
        }

        // Ahora por cada entrada del mapa, voy desagrupando
        for (Map.Entry<Dimension, String> entry : criterios_expansion.entrySet()) {

            // Tomo la clave y valor del mapo
            Dimension dimension_expansion = entry.getKey();
            String nivel_expansion = entry.getValue();

            // Genero una instancia de RollUp
            ComandoDrillDown comando = new ComandoDrillDown(this.tabla_operacion, dimension_expansion, nivel_expansion);

            // Ejecuto la operación
            comando.ejecutar();

            // Modifico el estado del cubo
            this.tabla_operacion = comando.getResultado();

        }
        
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

        // Convierto los argumentos en un mapa para usar ComandoDice
        Map<String, List<String>> nivel_corte = new LinkedHashMap<>();
        nivel_corte.put(nivel, Arrays.asList(valor_corte));
        Map<Dimension, Map<String, List<String>>> criterio_slice= new LinkedHashMap<>();
        criterio_slice.put(dimension, nivel_corte);

        // Genero una instancia de Slice
        ComandoDice comando = new ComandoDice(this.tabla_operacion, criterio_slice);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación y lo guardo en el atributo 'proyeccion_cubo'
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
    
        // Genero una instancia de Dice
        ComandoDice comando = new ComandoDice(this.tabla_operacion, criterios);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación y lo guardo en el atributo 'proyeccion_cubo'
        this.tabla_operacion = comando.getResultado();

    }
    
    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Nombre del cubo
        sb.append("CuboOLAP: ").append(nombre).append("\n");
        
        // Información de dimensiones
        sb.append("Dimensiones: ").append(dimensiones.size()).append("\n");
        for (Dimension dimension : dimensiones) {
            sb.append(" - ").append(dimension.toString()).append("\n");
        }
        
        // Información de la tabla de hechos usando su propio toString
        sb.append(hecho.toString());
        
        return sb.toString();
    }

    public void resetear() throws TablaException{
        this.tabla_operacion = tabla_base.getHechoCopy();
    }

    public void proyectar(List<String> columnas, int n_filas) throws TablaException{

        // Verifico si las columnas especificadas existen en los headers
        for (String columna : columnas){
            if (!this.tabla_operacion.getHeaders().contains(columna)){
                throw new ColumnaNoPresenteException("La columna especificada " + columna + " no existe en los encabezados.");
            }
        }

        // Armo una lista y guardo las columnas seleccionadas
        List<List<String>> columnas_seleccionadas = new ArrayList<>();
        for (String columna : columnas) {
            columnas_seleccionadas.add(this.tabla_operacion.getColumna(columna));
        }

        //Imprimo las columnas seleccionadas con cierto formato
        for (String columna : columnas) {
            System.out.print(String.format("%-20s", columna));
        }
        System.out.println();

        // Imprimo los datos con cierto formato
        for (int i = 0; i < n_filas; i++) {
            for (List<String> columna : columnas_seleccionadas) {
                System.out.print(String.format("%-20s", columna.get(i)));
            }
            System.out.println();
        }  
    }
}

