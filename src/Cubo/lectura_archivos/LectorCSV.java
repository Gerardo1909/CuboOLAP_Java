package Cubo.lectura_archivos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorCSV implements EstrategiaLecturaArchivo{
    @Override
    public List<List<String>> leerArchivo(String ruta_archivo) throws IOException{
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta_archivo))) {
        String line;

        // Crea una lista para almacenar las filas del archivo
        List<List<String>> data = new ArrayList<>();

        while ((line = reader.readLine())!= null) {

            // Analiza la línea utilizando el método parsearLinea
            List<String> row = parsearLinea(line);

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
     * Esta función toma una cadena de entrada y devuelve una lista de cadenas, donde cada elemento de la lista representa un campo en la cadena de entrada. La función utiliza una coma como separador entre los campos. Si un campo contiene una coma, se encuentra entre comillas dobles. La función elimina cualquier espacio en blanco inicial o final de cada campo.
     * 
     * @param line la cadena de entrada
     * @return una lista de cadenas, donde cada elemento representa un campo en la cadena de entrada
     */
    private List<String> parsearLinea(String line) {

        // Crea una lista para almacenar los campos de la fila
        List<String> fila = new ArrayList<>();

        // Y este es el "campo" que se irá rellenando
        String campo = "";

        // Este es un flag que indica si está o no dentro de comillas el iterador
        boolean dentroComillas = false;

        // Recorre cada carácter en la línea de texto
        for (char c : line.toCharArray()) {
            if (c == '"') {
                // Si se encuentra una comilla, invierte el estado de dentroComillas
                dentroComillas =!dentroComillas;
            } else if (c == ',' &&!dentroComillas) {
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
