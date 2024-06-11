package Cubo.excepciones.excepcionesOperacion;

public class NivelDesagregadoException extends Exception{
    public NivelDesagregadoException(String message) {
        super(message);
    }

    public NivelDesagregadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
