package Cubo.excepciones.excepcionesDimension;

public class ValorNoPresenteException extends DimensionException{

    public ValorNoPresenteException(String mensaje) {
        super(mensaje);
    }

    public ValorNoPresenteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
