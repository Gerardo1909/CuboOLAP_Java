package Cubo.comandos;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import Cubo.CuboOLAP;
import Cubo.cubo_utils.OperacionAgregacion;
import Cubo.excepciones.excepciones_tabla.TablaException;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;
import Cubo.tablas.Tabla;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Esta clase implementa el comando RollUp para la clase {@link CuboOLAP}.
 * Agrupa los hechos por los criterios de reducción y aplica una operación de agregación.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoRollUp implements ComandoCubo{

    private Hecho tabla_operacion;
    private Map<Dimension, String> criterios_reduccion;
    private List<String> niveles_operacion;
    private List<String> hechos_seleccionados;
    private OperacionAgregacion agregacion;
    private List<ComandoRollUp> historial_rollUp;

    /**
     * Constructor para la clase ComandoRollUp.
     *
     * @param tabla_operacion La tabla de hechos que se utilizará para llevar a cabo la operación.
     * @param criterios_reduccion Un mapa que contiene la dimensión junto con su criterio de reducción.
     * @param hechos_seleccionados Los hechos que se verán involucrados en la operación.
     * @param agregacion La operación de agregación a aplicar.
     * @param historial_rollUp El historial de operaciones RollUp aplicados sobre la instancia de 'CuboOLAP' que 
     *                        invoca esta clase.
     */
    public ComandoRollUp(Hecho tabla_operacion, Map<Dimension, String> criterios_reduccion, 
                         List<String> hechos_seleccionados, OperacionAgregacion agregacion, List<ComandoRollUp> historial_rollUp) {

        this.tabla_operacion = tabla_operacion;
        this.criterios_reduccion = criterios_reduccion;
        this.agregacion = agregacion;
        this.hechos_seleccionados = hechos_seleccionados;
        this.historial_rollUp = historial_rollUp;

        // Obtengo los niveles de la operacion
        this.niveles_operacion = new ArrayList<>();
        for (Map.Entry<Dimension, String> entrada : criterios_reduccion.entrySet()) {
            Dimension dimension = entrada.getKey();
            String nivel = entrada.getValue();
            this.niveles_operacion = obtenerNivelesOperacion(dimension, nivel, this.niveles_operacion);
        }

    }

    /**
     * Crea un objeto ComandoRollUp que representa un comando de roll-up en una operación OLAP.
     *
     * @param tabla_operacion     El hecho sobre el cual se realiza la operación.
     * @param hechos_seleccionados Los hechos seleccionados para la operación.
     * @param agregacion          La operación de agregación a aplicar.
     * @param niveles_operacion   Los niveles de operación para el roll-up.
     * @param historial_rollUp    El historial de comandos de roll-up.
     */
    protected ComandoRollUp(Hecho tabla_operacion, List<String> hechos_seleccionados, OperacionAgregacion agregacion, 
                            List<String> niveles_operacion, List<ComandoRollUp> historial_rollUp) {

        this.tabla_operacion = tabla_operacion;
        this.agregacion = agregacion;
        this.hechos_seleccionados = hechos_seleccionados;
        this.niveles_operacion = niveles_operacion;
        this.historial_rollUp = historial_rollUp;
    }

    /**
     * Ejecuta el comando RollUp.
     * Agrupa los hechos por los criterios de reducción, aplica la operación de agregación,
     * y almacena el resultado en 'tabla_operacion', alterando el estado del cubo.
     */
    @Override
    public void ejecutar() {
    
        // Añado al historial el comando antes de ejecutarlo
        this.historial_rollUp.add(this);

        // Primero agrupo según 'niveles_operacion'
        Map<List<String>, List<List<String>>> mapa_agrupacion = new LinkedHashMap<>();
        try {
            mapa_agrupacion = Tabla.groupBy(this.tabla_operacion, this.niveles_operacion, this.hechos_seleccionados);
        } catch (TablaException e) {
            // Este error nunca debería ocurrir ya que todas las posibles excepciones fueron verificadas
            e.printStackTrace();
        }

        // Ahora armo un nuevo 'mapa_operable' que tendrá como valores las mismas listas pero de tipo double
        Map<List<String>, List<List<Double>>> mapa_operable = convertirAListasDouble(mapa_agrupacion);

        // Armo el mapa que tendrá el resultado con la operación de agregación elegida aplicada
        Map<List<String>, List<Double>> mapa_operacion = aplicarAgregacion(mapa_operable);

        // Ahora guardo en una lista de listas de String la información del mapa resultante
        List<List<String>> operacion_resultante = convertirAListaString(mapa_operacion);

        // Genero una lista para guardar los headers de la operacion resultante
        List<String> headers_operacion = obtenerHeadersOperacion();

        // Finalmente modifico 'tabla_operacion'
        try {
            this.tabla_operacion = new Hecho(this.tabla_operacion.getNombre(), operacion_resultante, headers_operacion, this.hechos_seleccionados);
        } catch (TablaException tabla_excpetion) {
            // Esta excepcion no debería ocurrir, ya que la tabla de hechos original debería ser válida
            tabla_excpetion.printStackTrace();
        }
    }


    // Métodos de ayuda para método ejecutar()

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
        List<String> headers_operacion = new ArrayList<>(this.niveles_operacion);

        // Agrego los hechos seleccionados a la lista
        headers_operacion.addAll(this.hechos_seleccionados);

        // Devuelvo la lista de headers resultante
        return headers_operacion;
    }

    /**
     * Obtiene los niveles de operación para una dimensión y nivel específicos.
     *
     * @param dimension la dimensión en la que se realizará la operación
     * @param nivel el nivel específico a partir del cual se obtendrán los niveles de operación
     * @param niveles_operacion la lista donde se guardan los niveles de operación
     * @return la lista de niveles de operación modificada
     */
    protected static List<String> obtenerNivelesOperacion(Dimension dimension, String nivel, List<String> niveles_operacion) {

        // Obtengo el índice del nivel
        int indice_nivel = dimension.getIndicesNiveles().get(nivel);

        // Si es distinto de 0, es decir no es el más abstracto, debo incluir en la lista
        // todo los niveles detrás de él
        if (indice_nivel != 0) {
            for (int i = 0; i <= indice_nivel; i++) {

                // Obtengo el nivel por su indice
                String nivel_anterior = obtenerClavePorValor(dimension.getIndicesNiveles(), i);

                // Y lo agrego a la lista
                niveles_operacion.add(nivel_anterior);

            }
        } else {
            // Si es el más abstracto, simplemente lo agrego a la lista
            niveles_operacion.add(nivel);
        }

        // Retorno la lista 'niveles_operacion' modificada
        return niveles_operacion;
        }

    /**
     * Este método obtiene la clave de un mapa dado su valor.
     * Itera a través de las entradas del mapa y verifica si el valor coincide con el valor dado.
     * Si se encuentra una coincidencia, devuelve la clave correspondiente.
     *
     * @param <K> El tipo de la clave del mapa.
     * @param <V> El tipo del valor del mapa.
     * @param mapa El mapa del que se quiere obtener la clave.
     * @param valor El valor por el que se quiere buscar en el mapa.
     * @return La clave del mapa que coincide con el valor dado, o null si no se encuentra ninguna coincidencia.
     */
    protected static <K, V> K obtenerClavePorValor(Map<K, V> mapa, V valor) {
        for (Map.Entry<K, V> entrada : mapa.entrySet()) {
            if (Objects.equals(entrada.getValue(), valor)) {
                return entrada.getKey();
            }
        }
        return null; 
    }

    
    // Getters de la clase

    /**
     * Devuelve la 'tabla_operacion' del cubo con el método ya aplicado.
     *
     * @return Un objeto de tipo hecho que representa la tabla sobre la cual se ejecutan las operaciones del cubo.
     */
    public Hecho getResultado() {
        return this.tabla_operacion;
    }

    /**
     * Devuelve el historial de operaciones RollUp aplicadas sobre la instancia de 'CuboOLAP' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoRollUp que representan las operaciones RollUp efectuadas sobre el cubo.
     */
    public List<ComandoRollUp> getHistorial(){
        return this.historial_rollUp;
    }

    /**
     * Devuelve los niveles de agregación implicados en la operación.
     *
     * @return Una lista de cadenas que representa los niveles de operación.
     */
    protected List<String> getNivelesOperacion(){
        return new ArrayList<>(this.niveles_operacion);
    }

}
