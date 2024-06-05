package Cubo.comandos;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import Cubo.CuboOLAP;
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
    private String agregacion;
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
                         List<String> hechos_seleccionados, String agregacion, List<ComandoRollUp> historial_rollUp) {

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

    // Constructor para uso dentro del módulo
    protected ComandoRollUp(Hecho tabla_operacion, List<String> hechos_seleccionados, String agregacion, 
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
     * @throws TablaException Si se produce algún error durante la ejecución del comando.
     */
    @Override
    public void ejecutar() throws TablaException {
    
        // Añado al historial el comando antes de ejecutarlo
        this.historial_rollUp.add(this);

        // Primero agrupo según 'niveles_operacion'
        Map<List<String>, List<List<String>>> mapa_agrupacion = Tabla.groupBy(this.tabla_operacion,this.niveles_operacion, this.hechos_seleccionados);

        // Ahora armo un nuevo 'mapa_operable' que tendrá como valores las mismas listas pero de tipo double
        Map<List<String>, List<List<Double>>> mapa_operable = new LinkedHashMap<>();
        for (Map.Entry<List<String>, List<List<String>>> entrada : mapa_agrupacion.entrySet()) {

            // Obtengo clave y valor del mapa_agrupacion
            List<String> clave = entrada.getKey();
            List<List<String>> listasHechos_string = entrada.getValue();
            
            // Recorro cada lista de hechos_string y la convierto en una lista de doubles
            List<List<Double>> listasHechos_double = new ArrayList<>();
            for (List<String> listaString : listasHechos_string) {
                List<Double> listaDouble = new ArrayList<>();
                for (String valorString : listaString) {
                    // Verifico que el valor sea un número y lo convierto en double
                    try {
                        Double valorDouble = Double.parseDouble(valorString);
                        listaDouble.add(valorDouble);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                listasHechos_double.add(listaDouble);
            }
        
            // Guardo la entrada resultante
            mapa_operable.put(clave, listasHechos_double);
        }

        // Armo el mapa que tendrá el resultado con la operación de agregación elegida aplicada
        Map<List<String>, List<Double>> mapa_operacion = new LinkedHashMap<>();

        // Recorro cada entrada de 'mapa_operable'
        for (Map.Entry<List<String>, List<List<Double>>> entrada : mapa_operable.entrySet()) {
            List<String> clave = entrada.getKey();
            List<List<Double>> listasHechos = entrada.getValue();

            // Aplico la operación elegida a cada lista de hechos
            List<Double> operaciones = new ArrayList<>();
            for (List<Double> lista : listasHechos) {

                switch (this.agregacion) {
                    case "sum":
                        double suma = sumarLista(lista);
                        operaciones.add(suma);
                        break;
                    case "max":
                        double max = Collections.max(lista);
                        operaciones.add(max);
                        break;
                    case "min":
                        double min = Collections.min(lista);
                        operaciones.add(min);
                        break;
                    case "count":
                        double count = (double) lista.size();
                        operaciones.add(count);
                        break;
                }
            }

            // Guardo la entrada resultante
            mapa_operacion.put(clave, operaciones);
        }

        // Ahora guardo en una lista de listas de String la información del mapa resultante
        List<List<String>> operacion_resultante = new ArrayList<>();
        for (Map.Entry<List<String>, List<Double>> entrada : mapa_operacion.entrySet()){

            //Tomo la clave y valor del mapa_operacion
            List<String> clave = entrada.getKey();
            List<Double> valor = entrada.getValue();

            //Parseo los valores Double de la lista 'valor'
            List<String> valor_parseado = new ArrayList<>();
            for (Double hecho : valor) {
                valor_parseado.add(String.valueOf(hecho));
            }

            //Creo una nueva lista que contenga los valores de clave y valor, simulando una fila
            List<String> fila = new ArrayList<>(clave);
            fila.addAll(valor_parseado);

            //Y añado dicha fila a fila a 'operacion_resultante'
            operacion_resultante.add(fila);
        }

        // Genero una lista para guardar los headers de la operacion resultante
        List<String> headers_operacion = new ArrayList<>(this.niveles_operacion);
        headers_operacion.addAll(this.hechos_seleccionados);

        // Finalmente modifico 'tabla_operacion'
        this.tabla_operacion = new Hecho(this.tabla_operacion.getNombre(), operacion_resultante, 
                                         headers_operacion, this.hechos_seleccionados);
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

    /**
     * Método privado de ayuda para calcular la suma de una lista de dobles.
     *
     * @param lista_a_sumar La lista de dobles a sumar.
     * @return La suma de los elementos en la lista.
     */
    private Double sumarLista(List<Double> lista_a_sumar){
        Double suma = 0.0;
        for (Double numero : lista_a_sumar){
            suma+= numero;
        }
        return suma;
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

}
