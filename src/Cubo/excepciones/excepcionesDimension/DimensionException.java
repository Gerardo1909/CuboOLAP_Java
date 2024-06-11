package Cubo.excepciones.excepcionesDimension;

import Cubo.excepciones.excepcionesTabla.TablaException;

public class DimensionException extends TablaException {
    public DimensionException(String message) {
        super(message);
    }

    public DimensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
