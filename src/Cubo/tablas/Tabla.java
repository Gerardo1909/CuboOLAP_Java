package Cubo.tablas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import Cubo.excepciones.excepciones_tabla.ColumnaNoPresenteException;
import Cubo.excepciones.excepciones_tabla.FilaFueraDeRangoException;
import Cubo.lectura_archivos.EstrategiaLecturaArchivo;
import Cubo.utils.Visualizable;

/**
 * Clase abstracta que representa una tabla, datos en formato tabular.
 * Proporciona métodos para agrupar, fusionar, obtener valores únicos y visualizar la tabla.
 * Implementa la interfaz {@link Visualizable}.
 */
public abstract class Tabla implements Visualizable{

    protected List<String> headers;
    protected List<List<String>> data;
    protected String nombre;

    /**
     * Constructor para crear una tabla a partir de un archivo.
     *
     * @param nombre Nombre de la tabla.
     * @param estrategia_lectura Estrategia usada para leer el archivo.
     * @param ruta_archivo Ruta del archivo que se va a leer.
     * @throws IOException Si hay un error de E/S durante la lectura del archivo.
     */
    public Tabla(String nombre ,EstrategiaLecturaArchivo estrategia_lectura, String ruta_archivo) throws IOException{

        // Guardo el archivo leido
        List<List<String>> archivo = estrategia_lectura.leerArchivo(ruta_archivo);

        // Guardo los headers
        List<String> headers = archivo.get(0);
        archivo.remove(0);

        // Guardo la data 
        List<List<String>> data = archivo;

        // Ahora asigno cada variable al lugar que corresponde
        this.nombre = nombre;
        this.headers = headers;
        this.data = data;
    }
   
    /**
     * Constructor para crear una tabla a partir de datos y encabezados dados.
     * Crea nuevas listas para 'encabezados' y 'datos' para asegurarse de su inmutabilidad.
     *
     * @param nombre El nombre de la tabla.
     * @param data Los datos de la tabla. Debe ser una lista de listas, donde cada lista interna representa una fila.
     * @param headers Los encabezados de la tabla. Debe ser una lista de cadenas, donde cada cadena representa un nombre de columna.
     */
    public Tabla(String nombre, List<List<String>> data, List<String> headers){
        this.nombre = nombre;
        this.headers = new ArrayList<>(headers);
        this.data = new ArrayList<>(data);
    }

    /**
     * Método estático para agrupar filas de una tabla según columnas específicas.
     *
     * @param tabla_operacion Tabla en la que se va a realizar la operación de agrupación.
     * @param columnas_agrupacion Columnas por las que se va a agrupar.
     * @param columnas_a_agrupar Columnas que se van a agrupar.
     * @return Mapa con claves de columnas de agrupación y valores de listas de listas de datos agrupados.
     * @throws ColumnaNoPresenteException Si alguna de las columnas especificadas no existe en la tabla.
     */
    public static Map<List<String>, List<List<String>>> groupBy(Tabla tabla_operacion, List<String> columnas_agrupacion, List<String> columnas_a_agrupar) throws ColumnaNoPresenteException {

        // Verifico que estén las columnas de agrupación
        for (String columna : columnas_agrupacion) {
            if (!tabla_operacion.getHeaders().contains(columna)) {
                throw new ColumnaNoPresenteException("La columna que agrupa" + columna + " no está presente en la tabla " + tabla_operacion.getNombre());
            }
        }

        // Verifico que estén las columnas a agrupar
        for (String columna : columnas_a_agrupar) {
            if (!tabla_operacion.getHeaders().contains(columna)) {
                throw new ColumnaNoPresenteException("La columna a agrupar" + columna + " no está presente en la tabla " + tabla_operacion.getNombre());
            }
        }

        // Guardo primero los índices de las columnas por las cuales se agrupa
        List<Integer> indices_agrupacion = new ArrayList<>();
        for (String columna : columnas_agrupacion) {
            indices_agrupacion.add(tabla_operacion.getHeaders().indexOf(columna));
        }

        // Ahora guardo los índices de las columnas a agrupar
        List<Integer> indices_a_agrupar = new ArrayList<>();
        for (String columna : columnas_a_agrupar) {
            indices_a_agrupar.add(tabla_operacion.getHeaders().indexOf(columna));
        }

        // Armo un mapa vacío que guardará los resultados
        Map<List<String>, List<List<String>>> mapa_agrupacion = new LinkedHashMap<>();

        // Recorro las filas de la tabla
        for (List<String> fila : tabla_operacion.getData()) {

            // Creo la clave del grupo
            List<String> clave = new ArrayList<>();
            for (int indice_columna : indices_agrupacion) {
                clave.add(fila.get(indice_columna));
            }

            // Verifico si la clave no está en 'mapa_agrupacion'
            if (!mapa_agrupacion.containsKey(clave)) {

                // Armo la lista para las columnas a agrupar
                List<List<String>> listaColsAgrupar = new ArrayList<>();
                mapa_agrupacion.put(clave, listaColsAgrupar);

                // Y dentro de 'listaColsAgrupar' añado una lista por cada columna en la lista de las que voy a agrupar
                for (String columna : columnas_a_agrupar) {
                    listaColsAgrupar.add(new ArrayList<>());
                }
            }

            // Ahora recorro las columnas a agrupar y las agrego a su lista correspondiente
            for (int i = 0; i < indices_a_agrupar.size(); i++) {

                // Obtengo el índice de la columna a agrupar
                int indice_hecho = indices_a_agrupar.get(i);

                // Lo añado a la lista que corresponde
                mapa_agrupacion.get(clave).get(i).add(fila.get(indice_hecho));

            }

        }

        return mapa_agrupacion;

    }

    /**
     * Método estático para fusionar dos tablas según una columna específica.
     *
     * @param tabla_izq Tabla izquierda.
     * @param tabla_der Tabla derecha.
     * @param on Columna por la que se va a fusionar.
     * @throws ColumnaNoPresenteException Si la columna especificada no existe en alguna de las tablas.
     */
    public static void merge(Tabla tabla_izq, Tabla tabla_der, String on) throws ColumnaNoPresenteException{

        // Obtengo las columnas de ambos archivos
        List<String> columnas_tabla_izq = tabla_izq.getHeaders();
        List<String> columnas_tabla_der = tabla_der.getHeaders();

        // Verifico si "on" existe en ambos archivos
        if (!columnas_tabla_izq.contains(on) || !columnas_tabla_der.contains(on)) {
            throw new ColumnaNoPresenteException("La columna especificada " + on + " no existe en ambos archivos.");
        }

        // Genero la lista donde se van a guardar los resultados
        List<List<String>> resultado = new ArrayList<>();

        // Me armo una lista para guardar los headers de la tabla resultante
        List<String> headersResultado = new ArrayList<>(columnas_tabla_izq); // Ver que inicialmente tiene las columnas de la tabla de izquierda
        headersResultado.addAll(columnas_tabla_der);

        // Ahora armo un mapa que por clave tiene valores únicos de la columna por la cual se junta
        // y como valor tiene todas las filas que coinciden en dicha columna
        Map<String, List<List<String>>> mapa_merge = new LinkedHashMap<>();

        // Primero itero por la tabla izquierda para añadirle los primeros valores al mapa
        for (List<String> fila_tabla_izq : tabla_izq.getData()) {

            // Evito una posible 'NullPointerException'
            if (fila_tabla_izq == null) continue;

            // En cada iteración obtengo el valor de la columna por la cual se junta para esa fila
            String valor_on = fila_tabla_izq.get(columnas_tabla_izq.indexOf(on));

            // Esta lista de listas de String representa todas las filas con ese mismo valor
            // trás cada iteración se debe hacer más grande hasta terminar
            List<List<String>> filas_coincidentes = mapa_merge.get(valor_on);

            // Y esta es una verificación para añadir esa listas de filas de no existir aún
            if (filas_coincidentes == null) {
                filas_coincidentes = new ArrayList<>();
                mapa_merge.put(valor_on, filas_coincidentes);
            }
            filas_coincidentes.add(fila_tabla_izq);
        }

        // Itero sobre las filas de la tabla derecha para terminar de completar las filas del mapa_merge
        for (List<String> fila_tabla_der : tabla_der.getData()) {

            // Evito una posible 'NullPointerException'
            if (fila_tabla_der == null) continue;

            // En cada iteración obtengo el valor de la columna por la cual se junta para esa fila            
            String valor_on = fila_tabla_der.get(columnas_tabla_der.indexOf(on));

            // Y aquí hago la validación para juntar las tablas
            if (mapa_merge.containsKey(valor_on)) {

                // Tomo todas las filas con la misma clave
                List<List<String>> filas_coincidentes = mapa_merge.get(valor_on);

                // Recorro cada una
                for (List<String> fila_coincidente : filas_coincidentes) {

                    // Armo una lista que representa la fila resultante de la unión
                    List<String> filaResultado = new ArrayList<>(fila_coincidente);
                    filaResultado.addAll(fila_tabla_der);

                    // Y lo añado a la tabla resultante
                    resultado.add(filaResultado);
                }
            }
        }

        // Modifico los atributos 'data' y 'headers' de la tabla izquierda
        tabla_izq.data = resultado;
        tabla_izq.headers = headersResultado;
}

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

    /**
     * Obtiene el nombre de la tabla.
     *
     * @return Nombre de la tabla.
     */
    public String getNombre(){
        return this.nombre;
    }

    /**
     * Ver documentación en {@link Visualizable}.
     */
    @Override
    public void ver(int n_filas, List<String> columnas) throws ColumnaNoPresenteException, FilaFueraDeRangoException{

        // Prevengo el caso de que "n_filas" sea mayor a las filas disponibles
        if (n_filas > this.data.size()){
            throw new FilaFueraDeRangoException("La cantidad de filas solicitadas es mayor a la longitud disponible en la tabla " + this.getNombre());
        }

        // Verifico si las columnas especificadas existen en los headers
        for (String columna : columnas){
            if (!this.headers.contains(columna)){
                throw new ColumnaNoPresenteException("La columna especificada" + columna + "no existe en los encabezados.");
            }
        }

        // Armo una lista y guardo las columnas seleccionadas
        List<List<String>> columnas_seleccionadas = new ArrayList<>();
        for (String columna : columnas) {
            columnas_seleccionadas.add(this.getColumna(columna));
        }

        //Imprimo las columnas seleccionadas con cierto formato
        for (String columna : columnas) {
            System.out.print(String.format("%-20s", columna));
        }
        System.out.println();

        // Imprimo los datos con cierto formato
        for (int i = 0; i < n_filas; i++) {
            for (List<String> columna : columnas_seleccionadas) {
                System.out.print(String.format("%-20s", columna.get(i)));
            }
            System.out.println();
        }          
    }

}
