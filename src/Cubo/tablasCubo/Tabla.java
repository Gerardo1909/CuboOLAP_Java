package Cubo.tablasCubo;

import java.util.ArrayList;
import java.util.List;
import Cubo.excepciones.excepcionesTabla.ColumnaNoPresenteException;
import Cubo.excepciones.excepcionesTabla.FilaFueraDeRangoException;

/**
 * <p>
 * Esta clase es una abstracción para representar información en formato
 * tabular.
 * </p>
 * 
 * <p>
 * Se encarga de representar el comportamiento común entre datos que presentan 
 * un formato tabular.
 * </p>
 */
public abstract class Tabla{

    // Atributos de la clase Tabla
    protected List<String> headers;
    protected List<List<String>> datosTabla;
    protected String nombre;
   
    /**
     * <p>
     * <b>Constructor para la clase Tabla.</b>
     * </p>
     * 
     * <p>
     * Se encarga de inicializar una nueva tabla con un nombre, una matriz de información interna y
     * encabezados que representan las columnas presentes en la misma.
     * </p>
     *
     * @param nombre El nombre de la tabla.
     * @param datosTabla Una matriz que representa la información interna de la tabla.
     * @param headers Una lista que representa los encabezados de la tabla..
     */
    protected Tabla(String nombre, List<List<String>> datosTabla, List<String> headers){
        this.nombre = nombre;
        this.headers = new ArrayList<>(headers);
        this.datosTabla = new ArrayList<>(datosTabla);
    }


    // Getters de la clase

    /**
     * @return El nombre de esta tabla.
     */
    public String getNombre(){
        return this.nombre;
    }

    /**
     * @return Una copia de la lista que contiene los encabezados de esta tabla.
     */
    public List<String> getHeaders() {
        return new ArrayList<>(headers);
        }
    
    /**
     * @return Una copia de la matriz que contiene la información
     *         interna de esta tabla.
     */
    public List<List<String>> getDatosTabla() {
        List<List<String>> datosCopy = new ArrayList<>();
            for (List<String> fila : this.datosTabla) {
                datosCopy.add(new ArrayList<>(fila));
            }
        return datosCopy;
    }

    /**
     * Se encarga de obtener la información interna de una columna presente en esta 
     * tabla. 
     *
     * @param nombre_columna Nombre de la columna que se va a obtener.
     * 
     * @throws ColumnaNoPresenteException Si la columna especificada no existe en la tabla.
     * 
     * @return Una lista que contiene los valores presentes en la columna seleccionada de esta tabla, 
     *         notar que no se incluye el nombre que representa a la misma.
     */
    public List<String> getColumna(String nombre_columna){
 
        // Verifico si el nombre de la columna existe en los headers
        int indice_columna = this.headers.indexOf(nombre_columna);
        if (indice_columna == -1) {
            throw new ColumnaNoPresenteException("La columna especificada '" + nombre_columna + "' no existe en los encabezados.");
        }
    
        // Hago una copia de la matriz de datos y añado la 
        // información referente a la columna solicitada
        List<List<String>> datos_copy = this.getDatosTabla();
        List<String> columna = new ArrayList<>();
        for (int i = 0; i < datos_copy.size(); i++) {
            columna.add(datos_copy.get(i).get(indice_columna));
        }
    
        return columna;
    }


    // Métodos de la clase

    /**
     * Muestra una parte especificada los datos internos de esta tabla mediante una impresión por consola.
     *
     * @param cantFilas El número de filas a mostrar.
     * @param columnas Una lista que contiene los nombres de las columnas a mostrar.
     * 
     * @throws ColumnaNoPresenteException Si una de las columnas solicitadas no está presente en esta tabla.
     * @throws FilaFueraDeRangoException Si el número solicitado de filas a mostrar es mayor a las disponibles
     *                                   en esta tabla.
     */
    public void ver(int cantFilas, List<String> columnas){

        // Máximo de columnas a mostrar en la consola para evitar desborde
        int maxColsMostrar = 4;

        // Verifico que los argumentos estén dentro de los límites
        if (cantFilas > this.datosTabla.size()) {
            throw new FilaFueraDeRangoException("La cantidad de filas solicitadas es mayor a la longitud disponible en la tabla '" + this.getNombre() + "'.");
        }
        for (String columna : columnas) {
            if (!this.headers.contains(columna)) {
                throw new ColumnaNoPresenteException("La columna especificada '" + columna + "' no existe entre los encabezados.");
            }
        }

        // Hago la impresión por consola
        List<List<String>> columnasSeleccionadas = seleccionarColumnas(columnas);
        int cantidadColumnas = columnas.size();
        int chunks = (int) Math.ceil((double) cantidadColumnas / maxColsMostrar);
        for (int indiceChunk = 0; indiceChunk < chunks; indiceChunk++) {
            int inicio = indiceChunk * maxColsMostrar;
            int fin = Math.min(inicio + maxColsMostrar, cantidadColumnas);

            imprimirEncabezados(columnas, inicio, fin);
            imprimirFilas(cantFilas, columnasSeleccionadas, inicio, fin);

            System.out.println();
        }
    }

    /**
     * Se encarga de traer la información interna de las columnas que se desean visualizar en 
     * el método 'ver()'.
     *
     * @param columnas Una lista que contiene los nombres de las columnas seleccionadas.
     * 
     * @return Una matriz que contiene únicamente la información interna de las columnas seleccionadas.
     */
    private List<List<String>> seleccionarColumnas(List<String> columnas) {
        List<List<String>> columnasSeleccionadas = new ArrayList<>();
        for (String columna : columnas) {
            columnasSeleccionadas.add(this.getColumna(columna));
        }
        return columnasSeleccionadas;
    }

    /**
     * Se encarga de imprimir los encabezados de las columnas que se desean visualizar en 
     * el método 'ver()'.
     */
    private void imprimirEncabezados(List<String> columnas, int inicio, int fin) {
        for (int i = inicio; i < fin; i++) {
            System.out.print(String.format("%-30s", columnas.get(i)));
        }
        if (fin < columnas.size()) {
            System.out.print(String.format("%-30s", "..."));
        }
        System.out.println();
    }

    /**
     * Se encarga de imprimir las filas de las columnas que se desean visualizar en 
     * el método 'ver()'.
     */
    private void imprimirFilas(int cantFilas, List<List<String>> columnasSeleccionadas, int inicio, int fin) {
        for (int i = 0; i < cantFilas; i++) {
            for (int j = inicio; j < fin; j++) {
                System.out.print(String.format("%-30s", columnasSeleccionadas.get(j).get(i)));
            }
            if (fin < columnasSeleccionadas.size()) {
                System.out.print(String.format("%-30s", "..."));
            }
            System.out.println();
        }
    }

    /**
     * Método para comparar dos intancias de la clase Tabla.
     * 
     * @param obj El objeto a comparar con esta tabla.
     * 
     * @return `true` si los objetos comparados contienen la misma matriz de información interna y los mismos encabezados, 
     *          `false` en caso contrario.
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
        return this.headers.equals(tabla.headers) && this.datosTabla.equals(tabla.datosTabla);
    }

    /**
     * @return El valor hash de esta tabla.
     */
    @Override  
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.headers.hashCode();
        result = 31 * result + this.datosTabla.hashCode();
        return result;
    }

}
