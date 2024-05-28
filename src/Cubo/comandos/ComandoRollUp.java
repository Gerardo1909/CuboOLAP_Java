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
 * Agrupa el cubo por los criterios de reducción y aplica una operación de agregación.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoRollUp implements ComandoCubo{

    private Hecho tabla_operacion;
    private Hecho tabla_antes_de_operar;
    private Dimension dimension_reduccion;
    private String nivel_reduccion;
    private List<String> niveles_operacion;
    private String agregacion;
    private Map<Dimension, ComandoRollUp> historial_rollup;

    /**
     * Constructor para la clase ComandoRollUp.
     *
     * @param tabla_operacion La tabla de hechos que se utilizará para llevar a cabo la operación.
     * @param dimension_reduccion La dimensión sobre la cual se realizará la reducción.
     * @param nivel_reduccion El nivel de reducción dentro de la dimensión especificada.
     * @param agregacion La operación de agregación a aplicar.
     * @throws TablaException Si se produce un error al inicializar la operación.
     */
    public ComandoRollUp(Hecho tabla_operacion, Dimension  dimension_reduccion, 
                        String nivel_reduccion, String agregacion, Map<Dimension, ComandoRollUp> historial_rollup) throws TablaException{

        // Inicializo los atributos que se verán implicados en la operación
        this.tabla_operacion = tabla_operacion;
        this.dimension_reduccion = dimension_reduccion;
        this.nivel_reduccion = nivel_reduccion;
        this.agregacion = agregacion;
        this.historial_rollup = historial_rollup;

        // Obtengo los niveles de la operacion
        this.niveles_operacion = this.obtenerNivelesOperacion(this.dimension_reduccion, this.nivel_reduccion);

        // Me guardo una copia de 'tabla_operacion' en el momento de la creación
        // Así puedo obtener la tabla antes de aplicar el cambio
        this.tabla_antes_de_operar = tabla_operacion.getHechoCopy();
    }

    /**
     * Ejecuta el comando RollUp.
     * Agrupa los hechos por los criterios de reducción, aplica la operación de agregación,
     * y modifica el estado interno  del cubo sobre el cual se aplica.
     *
     * @throws TablaException Si se produce algún error durante la ejecución del comando.
     */
    @Override
    public void ejecutar() throws TablaException {

        // Actualizo el historial de operaciones rollup antes de operar
        this.historial_rollup.put(this.dimension_reduccion, this);

        // Agrupo según 'niveles_operacion'
        Map<List<String>, List<List<String>>> mapa_agrupacion = Tabla.groupBy(this.tabla_operacion,this.niveles_operacion, this.tabla_operacion.getHechos());

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
        headers_operacion.addAll(this.tabla_operacion.getHechos());

        // Finalmente modifico 'tabla_operacion'
        this.tabla_operacion = new Hecho(this.tabla_operacion.getNombre(), operacion_resultante, headers_operacion, this.tabla_operacion.getHechos());
    }

    /**
     * Obtiene la tabla resultante de la operación de RollUp.
     *
     * @return La tabla de hechos resultante.
     */
    public Hecho getResultado(){
        return this.tabla_operacion;
    }

    public Map<Dimension, ComandoRollUp> getHistorial(){
        return this.historial_rollup;
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
     * Obtiene los niveles de operación para la dimensión y el nivel de reducción especificados.
     *
     * @param dimension La dimensión sobre la cual se realizará la reducción.
     * @param nivel El nivel de reducción dentro de la dimensión especificada.
     * @return Una lista de niveles de operación.
     */
    private List<String> obtenerNivelesOperacion(Dimension dimension, String nivel){

        // Genero una lista para guardar el resultado final
        List<String> niveles_resultantes = new ArrayList<>();

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

        // Ahora tomo los headers de la tabla de operación
        List<String> headers_operacion = new ArrayList<>(this.tabla_operacion.getHeaders());
    
        // Le quito todas las columnas relacionadas a la dimensión que agrupo
        headers_operacion.removeAll(dimension.getHeaders());

        // Y ahora añado las dimensiones restantes a 'niveles_resultantes'
        niveles_resultantes.addAll(headers_operacion);
        
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

    /**
     * Obtiene una copia de la tabla operada antes de aplicar el cambio.
     *
     * @return Una copia de la tabla que se generó antes de aplicar la operación.
     * @throws TablaException Si se produce algún error al obtener la copia.
     */
    protected Hecho getTablaAOperacion() throws TablaException{
        return this.tabla_antes_de_operar.getHechoCopy();
    }

    protected Dimension getDimensionReduccion(){
        return this.dimension_reduccion;
    }

    protected String getNivelReduccion(){
        return this.nivel_reduccion;
    }
}
