package Cubo.comandos;

import java.util.List;
import java.util.Map;

public class ComandoDrillDown implements ComandoCubo {

    private String dimension;
    private Map<List<String>, List<Double>> resultado;


    public ComandoDrillDown(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public void ejecutar() {
        // Aquí va la implementación de Drill-down
        System.out.println("Realizando Drill-down en la dimensión: " + dimension);
    }

    public Map<List<String>, List<Double>> getResultado() {
        return this.resultado;
    }
}
    

