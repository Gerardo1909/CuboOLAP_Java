package Cubo.lecturaArchivos;

import java.io.IOException;
import java.util.List;

/**
 * Este interfaz define una estrategia para leer archivos.
 * Proporciona la firma de un método para leer un archivo y devolver su contenido como una lista de listas de cadenas.
 */
public interface EstrategiaLecturaArchivo {

    /**
     * Lee un archivo desde la ruta dada y devuelve su contenido como una lista de listas de cadenas.
     * Cada lista interna representa una línea en el archivo, y cada cadena en la lista interna representa un campo.
     *
     * @param rutaArchivo La ruta del archivo que se va a leer.
     * @return Una lista de listas de cadenas que representa el contenido del archivo.
     * @throws IOException Si se produce un error de E/S al leer el archivo.
     */ 
    public List<List<String>> leerArchivo(String rutaArchivo) throws IOException;
}
