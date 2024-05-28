package Cubo.excepciones.excepciones_operacion;

public class NivelDesagregadoException extends Exception{
    public NivelDesagregadoException(String message) {
        super(message);
    }

    public NivelDesagregadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
