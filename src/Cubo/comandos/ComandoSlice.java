package Cubo.comandos;

public class ComandoSlice implements ComandoCubo {

    private String dimension;
    private String value;

    public ComandoSlice(String dimension, String value) {
        this.dimension = dimension;
        this.value = value;
    }

    @Override
    public void ejecutar() {
        // Aquí va la implementación de Slice
        System.out.println("Realizando Slice en la dimensión: " + dimension + " con valor: " + value);
    }

}
