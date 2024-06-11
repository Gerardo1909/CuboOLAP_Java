package Cubo.excepciones.excepcionesHechos;

public class HechoNoPresenteException extends HechoException{
    public HechoNoPresenteException(String message) {
        super(message);
    }

    public HechoNoPresenteException(String message, Throwable cause) {
        super(message, cause);
    }
}
