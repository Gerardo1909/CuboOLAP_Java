package Cubo.tablasCubo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Cubo.excepciones.excepcionesDimension.ClaveNoPresenteException;
import Cubo.excepciones.excepcionesDimension.NivelNoPresenteException;
import Cubo.excepciones.excepcionesTabla.ColumnaNoPresenteException;
import Cubo.lecturaArchivos.EstrategiaLecturaArchivo;

/**
 * <p>
 * Esta clase representa una tabla de dimensión.
 * </p>
 * 
 * <p>
 * Esta clase extiende de la clase {@link Tabla}.
 * </p>
 */
public class Dimension extends Tabla {

    // Atributos de la clase Hecho
    private Map<String, List<String>> niveles;
    private Map<String, Integer> indicesNiveles;
    private String primaryKey;


    // Métodos para la creación de instancias de la clase Dimension

    /**
     * </p>
     * Método para crear una <b>tabla de dimensión</b>, la cual es instancia de la clase Dimension.
     * </p>
     * 
     * </p>
     * Crea una nueva tabla de dimension con un nombre, niveles asociados y toda su información interna.
     * </p>
     * 
     * @param nombre El nombre de la tabla de dimensión.
     * @param niveles Una lista que representa los niveles de la dimensión presentes en la tabla. 
     *                Los niveles deben ser pasados en orden de jerarquía siendo el nivel más alto/abstracto 
     *                el primero en la lista y el más fino el último en la lista.
     * @param primaryKey la clave primaria de la tabla de dimensión.
     * @param estrategiaLectura La estrategia de lectura de archivos a utilizar.
     * @param rutaArchivo La ruta donde se ubica el archivo a leer.
     * 
     * @throws IOException Si ocurre un error de entrada/salida al leer los datos de la tabla.
     * @throws ClaveNoPresenteException Si la clave primaria indicada no está presente en la tabla de dimensión.
     * @throws NivelNoPresenteException Si alguno de los niveles especificados no está presente en la tabla de dimensión.
     * 
     * @return Una nueva instancia de la clase Dimension, la cual representa una tabla de dimensión.
     */
    public static Dimension crearTablaDimension(String nombre, List<String> niveles, String primaryKey, 
                                                EstrategiaLecturaArchivo estrategiaLectura, String rutaArchivo) throws IOException{
       
        // Guardo la información de la tabla de dimensión
        List<List<String>> archivo_dim = estrategiaLectura.leerArchivo(rutaArchivo);
        List<String> headers_dim = archivo_dim.get(0);
        archivo_dim.remove(0);

        // Verifico que la clave primaria exista en la dimensión
        if (!headers_dim.contains(primaryKey)) {
            throw new ClaveNoPresenteException("La clave primaria '" + primaryKey + "' no esta presente en la dimension.");
        }

        // Verifico que los niveles estén presentes en la dimensión
        for (String nivel : niveles) {
            if (!headers_dim.contains(nivel)) {
                throw new NivelNoPresenteException("El nivel '" + nivel + "' no esta presente en la dimension.");
            }
        }

        // Retorno una nueva tabla de dimensión
        return new Dimension(nombre, niveles, primaryKey, archivo_dim, headers_dim);
    }
 
    /**
     * <p>
     * <b>Constructor para la clase Dimension.</b>
     * </p>
     * 
     * <p>
     * Se encarga de inicializar una nueva tabla de dimensión con un nombre, una matriz de información interna,
     * encabezados que representan las columnas y los niveles y clave primaria presentes en la misma.
     * </p>
     *
     * @param nombre El nombre de la tabla de dimensión.
     * @param niveles Una lista que representa los niveles presentes en la dimensión. 
     * @param primaryKey La clave primaria de la dimensión.
     * @param datosTabla Una matriz que representa la información interna de la tabla.
     * @param headers Una lista que representa los encabezados de la tabla.
     */
    private Dimension (String nombre , List<String> niveles, String primaryKey, List<List<String>> datosTabla, List<String> headers) {

        // Uso el constructor base para la información general
        super(nombre, datosTabla, headers);

        // Obtengo los índices de jerarquía de los niveles 
        Map<String, Integer> map_indices = new LinkedHashMap<>();
        for (int i = 0; i < niveles.size(); i++) {
            map_indices.put(niveles.get(i), i);
        }
        // Guardo la información sobre los niveles
        Map<String, List<String>> map_niveles = new LinkedHashMap<>();
        for (String nivel : niveles) {
            map_niveles.put(nivel, this.obtenerValoresUnicos(nivel));
        }

        // Guardo la información propia de la tabla de dimensión
        this.primaryKey = primaryKey;
        this.niveles = map_niveles;  
        this.indicesNiveles = map_indices;  
    }

    /**
     * Se encarga de obtener los valores únicos de una columna de la tabla.
     *
     * @param columna La columna de la que se van a obtener los valores únicos.
     * @return Una columna que contiene solo los valores únicos de la tabla.
     */
    private List<String> obtenerValoresUnicos(String columna){

        List<String> columna_seleccionada = this.getColumna(columna);
        Set<String> valores_unicos = new HashSet<>();
        for (String valor : columna_seleccionada) {
            if (valor != null) {
                valores_unicos.add(valor);
            }
        }
        return new ArrayList<>(valores_unicos);
    }
    

    // Métodos de la clase Dimension

    /**
     * @return La clave primaria de esta tabla de dimensión.
     */
    public String getPrimaryKey(){
        return this.primaryKey;
    }

    /**
     * @return Una copia del mapa que contiene como clave el nombre del nivel de esta 
     *         dimensión y como valor la lista de valores únicos en ese nivel.
     */
    public Map<String, List<String>> getNiveles() {
        return new HashMap<>(this.niveles);
    }

    /**
     * @return Una copia del mapa que contiene como clave el nombre del nivel
     *         de esta dimensión y como valor un índice que representa su nivel 
     *         de jerarquía, siendo el 0 el nivel más alto en la misma.
     */
    public Map<String, Integer> getIndicesNiveles(){
        return new HashMap<>(this.indicesNiveles);
    }

    /**
     * <p>
     * Devuelve una representación en forma de cadena de esta tabla de dimensión.
     * </p>
     * 
     * <p>
     * La representación incluye el nombre de la tabla y los niveles de la misma ordenados
     * según nivel de jerarquía.
     * </p>
     *
     * @return La representación en forma de cadena de esta tabla.
     */
    @Override
    public String toString() {

        String informacionTablaDimension = "TABLA DE DIMENSION <" + this.getNombre() + ">";
        informacionTablaDimension += " / (NIVELES: ";
        int contador = 0;
        for (String nivel : this.niveles.keySet()) {
            informacionTablaDimension += nivel;
            contador++;
            if (contador < this.niveles.size()) {
                informacionTablaDimension += ", ";
            }
        }
        informacionTablaDimension += ")";
        return informacionTablaDimension;
    }
}
