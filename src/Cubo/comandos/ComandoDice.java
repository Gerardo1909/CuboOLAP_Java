package Cubo.comandos;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import Cubo.tablas.Hecho;
import Cubo.CuboOLAP;
import Cubo.excepciones.excepciones_tabla.TablaException;
import Cubo.tablas.Dimension;
import java.util.ArrayDeque;

/**
 * Esta clase implementa el comando Dice para la clase {@link CuboOLAP}.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoDice implements ComandoCubo{

    private Hecho tabla_operacion;
    private Map<Dimension, Map<String, List<String>>> criterios;
    private Deque<ComandoDice> historial_dice;

    /**
     * Constructor para la clase ComandoDice.
     *
     * @param tabla_operacion La tabla de hechos que se utilizará para llevar a cabo la operación.
     * @param criterios Un mapa que contiene dimensiones como clave y como valor mapas con criterios de corte.
     */
    public ComandoDice(Hecho tabla_operacion, Map<Dimension, Map<String, List<String>>> criterios, 
                       Deque<ComandoDice> historial_dice) {
        this.criterios = criterios;
        this.tabla_operacion = tabla_operacion;
        this.historial_dice = historial_dice;
    }

    /**
     * Ejecuta el comando Dice.
     * Ejecuta la operación de corte en varias dimensiones en la tabla de operación, 
     * y almacena el estado interno del cubo mediante 'tabla_operacion'.
     * @throws TablaException Si se produce algún error durante la ejecución del comando.
     */
    @Override
    public void ejecutar() throws TablaException {

        // Añado al historial el comando antes de ejecutarlo
        this.historial_dice.addLast(this);

        // Genero una matriz que contendrá la operación resultante
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Itero sobre cada fila de la tabla de operación
        for (List<String> fila : tabla_operacion.getData()) {

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

        // Genero una lista para guardar los headers de la operación resultante
        List<String> headers_operacion = new ArrayList<>(tabla_operacion.getHeaders());

        // Finalmente modifico 'tabla_operacion'
        this.tabla_operacion = new Hecho(this.tabla_operacion.getNombre(), operacion_resultante, headers_operacion, this.tabla_operacion.getHechos());
    }

    public Hecho getResultado() {
        return this.tabla_operacion;
    }

    public Deque<ComandoDice> getHistorial(){
        return this.historial_dice;
    }

    protected Map<Dimension, Map<String, List<String>>> getCriterios(){
        return this.criterios;
    }

}
