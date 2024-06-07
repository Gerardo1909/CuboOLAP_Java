package Cubo.comandos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import Cubo.CuboOLAP;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

/**
 * Esta clase implementa el comando Slice para la clase {@link CuboOLAP}.
 * "Corta" los hechos según la dimensión, nivel y valor especificados, resultando un cubo 
 * de menor dimensionalidad pero con información según los criterios especificados.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoSlice implements ComandoCubo {

    private Hecho tabla_operacion;
    private Dimension dimension;
    private String nivel;
    private String valor_corte;
    private List<ComandoSlice> historial_slice;

    /**
     * Constructor para la clase ComandoSlice.
     *
     * @param tabla_operacion La tabla de hechos que se utilizará para llevar a cabo la operación.
     * @param dimension La dimensión en la que se va a cortar.
     * @param nivel El nivel dentro de la dimensión en la que se va a cortar.
     * @param valor_corte El valor para filtrar.
     * @param historial_slice El historial de operaciones Slice aplicados sobre la instancia de 'CuboOLAP' que 
     *                        invoca esta clase.
     */
    public ComandoSlice(Hecho tabla_operacion, Dimension dimension, String nivel,
                        String valor_corte, List<ComandoSlice> historial_slice) {
        this.dimension = dimension;
        this.valor_corte = valor_corte;
        this.tabla_operacion = tabla_operacion;
        this.nivel = nivel;
        this.historial_slice = historial_slice;
    }

    /**
     * Ejecuta el comando Slice.
     * Ejecuta la operación de corte en tabla_operacion', 
     * y almacena el resultado en la misma 'tabla_operacion', alterando el estado del cubo.
     */
    @Override
    public void ejecutar() {
        // Añado al historial el comando antes de ejecutarlo
        this.historial_slice.add(this);

        // Primero obtengo el índice del nivel por el cual se va a filtrar
        Integer indice_nivel = this.tabla_operacion.getHeaders().indexOf(this.nivel);

        // Genero la tabla que contendrá a la operación resultante
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Ahora itero por cada fila de 'tabla_operacion' y me quedo con aquellas que cumplan con la condición de corte
        for (List<String> fila : this.tabla_operacion.getData()) {
            if (fila.get(indice_nivel).equals(this.valor_corte)) {
                List<String> nueva_fila = crearFilaFiltrada(fila);
                operacion_resultante.add(nueva_fila);
            }
        }

        // Genero una lista para guardar los headers de la operacion resultante
        List<String> headers_operacion = obtenerHeadersOperacion();

        // Finalmente modifico 'tabla_operacion'
        try {
            this.tabla_operacion = new Hecho(this.tabla_operacion.getNombre(), operacion_resultante, headers_operacion, this.tabla_operacion.getHechos());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }


    // Métodos de ayuda para método ejecutar()

    /**
     * Crea una nueva fila filtrada eliminando las columnas correspondientes a la dimensión
     * sobre la cual se aplica la operación de Slice.
     *
     * @param fila La fila original de la tabla de hechos.
     * @return Una nueva fila filtrada.
     */
    private List<String> crearFilaFiltrada(List<String> fila) {
        
        // Creo una nueva lista de strings que contendrá la fila filtrada
        List<String> nueva_fila = new ArrayList<>(fila);

        // Itero sobre los headers de la dimensión
        for (String columna : this.dimension.getHeaders()) {

            // Obtengo el índice de la columna en la tabla de hechos
            int indice_nivel_eliminar = this.tabla_operacion.getHeaders().indexOf(columna);

            // Verifico si el índice es válido y si la fila tiene suficientes elementos
            if (indice_nivel_eliminar != -1 && indice_nivel_eliminar < nueva_fila.size()) {

            // Reemplazo el valor de la columna por null
            nueva_fila.set(indice_nivel_eliminar, null);
            }
        }

        // Elimino los elementos nulos de la fila
        nueva_fila.removeIf(Objects::isNull);
        return nueva_fila;
    }

    /**
     * Obtiene los headers de la operación resultante eliminando las 
     * columnas correspondientes a la dimensión sobre la cual se aplica la operación
     * de Slice.
     *
     * @return Los headers de la operación resultante.
     */
    private List<String> obtenerHeadersOperacion() {
        // Creo una nueva lista de headers de operación y la inicializo con los headers de la tabla de operación
        List<String> headers_operacion = new ArrayList<>(this.tabla_operacion.getHeaders());
        
        // Remuevo los headers correspondientes a la dimensión sobre la cual se aplica la operación de Slice
        headers_operacion.removeAll(this.dimension.getHeaders());
        
        // Devuelvo la lista de headers resultante
        return headers_operacion;
    }


    // Getters de la clase

    /**
     * Devuelve la 'tabla_operacion' del cubo con el método ya aplicado.
     *
     * @return Un objeto de tipo hecho que representa la tabla sobre la cual se ejecutan las operaciones del cubo.
     */
    public Hecho getResultado() {
        return this.tabla_operacion;
    }

    /**
     * Devuelve el historial de operaciones Slice aplicadas sobre la instancia de 'CuboOLAP' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoSlice que representan las operaciones Slice efectuadas sobre el cubo.
     */
    public List<ComandoSlice> getHistorial(){
        return this.historial_slice;
    }

}
