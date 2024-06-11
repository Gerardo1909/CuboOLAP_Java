package Cubo.tablasCubo;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Cubo.excepciones.excepcionesDimension.NivelNoPresenteException;
import Cubo.excepciones.excepcionesTabla.ColumnaNoPresenteException;
import Cubo.lecturaArchivos.EstrategiaLecturaArchivo;

/**
 * Esta clase representa una tabla de dimensión en un cubo de datos.
 * Hereda de la clase Tabla y añade atributos y métodos específicos para las dimensiones.
 */
public class Dimension extends Tabla {

    private Map<String, List<String>> niveles;
    private Map<String, Integer> indices_niveles;
    private String primaryKey;


    /**
     * Crea una instancia de la clase Dimension con los parámetros especificados.
     * 
     * @param nombre el nombre de la dimensión
     * @param niveles la lista de niveles de la dimensión
     * @param primaryKey la clave primaria de la dimensión
     * @param estrategia_lectura la estrategia de lectura del archivo de la dimensión
     * @param rutaArchivo la ruta del archivo de la dimensión
     * @return una instancia de la clase Dimension
     * @throws IOException si ocurre un error de lectura del archivo
     * @throws ColumnaNoPresenteException si la clave primaria no está presente en la dimensión
     * @throws NivelNoPresenteException si alguno de los niveles no está presente en la dimensión
     */
    public static Dimension crearTablaDimension(String nombre, List<String> niveles, String primaryKey, EstrategiaLecturaArchivo estrategia_lectura, String rutaArchivo) throws IOException, ColumnaNoPresenteException, NivelNoPresenteException {
       
        // Leo el archivo de la dimensión
        List<List<String>> archivo_dim = estrategia_lectura.leerArchivo(rutaArchivo);

        // Guardo los headers de la dimensión
        List<String> headers_dim = archivo_dim.get(0);
        archivo_dim.remove(0);

        // Verifico que la clave primaria exista en la dimensión
        if (!headers_dim.contains(primaryKey)) {
            throw new ColumnaNoPresenteException("La clave primaria '" + primaryKey + "' no esta presente en la dimension.");
        }

        // Verifico que los niveles estén presentes en la dimensión
        for (String nivel : niveles) {
            if (!headers_dim.contains(nivel)) {
                throw new NivelNoPresenteException("El nivel '" + nivel + "' no esta presente en la dimension.");
            }
        }

        return new Dimension(nombre, niveles, primaryKey, archivo_dim, headers_dim);
    }
 
    /**
     * Constructor de la clase Dimension.
     * Inicializa la tabla de dimensión con información general,
     * lista de niveles y clave primaria.
     *
     * @param nombre El nombre de la dimensión.
     * @param niveles La lista de niveles de la dimensión. Estas deben ser pasadas en orden de jerarquía
     *                siendo el nivel más alto/abstracto el primero en la lista y el más fino el último en la lista
     * @param primaryKey La clave primaria de la dimensión.
     * @param data Los datos de la tabla. Debe ser una lista de listas, donde cada lista interna representa una fila.
     * @param headers Los encabezados de la tabla. Debe ser una lista de cadenas, donde cada cadena representa un nombre de columna.
     * @throws ColumnaNoPresenteException
     */
    private Dimension (String nombre , List<String> niveles, String primaryKey, List<List<String>> data, List<String> headers)throws ColumnaNoPresenteException {

        // Uso el constructor base para la información general
        super(nombre, data, headers);

        // Ahora obtengo los índices de los niveles 
        Map<String, Integer> map_indices = new LinkedHashMap<>();
        for (int i = 0; i < niveles.size(); i++) {
            map_indices.put(niveles.get(i), i);
        }

        // Guardo el nombre de la clave primaria
        this.primaryKey = primaryKey;

        // Guardo la información sobre los niveles
        Map<String, List<String>> map_niveles = new LinkedHashMap<>();
        for (String nivel : niveles) {
            map_niveles.put(nivel, this.obtenerValoresUnicos(nivel));
        }

        // Añado la información a niveles
        this.niveles = map_niveles;  
        this.indices_niveles = map_indices;  
    }

    /**
     * Método para obtener la clave primaria de la dimensión.
     *
     * @return La clave primaria de la dimensión.
     */
    public String getPrimaryKey(){
        return this.primaryKey;
    }

    /**
     * Método para obtener el mapa de niveles de la dimensión.
     *
     * @return El mapa de niveles de la dimensión.
     */
    public Map<String, List<String>> getNiveles() {
        return this.niveles;
    }

    /**
     * Método para obtener el mapa de indices de los niveles de la dimensión.
     * El mapa contiene los nombres de los niveles como claves y sus índices correspondientes como valores.
     * Los índices representan el orden jerárquico de los niveles, con 0 representando el nivel más alto/abstracto.
     *
     * @return Un mapa que contiene los nombres de los niveles como claves y sus índices correspondientes como valores.
     */
    public Map<String, Integer> getIndicesNiveles(){
        return this.indices_niveles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getNombre()).append(" (Niveles : ");
        
        int size = niveles.size();
        int count = 0;
        
        for (String nivel : niveles.keySet()) {
            sb.append(nivel);
            if (count < size - 1) {
                sb.append(" > ");
            }
            count++;
        }
        
        sb.append(")");
        return sb.toString();
    }
}
