package Cubo.excepciones.excepcionesDimension;

public class ClaveNoPresenteException extends DimensionException {
    public ClaveNoPresenteException(String message) {
        super(message);
    }

    public ClaveNoPresenteException(String message, Throwable cause) {
        super(message, cause);
    }
}