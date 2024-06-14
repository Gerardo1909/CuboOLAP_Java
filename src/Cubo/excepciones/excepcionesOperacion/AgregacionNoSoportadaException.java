package Cubo.excepciones.excepcionesOperacion;

public class AgregacionNoSoportadaException extends RuntimeException {
    public AgregacionNoSoportadaException(String message) {
        super(message);
    }

    public AgregacionNoSoportadaException(String message, Throwable cause) {
        super(message, cause);
    }
}
