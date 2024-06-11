package Cubo.ImplementacionCubo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Cubo.tablasCubo.Dimension;
import Cubo.tablasCubo.Hecho;
import Cubo.tablasCubo.Tabla;

public class CuerpoCubo extends Tabla{

    private List<String> hechosCubo;

    /**
     * Método estático para configurar el cuerpo del cubo OLAP.
     *
     * @param tablaHechos Tabla de hechos.
     * @param tablasDimensiones Lista de tablas de dimensiones.
     * @return CuerpoCubo configurado.
     */
    public static CuerpoCubo configurarCubo(Hecho tablaHechos, List<Dimension> tablasDimensiones){

        // Inicializo el cuerpo del cubo obteniendo la información de la tabla de hechos
        CuerpoCubo cuerpoCubo = new CuerpoCubo(tablaHechos.getData(), tablaHechos.getHeaders(), tablaHechos.getHechos());

        // Fusiono la tabla de hechos con cada una de las tablas de dimensiones
        for (Dimension dimension : tablasDimensiones) {
            cuerpoCubo.merge(cuerpoCubo, dimension, dimension.getPrimaryKey());
        }
        
        // Elimino las columnas primaryKey de las dimensiones de 'tablaOperacion'
        // ya que una vez toda la información junta, estas no sirven más
        for (Dimension dimension : tablasDimensiones){
            cuerpoCubo.eliminarColumna(dimension.getPrimaryKey());
        }

        return cuerpoCubo;
    }

    protected CuerpoCubo(List<List<String>> data, List<String> headers, List<String> hechosCubo) {
        super("Estructura interna del cubo OLAP",data, headers);
        this.hechosCubo = hechosCubo;
    }


    // Métodos de ayuda para la clase

    /**
     * Método para fusionar dos tablas según una columna específica.
     *
     * @param tabla_izq Tabla izquierda.
     * @param tabla_der Tabla derecha.
     * @param on Columna por la que se va a fusionar.
     */
    private void merge(Tabla tabla_izq, Tabla tabla_der, String on){

        // Obtengo las columnas de ambos archivos
        List<String> columnas_tabla_izq = tabla_izq.getHeaders();
        List<String> columnas_tabla_der = tabla_der.getHeaders();

        // Genero la lista donde se van a guardar los resultados
        List<List<String>> resultado = new ArrayList<>();

        // Me armo una lista para guardar los headers de la tabla resultante
        List<String> headersResultado = new ArrayList<>(columnas_tabla_izq); // Ver que inicialmente tiene las columnas de la tabla de izquierda
        headersResultado.addAll(columnas_tabla_der);

        // Ahora armo un mapa que por clave tiene valores únicos de la columna por la cual se junta
        // y como valor tiene todas las filas que coinciden en dicha columna
        Map<String, List<List<String>>> mapa_merge = new LinkedHashMap<>();

        // Primero itero por la tabla izquierda para añadirle los primeros valores al mapa
        for (List<String> fila_tabla_izq : tabla_izq.getData()) {

            // Evito una posible 'NullPointerException'
            if (fila_tabla_izq == null) continue;

            // En cada iteración obtengo el valor de la columna por la cual se junta para esa fila
            String valor_on = fila_tabla_izq.get(columnas_tabla_izq.indexOf(on));

            // Esta lista de listas de String representa todas las filas con ese mismo valor
            // trás cada iteración se debe hacer más grande hasta terminar
            List<List<String>> filas_coincidentes = mapa_merge.get(valor_on);

            // Y esta es una verificación para añadir esa listas de filas de no existir aún
            if (filas_coincidentes == null) {
                filas_coincidentes = new ArrayList<>();
                mapa_merge.put(valor_on, filas_coincidentes);
            }
            filas_coincidentes.add(fila_tabla_izq);
        }

        // Itero sobre las filas de la tabla derecha para terminar de completar las filas del mapa_merge
        for (List<String> fila_tabla_der : tabla_der.getData()) {

            // Evito una posible 'NullPointerException'
            if (fila_tabla_der == null) continue;

            // En cada iteración obtengo el valor de la columna por la cual se junta para esa fila            
            String valor_on = fila_tabla_der.get(columnas_tabla_der.indexOf(on));

            // Y aquí hago la validación para juntar las tablas
            if (mapa_merge.containsKey(valor_on)) {

                // Tomo todas las filas con la misma clave
                List<List<String>> filas_coincidentes = mapa_merge.get(valor_on);

                // Recorro cada una
                for (List<String> fila_coincidente : filas_coincidentes) {

                    // Armo una lista que representa la fila resultante de la unión
                    List<String> filaResultado = new ArrayList<>(fila_coincidente);
                    filaResultado.addAll(fila_tabla_der);

                    // Y lo añado a la tabla resultante
                    resultado.add(filaResultado);
                }
            }
        }

        // Modifico los atributos 'data' y 'headers' del cuerpo del cubo
        this.data = resultado;
        this.headers = headersResultado;
}

    /**
     * Elimina todas las columnas de la tabla que tengan el nombre especificado.
     * 
     * Este método busca todas las columnas cuyo encabezado coincida con el nombre proporcionado
     * y las elimina tanto de la lista de encabezados como de cada fila de datos.
     *
     * @param nombre_columna El nombre de la columna que se va a eliminar.
     */
    private void eliminarColumna(String nombreColumna){

        // Encuentro todos los índices de la columna según 'nombre_columna'
        List<Integer> indices_columnas = new ArrayList<>();
        for (int i = 0; i < this.headers.size(); i++) {
            if (this.headers.get(i).equals(nombreColumna)) {
                indices_columnas.add(i);
            }
        }
        

        // Elimino los encabezados de las columnas en orden inverso para evitar problemas de desplazamiento
        Collections.sort(indices_columnas, Collections.reverseOrder());
        for (int indice_columna : indices_columnas) {
            this.headers.remove(indice_columna);
        }
        
        // Elimino los datos de las columnas en cada fila
        for (List<String> row : this.data) {
            for (int indice_columna : indices_columnas) {
                if (row.size() > indice_columna) {
                    row.remove(indice_columna);
                }
            }
        }
    }


    // Getters de la clase

    /**
     * Obtiene los hechos del cubo.
     *
     * @return Lista de hechos del cubo.
     */
    public List<String> getHechosCubo() {
        return new ArrayList<>(hechosCubo);
    }    

    public CuerpoCubo getCuerpoCopy(){
        return new CuerpoCubo(this.getData(), this.getHeaders(), this.getHechosCubo());
    }

}
