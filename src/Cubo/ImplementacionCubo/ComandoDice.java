package Cubo.ImplementacionCubo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
class ComandoDice implements ComandoCubo{

    // Atributos de la clase ComandoDice
    private CuerpoCubo tablaOperacion;
    private Map<Dimension, Map<String, List<String>>> criteriosDice;
    private List<ComandoDice> historialDice;

    /**
     * <p>
     * <b>Constructor para la clase ComandoDice.</b>
     * </p>
     * 
     * <p>
     * Se encarga de recibir todos los argumentos del método junto con el cuerpo del cubo y su historial de operaciones.
     * </p>
     *
     * @param tablaOperacion La tabla que se utilizará para llevar a cabo la operación.
     * @param criteriosDice Un mapa que contiene como clave la dimensión sobre la cual se aplica el filtrado y como 
     *                      valor otro mapa que contiene como clave el nivel de la dimensión que se filtra y como valor
     *                      una lista con los nombres de los valores que se quieren permitir en ese nivel.
     * @param historialDice El historial de operaciones Dice aplicados sobre la instancia de Cubo que 
     *                        invoca esta clase.
     */
    public ComandoDice(CuerpoCubo tablaOperacion, Map<Dimension, Map<String, List<String>>> criteriosDice, 
                       List<ComandoDice> historialDice) {
        this.criteriosDice = criteriosDice;
        this.tablaOperacion = tablaOperacion;
        this.historialDice = historialDice;
    }

    /**
     * Ejecuta el método Dice para la clase {@link Cubo}.
     */
    @Override
    public void ejecutar(){

        // Añado al historial el comando antes de ejecutarlo
        this.historialDice.add(this);

        // Genero una matriz que contendrá la operación resultante
        // e itero sobre cada fila de la tabla de operación
        List<List<String>> operacionResultante = new ArrayList<>();
        for (List<String> fila : this.tablaOperacion.getDatosTabla()) {
            if (cumpleCriterios(fila)) {
                operacionResultante.add(fila);
            }
        }

        // Actualizo el estado interno del cubo
        this.tablaOperacion = new CuerpoCubo(operacionResultante, this.tablaOperacion.getHeaders(), this.tablaOperacion.getHechosCubo());
    }

    
    // Métodos de ayuda para método ejecutar()

    /**
     * Se encarga de verificar si una fila en la información interna
     * del cubo cumple con los criterios de filtrado.
     *
     * @param fila La fila a verificar.
     * @return true si la fila cumple con los criterios, false de lo contrario.
     */
    private boolean cumpleCriterios(List<String> fila) {
        
        // Itero sobre los criterios de la dimensión
        for (Map.Entry<Dimension, Map<String, List<String>>> criterioDimension : this.criteriosDice.entrySet()) {

            // Obtengo el mapa que contiene a los niveles con los valores permitidos
            Map<String, List<String>> valores_nivel = criterioDimension.getValue();

            // Itero sobre cada nivel
            for (Map.Entry<String, List<String>> valor_nivel : valores_nivel.entrySet()) {

                // Obtengo el nombre del nivel y los valores permitidos para ese nivel
                String nivel = valor_nivel.getKey();
                List<String> valores_permitidos = valor_nivel.getValue();

                // Obtengo el índice del nivel de la dimensión en la tabla de operación
                int indice_nivel = tablaOperacion.getHeaders().indexOf(nivel);

                // Verifico si la fila en la que estoy no contiene alguno de los valores permitidos
                if (!valores_permitidos.contains(fila.get(indice_nivel))) {
                    return false;
                }
            }
        }

        return true;
    }


    // Getters de la clase

    /**
     * @return El cuerpo del cubo con el método ya aplicado
     */
    public CuerpoCubo getResultado() {
        return this.tablaOperacion;
    }

    /**
     * @return Una lista que representa el historial de métodos Dice aplicados 
     *         sobre la instancia de Cubo que invoca esta clase junto con la
     *         instancia que se encargó de ejecutar el mismo.
     */
    public List<ComandoDice> getHistorial(){
        return this.historialDice;
    }

    /**
     * @return El mapa que contiene los criterios de filtrado que se vieron
     *         implicados en la ejecución de este método.
     */
    public Map<Dimension, Map<String, List<String>>> getCriteriosDice(){
        return this.criteriosDice;
    }
}
