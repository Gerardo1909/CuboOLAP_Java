package Cubo.comandos;

import java.util.List;
import java.util.Map;
import Cubo.tablas.Hecho;
import java.util.HashMap;
import java.util.ArrayList;

public class ComandoRollUp implements ComandoCubo{

    private Hecho tabla_hechos;
    private List<String> criterio_reduccion;
    private String agregacion;
    private Map<List<String>, List<Double>> resultado;


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
        Map<List<String>, List<Double>> mapa_resultante = new HashMap<>();

        // Recorro cada entrada de 'mapa_agrupacion'
        for (Map.Entry<List<String>, List<List<Double>>> entrada : mapa_agrupacion.entrySet()) {
            List<String> clave = entrada.getKey();
            List<List<Double>> listasHechos = entrada.getValue();

            // Aplico la operación elegida a cada lista de hechos
            List<Double> operaciones = new ArrayList<>();
            for (List<Double> lista : listasHechos) {
                double suma = sumarLista(lista);
                operaciones.add(suma);
            }

            // Guardo la entrada resultante
            mapa_resultante.put(clave, operaciones);
        }

        this.resultado = mapa_resultante;
    }


    public Map<List<String>, List<Double>> getResultado() {
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
