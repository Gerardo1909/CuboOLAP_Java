package Cubo.comandos;

import java.util.List;
import java.util.Map;
import Cubo.tablas.Hecho;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

public class ComandoRollUp implements ComandoCubo{

    private Hecho tabla_hechos;
    private List<String> criterio_reduccion;
    private String agregacion;
    private Map<List<String>, List<List<String>>>resultado;


    public ComandoRollUp(Hecho hecho, List<String> criterio_reduccion, String agregacion) {
        this.tabla_hechos = hecho;
        this.criterio_reduccion = criterio_reduccion;
        this.agregacion = agregacion;
    }

    @Override
    public void ejecutar() {

        // Obtengo el resultado del groupBy
        Map<List<String>, List<List<Double>>> mapa_agrupacion = this.tabla_hechos.groupBy(criterio_reduccion);

        // Armo el mapa que tendrá el resultado con la operación de agregación elegida aplicada
        Map<List<String>, List<Double>> mapa_operacion = new HashMap<>();

        // Recorro cada entrada de 'mapa_agrupacion'
        for (Map.Entry<List<String>, List<List<Double>>> entrada : mapa_agrupacion.entrySet()) {
            List<String> clave = entrada.getKey();
            List<List<Double>> listasHechos = entrada.getValue();

            // Aplico la operación elegida a cada lista de hechos
            List<Double> operaciones = new ArrayList<>();
            for (List<Double> lista : listasHechos) {

                switch (this.getAgregacion()) {
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
                    default:
                        throw new IllegalArgumentException("Operación de agregación no soportada: " + this.getAgregacion());
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
        List<String> headers_operacion = new ArrayList<>(criterio_reduccion);
        headers_operacion.addAll(tabla_hechos.getHechos());

        // Y ahora armo el mapa que contiene como clave los headers de la operación
        // y como valor contiene la matriz que contiene la operación
        Map<List<String>, List<List<String>>> mapa_resultante = new HashMap<>();
        mapa_resultante.put(headers_operacion, operacion_resultante);

        this.resultado = mapa_resultante;
    }

    private String getAgregacion(){
        return this.agregacion.toLowerCase().trim();
    }


    public Map<List<String>, List<List<String>>> getResultado() {
        return this.resultado;
    }

    private Double sumarLista(List<Double> lista_a_sumar){
        Double suma = 0.0;
        for (Double numero : lista_a_sumar){
            suma+= numero;
        }
        return suma;
    }

}
