package Cubo.excepciones.excepcionesTabla;

public class ColumnaNoPresenteException  extends TablaException{
    public ColumnaNoPresenteException(String message) {
        super(message);
    }

    public ColumnaNoPresenteException(String message, Throwable cause) {
        super(message, cause);
    }
}
