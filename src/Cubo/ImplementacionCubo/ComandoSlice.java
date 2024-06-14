package Cubo.ImplementacionCubo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import Cubo.tablasCubo.Dimension;

/**
 * <p>
 * Esta clase se encarga de implementar el método Dice para la clase {@link Cubo}.
 * </p>
 * 
 * <p>
 * Implementa la interfaz {@link ComandoCubo}.
 * </p>
 */
class ComandoSlice implements ComandoCubo {

    // Atributos de la clase ComandoSlice
    private CuerpoCubo tablaOperacion;
    private Dimension dimension;
    private String nivel;
    private String valorCorte;
    private List<ComandoSlice> historialSlice;

    /**
     * <p>
     * <b>Constructor para la clase ComandoSlice.</b>
     * </p>
     * 
     * <p>
     * Se encarga de recibir todos los argumentos del método junto con el cuerpo del cubo y su historial de operaciones.
     * </p>
     *
     * @param tablaOperacion La tabla que se utilizará para llevar a cabo la operación.
     * @param dimension La dimensión sobre la cual se aplica el método.
     * @param nivel El nivel de la dimensión implicado en el método.
     * @param valorCorte El valor del nivel de la dimensión al cual se fijará la misma.
     * @param historialSlice El historial de operaciones Slice aplicados sobre la instancia de Cubo que 
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
     * Ejecuta el método Slice para la clase {@link Cubo}.
     */
    @Override
    public void ejecutar() {

        // Añado al historial el comando antes de ejecutarlo
        this.historialSlice.add(this);

        // Obtengo el índice del nivel por el cual se va a filtrar
        Integer indice_nivel = this.tablaOperacion.getHeaders().indexOf(this.nivel);

        // Genero una matriz que contendrá a la operación resultante
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Ahora itero por cada fila de la tabla de operación y me quedo 
        // con aquellas que cumplan con la condición de corte
        for (List<String> fila : this.tablaOperacion.getDatosTabla()) {
            if (fila.get(indice_nivel).equals(this.valorCorte)) {
                List<String> nueva_fila = crearFilaFiltrada(fila);
                operacion_resultante.add(nueva_fila);
            }
        }

        // Guardo los headers de la operación
        List<String> headers_operacion = obtenerHeadersOperacion();

        // Actualizo el estado interno del cubo
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
        // e itero sobre los encabezados de la dimensión
        List<String> nueva_fila = new ArrayList<>(fila);
        for (String columna : this.dimension.getHeaders()) {

            // Obtengo el indice del nivel a eliminar
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
     * Se encarga de generar los encabezados de la operación resultante.
     *
     * @return Una lista que contiene los encabezados implicados en la operación Slice.
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
     * @return El cuerpo del cubo con el método ya aplicado
     */
    public CuerpoCubo getResultado() {
        return this.tablaOperacion;
    }

    /**
     * @return Una lista que representa el historial de métodos Slice aplicados 
     *         sobre la instancia de Cubo que invoca esta clase junto con la
     *         instancia que se encargó de ejecutar el mismo.
     */
    public List<ComandoSlice> getHistorial(){
        return this.historialSlice;
    }

    /**
     * @return La dimensión que se vió implicada en la 
     *         ejecución de este método.
     */
    public Dimension getDimension() {
        return this.dimension;
    }

    /**
     * @return El nivel de la dimensión que se vió implicada en la 
     *         ejecución de este método.
     */
    public String getNivelCorte() {
        return this.nivel;
    }

    /**
     * @return El valor del nivel de la dimensión que se vió implicada en la 
     *         ejecución de este método.
     */
    public String getValorCorte() {
        return this.valorCorte;
    }

}
