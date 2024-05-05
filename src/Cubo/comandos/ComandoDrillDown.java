package Cubo.comandos;

public class ComandoDrillDown implements ComandoCubo {

    private String dimension;

    public ComandoDrillDown(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public void ejecutar() {
        // Aquí va la implementación de Drill-down
        System.out.println("Realizando Drill-down en la dimensión: " + dimension);
    }
    
}
