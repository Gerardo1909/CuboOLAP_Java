package Cubo.tablas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Tabla {

    protected List<String> headers;
    protected List<List<String>> data;
    protected String nombre;

    public Tabla(String nombre ,List<List<String>> data, List<String> headers){
        this.data = data;
        this.headers = headers;
        this.nombre = nombre;
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

}
