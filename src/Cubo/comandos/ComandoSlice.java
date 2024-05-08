package Cubo.comandos;

import java.util.List;
import java.util.Map;

public class ComandoSlice implements ComandoCubo {

    private String dimension;
    private String value;
    private Map<List<String>, List<Double>> resultado;

    
    public ComandoSlice(String dimension, String value) {
        this.dimension = dimension;
        this.value = value;
    }

    @Override
    public void ejecutar() {
        // Aquí va la implementación de Slice
        System.out.println("Realizando Slice en la dimensión: " + dimension + " con valor: " + value);
    }

    public Map<List<String>, List<Double>> getResultado() {
        return this.resultado;
    }

}
