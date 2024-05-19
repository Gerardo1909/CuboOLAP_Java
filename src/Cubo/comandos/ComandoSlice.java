package Cubo.comandos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Cubo.CuboOLAP;
import Cubo.excepciones.excepciones_tabla.TablaException;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

/**
 * Esta clase implementa el comando Slice para la clase {@link CuboOLAP}.
 * "Corta" los hechos según la dimensión, nivel y valor especificados, resultando un cubo que cuenta con esos valores fijados en dicha dimensión.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoSlice implements ComandoCubo {

    private Hecho tabla_operacion;
    private Dimension dimension;
    private String nivel;
    private String valor_corte;
    private Map<List<String>, List<List<String>>> resultado;

    /**
     * Constructor para la clase ComandoSlice.
     *
     * @param tabla_operacion La tabla de hechos que se va a cortar.
     * @param dimension       La dimensión en la que se va a cortar.
     * @param nivel           El nivel dentro de la dimensión en la que se va a cortar.
     * @param valor_corte     El valor para filtrar.
     */
    public ComandoSlice(Hecho tabla_operacion, Dimension dimension,String nivel ,String valor_corte) {
        this.dimension = dimension;
        this.valor_corte = valor_corte;
        this.tabla_operacion = tabla_operacion;
        this.nivel = nivel;
    }

    /**
     * Ejecuta el comando Slice.
     * Ejecuta la operación de corte en la tabla de operación, 
     * y almacena el resultado en el atributo 'resultado'.
     */
    @Override
    public void ejecutar() throws TablaException {

        // Primero obtengo el índice del nivel por el cual se va a filtrar
        Integer indice_nivel = this.tabla_operacion.getHeaders().indexOf(this.nivel);

        // Genero una matriz que contendrá la operacion resultante
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Ahora itero por cada fila de la tabla de operación y me quedo con aquellas que cumplan con la condición de corte
        for (List<String> fila : this.tabla_operacion.getData()){
            // Si el valor de corte coindice en dicha fila la añado a la lista de información resultante
            if (fila.get(indice_nivel).equals(this.valor_corte)) {
                operacion_resultante.add(fila);
            }
        }

        // Genero una lista para guardar los headers de la operacion resultante
        List<String> headers_operacion = new ArrayList<>(this.tabla_operacion.getHeaders());

        // Y ahora armo el mapa que contiene como clave los headers de la operación
        // y como valor contiene la matriz que contiene la operación
        Map<List<String>, List<List<String>>> mapa_resultante = new HashMap<>();
        mapa_resultante.put(headers_operacion, operacion_resultante);

        this.resultado = mapa_resultante;

    }

    /**
     * Devuelve el resultado del comando Slice.
     *
     * @return Un mapa donde las claves son los encabezados de la operación y los valores son la matriz de la operación.
     */
    public Map<List<String>, List<List<String>>> getResultado() {
        return this.resultado;
    }

}
