package Cubo.comandos;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import Cubo.CuboOLAP;
import Cubo.excepciones.excepciones_tabla.TablaException;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;
import Cubo.tablas.Tabla;
import java.util.HashMap;
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
    private Map<List<String>, List<List<String>>>resultado;

    /**
     * Constructor para la clase ComandoRollUp.
     *
     * @param tabla_operacion La tabla de hechos que se utilizará para llevar a cabo la operación.
     * @param criterios_reduccion Un mapa que contiene la dimensión junto con su criterio de reducción.
     * @param hechos_seleccionados Los hechos que se verán involucrados en la operación.
     * @param agregacion La operación de agregación a aplicar.
     */
    public ComandoRollUp(Hecho tabla_operacion, Map<Dimension, String> criterios_reduccion, 
                         List<String> hechos_seleccionados, String agregacion) {

        this.tabla_operacion = tabla_operacion;
        this.criterios_reduccion = criterios_reduccion;
        this.agregacion = agregacion;
        this.hechos_seleccionados = hechos_seleccionados;

        // Obtengo los niveles de la operacion
        this.niveles_operacion = this.obtenerNivelesOperacion(criterios_reduccion);
    }

    /**
     * Ejecuta el comando RollUp.
     * Agrupa los hechos por los criterios de reducción, aplica la operación de agregación,
     * y almacena el resultado en el atributo 'resultado'.
     * @throws TablaException Si se produce algún error durante la ejecución del comando.
     */
    @Override
    public void ejecutar() throws TablaException {

        // Primero agrupo según 'niveles_operacion'
        Map<List<String>, List<List<String>>> mapa_agrupacion = Tabla.groupBy(this.tabla_operacion,this.niveles_operacion, this.hechos_seleccionados);

        // Ahora armo un nuevo 'mapa_operable' que tendrá como valores las mismas listas pero de tipo double
        Map<List<String>, List<List<Double>>> mapa_operable = new HashMap<>();
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
        Map<List<String>, List<Double>> mapa_operacion = new HashMap<>();

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

        // Y ahora armo el mapa que contiene como clave los headers de la operación
        // y como valor contiene la matriz que contiene la operación
        Map<List<String>, List<List<String>>> mapa_resultante = new HashMap<>();
        mapa_resultante.put(headers_operacion, operacion_resultante);

        this.resultado = mapa_resultante;
    }

    /**
     * Devuelve el resultado del comando RollUp.
     *
     * @return Un mapa donde las claves son los encabezados de la operación y los valores son la matriz de la operación.
     */
    public Map<List<String>, List<List<String>>> getResultado() {
        return this.resultado;
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
     * Este método recupera los niveles de operación basándose en el mapa proporcionado de dimensiones y sus niveles correspondientes.
     * Itera a través de cada entrada en el mapa y recupera la dimensión y el nivel.
     * Luego, obtiene el índice del nivel y, si no es el más abstracto (cuando el índice es distinto de 0),
     * incluye en la lista todos los niveles posteriores a él.
     *
     * @param mapa_dimension_nivel Un mapa que contiene las dimensiones y sus niveles correspondientes.
     * @return Una lista de cadenas que representa los niveles de operación.
     */
    private List<String> obtenerNivelesOperacion(Map <Dimension, String> mapa_dimension_nivel){

        // Genero una lista para guardar el resultado final
        List<String> niveles_resultantes = new ArrayList<>();

        // Itero sobre cada entrada del mapa 'criterios_reduccion'
        for (Map.Entry<Dimension, String> entrada : mapa_dimension_nivel.entrySet()) {

            // Guardo la dimension y el nivel sobre el cual estoy ahora
            Dimension dimension = entrada.getKey();
            String nivel = entrada.getValue();

            // Obtengo el índice del nivel
            int indice_nivel = dimension.getIndicesNiveles().get(nivel);

            // Si es distinto de 0, es decir no es el más abstracto, debo incluir en la lista
            // todo los niveles detrás de él
            if (indice_nivel!= 0) {
                for (int i = 0; i <= indice_nivel; i++) {
                   
                    // Obtengo el nivel por su indice
                    String nivel_anterior = obtenerClavePorValor(dimension.getIndicesNiveles(), i);

                    // Y lo agrego a la lista
                    niveles_resultantes.add(nivel_anterior);

                }
            }
            else { 
                niveles_resultantes.add(nivel);
            }

        }

        return niveles_resultantes;
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
    private static <K, V> K obtenerClavePorValor(Map<K, V> mapa, V valor) {
        for (Map.Entry<K, V> entrada : mapa.entrySet()) {
            if (Objects.equals(entrada.getValue(), valor)) {
                return entrada.getKey();
            }
        }
        return null; 
    }

}
