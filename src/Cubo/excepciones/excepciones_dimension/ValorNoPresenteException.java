package Cubo.excepciones.excepciones_dimension;

public class ValorNoPresenteException extends DimensionException{

    public ValorNoPresenteException(String mensaje) {
        super(mensaje);
    }

    public ValorNoPresenteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
