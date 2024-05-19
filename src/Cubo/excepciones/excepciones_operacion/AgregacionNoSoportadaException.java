package Cubo.excepciones.excepciones_operacion;

public class AgregacionNoSoportadaException extends Exception {
    public AgregacionNoSoportadaException(String message) {
        super(message);
    }

    public AgregacionNoSoportadaException(String message, Throwable cause) {
        super(message, cause);
    }
}
