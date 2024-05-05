package Cubo.tablas;

import java.io.IOException;
import java.util.List;
import Cubo.lectura_archivos.EstrategiaLecturaArchivo;
import Cubo.lectura_archivos.LectorCSV;

public class Hecho extends Tabla {

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

}
