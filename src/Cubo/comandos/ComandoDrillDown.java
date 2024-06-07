package Cubo.comandos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Cubo.CuboOLAP;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

/**
 * Esta clase implementa el comando DrillDown para la clase {@link CuboOLAP}.
 * Desagrega los datos de una dimensión en niveles más detallados, resultando un cubo con más registros.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoDrillDown implements ComandoCubo {

    private Hecho tabla_base;
    private Map<Dimension, String> criterios_expansion;
    private List<ComandoRollUp> historial_rollUp;
    private List<ComandoDice> historial_dice;
    private List<ComandoSlice> historial_slice;
    private List<ComandoDrillDown> historial_drillDown;


    /**
     *  Constructor para la clase ComandoDrillDown.
     * 
     * @param criterios_expansion Un mapa que contiene dimensiones como clave y como valor el nivel al cual se va a expandir.
     * @param tabla_base La tabla de hechos que se utilizará para llevar a cabo la operación.
     * @param historial_rollUp El historial de operaciones RollUp aplicados sobre la instancia de 'CuboOLAP' que invoca esta clase 
     * @param historial_dice El historial de operaciones Dice aplicados sobre la instancia de 'CuboOLAP' que invoca esta clase 
     * @param historial_slice El historial de operaciones Slice aplicados sobre la instancia de 'CuboOLAP' que invoca esta clase  
     * @param historial_drillDown  El historial de operaciones DrillDown aplicados sobre la instancia de 'CuboOLAP' que invoca esta clase 
     */
    public ComandoDrillDown(Map<Dimension, String> criterios_expansion, Hecho tabla_base, List<ComandoRollUp> historial_rollUp, List<ComandoDice> historial_dice, 
                            List<ComandoSlice> historial_slice, List<ComandoDrillDown> historial_drillDown) {

        this.criterios_expansion = criterios_expansion;
        this.tabla_base = tabla_base;
        this.historial_rollUp = historial_rollUp;
        this.historial_dice = historial_dice;
        this.historial_slice = historial_slice;
        this.historial_drillDown = historial_drillDown;
    }

    /**
     * Ejecuta el comando RollUp.
     * Desagrupa los hechos por los criterios de expansion, el cubo vuelve a contener todos los hechos originales,
     * y almacena el resultado en 'tabla_base', alterando el estado del cubo.
     */
    @Override
    public void ejecutar(){

        // Añado al historial el comando antes de ejecutarlo
        this.historial_drillDown.add(this);

        // Obtengo el último método RollUp aplicado sobre el cubo
        ComandoRollUp ultimo_rollUp = this.historial_rollUp.get(this.historial_rollUp.size() - 1);

        // Obtengo los niveles sobre los cuales se agrupó, esto representaría el estado actual del cubo en cuanto 
        // a agrupación
        List<String> niveles_actuales = ultimo_rollUp.getNivelesOperacion();

        // Ahora por cada entrada del mapa "criterios_expansion" voy obteniendo los niveles de detalle especificados 
        for (Map.Entry<Dimension, String> criterio : this.criterios_expansion.entrySet()) {
            
            // Guardo la dimension y el nivel de detalle
            Dimension dimension = criterio.getKey();
            String nivel_detalle = criterio.getValue();

            // Obtengo los niveles de desagregación
            List<String> niveles_detalle = new ArrayList<>();
            List<String> niveles_desagregacion = ComandoRollUp.obtenerNivelesOperacion(dimension, nivel_detalle, niveles_detalle);

            // Añado dichos niveles a 'niveles_actuales', esto logra el efecto de desagrupar las dimensiones seleccionadas
            niveles_actuales = obtenerNivelesDesagregacion(niveles_actuales, niveles_desagregacion, dimension);
        }

        // Finalmente uso la clase ComandoRollUp para "agrupar" por los niveles obtenidos
        ComandoRollUp comando = new ComandoRollUp(this.tabla_base, this.tabla_base.getHechos(), "sum", niveles_actuales, historial_rollUp);

        // Ejecuto el comando
        comando.ejecutar();

        // Obtengo el resultado, modificando la tabla que usé para operar
        this.tabla_base = comando.getResultado();
    }


    // Métodos de ayuda para método ejecutar()

    /**
     * Obtiene los niveles de desagregación a partir de una lista de niveles y una lista de niveles de desagregación,
     * en función de una dimensión dada.
     *
     * @param lista_niveles           La lista de niveles existentes.
     * @param niveles_desagregacion   La lista de niveles de desagregación a añadir.
     * @param dimension               La dimensión sobre la cual se realiza la desagregación.
     * @return                        Una lista con los niveles de desagregación añadidos.
     */
    private List<String> obtenerNivelesDesagregacion(List<String> lista_niveles, List<String> niveles_desagregacion, Dimension dimension) {

        // Primero defino una lista 'lista_resultado' 
        List<String> lista_resultado = new ArrayList<>();

        // Obtengo el nivel mas abstracto de la dimensión sobre la cual itero
        String nivel_abstracto = ComandoRollUp.obtenerClavePorValor(dimension.getIndicesNiveles(), 0);

        // En lista resultado añado todos los niveles que no estén en la dimensión
        for (String nivel : lista_niveles) {
            if (!dimension.getHeaders().contains(nivel)) {
                lista_resultado.add(nivel);
            }
        }

        // Inicializo una variable para guardar la posición de inserción y una flag que me 
        // indica si ya encontré el nivel abstracto de la dimensión que quiero añadir
        int posicion_insercion = 0;
        boolean encontrado = false;

        // Itero sobre la lista de niveles
        for (int i = 0; i < lista_niveles.size(); i++) {

            // Inserto la información a partir de la posición del nivel más abstracto
            // dicho nivel siempre estará presente tanto en agrupación como en desagregación
            if (lista_niveles.get(i).equals(nivel_abstracto)) {
                if (!encontrado) {
                    posicion_insercion = i;
                    encontrado = true;
                }
            } else if (encontrado) {
                break;
            }
        }

        // Añado los niveles de desagregación en la posición correspondiente
        lista_resultado.addAll(posicion_insercion, niveles_desagregacion);

        // Devuelvo la lista con los niveles de desagregación añadidos
        return lista_resultado;
    }


    // Getters de la clase

    /**
     * Devuelve la 'tabla_operacion' del cubo con el método ya aplicado.
     *
     * @return Un objeto de tipo hecho que representa la tabla sobre la cual se ejecutan las operaciones del cubo.
     */
    public Hecho getResultado() {
        return this.tabla_base;
    }

    /**
     * Devuelve el historial de operaciones DrillDown aplicadas sobre la instancia de 'CuboOLAP' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoDrillDown que representan las operaciones DrillDown efectuadas sobre el cubo.
     */
    public List<ComandoDrillDown> getHistorial(){
        return this.historial_drillDown;
    }


}
    

