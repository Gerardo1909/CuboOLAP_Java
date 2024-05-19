package Cubo.lectura_archivos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase implementa la interfaz EstrategiaLecturaArchivo para leer archivos CSV.
 * Utiliza un BufferedReader para leer el archivo línea por línea y un método personalizado de análisis para dividir cada línea en campos.
 * El método de análisis admite campos encerrados entre comillas dobles y maneja correctamente las comillas escapadas.
 */
public class LectorCSV implements EstrategiaLecturaArchivo{

    /**
     * El carácter utilizado para separar los campos en el archivo CSV.
     */
    private char separador;

    /**
     * Constructor para la clase LectorCSV.
     * @param separador El carácter utilizado para separar los campos en el archivo CSV.
     */
    public LectorCSV(char separador) {
        this.separador = separador;
    }

    /**
     * Lee un archivo CSV y devuelve una lista de listas, donde cada lista interna representa una fila del archivo.
     * @param ruta_archivo La ruta al archivo CSV.
     * @return Una lista de listas, donde cada lista interna representa una fila del archivo.
     * @throws IOException Si se produce algún error al leer el archivo.
     */
    @Override
    public List<List<String>> leerArchivo(String ruta_archivo) throws IOException{
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta_archivo))) {
            String line;

            // Crea una lista para almacenar las filas del archivo
            List<List<String>> data = new ArrayList<>();

            while ((line = reader.readLine())!= null) {

                // Analiza la línea utilizando el método parsearLinea
                List<String> row = parsearLinea(line,this.separador);

                // Agrega la fila a la lista de datos
                data.add(row);
            }

            return data;
        } catch (IOException e) {
            // Lanza excepción en caso de algún error
            throw e;
        }
    }

    /**
     * Analiza una línea de un archivo CSV en una lista de campos.
     * @param line La línea a analizar.
     * @param separador El carácter utilizado para separar los campos en el archivo CSV.
     * @return Una lista de campos de la línea.
     */
    private List<String> parsearLinea(String line, char separador) {

        // Crea una lista para almacenar los campos de la fila
        List<String> fila = new ArrayList<>();

        // Este es el "campo" que se irá rellenando
        String campo = "";

        // Este es un flag que indica si está o no dentro de comillas el iterador
        boolean dentroComillas = false;

        // Recorre cada carácter en la línea de texto
        for (char c : line.toCharArray()) {
            if (c == '"') {
                // Si se encuentra una comilla, invierte el estado de dentroComillas
                dentroComillas =!dentroComillas;
            } else if (c == separador &&!dentroComillas) {
                // Si se encuentra una coma y no estás dentro de comillas, agrega el campo actual a la lista de campos y vacía el campo actual
                fila.add(campo.trim());
                campo = "";
            } else {
                // Si no se cumple ninguna de las condiciones anteriores, agrega el carácter actual al campo actual
                campo += c;
            }
        }

        // Agrega el último campo a la lista de campos
        fila.add(campo.trim());

        // Retorna la lista de campos
        return fila;
    }

}
