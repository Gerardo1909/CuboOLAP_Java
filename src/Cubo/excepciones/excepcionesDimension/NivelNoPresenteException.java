package Cubo.excepciones.excepcionesDimension;

public class NivelNoPresenteException extends DimensionException  {
    public NivelNoPresenteException(String message) {
        super(message);
    }

    public NivelNoPresenteException(String message, Throwable cause) {
        super(message, cause);
    }
}
