package Cubo.lecturaArchivos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Esta clase se encarga de la lectura de archivos en formato CSV.
 * </p>
 * 
 * <p>
 * Implementa la interfaz {@link EstrategiaLecturaArchivo}.
 * </p>
 */
public class LectorCSV implements EstrategiaLecturaArchivo{

    // Atributos de la clase LectorCSV
    private char separador;

    /**
     * <p>
     * <b>Constructor para la clase LectorCSV.</b>
     * </p>
     * 
     * <p>
     * Se encarga de guardar el separador que se usará para la lectura del archivo CSV.
     * </p>
     * 
     * @param separador El carácter utilizado para separar los campos en el archivo CSV.
     */
    public LectorCSV(char separador) {
        this.separador = separador;
    }

    /**
     * <p>
     * Lee un archivo CSV desde la ruta dada y devuelve su contenido como una matriz de datos de tipo cadena.
     * </p>
     * 
     * <p>
     * En este enfoque cada lista dentro de la matriz representa una fila del archivo CSV y cada 
     * elemento en cada lista representa un campo dentro del mismo.
     * </p>
     *
     * @param rutaArchivo La ruta donde se ubica el archivo CSV a leer.
     * 
     * @throws IOException Si ocurre un error de entrada/salida al leer los datos del archivo CSV.
     * 
     * @return Una matriz de datos de tipo cadena que representa la información interna del archivo CSV. 
     */
    @Override
    public List<List<String>> leerArchivo(String rutaArchivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            List<List<String>> contenido = new ArrayList<>();
            String linea;
            while ((linea = reader.readLine()) != null) {
                List<String> fila = parsearLinea(linea, separador);
                contenido.add(fila);
            }
            return contenido;
        }
    }

    /**
     * Se encarga de analizar los datos de una fila del archivo CSV que se está leyendo 
     * para evitar errores inesperados.
     * 
     * @return Una lista que contiene los campos de la fila.
     */
    private List<String> parsearLinea(String line, char separador) {
        List<String> fila = new ArrayList<>();
        String campo = "";
        boolean dentroComillas = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                dentroComillas = !dentroComillas;
            } else if (c == separador && !dentroComillas) {
                fila.add(campo.isEmpty() ? null : campo.trim());
                campo = "";
            } else {
                campo += c;
            }
        }

        // Agrego el último campo a la lista de campos
        fila.add(campo.isEmpty() ? null : campo.trim());
        return fila;
    }

}
