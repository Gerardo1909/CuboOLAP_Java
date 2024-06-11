package Cubo.excepciones.excepcionesHechos;

import Cubo.excepciones.excepcionesTabla.TablaException;

public class HechoException extends TablaException  {
    public HechoException(String message) {
        super(message);
    }

    public HechoException(String message, Throwable cause) {
        super(message, cause);
    }
}
