package Cubo.excepciones.excepcionesCubo;

public class CuboException extends Exception {

    public CuboException(String message) {
        super(message);
    }

    public CuboException(String message, Throwable cause) {
        super(message, cause);
    }

}
