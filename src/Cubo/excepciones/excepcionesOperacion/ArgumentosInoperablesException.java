package Cubo.excepciones.excepcionesOperacion;

public class ArgumentosInoperablesException  extends Exception{
    public ArgumentosInoperablesException(String mensaje){
        super(mensaje);
    }

    public ArgumentosInoperablesException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
