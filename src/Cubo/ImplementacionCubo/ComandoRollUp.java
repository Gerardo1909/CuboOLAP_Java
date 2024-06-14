package Cubo.ImplementacionCubo;

import java.util.List;
import java.util.Map;
import Cubo.tablasCubo.Dimension;
import Cubo.tablasCubo.Tabla;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * <p>
 * Esta clase se encarga de implementar el método RollUp para la clase {@link Cubo}.
 * </p>
 * 
 * <p>
 * Implementa la interfaz {@link ComandoCubo}.
 * </p>
 */
class ComandoRollUp implements ComandoCubo{

    // Atributos de la clase ComandoRollUp
    private CuerpoCubo tablaOperacion;
    private List<String> nivelesOperacion;
    private List<String> hechosSeleccionados;
    private OperacionAgregacion agregacion;
    private List<ComandoRollUp> historialRollUp;

    /**
     * <p>
     * <b>Constructor para la clase ComandoRollUp.</b>
     * </p>
     * 
     * <p>
     * Se encarga de recibir todos los argumentos del método junto con el cuerpo del cubo y su historial de operaciones.
     * </p>
     *
     * @param tablaOperacion La tabla que se utilizará para llevar a cabo la operación.
     * @param criteriosAgregacion Un mapa que como clave tiene la dimensión y como valor el nivel al cual se quiere agrupar la misma.
     * @param hechosSeleccionados Los hechos que se verán involucrados en la operación.
     * @param agregacion La operación de agregación a aplicar.
     * @param historialRollUp El historial de operaciones RollUp aplicados sobre la instancia de Cubo que 
     *                        invoca esta clase.
     */
    public ComandoRollUp(CuerpoCubo tablaOperacion, Map<Dimension, String> criteriosAgregacion, 
                         List<String> hechosSeleccionados, OperacionAgregacion agregacion, List<ComandoRollUp> historialRollUp) {

        // Guardo los argumentos y tabla de operación en los atributos de la clase
        this.tablaOperacion = tablaOperacion;
        this.agregacion = agregacion;
        this.hechosSeleccionados = hechosSeleccionados;
        this.historialRollUp = historialRollUp;

        // Obtengo los niveles de la operacion
        this.nivelesOperacion = new ArrayList<>();
        for (Map.Entry<Dimension, String> entrada : criteriosAgregacion.entrySet()) {
            Dimension dimension = entrada.getKey();
            String nivel = entrada.getValue();
            this.nivelesOperacion = ComandosUtils.obtenerNivelesOperacion(dimension, nivel, this.nivelesOperacion);
        }

    }

    /**
     * <p>
     * <b>Constructor de ayuda para la clase ComandoDrillDown.</b>
     * </p>
     *
     * @param tablaOperacion La tabla que se utilizará para llevar a cabo la operación.
     * @param hechosSeleccionados Los hechos que se verán involucrados en la operación.
     * @param agregacion La operación de agregación a aplicar.
     * @param nivelesOperacion  Los niveles que se verán implicados en la operación de agrupación.
     * @param historialRollUp El historial de operaciones RollUp aplicados sobre la instancia de Cubo que 
     *                        invoca esta clase.
     */
    public ComandoRollUp(CuerpoCubo tablaOperacion, List<String> hechosSeleccionados, OperacionAgregacion agregacion, 
                            List<String> nivelesOperacion, List<ComandoRollUp> historialRollUp) {

        // Inicializo los atributos de la clase
        this.tablaOperacion = tablaOperacion;
        this.agregacion = agregacion;
        this.hechosSeleccionados = hechosSeleccionados;
        this.nivelesOperacion = nivelesOperacion;
        this.historialRollUp = historialRollUp;
    }

    /**
     * Ejecuta el método RollUp para la clase {@link Cubo}.
     */
    @Override
    public void ejecutar() {
    
        // Añado al historial el comando antes de ejecutarlo
        this.historialRollUp.add(this);

        // Primero agrupo según 'nivelesOperacion'        
         Map<List<String>, List<List<String>>> mapaAgrupacion = groupBy(this.tablaOperacion, this.nivelesOperacion, this.hechosSeleccionados);

        // Convierto las listas de los hechos agrupados a tipo Double
        Map<List<String>, List<List<Double>>> mapaOperable = convertirAListasDouble(mapaAgrupacion);

        // Aplico la operación de agregación elegida
        Map<List<String>, List<Double>> mapaAgregado = aplicarAgregacion(mapaOperable);

        // Vuelvo a convertir las listas de los hechos ahora agregados para unirlos a la información
        // interna del cubo
        List<List<String>> operacionResultante = convertirAListaString(mapaAgregado);

        // Guardo los headers de la operación
        List<String> headersOperacion = obtenerHeadersOperacion();

        // Actualizo el estado interno del cubo
        this.tablaOperacion = new CuerpoCubo(operacionResultante, headersOperacion, this.hechosSeleccionados);
    }


    // Métodos de ayuda para método ejecutar()

   /**
     * Se encarga de agrupar la información contenida en el cuerpo del cubo.
     *
     * @param tablaOperacion La tabla sobre la que se realiza la operación de agrupación.
     * @param columnasAgrupadoras Las columnas por las que se va a agrupar.
     * @param columnasAgrupadas Las columnas que se van a agrupar.
     * 
     * @return Un mapa que como clave tiene las columnas que agrupan y como valor las columnas ya agrupadas.
     */
    private static Map<List<String>, List<List<String>>> groupBy(Tabla tablaOperacion, List<String> columnasAgrupadoras, List<String> columnasAgrupadas) {

        // Guardo los indices de las columnas que agrupan
        List<Integer> indicesAgrupacion = new ArrayList<>();
        for (String columna : columnasAgrupadoras) {
            indicesAgrupacion.add(tablaOperacion.getHeaders().indexOf(columna));
        }

        // Guardo los indices de las columnas a agrupar
        List<Integer> indicesAgrupados = new ArrayList<>();
        for (String columna : columnasAgrupadas) {
            indicesAgrupados.add(tablaOperacion.getHeaders().indexOf(columna));
        }

        // Armo un mapa vacío que guardará los resultados y recorro las filas de la tabla
        Map<List<String>, List<List<String>>> mapaAgrupacion = new LinkedHashMap<>();
        for (List<String> fila : tablaOperacion.getDatosTabla()) {
            // Creo la clave del grupo
            List<String> clave = new ArrayList<>();
            for (int indiceColumna : indicesAgrupacion) {
                clave.add(fila.get(indiceColumna));
            }

            // Verifico si la clave no está en 'mapaAgrupacion'
            if (!mapaAgrupacion.containsKey(clave)) {
                // Armo la lista para las columnas a agrupar
                List<List<String>> listaColsAgrupar = new ArrayList<>();
                mapaAgrupacion.put(clave, listaColsAgrupar);

                // Y dentro de 'listaColsAgrupar' añado una lista por cada columna en la lista de las que voy a agrupar
                for (int i = 0; i < indicesAgrupados.size(); i++){
                    listaColsAgrupar.add(new ArrayList<>());
                }
            }

            // Ahora recorro las columnas a agrupar y las agrego a su lista correspondiente
            for (int i = 0; i < indicesAgrupados.size(); i++) {

                // Obtengo el índice de la columna a agrupar
                int indiceAgrupado = indicesAgrupados.get(i);

                // Lo añado a la lista que corresponde
                mapaAgrupacion.get(clave).get(i).add(fila.get(indiceAgrupado));
            }

        }

        // Retorno el mapa con los datos agrupados
        return mapaAgrupacion;

    }

    /**
     * Se encarga de convertir las listas contenidas en el mapa de la agrupación a tipo Double.
     *
     * @param mapaAgrupacion El mapa que contiene la información agrupada.
     * @return Un nuevo mapa con las mismas claves pero que en sus valores tiene listas de tipo Double.
     */
    private Map<List<String>, List<List<Double>>> convertirAListasDouble(Map<List<String>, List<List<String>>> mapaAgrupacion) {

        // Creo un mapa para guardar la operación resultante
        Map<List<String>, List<List<Double>>> mapaOperable = new LinkedHashMap<>();
    
        // Itero sobre el mapa de agrupación
        for (Map.Entry<List<String>, List<List<String>>> entrada : mapaAgrupacion.entrySet()) {
            // Obtengo la clave y los valores del mapa
            List<String> clave = entrada.getKey();
            List<List<String>> listasAgrupadosString = entrada.getValue();
    
            // Creo una lista de listas de valores Double para guardar los valores convertidos
            List<List<Double>> listasAgrupadosDouble = new ArrayList<>();
    
            // Itero sobre las listas de valores String
            for (List<String> listaString : listasAgrupadosString) {
                // Creo una lista para guardar los valores Double
                List<Double> listaDouble = new ArrayList<>();
    
                // Itero sobre los valores de tipo String
                for (String valorString : listaString) {
                    // Verifico si el valor String no es null
                    if (valorString != null) {
                        // Convierto el valor de tipo String a tipo Double y lo agrego a la lista
                        Double valorDouble = Double.parseDouble(valorString);
                        listaDouble.add(valorDouble);
                    }
                }
    
                // Agrego la lista de valores Double a la lista de listas de valores Double
                listasAgrupadosDouble.add(listaDouble);
            }
    
            // Agrego las claves y la lista de listas de valores Double al mapa operable
            mapaOperable.put(clave, listasAgrupadosDouble);
        }
    
        // Retorno el mapa operable con listas de valores Double
        return mapaOperable;
    }

    /**
     * Se encarga de aplicar la operación de agregación a las listas de información agrupada.
     *
     * @param mapaOperable El mapa que contiene la información agrupada y los valores del mismo convertidos a listas 
     *                     de tipo Double.
     * @return El mapa con la operación de agregación aplicada.
     */
    private Map<List<String>, List<Double>> aplicarAgregacion(Map<List<String>, List<List<Double>>> mapaOperable) {
        
        // Creo un mapa para guardar la operación resultante e itero sobre el mapa operable
        Map<List<String>, List<Double>> mapaAgregado = new LinkedHashMap<>();
        for (Map.Entry<List<String>, List<List<Double>>> entrada : mapaOperable.entrySet()) {

            // Obtengo la clave y los valores del mapa
            List<String> clave = entrada.getKey();
            List<List<Double>> listasHechos = entrada.getValue();

            // Creo una lista para guardar las operaciones de agregación
            List<Double> operaciones = new ArrayList<>();

            // Itero sobre las listas de hechos seleccionados
            for (List<Double> lista : listasHechos) {

                // Realizo la operación de agregación correspondiente
                double resultado = this.agregacion.aplicar(lista);
                operaciones.add(resultado);
            }

            // Agrego las claves y las operaciones al mapa de operación
            mapaAgregado.put(clave, operaciones);
        }

        // Retorno el mapa de operación con la operación de agregación aplicada
        return mapaAgregado;
    }

    /**
     * Se encarga de convertir la información contenida en el mapa de la agrupación ya con su operación de agregación aplicada
     * de vuelta a listas de tipo String.
     *
     * @param mapaAgregado El mapa que contiene la información agrupada y agregada.
     * @return La lista de listas de String que contiene la información agregada.
     */
    private List<List<String>> convertirAListaString(Map<List<String>, List<Double>> mapaAgregado) {
        
        // Creo una lista de listas de String para guardar la operación resultante
        // e itero sobre el mapa agregado
        List<List<String>> operacionResultante = new ArrayList<>();
        for (Map.Entry<List<String>, List<Double>> entrada : mapaAgregado.entrySet()) {

            // Obtengo la clave y los valores del mapa
            List<String> clave = entrada.getKey();
            List<Double> valoresAgrupados = entrada.getValue();

            // Creo una nueva lista para guardar los valores parseados
            List<String> filaString = new ArrayList<>();

            // Convierto los valores double a String
            for (Double valorAgrupado : valoresAgrupados) {
                filaString.add(String.valueOf(valorAgrupado));
            }

            // Creo una nueva fila con la clave y los valores parseados
            List<String> fila = new ArrayList<>(clave);
            fila.addAll(filaString);

            // Agrego la fila a la lista 'operacionResultante'
            operacionResultante.add(fila);
        }

        // Retorno la lista de listas de String
        return operacionResultante;
    }

    /**
     * Se encarga de generar los encabezados de la operación resultante.
     *
     * @return Una lista que contiene los encabezados implicados en la operación rollUp.
     */
    private List<String> obtenerHeadersOperacion() {

        // Creo una lista con los niveles de operación
        List<String> headersOperacion = new ArrayList<>(this.nivelesOperacion);

        // Agrego los hechos seleccionados a la lista
        headersOperacion.addAll(this.hechosSeleccionados);

        // Devuelvo la lista de headers resultante
        return headersOperacion;
    }

    
    // Getters de la clase

    /**
     * @return El cuerpo del cubo con el método ya aplicado
     */
    public CuerpoCubo getResultado() {
        return this.tablaOperacion;
    }

    /**
     * @return Una lista que representa el historial de métodos RollUp aplicados 
     *         sobre la instancia de Cubo que invoca esta clase junto con la
     *         instancia que se encargó de ejecutar el mismo.
     */
    public List<ComandoRollUp> getHistorial(){
        return this.historialRollUp;
    }

    /**
     * @return Una lista que contiene los niveles de las dimensiones que se 
     *        vieron implicadas en la ejecución de este método.
     */
    public List<String> getNivelesOperacion(){
        return new ArrayList<>(this.nivelesOperacion);
    }

}
