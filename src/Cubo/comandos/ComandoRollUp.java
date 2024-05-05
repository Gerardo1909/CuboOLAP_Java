package Cubo.comandos;

public class ComandoRollUp implements ComandoCubo{

    private String dimension;

    public ComandoRollUp(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public void ejecutar() {
        // Aquí va la implementación de Roll-up
        System.out.println("Realizando Roll-up en la dimensión: " + dimension);
    }

}
