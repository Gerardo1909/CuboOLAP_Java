package Cubo.excepciones.excepciones_tabla;

public class ColumnaNoPresenteException  extends TablaException{
    public ColumnaNoPresenteException(String message) {
        super(message);
    }

    public ColumnaNoPresenteException(String message, Throwable cause) {
        super(message, cause);
    }
}
