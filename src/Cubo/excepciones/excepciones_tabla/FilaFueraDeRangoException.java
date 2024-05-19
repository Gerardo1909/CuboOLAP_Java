package Cubo.excepciones.excepciones_tabla;

public class FilaFueraDeRangoException extends TablaException {
    public FilaFueraDeRangoException(String message) {
        super(message);
    }
    public FilaFueraDeRangoException(String message, Throwable cause) {
        super(message, cause);
    }
}
