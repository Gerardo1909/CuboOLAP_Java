package Cubo.excepciones.excepciones_dimension;

public class NivelNoPresenteException extends DimensionException  {
    public NivelNoPresenteException(String message) {
        super(message);
    }

    public NivelNoPresenteException(String message, Throwable cause) {
        super(message, cause);
    }
}
