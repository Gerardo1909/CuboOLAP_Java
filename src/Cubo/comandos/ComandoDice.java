package Cubo.comandos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Cubo.tablas.Hecho;
import Cubo.CuboOLAP;
import Cubo.excepciones.excepciones_tabla.TablaException;
import Cubo.tablas.Dimension;

/**
 * Esta clase implementa el comando Dice para la clase {@link CuboOLAP}.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoDice implements ComandoCubo{

    private Hecho tabla_operacion;
    private Map<Dimension, Map<String, List<String>>> criterios;
    private List<ComandoDice> historial_dice;

    /**
     * Constructor para la clase ComandoDice.
     *
     * @param tabla_operacion La tabla de hechos que se utilizará para llevar a cabo la operación.
     * @param criterios Un mapa que contiene dimensiones como clave y como valor mapas con criterios de corte.
     * @param historial_dice El historial de operaciones Dice aplicados sobre la instancia de 'CuboOLAP' que 
     *                        invoca esta clase.
     */
    public ComandoDice(Hecho tabla_operacion, Map<Dimension, Map<String, List<String>>> criterios, 
                       List<ComandoDice> historial_dice) {
        this.criterios = criterios;
        this.tabla_operacion = tabla_operacion;
        this.historial_dice = historial_dice;
    }

    /**
     * Ejecuta el comando Dice.
     * Ejecuta la operación de corte en varias dimensiones en la tabla de operación, 
     * y almacena el resultado en la misma 'tabla_operacion', alterando el estado del cubo.
     * @throws TablaException Si se produce algún error durante la ejecución del comando.
     */
    @Override
    public void ejecutar() throws TablaException {

        // Añado al historial el comando antes de ejecutarlo
        this.historial_dice.add(this);

        // Genero una matriz que contendrá la operación resultante
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Itero sobre cada fila de la tabla de operación
        for (List<String> fila : this.tabla_operacion.getData()) {

            //Genero un flag que me va a ayudar a verificar si una fila de la tabla cumple con los criterios o no
            boolean cumple_criterios = true;

            // Itero sobre los criterios de la dimensión
            for (Map.Entry<Dimension, Map<String, List<String>>> criterioDimension : this.criterios.entrySet()) {

                // Obtengo el mapa que contiene a los niveles con los valores permitidos
                Map<String, List<String>> valores_nivel = criterioDimension.getValue();

                // Itero sobre cada nivel
                for (Map.Entry<String, List<String>> valor_nivel : valores_nivel.entrySet()) {

                    // Obtengo el nombre del nivel y los valores permitidos para ese nivel
                    String nivel = valor_nivel.getKey();
                    List<String> valores_permitidos = valor_nivel.getValue();

                    // Obtengo el índice del nivel de la dimensión en la tabla de operación
                    int indice_nivel = tabla_operacion.getHeaders().indexOf(nivel);

                    // Verifico si la fila en la que estoy contiene alguno de los valores permitidos
                    if (!valores_permitidos.contains(fila.get(indice_nivel))) {
                        // De no ser así entonces pongo el flag en falso
                        cumple_criterios = false;
                        break;
                    }
                }

                 // Si un criterio de dimensión no se cumple para una fila, no tiene sentido seguir revisando más criterios
                if (!cumple_criterios) {
                    break;
                }
            }

            // Si la fila cumple con todos los criterios, la añado a la lista de operacion_resultante
            if (cumple_criterios) {
                operacion_resultante.add(fila);
            }
        }

        // Finalmente modifico 'tabla_operacion'
        this.tabla_operacion = new Hecho(this.tabla_operacion.getNombre(), operacion_resultante, 
                                         this.tabla_operacion.getHeaders(), this.tabla_operacion.getHechos());
    }


    /**
     * Devuelve la 'tabla_operacion' del cubo con el método ya aplicado.
     *
     * @return Un objeto de tipo hecho que representa la tabla sobre la cual se ejecutan las operaciones del cubo.
     */
    public Hecho getResultado() {
        return this.tabla_operacion;
    }

    /**
     * Devuelve el historial de operaciones Dice aplicadas sobre la instancia de 'CuboOLAP' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoDice que representan las operaciones Dice efectuadas sobre el cubo.
     */
    public List<ComandoDice> getHistorial(){
        return this.historial_dice;
    }
}
