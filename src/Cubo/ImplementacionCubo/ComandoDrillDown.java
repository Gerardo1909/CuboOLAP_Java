package Cubo.ImplementacionCubo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Cubo.Cubo;
import Cubo.tablasCubo.Dimension;

/**
 * Esta clase implementa el comando DrillDown para la clase {@link Cubo}.
 * Desagrega los datos de una dimensión en niveles más detallados, resultando un cubo con más registros.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoDrillDown implements ComandoCubo {

    private CuerpoCubo tablaBase;
    private Map<Dimension, String> criteriosDesagregacion;
    private List<ComandoRollUp> historialRollUp;
    private List<ComandoDice> historialDice;
    private List<ComandoSlice> historialSlice;
    private List<ComandoDrillDown> historialDrillDown;


    /**
     *  Constructor para la clase ComandoDrillDown.
     * 
     * @param criteriosDesagregacion Un mapa que contiene dimensiones como clave y como valor el nivel al cual se va a expandir.
     * @param tablaBase La tabla que se utilizará para llevar a cabo la operación.
     * @param historialRollUp El historial de operaciones RollUp aplicados sobre la instancia de 'Cubo' que invoca esta clase 
     * @param historialDice El historial de operaciones Dice aplicados sobre la instancia de 'Cubo' que invoca esta clase 
     * @param historialSlice El historial de operaciones Slice aplicados sobre la instancia de 'Cubo' que invoca esta clase  
     * @param historialDrillDown  El historial de operaciones DrillDown aplicados sobre la instancia de 'Cubo' que invoca esta clase 
     */
    public ComandoDrillDown(Map<Dimension, String> criteriosDesagregacion, CuerpoCubo tablaBase, List<ComandoRollUp> historialRollUp, List<ComandoDice> historialDice, 
                            List<ComandoSlice> historialSlice, List<ComandoDrillDown> historialDrillDown) {

        this.criteriosDesagregacion = criteriosDesagregacion;
        this.tablaBase = tablaBase;
        this.historialRollUp = historialRollUp;
        this.historialDice = historialDice;
        this.historialSlice = historialSlice;
        this.historialDrillDown = historialDrillDown;
    }

    /**
     * Ejecuta el comando RollUp.
     * Desagrupa los hechos por los criterios de expansion, el cubo vuelve a contener todos los hechos originales,
     * y almacena el resultado en 'tablaBase', alterando el estado del cubo.
     */
    @Override
    public void ejecutar(){

        // Añado al historial el comando antes de ejecutarlo
        this.historialDrillDown.add(this);

        // Hago una copia del historial de operaciones dice para no modificar el original
        List<ComandoDice> historialDice_copy = new ArrayList<>(this.historialDice);

        // Aplico todas las operaciones dice que se hayan hecho antes de aplicar el rollUp
        for (ComandoDice comando : this.historialDice) {
            
            // Obtengo los criterios
            Map<Dimension, Map<String, List<String>>> criterios = comando.getCriteriosDice();

            // Creo una instancia que represente el comando que estoy ejecutando
            ComandoDice comando_dice = new ComandoDice(this.tablaBase, criterios, historialDice_copy);

            // Ejecuto el comando
            comando_dice.ejecutar();

            // Modifico la tabla base
            this.tablaBase = comando_dice.getResultado();

        }


        // Obtengo el último método RollUp aplicado sobre el cubo
        ComandoRollUp ultimo_rollUp = this.historialRollUp.get(this.historialRollUp.size() - 1);

        // Obtengo los niveles sobre los cuales se agrupó, esto representaría el estado actual del cubo en cuanto 
        // a agrupación
        List<String> niveles_actuales = ultimo_rollUp.getNivelesOperacion();

        // Ahora por cada entrada del mapa "criteriosDesagregacion" voy obteniendo los niveles de detalle especificados 
        for (Map.Entry<Dimension, String> criterio : this.criteriosDesagregacion.entrySet()) {
            
            // Guardo la dimension y el nivel de detalle
            Dimension dimension = criterio.getKey();
            String nivel_detalle = criterio.getValue();

            // Obtengo los niveles de desagregación
            List<String> niveles_detalle = new ArrayList<>();
            List<String> niveles_desagregacion = ComandosUtils.obtenerNivelesOperacion(dimension, nivel_detalle, niveles_detalle);

            // Añado dichos niveles a 'niveles_actuales', esto logra el efecto de desagrupar las dimensiones seleccionadas
            niveles_actuales = obtenerNivelesDesagregacion(niveles_actuales, niveles_desagregacion, dimension);
        }

        // Utilizo la operación de agregación suma, ya que sería la operación esperada a la hora de desagrupar información
        OperacionAgregacion operacion_suma = OperacionAgregacion.SUM;

        // Finalmente uso la clase ComandoRollUp para "agrupar" por los niveles obtenidos
        ComandoRollUp comando = new ComandoRollUp(this.tablaBase, this.tablaBase.getHechosCubo(), operacion_suma, 
                                                  niveles_actuales, this.historialRollUp);

        // Ejecuto el comando
        comando.ejecutar();

        // Obtengo el resultado, modificando la tabla que usé para operar
        this.tablaBase = comando.getResultado();
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
        String nivel_abstracto = ComandosUtils.obtenerClavePorValor(dimension.getIndicesNiveles(), 0);

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
     * Devuelve la 'tablaOperacion' del cubo con el método ya aplicado.
     *
     * @return Un objeto de tipo CuerpoCubo que representa la tabla sobre la cual se ejecutan las operaciones del cubo.
     */
    public CuerpoCubo getResultado() {
        return this.tablaBase;
    }

    /**
     * Devuelve el historial de operaciones DrillDown aplicadas sobre la instancia de 'Cubo' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoDrillDown que representan las operaciones DrillDown efectuadas sobre el cubo.
     */
    public List<ComandoDrillDown> getHistorial(){
        return this.historialDrillDown;
    }


}
    

