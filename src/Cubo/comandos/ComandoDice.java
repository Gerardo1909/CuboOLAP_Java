package Cubo.comandos;

import java.util.List;
import java.util.Map;

public class ComandoDice implements ComandoCubo{

    private Map<String, List<String>> filters;
    private Map<List<String>, List<Double>> resultado;

    public ComandoDice(Map<String, List<String>> filters) {
        this.filters = filters;
    }

    @Override
    public void ejecutar() {
        // Aquí va la implementación de Dice
        System.out.println("Realizando Dice con filtros: " + filters);
    }

    public Map<List<String>, List<Double>> getResultado() {
        return this.resultado;
    }
}
