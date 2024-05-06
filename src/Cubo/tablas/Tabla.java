package Cubo.tablas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    public String getNombre(){
        return this.nombre;
    }

    @Override
    public void ver(int n_filas, List<String> columnas){
        // Prevengo el caos de que "n_filas" sea mayor a las filas disponibles
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
