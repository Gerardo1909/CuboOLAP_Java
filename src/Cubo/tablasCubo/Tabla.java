package Cubo.tablasCubo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import Cubo.excepciones.excepcionesTabla.ColumnaNoPresenteException;
import Cubo.excepciones.excepcionesTabla.FilaFueraDeRangoException;

/**
 * Clase abstracta que representa una tabla de datos en formato tabular.
 */
public abstract class Tabla{

    protected List<String> headers;
    protected List<List<String>> data;
    protected String nombre;
   
    /**
     * Constructor para crear una tabla a partir de datos y encabezados dados.
     * Crea nuevas listas para 'encabezados' y 'datos' para asegurarse de su inmutabilidad.
     *
     * @param nombre El nombre de la tabla.
     * @param data Los datos de la tabla. Debe ser una lista de listas, donde cada lista interna representa una fila.
     * @param headers Los encabezados de la tabla. Debe ser una lista de cadenas, donde cada cadena representa un nombre de columna.
     */
    protected Tabla(String nombre, List<List<String>> data, List<String> headers){
        this.nombre = nombre;
        this.headers = new ArrayList<>(headers);
        this.data = new ArrayList<>(data);
    }


    // Getters de la clase

    /**
     * Obtiene el nombre de la tabla.
     *
     * @return Nombre de la tabla.
     */
    public String getNombre(){
        return this.nombre;
    }

    /**
     * Obtiene los encabezados de la tabla.
     *
     * @return Lista de encabezados de la tabla.
     */
    public List<String> getHeaders() {
        return new ArrayList<>(headers);
        }
    
    /**
     * Obtiene los datos de la tabla.
     *
     * @return Lista de listas de datos de la tabla.
     */
    public List<List<String>> getData() {
        List<List<String>> datosCopy = new ArrayList<>();
            for (List<String> row : data) {
                datosCopy.add(new ArrayList<>(row));
            }
        return datosCopy;
    }

    /**
     * Obtiene una columna específica de la tabla.
     *
     * @param nombre_columna Nombre de la columna que se va a obtener.
     * @return Lista de valores de la columna.
     * @throws ColumnaNoPresenteException Si la columna especificada no existe en la tabla.
     */
    public List<String> getColumna(String nombre_columna) throws ColumnaNoPresenteException{

        //Primero busco el índice de la columna según 'nombre_columna'
        int indice_columna = this.headers.indexOf(nombre_columna);
        
        // Verifico si el nombre de la columna existe en los headers
        if (indice_columna == -1) {
            throw new ColumnaNoPresenteException("La columna especificada" + nombre_columna + "no existe en los encabezados.");
        }
    
        // Ahora hago una copia de los datos para cuidar la inmutabilidad
        List<List<String> > datos_copy = this.getData();
    
        // Creo la lista que corresponde a la columna
        List<String>  columna = new ArrayList<>();
    
        // Y agrego el resto de datos
        for (int i = 0; i < datos_copy.size(); i++) {
            columna.add(datos_copy.get(i).get(indice_columna));
        }
    
        return columna;
    }


    // Métodos de la clase

    /**
     * Obtiene valores únicos de una columna específica de la tabla.
     *
     * @param columna Columna de la que se van a obtener los valores únicos.
     * @return Lista de valores únicos de la columna.
     * @throws ColumnaNoPresenteException Si la columna especificada no existe en la tabla.
     */
    public List<String> obtenerValoresUnicos(String columna) throws ColumnaNoPresenteException{

        // Verifico si la columna existe en los headers
        if (!this.headers.contains(columna)) {
            throw new ColumnaNoPresenteException("La columna especificada" + columna + "no existe en los encabezados.");
        }

        // Primero obtengo la columna deseada
        List<String> columna_seleccionada = this.getColumna(columna);

        // Uso un set que solo permite valores únicos
        Set<String> valores_unicos = new HashSet<>();

        // Recorro la columna y agrego los valores únicos a un set
        for (String valor : columna_seleccionada) {
            if (valor != null) {
                valores_unicos.add(valor);
            }
        }

        return new ArrayList<>(valores_unicos);
}

    /**
     * Muestra una parte seleccionada de los datos de la tabla en un formato tabular.
     *
     * @param cantFilas El número de filas a mostrar.
     * @param columnas La lista de nombres de columnas a mostrar.
     * @throws ColumnaNoPresenteException Si una columna solicitada no está presente en los datos del objeto.
     * @throws FilaFueraDeRangoException Si el número solicitado de filas está fuera del rango de datos del objeto.
     */
    public void ver(int cantFilas, List<String> columnas) throws ColumnaNoPresenteException, FilaFueraDeRangoException {

        // Aquí defino el máximo de columnas que se pueden ver
        int max_cols_mostrar = 4;

        // Previengo el caso donde cantFilas es mayor que las filas disponibles
        if (cantFilas > this.data.size()) {
            throw new FilaFueraDeRangoException("La cantidad de filas solicitadas es mayor a la longitud disponible en la tabla " + this.getNombre());
        }

        // Verifico si las columnas especificadas existen en los encabezados
        for (String columna : columnas) {
            if (!this.headers.contains(columna)) {
                throw new ColumnaNoPresenteException("La columna especificada " + columna + " no existe en los encabezados.");
            }
        }

        // Genero una lista y almaceno las columnas seleccionadas
        List<List<String>> columnas_seleccionadas = new ArrayList<>();
        for (String columna : columnas) {
            columnas_seleccionadas.add(this.getColumna(columna));
        }

        // Determino el número de bloques (chunks) a mostrar
        int cantidad_columnas = columnas.size();
        int chunks = (int) Math.ceil((double) cantidad_columnas / max_cols_mostrar);

        // Itero a través de cada bloque de columnas
        for (int indice_chunk = 0; indice_chunk < chunks; indice_chunk++) {
            int inicio = indice_chunk * max_cols_mostrar;
            int fin = Math.min(inicio + max_cols_mostrar, cantidad_columnas);

            // Imprimo los encabezados de las columnas
            for (int i = inicio; i < fin; i++) {
                System.out.print(String.format("%-30s", columnas.get(i)));
            }
            // Imprimo puntos suspensivos si hay más columnas por mostrar
            if (fin < cantidad_columnas) {
                System.out.print(String.format("%-30s", "..."));
            }
            System.out.println();

            // Imprimo las filas de datos
            for (int i = 0; i < cantFilas; i++) {
                for (int j = inicio; j < fin; j++) {
                    System.out.print(String.format("%-30s", columnas_seleccionadas.get(j).get(i)));
                }
                if (fin < cantidad_columnas) {
                    System.out.print(String.format("%-30s", "..."));
                }
                System.out.println();
            }

            // Añado una línea en blanco entre bloques para mejor legibilidad
            System.out.println();
        }
    }

    /**
    * Compara este objeto `Tabla` con el objeto especificado para determinar si son iguales.
    * Devuelve `true` si el objeto dado también es un objeto `Tabla` y tiene los mismos encabezados y datos que este objeto `Tabla`.
    * 
    * @param obj el objeto a comparar con este objeto `Tabla`
    * @return `true` si el objeto dado es igual a este objeto `Tabla`, `false` en caso contrario
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tabla)) {
            return false;
        }
        Tabla tabla = (Tabla) obj;

        // La igualdad la defino según si sus headers y su información son iguales
        return this.headers.equals(tabla.headers) && this.data.equals(tabla.data);
    }

    /**
     * Devuelve el valor hash del objeto Tabla.
     * 
     * @return El valor hash del objeto Tabla.
     */
    @Override  
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.headers.hashCode();
        result = 31 * result + this.data.hashCode();
        return result;
    }



}
