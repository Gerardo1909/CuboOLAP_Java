package Cubo.tablas;

import java.util.List;
import java.io.IOException;
import Cubo.lectura_archivos.EstrategiaLecturaArchivo;

public class Hecho extends Tabla {

    public Hecho(String nombre, List<String> hechos ,EstrategiaLecturaArchivo estrategia_lectura, String ruta_archivo) throws IOException{
        
        // Uso el constructor base para la información general
        super(nombre, estrategia_lectura, ruta_archivo);
 
    }


}
