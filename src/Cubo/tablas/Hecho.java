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

    // Constructor privado, para uso dentro de los métodos de la clase
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
        headersResultado.addAll(columnas_dimension);

        // Ahora armo un mapa que por clave tiene valores únicos de la columna por la cual se junta
        // y como valor tiene todas las filas que coinciden en dicha columna
        Map<String, List<List<String>>> mapa_hechos = new HashMap<>();

        // le voy agregando entradas al mapa
        for (List<String> fila : this.getData()) {

            // En cada iteración obtengo el valor de la columna por la cual se junta para esa fila
            String valor_on = fila.get(columnas_hecho.indexOf(on));

            // Esta lista de listas de String representa todas las filas con ese mismo valor
            // trás cada iteración se debe hacer más grande hasta terminar
            List<List<String>> filas = mapa_hechos.get(valor_on);

            // Y esta es una verificación para añadir esa listas de filas de no existir aún
            if (filas == null) {
                filas = new ArrayList<>();
                mapa_hechos.put(valor_on, filas);
            }
            filas.add(fila);
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
        return new ArrayList<>(this.hechos);
    }

/**
 * Este método agrupa los datos del objeto Hecho por las columnas especificadas y realiza la operación de agregación especificada en cada grupo.
 * 
 * @param columnas_agrupacion una lista de los nombres de columnas a utilizar para agrupar
 * @param hechos_agrupacion una lista de los nombres de los hechos que se usarán en la agrupación
 * @return un mapa donde las claves son las claves de grupo y los valores son los valores agregados para cada grupo
 */
    public Map<List<String>, List<List<Double>>> groupBy(List<String> columnas_agrupacion, List<String> hechos_agrupacion) {

        // Guardo primero los índices de las columnas por las cuales se agrupa
        List<Integer> indices_agrupacion = new ArrayList<>();
        for (String columna : columnas_agrupacion) {
            indices_agrupacion.add(this.getHeaders().indexOf(columna));
        }

        // Ahora guardo los índices de los hechos
        List<Integer> indices_hechos = new ArrayList<>();
        for (String hecho : hechos_agrupacion) {
            indices_hechos.add(this.getHeaders().indexOf(hecho));
        }

        // Armo un mapa vacío que guardará los resultados
        Map<List<String>, List<List<Double>>> mapa_agrupacion = new HashMap<>();

        // Recorro las filas de la tabla
        for (List<String> fila : this.getData()) {

            // Creo la clave del grupo
            List<String> clave = new ArrayList<>();
            for (int indice_columna : indices_agrupacion) {
                clave.add(fila.get(indice_columna));
            }

            // Verifico si la clave no está en 'mapa_agrupacion'
            if (!mapa_agrupacion.containsKey(clave)) {

                // Armo la lista para los hechos
                List<List<Double>> listasHechos = new ArrayList<>();
                mapa_agrupacion.put(clave, listasHechos);

                // Ahora añado una lista por cada hecho dentro del valor
                for (String hecho : hechos_agrupacion) {
                    listasHechos.add(new ArrayList<>());
                }
            }

            // Ahora recorro los hechos y los agrego a la lista correspondiente
            for (int i = 0; i < indices_hechos.size(); i++) {

                // Obtengo el índice del hecho
                int indice_hecho = indices_hechos.get(i);

                // Lo convierto a número para poder operarlo
                Double hecho_num = Double.valueOf(fila.get(indice_hecho));

                // Lo añado a la lista que corresponde
                mapa_agrupacion.get(clave).get(i).add(hecho_num);

            }

        }

        return mapa_agrupacion;

    }

    public Hecho getHechoCopy() {
        // Método para hacer una copia profunda del objeto actual
        return new Hecho(this.getNombre(), this.getData(), this.getHeaders(), this.getHechos());
    }

}
