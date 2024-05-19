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
import Cubo.excepciones.excepciones_tabla.FilaFueraDeRangoException;
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
    private Map<List<String>, List<List<String>>> estado_cubo;

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
     * @param criterio_reduccion La lista de niveles en los que reducir el cubo.
     * @param hechos_seleccionados La lista de hechos a incluir en la operación de roll-up.
     * @param agregacion La operación de agregación a realizar. Solo son soportadas: "sum", "max", "min", "count" (Escribir tal cual al pasar el argumento!).
     * @throws AgregacionNoSoportadaException Si la operación de agregación seleccionada no está entre las disponibles.
     * @throws NivelNoPresenteException Si algún nivel especificado no está presente en alguna dimensión.
     * @throws HechoNoPresenteException Si algún hecho seleccionado no está presente en la tabla de hechos.
     * @throws TablaException Si ocurre un error inesperado al invocar el comando.
     */
    public void rollUp(List<String> criterio_reduccion, List<String> hechos_seleccionados, String agregacion) throws TablaException, AgregacionNoSoportadaException, NivelNoPresenteException, HechoNoPresenteException{

        // Formateo el string de la operación de agregacion
        String agregacion_parsed = agregacion.toLowerCase().trim();

        // Armo una lista que contiene los nombres exactos de las operaciones de agregación soportadas
        List<String> operaciones_soportadas = new ArrayList<>(Arrays.asList("sum", "max", "min", "count"));

        // Verifico que la operación de agregacion seleccionada esté presente en dicha lista
        if (!operaciones_soportadas.contains(agregacion_parsed)){
            throw new AgregacionNoSoportadaException("La operación de agregación " + agregacion_parsed + " no está soportada.");
        }


        // Verifico que estén presentes todos los criterios de reducción pasados como argumento
        for (String criterio: criterio_reduccion){
            if (!tabla_operacion.getHeaders().contains(criterio)){
                throw new NivelNoPresenteException("El nivel " + criterio + " no está en ninguna de las dimensiones.");
            }
        }

        // Verifico que estén presentes todos los hechos seleccionados para la operación
        for (String hecho: hechos_seleccionados){
            if (!tabla_operacion.getHeaders().contains(hecho)){
                throw new HechoNoPresenteException("El hecho " + hecho + " no está presente en la tabla de hechos " + this.hecho.getNombre());
            }
        }

        // Genero una instancia de RollUp
        ComandoRollUp comando = new ComandoRollUp(this.tabla_operacion, criterio_reduccion, hechos_seleccionados, agregacion_parsed);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación y lo guardo en el atributo 'estado_cubo'
        this.estado_cubo = comando.getResultado();
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

        // Obtengo el resultado de la operación y lo guardo en el atributo 'estado_cubo'
        this.estado_cubo = comando.getResultado();

    }

    public void dice(Map<String, List<String>> filters) throws TablaException{
        new ComandoDice(filters).ejecutar();
    }
    
    /**
     * Ver documentación en {@link Visualizable}.
     */
    @Override
    public void ver(int n_filas, List<String> columnas) throws ColumnaNoPresenteException, FilaFueraDeRangoException{
    
        // Verifico que el cubo haya sido operado antes de visualizarse
        if (this.estado_cubo.isEmpty()) {
            System.out.println("Al cubo " + this.getNombre() + " no se le han aplicado operaciones visibles aún");
            return; 
        }

        // Verifico si las columnas especificadas existen en los headers
        for (String columna : columnas){
            if (!this.tabla_operacion.getHeaders().contains(columna)){
                throw new ColumnaNoPresenteException("La columna especificada" + columna + "no existe en los encabezados.");
            }
        }

        // Obtengo los índices de las columnas seleccionadas
        List<Integer> indices_columnas = new ArrayList<>();
        for (String columna : columnas) {
            indices_columnas.add(this.estado_cubo.keySet().iterator().next().indexOf(columna));
        }

        // Filtro la matriz resultante para quedarme solo con las columnas seleccionadas
        List<List<String>> matriz_filtrada = new ArrayList<>();
        for (List<String> fila : this.estado_cubo.values().iterator().next()) {
            List<String> fila_filtrada = new ArrayList<>();
            for (int indice : indices_columnas) {
                fila_filtrada.add(fila.get(indice));
            }
            matriz_filtrada.add(fila_filtrada);
        }

        // Muestro las columnas seleccionadas
        for (String columna : columnas) {
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
}

