package Cubo.implementacionCubo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Cubo.tablasCubo.Dimension;

/**
 * <p>
 * Esta clase se encarga de implementar el método DrillDown para la clase {@link Cubo}.
 * </p>
 * 
 * <p>
 * Implementa la interfaz {@link ComandoCubo}.
 * </p>
 */
class ComandoDrillDown implements ComandoCubo {

    // Atributos de la clase ComandoDrillDown
    private CuerpoCubo tablaBase;
    private Map<Dimension, String> criteriosDesagregacion;
    private List<ComandoRollUp> historialRollUp;
    private List<ComandoDice> historialDice;
    private List<ComandoSlice> historialSlice;
    private List<ComandoDrillDown> historialDrillDown;


    /**
     * <p>
     * <b>Constructor para la clase ComandoDrillDown.</b>
     * </p>
     * 
     * <p>
     * Se encarga de recibir todos los argumentos del método junto con el cuerpo del cubo y todos los historiales 
     * de operaciones aplicadas sobre el mismo.
     * </p>
     * 
     * @param criteriosDesagregacion Un mapa que como clave tiene la dimensión y como valor el nivel al cual se quiere desagrupar la misma.
     * @param tablaBase La tabla que se utilizará para llevar a cabo la operación.
     * @param historialRollUp El historial de operaciones RollUp aplicados sobre la instancia de Cubo que invoca esta clase 
     * @param historialDice El historial de operaciones Dice aplicados sobre la instancia de Cubo que invoca esta clase 
     * @param historialSlice El historial de operaciones Slice aplicados sobre la instancia de Cubo que invoca esta clase  
     * @param historialDrillDown  El historial de operaciones DrillDown aplicados sobre la instancia de Cubo que invoca esta clase 
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
     * Ejecuta el método DrillDown para la clase {@link Cubo}.
     */
    @Override
    public void ejecutar() {

        // Añado al historial el comando antes de ejecutarlo
        this.historialDrillDown.add(this);

        // Creo copias de los historiales de operaciones Dice y Slice
        List<ComandoDice> historialDiceCopy = new ArrayList<>(this.historialDice);
        List<ComandoSlice> historialSliceCopy = new ArrayList<>(this.historialSlice);
    
        // Aplico las operaciones Dice y Slice
        // así mantengo el estado de filtrado del cubo
        aplicarOperacionesDice(historialDiceCopy);
        aplicarOperacionesSlice(historialSliceCopy);
    
        // Obtengo la última operación RollUp aplicada sobre el cubo
        // esta me indica el estado actual de agrupación en la cual se encuentra
        // el mismo
        ComandoRollUp ultimoRollUp = this.historialRollUp.get(this.historialRollUp.size() - 1);
        List<String> nivelesActualesAgregacion = ultimoRollUp.getNivelesOperacion();
    
        // Actualizo el estado actual de agrupación del cubo, justamente desagrupando según
        // los criterios de desagregación
        nivelesActualesAgregacion = actualizarNivelesActualesConDesagregacion(nivelesActualesAgregacion, this.criteriosDesagregacion);
    
        // Finalmente ejecuto el RollUp pero con los niveles de menor jerarquía
        // logrando el efecto de desagregación
        ComandoRollUp comando = new ComandoRollUp(this.tablaBase, ultimoRollUp.getHechosSeleccionados(), OperacionAgregacion.SUM, nivelesActualesAgregacion, this.historialRollUp);
        comando.ejecutar();

        // Actualizo el estado interno del cubo
        this.tablaBase = comando.getResultado();
    }


    // Métodos de ayuda para método ejecutar()

    /**
     * Se encarga de re-ejecutar las operaciones Dice aplicadas anteriormente sobre el cubo.
     */
    private void aplicarOperacionesDice(List<ComandoDice> historialDice) {
        for (ComandoDice comando : this.historialDice) {
            ComandoDice comandoDice = new ComandoDice(this.tablaBase, comando.getCriteriosDice(), historialDice);
            comandoDice.ejecutar();
            this.tablaBase = comandoDice.getResultado();
        }
    }

    /**
     * Se encarga de re-ejecutar las operaciones Slice aplicadas anteriormente sobre el cubo.
     */
    private void aplicarOperacionesSlice(List<ComandoSlice> historialSlice) {
        for (ComandoSlice comando : this.historialSlice) {
            ComandoSlice comandoSlice = new ComandoSlice(this.tablaBase, comando.getDimension(), comando.getNivelCorte(), 
                                                        comando.getValorCorte(), historialSlice);
            comandoSlice.ejecutar();
            this.tablaBase = comandoSlice.getResultado();
        }
    }

    /**
     * Se encarga de obtener todos los niveles de desagregación según los criterios
     * del método y los añade a una lista.
     * 
     * @param listaNivelesActuales Una lista que contiene los niveles que agrupan al cubo.
     * @param nivelesDesagregacion La lista que contiene los niveles a los cuales 
     *                              se desagrega la dimensión especificada.
     * @param dimension La dimensión sobre la cual se realiza la desagregación.
     * @return Una lista con los niveles de desagregación añadidos.
     */
    private List<String> obtenerNivelesDesagregacion(List<String> listaNivelesActuales, List<String> nivelesDesagregacion, Dimension dimension) {

        //Defino una lista para guardar el resultado de la operación
        List<String> listaResultante = new ArrayList<>();

        // Obtengo el nivel mas abstracto de la dimensión por la cual voy a desagrupar
        String nivelMasAbstracto = ComandosUtils.obtenerClavePorValor(dimension.getIndicesNiveles(), 0);

        // En lista de resultado añado todos los niveles que no estén en la dimensión
        // a la cual le saco los niveles de desagregación
        for (String nivel : listaNivelesActuales) {
            if (!dimension.getHeaders().contains(nivel)) {
                listaResultante.add(nivel);
            }
        }

        // Inicializo una variable para guardar la posición de inserción y una flag que me 
        // indica si ya encontré el nivel abstracto de la dimensión a la cual quiero añadir
        // los niveles de desagregacion
        int posicionInsercion = 0;
        boolean encontrado = false;

        // Itero sobre la lista de niveles de agrupacion del cubo
        for (int i = 0; i < listaNivelesActuales.size(); i++) {
            // Inserto la información a partir de la posición del nivel más abstracto
            // dicho nivel siempre estará presente tanto en agrupación como en desagregación
            if (listaNivelesActuales.get(i).equals(nivelMasAbstracto)) {
                if (!encontrado) {
                    posicionInsercion = i;
                    encontrado = true;
                }
            } else if (encontrado) {
                break;
            }
        }

        // Añado los niveles de desagregación en la posición correspondiente
        listaResultante.addAll(posicionInsercion, nivelesDesagregacion);

        // Devuelvo la lista con los niveles de desagregación añadidos
        return listaResultante;
    }

    /**
     * Se encarga de actualizar los niveles actuales de agrupación del cubo
     * con los niveles de desagregación especificados en los criterios del método.
     * 
     * @param nivelesActualesAgregacion Una lista que contiene los niveles actuales de agrupación del cubo.
     * @param criteriosDesagregacion Un mapa que contiene la dimensión y el nivel de detalle al cual se desagrupa.
     * @return Una lista con los niveles de desagregación añadidos.
     */
    private List<String> actualizarNivelesActualesConDesagregacion(List<String> nivelesActualesAgregacion, Map<Dimension, String> criteriosDesagregacion) {
        for (Map.Entry<Dimension, String> criterio : criteriosDesagregacion.entrySet()) {
            // Guardo la dimension y el nivel de detalle
            Dimension dimension = criterio.getKey();
            String nivelDesagregacion = criterio.getValue();

            // Obtengo los niveles de desagregación
            List<String> nivelesDesagregacion = ComandosUtils.obtenerNivelesOperacion(dimension, nivelDesagregacion, new ArrayList<>());

            // Añado dichos niveles a 'nivelesActualesAgregacion', esto logra el efecto de desagrupar las dimensiones seleccionadas
            nivelesActualesAgregacion = obtenerNivelesDesagregacion(nivelesActualesAgregacion, nivelesDesagregacion, dimension);
        }
        return nivelesActualesAgregacion;
    }


    // Getters de la clase

    /**
     * @return El cuerpo del cubo con el método ya aplicado
     */
    public CuerpoCubo getResultado() {
        return this.tablaBase;
    }

    /**
     * @return Una lista que representa el historial de métodos DrillDown aplicados 
     *         sobre la instancia de Cubo que invoca esta clase junto con la
     *         instancia que se encargó de ejecutar el mismo.
     */
    public List<ComandoDrillDown> getHistorial(){
        return this.historialDrillDown;
    }

}
    

