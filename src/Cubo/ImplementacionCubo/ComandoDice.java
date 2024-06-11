package Cubo.ImplementacionCubo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Cubo.Cubo;
import Cubo.tablasCubo.Dimension;

/**
 * Esta clase implementa el comando Dice para la clase {@link Cubo}.
 * "Filtra" el cubo según los criterios especificados, resultando un cubo con misma dimensionalidad 
 * pero menos registros.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoDice implements ComandoCubo{

    private CuerpoCubo tablaOperacion;
    private Map<Dimension, Map<String, List<String>>> criteriosDice;
    private List<ComandoDice> historialDice;

    /**
     * Constructor para la clase ComandoDice.
     *
     * @param tablaOperacion La tabla que se utilizará para llevar a cabo la operación.
     * @param criteriosDice Un mapa que contiene dimensiones como clave y como valor mapas con criterios de corte.
     * @param historialDice El historial de operaciones Dice aplicados sobre la instancia de 'Cubo' que 
     *                        invoca esta clase.
     */
    public ComandoDice(CuerpoCubo tablaOperacion, Map<Dimension, Map<String, List<String>>> criteriosDice, 
                       List<ComandoDice> historialDice) {
        this.criteriosDice = criteriosDice;
        this.tablaOperacion = tablaOperacion;
        this.historialDice = historialDice;
    }

    /**
     * Ejecuta el comando Dice.
     * Ejecuta la operación de corte en varias dimensiones en la tabla de operación, 
     * y almacena el resultado en la misma 'tablaOperacion', alterando el estado del cubo.
     */
    @Override
    public void ejecutar(){

        // Añado al historial el comando antes de ejecutarlo
        this.historialDice.add(this);

        // Genero una matriz que contendrá la operación resultante
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Itero sobre cada fila de la tabla de operación
        for (List<String> fila : this.tablaOperacion.getData()) {

            // Verifico si la fila cumple con los criterios
            if (cumpleCriterios(fila)) {
                operacion_resultante.add(fila);
            }
        }

        // Actualizo la tabla de operación con la operación resultante
        this.tablaOperacion = new CuerpoCubo(operacion_resultante, this.tablaOperacion.getHeaders(), this.tablaOperacion.getHechosCubo());
    }

    
    // Métodos de ayuda para método ejecutar()

    /**
     * Verifica si una fila cumple con los criterios de corte.
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
     * Devuelve la 'tablaOperacion' del cubo con el método ya aplicado.
     *
     * @return Un objeto de tipo CuerpoCubo que representa la tabla sobre la cual se ejecutan las operaciones del cubo.
     */
    public CuerpoCubo getResultado() {
        return this.tablaOperacion;
    }

    /**
     * Devuelve el historial de operaciones Dice aplicadas sobre la instancia de 'Cubo' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoDice que representan las operaciones Dice efectuadas sobre el cubo.
     */
    public List<ComandoDice> getHistorial(){
        return this.historialDice;
    }

    protected Map<Dimension, Map<String, List<String>>> getCriteriosDice(){
        return this.criteriosDice;
    }
}
