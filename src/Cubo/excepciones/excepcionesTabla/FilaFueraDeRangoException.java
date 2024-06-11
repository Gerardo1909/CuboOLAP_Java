package Cubo.excepciones.excepcionesTabla;

public class FilaFueraDeRangoException extends TablaException {
    public FilaFueraDeRangoException(String message) {
        super(message);
    }
    public FilaFueraDeRangoException(String message, Throwable cause) {
        super(message, cause);
    }
}
