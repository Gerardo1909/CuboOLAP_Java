package Cubo;

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
import Cubo.excepciones.excepciones_tabla.TablaException;
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

    private final List<Dimension> dimensiones;
    private final Hecho hecho;
    private final String nombre;
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

        // Y ahora en tabla_operacion guardo una gran tabla resultado de hacer merge a la tabla de hechos por cada dimensión,
        // esto servirá para realizar las operaciones
        Hecho hechos_merged = hecho.getHechoCopy();
        for (Dimension dimension : dimensiones) {
            Tabla.merge(hechos_merged, dimension, dimension.getPrimaryKey());
        }
        this.tabla_operacion = hechos_merged;
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
        ComandoRollUp comando = new ComandoRollUp(this.tabla_operacion, criterios_reduccion, hechos_seleccionados, agregacion_parsed);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación y lo guardo en el atributo 'proyeccion_cubo'
        this.proyeccion_cubo = comando.getResultado();
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
        ComandoSlice comando = new ComandoSlice(this.tabla_operacion,dimension, nivel, valor_corte);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación y lo guardo en el atributo 'proyeccion_cubo'
        this.proyeccion_cubo = comando.getResultado();

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
        this.proyeccion_cubo = comando.getResultado();

    }
    
    /**
     * Muestra una vista parcial del cubo con las columnas especificadas y un número limitado de filas.
     *
     * @param n_filas  el número de filas a visualizar. Si se pasa un número grande de filas 
     *                 (entiendáse por grande un número mayor a la longitud de las filas implicadas en la operación)
     *                 se mostrarán todas las filas que resultaron de la operación aplicada.
     * @param columnas la lista de nombres de columnas a visualizar; si es null, se visualizan todas las columnas
     *                 usadas en la operación aplicada anteriormente.
     * @throws ColumnaNoPresenteException si alguna de las columnas especificadas no está presente en los encabezados.
     */
    @Override
    public void ver(int n_filas, List<String> columnas) throws ColumnaNoPresenteException{
    
        // Verifico que el cubo haya sido operado antes de visualizarse
        if (this.proyeccion_cubo.isEmpty()) {
            System.out.println("Al cubo " + this.getNombre() + " no se le han aplicado operaciones visibles aún");
            return; 
        }

        //Inicializo una lista que contiene los índices de las columnas a ver
        List<Integer> indices_columnas = new ArrayList<>();

        // Armo una nueva lista 'columnas_ver' que serán las que finalmente se visualizen, usando los índices obtenidos
        List<String> columnas_ver = new ArrayList<>();

        // Primero reviso el caso de que se pasaron columnas por argumento
        if (columnas!= null){
            // Verifico si las columnas especificadas existen en los headers
            for (String columna : columnas){
                if (!this.tabla_operacion.getHeaders().contains(columna)){
                    throw new ColumnaNoPresenteException("La columna especificada" + columna + "no existe en los encabezados.");
                }
            }

            // Obtengo los índices de las columnas seleccionadas
            for (String columna : columnas) {
                indices_columnas.add(this.proyeccion_cubo.keySet().iterator().next().indexOf(columna));
            }

            columnas_ver = columnas;
        }

        // Si no se seleccionan columnas por defecto obtengo los índices de todas las que están en la operación
        if (columnas == null){
            for (int i = 0; i < this.proyeccion_cubo.keySet().iterator().next().size(); i++) {
                indices_columnas.add(i);
            }

            columnas_ver = this.proyeccion_cubo.keySet().iterator().next();
        }

        // Filtro la matriz resultante para quedarme solo con las columnas seleccionadas
        List<List<String>> matriz_filtrada = new ArrayList<>();
        for (List<String> fila : this.proyeccion_cubo.values().iterator().next()) {
            List<String> fila_filtrada = new ArrayList<>();
            for (int indice : indices_columnas) {
                fila_filtrada.add(fila.get(indice));
            }
            matriz_filtrada.add(fila_filtrada);
        }

        // Muestro las columnas resultantes
        for (String columna : columnas_ver) {
            System.out.print(String.format("%-20s", columna));
        }
        System.out.println();
    
        // Itero sobre las primeras 'n_filas' filas de la matriz resultado
        int contador_filas = 0;
        for (List<String> fila : matriz_filtrada) {
            // Cuando se alcanze el máximo de filas salgo del ciclo
            if (contador_filas >= n_filas) {
                break;
            }
            // Printeo los elementos de la fila
            for (String elemento : fila) {
                System.out.print(String.format("%-20s", elemento));
            }
            System.out.println();
            contador_filas++;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public CuboOLAP getCuboOLAP() throws ClaveNoPresenteException, ColumnaNoPresenteException, HechoNoPresenteException{ 
        return new CuboOLAP(this.nombre, this.hecho.getHechoCopy(), new ArrayList<>(dimensiones));
    }

    public List<Dimension> getDimensiones() {
        return new ArrayList<>(dimensiones);
    }
}

