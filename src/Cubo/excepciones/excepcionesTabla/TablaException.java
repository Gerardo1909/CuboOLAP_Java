package Cubo.excepciones.excepcionesTabla;

public class TablaException  extends RuntimeException {
    public TablaException(String message) {
        super(message);
    }

    public TablaException(String message, Throwable cause) {
        super(message, cause);
    }
}
