package Cubo.excepciones.excepciones_cubo;

public class CuboException extends Exception {

    public CuboException(String message) {
        super(message);
    }

    public CuboException(String message, Throwable cause) {
        super(message, cause);
    }

}
