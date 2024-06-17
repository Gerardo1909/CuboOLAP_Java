package Cubo.excepciones.excepcionesDimension;

public class DimensionNoAsociadaException extends DimensionException{

    public DimensionNoAsociadaException(String message) {
        super(message);
    }

    public DimensionNoAsociadaException(String message, Throwable cause) {
        super(message, cause);
    }
}
