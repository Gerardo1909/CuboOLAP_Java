package Cubo.excepciones.excepciones_dimension;

public class DimensionException extends Exception {
    public DimensionException(String message) {
        super(message);
    }

    public DimensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
