package manejo_archivos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Joiner {

/**
 * Esta función realiza un inner join entre dos archivos basándose en una columna específica.
 * La función devuelve una lista de listas, donde cada sub-lista representa una fila de la tabla resultante.
 * La función lanza una excepción si la columna especificada no existe en ambos archivos de entrada.
 *
 * @param archivo1 el primer archivo de entrada
 * @param archivo2 el segundo archivo de entrada
 * @param columna_juntadora el nombre de la columna para unir los dos archivos
 * @return una lista de listas, donde cada sub-lista representa una fila de la tabla resultante
 */
public static List<List<String>> innerJoin(Archivo archivo1, Archivo archivo2, String columna_juntadora) {

    // Obtengo las columnas de ambos archivos
    List<String> columnasArchivo1 = archivo1.getHeaders();
    List<String> columnasArchivo2 = archivo2.getHeaders();

    // Obtengo los datos de ambos archivos
    List<List<String>> datosArchivo1 = archivo1.getDatos();
    List<List<String>> datosArchivo2 = archivo2.getDatos();

    // Verifico si "columna_juntadora" existe en ambos archivos
    if (!columnasArchivo1.contains(columna_juntadora) ||!columnasArchivo2.contains(columna_juntadora)) {
        throw new IllegalArgumentException("La columna especificada no existe en ambos archivos.");
    }

    // Genero la lista donde se van a guardar los resultados
    List<List<String>> resultado = new ArrayList<>();

    // Creo un mapa para almacenar las filas de archivo1 según el valor de columna_juntadora
    Map<String, List<String>> mapaArchivo1 = new HashMap<>();
    for (List<String> fila : datosArchivo1) {
        String valorColumnaJuntadora = fila.get(columnasArchivo1.indexOf(columna_juntadora));
        mapaArchivo1.putIfAbsent(valorColumnaJuntadora, fila);
    }

    // Itero sobre las filas de archivo2 y las combino con las correspondientes filas de archivo1
    for (List<String> fila : datosArchivo2) {
        String valorColumnaJuntadora = fila.get(columnasArchivo2.indexOf(columna_juntadora));
        if (mapaArchivo1.containsKey(valorColumnaJuntadora)) {
            List<String> filaArchivo1 = mapaArchivo1.get(valorColumnaJuntadora);
            List<String> filaResultado = new ArrayList<>(filaArchivo1);
            filaResultado.addAll(fila);
            resultado.add(filaResultado);
        }
    }

    return resultado;
}

}