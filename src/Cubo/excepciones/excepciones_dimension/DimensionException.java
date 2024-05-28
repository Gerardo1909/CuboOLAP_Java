package Cubo.excepciones.excepciones_dimension;

import Cubo.excepciones.excepciones_tabla.TablaException;

public class DimensionException extends TablaException  {
    public DimensionException(String message) {
        super(message);
    }

    public DimensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
