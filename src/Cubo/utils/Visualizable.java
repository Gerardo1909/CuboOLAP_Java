package Cubo.utils;

import java.util.List;
import Cubo.excepciones.excepciones_tabla.ColumnaNoPresenteException;
import Cubo.excepciones.excepciones_tabla.FilaFueraDeRangoException;

/**
 * Esta interfaz representa un objeto que puede ser visualizado.
 * Proporciona un método para ver los datos del objeto en un formato tabular.
 */
public interface Visualizable {
    /**
     * Muestra una parte de los datos del objeto en un formato tabular.
     *
     * @param n_filas El número de filas a mostrar.
     * @param columnas La lista de nombres de columnas a mostrar.
     * @throws ColumnaNoPresenteException Si una columna solicitada no está presente en los datos del objeto.
     * @throws FilaFueraDeRangoException Si el número solicitado de filas está fuera del rango de datos del objeto.
     */
    public void ver(int n_filas, List<String> columnas) throws ColumnaNoPresenteException, FilaFueraDeRangoException;
}
