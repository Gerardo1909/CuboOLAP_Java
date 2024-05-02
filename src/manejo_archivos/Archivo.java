package manejo_archivos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Archivo {

    private final List<String> headers;
    private final List<List<String>> datos;

public Archivo(String ruta, boolean has_headers) throws IOException{

        //Primero armo una lista temporal para guardar los datos
        List<List<String>> datos_temp = Lector.leerCSV(ruta);

        //Verifico el caso de que haya o no headers
        if (has_headers) {
            this.headers = datos_temp.get(0);
            datos_temp.remove(0);
        }else{
            this.headers = null;
        }

        //Luego de la verificación es que guardo los datos
        this.datos = datos_temp;

    }

public List<String> getHeaders() {
    if (headers == null) {
        return null;
    }
    return new ArrayList<>(headers);
    }

public List<List<String>> getDatos() {
    List<List<String>> datosCopy = new ArrayList<>();
        for (List<String> row : datos) {
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
    List<List<String> > datos_copy = this.getDatos();

    // Creo la lista que corresponde a la columna
    List<String>  columna = new ArrayList<>();

    // Y agrego el resto de datos
    for (int i = 0; i < datos_copy.size(); i++) {
        columna.add(datos_copy.get(i).get(indice_columna));
    }

    return columna;
}

public void mostrarDatos(int n_filas, List<String> columnas) {

    // Prevengo el caos de que "n_filas" sea mayor a las filas disponibles
    if (n_filas > this.datos.size()) {throw new IllegalArgumentException("n_filas es mayor a la cantidad de filas disponible.");}

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

}
