package Cubo.comandos;

import java.util.List;
import java.util.Map;

public class ComandoDice implements ComandoCubo{

    private Map<String, List<String>> filters;

    public ComandoDice(Map<String, List<String>> filters) {
        this.filters = filters;
    }

    @Override
    public void ejecutar() {
        // Aquí va la implementación de Dice
        System.out.println("Realizando Dice con filtros: " + filters);
    }
}
