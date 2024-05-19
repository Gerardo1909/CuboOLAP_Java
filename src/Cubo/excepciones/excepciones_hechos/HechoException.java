package Cubo.excepciones.excepciones_hechos;

public class HechoException extends Exception {
    public HechoException(String message) {
        super(message);
    }

    public HechoException(String message, Throwable cause) {
        super(message, cause);
    }
}
