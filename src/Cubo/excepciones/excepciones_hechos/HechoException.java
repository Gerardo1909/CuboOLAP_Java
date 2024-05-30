package Cubo.excepciones.excepciones_hechos;

import Cubo.excepciones.excepciones_tabla.TablaException;

public class HechoException extends TablaException  {
    public HechoException(String message) {
        super(message);
    }

    public HechoException(String message, Throwable cause) {
        super(message, cause);
    }
}
