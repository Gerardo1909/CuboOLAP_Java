package Cubo.tablas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import Cubo.lectura_archivos.EstrategiaLecturaArchivo;

public class Hecho extends Tabla {

    private List<String> hechos;

    public Hecho(String nombre, List<String> hechos ,EstrategiaLecturaArchivo estrategia_lectura, String ruta_archivo) throws IOException{
        
        // Uso el constructor base para la información general
        super(nombre, estrategia_lectura, ruta_archivo);
 
        //Guardo la información de los hechos
        this.hechos = hechos;
    }

    private Hecho(String nombre, List<List<String>> data, List<String> headers, List<String> hechos){
        super(nombre, data, headers);
        this.hechos = hechos;
    }

/**
 * Este método fusiona el objeto Hecho actual con otro objeto Dimension basándose en una columna común.
 *  
 * @param dimension el objeto Dimension para fusionar con
 * @param on el nombre de la columna para fusionar en
 * @return un nuevo objeto Hecho que es el resultado de la fusión
 * @throws IllegalArgumentException si la columna especificada no existe en ambos objetos Hecho y Dimension
 */
    public Hecho mergeDimension(Dimension dimension, String on) {

        // Obtengo las columnas de ambos archivos
        List<String> columnas_hecho = this.getHeaders();
        List<String> columnas_dimension = dimension.getHeaders();

        // Verifico si "on" existe en ambos archivos
        if (!columnas_hecho.contains(on) ||!columnas_dimension.contains(on)) {
            throw new IllegalArgumentException("La columna especificada no existe en ambos archivos.");
        }

        // Genero la lista donde se van a guardar los resultados
        List<List<String>> resultado = new ArrayList<>();

        // Me armo una lista para guardar los headers de la tabla resultante
        List<String> headersResultado = new ArrayList<>(columnas_hecho); // Ver que inicialmente tiene las columnas de la tabla de hechos
        for (String columna : columnas_dimension) {
            headersResultado.add(columna);
        }

        // Ahora armo un mapa que por clave tiene valores únicos de la columna por la cual se junta
        // y como valor tiene todas las filas que coinciden en dicha columna
        Map<String, List<List<String>>> mapa_hechos = new HashMap<>();

        // le voy agregando entradas al mapa
        for (List<String> fila : this.getData()) {
            String valor_on = fila.get(columnas_hecho.indexOf(on));
            mapa_hechos.computeIfAbsent(valor_on, k -> new ArrayList<>()).add(fila);
        }

        // Itero sobre las filas de la tabla de dimensión 
        for (List<String> fila_dimension : dimension.getData()) {
            String valor_on = fila_dimension.get(columnas_dimension.indexOf(on));

            // Y aquí hago la validación para juntar las tablas
            if (mapa_hechos.containsKey(valor_on)) {

                // Tomo todas las filas con la misma clave
                List<List<String>> filas_hechos = mapa_hechos.get(valor_on);

                // Recorro cada una
                for (List<String> fila_hecho : filas_hechos) {

                    // Armo una lista que representa la fila resultante de la unión
                    List<String> filaResultado = new ArrayList<>(fila_hecho);
                    filaResultado.addAll(fila_dimension);

                    // Y lo añado a la tabla resultante
                    resultado.add(filaResultado);
                }
            }
        }

        // Retorno un nuevo Hecho con el constructor privado que definí anteriormente
        return new Hecho(this.getNombre(), resultado, headersResultado, this.getHechos());
}

    public List<String> getHechos(){
        return List.copyOf(this.hechos);
    }

    public Map<List<String>, Integer> groupBy(List<String> columnas_agrupacion){

        // Guardo primero los indices de las columnas por la cuales se agrupa
        List<Integer> indices_agrupacion = new ArrayList<>();
        for (String columna : columnas_agrupacion) {
            indices_agrupacion.add(this.getHeaders().indexOf(columna));
        }

        // Armo un mapa vacio que guardará los resultados
        Map<List<String>, List<Integer>> mapa_agrupacion = new HashMap<>();

        // Recorro las filas de la tabla
        for (List<String> fila : this.getData()) {

            // Creo la clave del grupo
            List<String> clave = new ArrayList<>();
            for (int indice_columna : indices_agrupacion) {
                clave.add(fila.get(indice_columna));
            }
            
            // Verifico si la clave no está en el mapa de grupos y agregarla si es necesario
            if (!mapa_agrupacion.containsKey(clave)) {
                mapa_agrupacion.put(clave, new ArrayList<>());
            }

            // Obtengo el índice del hecho de interés
            int indice_hecho = this.getHeaders().indexOf("cantidad");

            // Lo añado a la entrada correspondiente en el mapa
            mapa_agrupacion.get(clave).add(Integer.valueOf(fila.get(indice_hecho)));
        }

        // Ahora armo el mapa que tendrá el resultado con la operación de agregación suma aplicada
        Map<List<String>, Integer> mapa_resultante = new HashMap<>();
        for (Map.Entry<List<String>, List<Integer>> entrada : mapa_agrupacion.entrySet()) {
            mapa_resultante.put(entrada.getKey(), entrada.getValue().stream().mapToInt(Integer::intValue).sum());
        }


        return mapa_resultante;

    }


}
