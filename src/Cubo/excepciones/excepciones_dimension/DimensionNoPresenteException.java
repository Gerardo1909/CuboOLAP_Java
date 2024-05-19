package Cubo.excepciones.excepciones_dimension;

public class DimensionNoPresenteException extends DimensionException {
    public DimensionNoPresenteException(String message) {
        super(message);
    }

    public DimensionNoPresenteException(String message, Throwable cause) {
        super(message, cause);
    }
}
