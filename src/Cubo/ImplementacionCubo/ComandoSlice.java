package Cubo.ImplementacionCubo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import Cubo.Cubo;
import Cubo.tablasCubo.Dimension;

/**
 * Esta clase implementa el comando Slice para la clase {@link Cubo}.
 * "Corta" los hechos según la dimensión, nivel y valor especificados, resultando un cubo 
 * de menor dimensionalidad pero con información según los criterios especificados.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoSlice implements ComandoCubo {

    private CuerpoCubo tablaOperacion;
    private Dimension dimension;
    private String nivel;
    private String valorCorte;
    private List<ComandoSlice> historialSlice;

    /**
     * Constructor para la clase ComandoSlice.
     *
     * @param tablaOperacion La tabla que se utilizará para llevar a cabo la operación.
     * @param dimension La dimensión en la que se va a cortar.
     * @param nivel El nivel dentro de la dimensión en la que se va a cortar.
     * @param valorCorte El valor para filtrar.
     * @param historialSlice El historial de operaciones Slice aplicados sobre la instancia de 'Cubo' que 
     *                        invoca esta clase.
     */
    public ComandoSlice(CuerpoCubo tablaOperacion, Dimension dimension, String nivel,
                        String valorCorte, List<ComandoSlice> historialSlice) {
        this.dimension = dimension;
        this.valorCorte = valorCorte;
        this.tablaOperacion = tablaOperacion;
        this.nivel = nivel;
        this.historialSlice = historialSlice;
    }

    /**
     * Ejecuta el comando Slice.
     * Ejecuta la operación de corte en tablaOperacion', 
     * y almacena el resultado en la misma 'tablaOperacion', alterando el estado del cubo.
     */
    @Override
    public void ejecutar() {
        // Añado al historial el comando antes de ejecutarlo
        this.historialSlice.add(this);

        // Primero obtengo el índice del nivel por el cual se va a filtrar
        Integer indice_nivel = this.tablaOperacion.getHeaders().indexOf(this.nivel);

        // Genero la tabla que contendrá a la operación resultante
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Ahora itero por cada fila de 'tablaOperacion' y me quedo con aquellas que cumplan con la condición de corte
        for (List<String> fila : this.tablaOperacion.getData()) {
            if (fila.get(indice_nivel).equals(this.valorCorte)) {
                List<String> nueva_fila = crearFilaFiltrada(fila);
                operacion_resultante.add(nueva_fila);
            }
        }

        // Genero una lista para guardar los headers de la operacion resultante
        List<String> headers_operacion = obtenerHeadersOperacion();

        // Actualizo la tabla de operación con la operación resultante
        this.tablaOperacion = new CuerpoCubo(operacion_resultante, headers_operacion, this.tablaOperacion.getHechosCubo());
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
            int indice_nivel_eliminar = this.tablaOperacion.getHeaders().indexOf(columna);

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
        List<String> headers_operacion = new ArrayList<>(this.tablaOperacion.getHeaders());
        
        // Remuevo los headers correspondientes a la dimensión sobre la cual se aplica la operación de Slice
        headers_operacion.removeAll(this.dimension.getHeaders());
        
        // Devuelvo la lista de headers resultante
        return headers_operacion;
    }


    // Getters de la clase

    /**
     * Devuelve la 'tablaOperacion' del cubo con el método ya aplicado.
     *
     * @return Un objeto de tipo CuerpoCubo que representa la tabla sobre la cual se ejecutan las operaciones del cubo.
     */
    public CuerpoCubo getResultado() {
        return this.tablaOperacion;
    }

    /**
     * Devuelve el historial de operaciones Slice aplicadas sobre la instancia de 'Cubo' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoSlice que representan las operaciones Slice efectuadas sobre el cubo.
     */
    public List<ComandoSlice> getHistorial(){
        return this.historialSlice;
    }

    /**
     * Devuelve la dimensión a la cual se le aplicó el método Slice.
     *
     * @return la dimensión asociada al método.
     */
    public Dimension getDimension() {
        return this.dimension;
    }
}
