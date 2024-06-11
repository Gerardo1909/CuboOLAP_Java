package Cubo.ImplementacionCubo;

import java.util.List;
import java.util.Map;
import Cubo.Cubo;
import Cubo.tablasCubo.Dimension;
import Cubo.tablasCubo.Tabla;

import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * Esta clase implementa el comando RollUp para la clase {@link Cubo}.
 * Agrupa los hechos por los criterios de reducción y aplica una operación de agregación.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoRollUp implements ComandoCubo{

    private CuerpoCubo tablaOperacion;
    private Map<Dimension, String> criteriosAgregacion;
    private List<String> nivelesOperacion;
    private List<String> hechosSeleccionados;
    private OperacionAgregacion agregacion;
    private List<ComandoRollUp> historialRollUp;

    /**
     * Constructor para la clase ComandoRollUp.
     *
     * @param tablaOperacion La tabla que se utilizará para llevar a cabo la operación.
     * @param criteriosAgregacion Un mapa que contiene la dimensión junto con su criterio de reducción.
     * @param hechosSeleccionados Los hechos que se verán involucrados en la operación.
     * @param agregacion La operación de agregación a aplicar.
     * @param historialRollUp El historial de operaciones RollUp aplicados sobre la instancia de 'Cubo' que 
     *                        invoca esta clase.
     */
    public ComandoRollUp(CuerpoCubo tablaOperacion, Map<Dimension, String> criteriosAgregacion, 
                         List<String> hechosSeleccionados, OperacionAgregacion agregacion, List<ComandoRollUp> historialRollUp) {

        this.tablaOperacion = tablaOperacion;
        this.criteriosAgregacion = criteriosAgregacion;
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
     * Crea un objeto ComandoRollUp que representa un comando de roll-up en una operación OLAP.
     *
     * @param tablaOperacion La tabla que se utilizará para llevar a cabo la operación.
     * @param hechosSeleccionados Los hechos seleccionados para la operación.
     * @param agregacion La operación de agregación a aplicar.
     * @param nivelesOperacion  Los niveles de operación para el roll-up.
     * @param historialRollUp  El historial de operaciones RollUp aplicados sobre la instancia de 'Cubo' que 
     *                          invoca esta clase.
     */
    protected ComandoRollUp(CuerpoCubo tablaOperacion, List<String> hechosSeleccionados, OperacionAgregacion agregacion, 
                            List<String> nivelesOperacion, List<ComandoRollUp> historialRollUp) {

        this.tablaOperacion = tablaOperacion;
        this.agregacion = agregacion;
        this.hechosSeleccionados = hechosSeleccionados;
        this.nivelesOperacion = nivelesOperacion;
        this.historialRollUp = historialRollUp;
    }

    /**
     * Ejecuta el comando RollUp.
     * Agrupa los hechos por los criterios de reducción, aplica la operación de agregación,
     * y almacena el resultado en 'tablaOperacion', alterando el estado del cubo.
     */
    @Override
    public void ejecutar() {
    
        // Añado al historial el comando antes de ejecutarlo
        this.historialRollUp.add(this);

        // Primero agrupo según 'nivelesOperacion'        
         Map<List<String>, List<List<String>>> mapa_agrupacion = groupBy(this.tablaOperacion, this.nivelesOperacion, this.hechosSeleccionados);

        // Ahora armo un nuevo 'mapa_operable' que tendrá como valores las mismas listas pero de tipo double
        Map<List<String>, List<List<Double>>> mapa_operable = convertirAListasDouble(mapa_agrupacion);

        // Armo el mapa que tendrá el resultado con la operación de agregación elegida aplicada
        Map<List<String>, List<Double>> mapa_operacion = aplicarAgregacion(mapa_operable);

        // Ahora guardo en una lista de listas de String la información del mapa resultante
        List<List<String>> operacion_resultante = convertirAListaString(mapa_operacion);

        // Genero una lista para guardar los headers de la operacion resultante
        List<String> headers_operacion = obtenerHeadersOperacion();

        // Actualizo la tabla de operación con la operación resultante
        this.tablaOperacion = new CuerpoCubo(operacion_resultante, headers_operacion, this.hechosSeleccionados);
    }


    // Métodos de ayuda para método ejecutar()

   /**
     * Método estático para agrupar filas de una tabla según columnas específicas.
     *
     * @param tablaOperacion Tabla en la que se va a realizar la operación de agrupación.
     * @param columnas_agrupacion Columnas por las que se va a agrupar.
     * @param columnas_a_agrupar Columnas que se van a agrupar.
     * @return Mapa con claves de columnas de agrupación y valores de listas de listas de datos agrupados.
     */
    private static Map<List<String>, List<List<String>>> groupBy(Tabla tablaOperacion, List<String> columnas_agrupacion, List<String> columnas_a_agrupar) {

        // Guardo primero los índices de las columnas por las cuales se agrupa
        List<Integer> indices_agrupacion = new ArrayList<>();
        for (String columna : columnas_agrupacion) {
            indices_agrupacion.add(tablaOperacion.getHeaders().indexOf(columna));
        }

        // Ahora guardo los índices de las columnas a agrupar
        List<Integer> indices_a_agrupar = new ArrayList<>();
        for (String columna : columnas_a_agrupar) {
            indices_a_agrupar.add(tablaOperacion.getHeaders().indexOf(columna));
        }

        // Armo un mapa vacío que guardará los resultados
        Map<List<String>, List<List<String>>> mapa_agrupacion = new LinkedHashMap<>();

        // Recorro las filas de la tabla
        for (List<String> fila : tablaOperacion.getData()) {

            // Creo la clave del grupo
            List<String> clave = new ArrayList<>();
            for (int indice_columna : indices_agrupacion) {
                clave.add(fila.get(indice_columna));
            }

            // Verifico si la clave no está en 'mapa_agrupacion'
            if (!mapa_agrupacion.containsKey(clave)) {

                // Armo la lista para las columnas a agrupar
                List<List<String>> listaColsAgrupar = new ArrayList<>();
                mapa_agrupacion.put(clave, listaColsAgrupar);

                // Y dentro de 'listaColsAgrupar' añado una lista por cada columna en la lista de las que voy a agrupar
                for (String columna : columnas_a_agrupar) {
                    listaColsAgrupar.add(new ArrayList<>());
                }
            }

            // Ahora recorro las columnas a agrupar y las agrego a su lista correspondiente
            for (int i = 0; i < indices_a_agrupar.size(); i++) {

                // Obtengo el índice de la columna a agrupar
                int indice_hecho = indices_a_agrupar.get(i);

                // Lo añado a la lista que corresponde
                mapa_agrupacion.get(clave).get(i).add(fila.get(indice_hecho));

            }

        }

        return mapa_agrupacion;

    }

    /**
     * Convierte el mapa de agrupación a un nuevo mapa con listas de tipo double.
     *
     * @param mapa_agrupacion El mapa de agrupación original.
     * @return El nuevo mapa con listas de tipo double.
     */
    private Map<List<String>, List<List<Double>>> convertirAListasDouble(Map<List<String>, List<List<String>>> mapa_agrupacion) {
        
        // Creo un mapa para guardar la operación resultante
        Map<List<String>, List<List<Double>>> mapa_operable = new LinkedHashMap<>();

        // Itero sobre el mapa de agrupación
        for (Map.Entry<List<String>, List<List<String>>> entrada : mapa_agrupacion.entrySet()) {

            // Obtengo la clave y los valores del mapa
            List<String> clave = entrada.getKey();
            List<List<String>> listasHechos_string = entrada.getValue();

            // Creo una lista de listas de valores Double para guardar los valores convertidos
            List<List<Double>> listasHechos_double = new ArrayList<>();

            // Itero sobre las listas de valores String
            for (List<String> listaString : listasHechos_string) {

                // Creo una lista para guardar los valores Double
                List<Double> listaDouble = new ArrayList<>();

                    // Itero sobre los valores de tipo String
                    for (String valorString : listaString) {

                        try {
                        // Intento convertir el valor de tipo String a tipo Double
                        Double valorDouble = Double.parseDouble(valorString);

                        // Si la conversión es exitosa, agrego el valor Double a la lista
                        listaDouble.add(valorDouble);

                        } catch (NumberFormatException number_exception) {
                        // Si ocurre una excepción al convertir el valor, imprimo el stack trace
                        number_exception.printStackTrace();
                        }

                    }

            // Agrego la lista de valores Double a la lista de listas de valores Double
            listasHechos_double.add(listaDouble);
            }

            // Agrego las claves y la lista de listas de valores Double al mapa operable
            mapa_operable.put(clave, listasHechos_double);
        }

        // Retorno el mapa operable con listas de valores Double
        return mapa_operable;
    }

    /**
     * Aplica la operación de agregación elegida al mapa operable.
     *
     * @param mapa_operable El mapa operable con listas de tipo double.
     * @return El mapa con la operación de agregación aplicada.
     */
    private Map<List<String>, List<Double>> aplicarAgregacion(Map<List<String>, List<List<Double>>> mapa_operable) {
        
        // Creo un mapa para guardar la operación resultante
        Map<List<String>, List<Double>> mapa_operacion = new LinkedHashMap<>();

        // Itero sobre el mapa operable
        for (Map.Entry<List<String>, List<List<Double>>> entrada : mapa_operable.entrySet()) {

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
            mapa_operacion.put(clave, operaciones);
        }

        // Retorno el mapa de operación con la operación de agregación aplicada
        return mapa_operacion;
    }

    /**
     * Convierte el mapa operación a una lista de listas de tipo String.
     *
     * @param mapa_operacion El mapa operación con listas de tipo double.
     * @return La lista de listas de tipo String.
     */
    private List<List<String>> convertirAListaString(Map<List<String>, List<Double>> mapa_operacion) {
        
        // Creo una lista de listas de String para guardar la operación resultante
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Itero sobre el mapa de operación
        for (Map.Entry<List<String>, List<Double>> entrada : mapa_operacion.entrySet()) {

            // Obtengo la clave y los valores del mapa
            List<String> clave = entrada.getKey();
            List<Double> valor = entrada.getValue();

            // Creo una nueva lista para guardar los valores parseados
            List<String> valor_parseado = new ArrayList<>();

            // Convierto los valores double a String
            for (Double hecho : valor) {
                valor_parseado.add(String.valueOf(hecho));
            }

            // Creo una nueva fila con la clave y los valores parseados
            List<String> fila = new ArrayList<>(clave);
            fila.addAll(valor_parseado);

            // Agrego la fila a la lista 'operacion_resultante'
            operacion_resultante.add(fila);
        }

        // Retorno la lista de listas de String
        return operacion_resultante;
    }

    /**
     * Genera los headers de la operación resultante.
     *
     * @return La lista de headers.
     */
    private List<String> obtenerHeadersOperacion() {

        // Creo una lista con los niveles de operación
        List<String> headers_operacion = new ArrayList<>(this.nivelesOperacion);

        // Agrego los hechos seleccionados a la lista
        headers_operacion.addAll(this.hechosSeleccionados);

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
     * Devuelve el historial de operaciones RollUp aplicadas sobre la instancia de 'Cubo' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoRollUp que representan las operaciones RollUp efectuadas sobre el cubo.
     */
    public List<ComandoRollUp> getHistorial(){
        return this.historialRollUp;
    }

    /**
     * Devuelve los niveles de agregación implicados en la operación.
     *
     * @return Una lista de cadenas que representa los niveles de operación.
     */
    public List<String> getNivelesOperacion(){
        return new ArrayList<>(this.nivelesOperacion);
    }

}
