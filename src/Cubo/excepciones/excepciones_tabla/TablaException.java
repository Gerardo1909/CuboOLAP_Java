package Cubo.excepciones.excepciones_tabla;

public class TablaException  extends Exception {
    public TablaException(String message) {
        super(message);
    }

    public TablaException(String message, Throwable cause) {
        super(message, cause);
    }
}