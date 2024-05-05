package Cubo.tablas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Cubo.lectura_archivos.EstrategiaLecturaArchivo;
import Cubo.lectura_archivos.LectorCSV;
import Cubo.utils.Visualizable;

public class Hecho extends Tabla implements Visualizable{

   //Constructor privado para que solo se instancie desde los métodos de lectura
    private Hecho(String nombre, List<List<String>> data, List<String> headers){
        super(nombre, data, headers);
    }

    public static Hecho CrearDesdeCSV(String ruta_archivo, String nombre, List<String> hechos) throws IOException{

        // Instancio el lector CSV
        EstrategiaLecturaArchivo estrategia = new LectorCSV();

        // Guardo el archivo leido
        List<List<String>> archivo = estrategia.leerArchivo(ruta_archivo);

        //Guardo los headers
        List<String> headers = archivo.get(0);
        archivo.remove(0);

        //Guardo la data 
        List<List<String>> data = archivo;

        return new Hecho(nombre, data, headers);
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
