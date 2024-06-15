package Cubo.lecturaArchivos;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Esta interfaz define un método para la lectura de archivos en diferentes formatos.
 * </p>
 * 
 * <p>
 * Está inspirada en el patrón de diseño "Strategy", en donde cada forma de lectura de archivos
 * tiene una clase que se encarga de su implementación.
 * </p>
 */
public interface EstrategiaLecturaArchivo {
    /**
     * <p>
     * Lee un archivo desde la ruta dada y devuelve su contenido como una matriz de datos de tipo cadena.
     * </p>
     * 
     * <p>
     * En este enfoque cada lista dentro de la matriz representa una fila del archivo y cada 
     * elemento en cada lista representa un campo.
     * </p>
     *
     * @param rutaArchivo La ruta donde se ubica el archivo a leer.
     * 
     * @throws IOException Si ocurre un error de entrada/salida al leer los datos del archivo.
     * 
     * @return Una matriz de datos de tipo cadena que representa la información interna del archivo. 
     */ 
    public List<List<String>> leerArchivo(String rutaArchivo) throws IOException;
}
