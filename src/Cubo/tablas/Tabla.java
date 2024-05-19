package Cubo.tablas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Cubo.lectura_archivos.EstrategiaLecturaArchivo;
import Cubo.utils.Visualizable;

public abstract class Tabla implements Visualizable{

    protected List<String> headers;
    protected List<List<String>> data;
    protected String nombre;


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
   
    // Defino un constructor protegido para el uso interno de las clases hijas
    protected Tabla(String nombre, List<List<String>> data, List<String> headers){
        this.nombre = nombre;
        this.headers = headers;
        this.data = data;
    }

    public static Map<List<String>, List<List<String>>> groupBy(Tabla tabla_operacion, List<String> columnas_agrupacion, List<String> columnas_a_agrupar) {

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
        Map<List<String>, List<List<String>>> mapa_agrupacion = new HashMap<>();

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

    public static void merge(Tabla tabla_izq, Tabla tabla_der, String on) {

        // Obtengo las columnas de ambos archivos
        List<String> columnas_tabla_izq = tabla_izq.getHeaders();
        List<String> columnas_tabla_der = tabla_der.getHeaders();

        // Verifico si "on" existe en ambos archivos
        if (!columnas_tabla_izq.contains(on) ||!columnas_tabla_der.contains(on)) {
            throw new IllegalArgumentException("La columna especificada no existe en ambos archivos.");
        }

        // Genero la lista donde se van a guardar los resultados
        List<List<String>> resultado = new ArrayList<>();

        // Me armo una lista para guardar los headers de la tabla resultante
        List<String> headersResultado = new ArrayList<>(columnas_tabla_izq); // Ver que inicialmente tiene las columnas de la tabla de izquierda
        headersResultado.addAll(columnas_tabla_der);

        // Ahora armo un mapa que por clave tiene valores únicos de la columna por la cual se junta
        // y como valor tiene todas las filas que coinciden en dicha columna
        Map<String, List<List<String>>> mapa_merge = new HashMap<>();

        // Primero itero por la tabla izquierda para añadirle los primeros valores al mapa
        for (List<String> fila_tabla_izq : tabla_izq.getData()) {

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

    public List<String> obtenerValoresUnicos(String columna){

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

    public List<String> getHeaders() {
        return new ArrayList<>(headers);
        }
    
    public List<List<String>> getData() {
        List<List<String>> datosCopy = new ArrayList<>();
            for (List<String> row : data) {
                datosCopy.add(new ArrayList<>(row));
            }
        return datosCopy;
    }

    public List<String> getColumna(String nombre_columna){

        //Primero busco el índice de la columna según 'nombre_columna'
        int indice_columna = this.headers.indexOf(nombre_columna);
        
        // Verifico si el nombre de la columna existe en los encabezados
        if (indice_columna == -1) {
            throw new IllegalArgumentException("La columna especificada no existe en los encabezados.");
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

    public String getNombre(){
        return this.nombre;
    }

    @Override
    public void ver(int n_filas, List<String> columnas){
        // Prevengo el caso de que "n_filas" sea mayor a las filas disponibles
        if (n_filas > this.data.size()) {throw new IllegalArgumentException("n_filas es mayor a la cantidad de filas disponible.");}

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
