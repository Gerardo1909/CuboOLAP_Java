package manejo_archivos;

import java.util.ArrayList;
import java.util.List;

public final class Joiner {


    public static List<List<String>> innerJoin(Archivo archivo1, Archivo archivo2, String columna_juntadora) {

        // Obtengo las columnas de ambos archivos
        List<String> columnasArchivo1 = archivo1.getHeaders();
        List<String> columnasArchivo2 = archivo2.getHeaders();

        // Obtengo los datos de ambos archivos
        List<List<String>> datosArchivo1 = archivo1.getDatos();
        List<List<String>> datosArchivo2 = archivo2.getDatos();

        // Verifico si "columna_juntadora" existe en ambos archivos
        if (!columnasArchivo1.contains(columna_juntadora) || !columnasArchivo2.contains(columna_juntadora)) {
            throw new IllegalArgumentException("La columna especificada no existe en ambos archivos.");
        }

        // Verifico que ambos archivos son del mismo tamaño
        if (datosArchivo1.size() != datosArchivo2.size()){
            throw new IllegalArgumentException("Los archivos deben ser del mismo tamaño.");
        }

        // Creo una lista de listas de Strings para guardar los datos resultantes
        List<List<String>> resultado = new ArrayList<>();
        
        // Obtengo los índices de la columna_juntadora en ambos archivos
        int indiceJuntadorArchivo1 = columnasArchivo1.indexOf(columna_juntadora);
        int indiceJuntadorArchivo2 = columnasArchivo2.indexOf(columna_juntadora);

        for (int i=0 ; i < datosArchivo1.size(); i++){

            // Obtengo las filas de ambos archivos
            List<String> fila1 = datosArchivo1.get(i);
            List<String> fila2 = datosArchivo2.get(i);

            // Verifico la igualdad de la columna juntadora
            if (fila1.get(indiceJuntadorArchivo1).equals(fila2.get(indiceJuntadorArchivo2))){

                // Remuevo la columna juntadora de una de las filas para evitar duplicados
                fila2.remove(indiceJuntadorArchivo2);

                //Hago la nueva lista que representa la fila en el resultado y le añado ambas filas
                List<String> resultado_fila = new ArrayList<>();
                resultado_fila.addAll(fila1);
                resultado_fila.addAll(fila1);
                resultado.add(resultado_fila);
            }
        }

        return resultado;
    }
}