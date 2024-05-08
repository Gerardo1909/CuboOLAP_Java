package Cubo.comandos;

import java.util.List;
import java.util.Map;
import Cubo.tablas.Hecho;

public class ComandoRollUp implements ComandoCubo{

    private Hecho tabla_hechos;
    private List<String> criterio_reduccion;
    private Map<List<String>, List<Double>> resultado;


    public ComandoRollUp(Hecho hecho, List<String> criterio_reduccion) {
        this.tabla_hechos = hecho;
        this.criterio_reduccion = criterio_reduccion;
    }

    @Override
    public void ejecutar() {
        this.resultado = this.tabla_hechos.groupBy(criterio_reduccion);
    }


    public Map<List<String>, List<Double>> getResultado() {
        return this.resultado;
    }
}
