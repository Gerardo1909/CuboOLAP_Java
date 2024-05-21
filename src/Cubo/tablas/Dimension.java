package Cubo.tablas;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Cubo.excepciones.excepciones_dimension.NivelNoPresenteException;
import Cubo.excepciones.excepciones_tabla.ColumnaNoPresenteException;
import Cubo.lectura_archivos.EstrategiaLecturaArchivo;

/**
 * Esta clase representa una tabla de dimensión en un cubo de datos.
 * Hereda de la clase Tabla y añade atributos y métodos específicos para las dimensiones.
 */
public class Dimension extends Tabla {

    /**
     * Un mapa para almacenar los valores únicos de cada nivel en la dimensión.
     * La clave es el nombre del nivel, y el valor es una lista de valores únicos.
     */
    private Map<String, List<String>> niveles;

    /**
     * La clave primaria de la tabla de dimensión.
     */
    private String primaryKey;

    /**
     * Constructor de la clase Dimension.
     * Inicializa la tabla de dimensión con información general,
     * la lista de niveles y la clave primaria.
     *
     * @param nombre El nombre de la dimensión.
     * @param niveles La lista de niveles de la dimensión.
     * @param primaryKey La clave primaria de la dimensión.
     * @param estrategia_lectura Estrategia usada para leer el archivo.
     * @param ruta_archivo La ruta del archivo de datos.
     * @throws IOException Si hay un error de E/S al leer el archivo.
     * @throws ColumnaNoPresenteException Si la clave primaria pasada como argumento no existe en la dimensión
     * @throws NivelNoPresenteException Si uno de los niveles pasado en la lista de niveles no existe en la dimensión
     */
    public Dimension(String nombre, List<String> niveles, String primaryKey,EstrategiaLecturaArchivo estrategia_lectura, String ruta_archivo) throws IOException, ColumnaNoPresenteException, NivelNoPresenteException{
        
        // Uso el constructor base para la información general
        super(nombre, estrategia_lectura, ruta_archivo);

        // Verifico que la clave primaria exista en la dimensión
        if (!this.getHeaders().contains(primaryKey)) {
            throw new ColumnaNoPresenteException("La clave primaria '" + primaryKey + "' no existe en la tabla.");
        }

        // Verifico que los niveles estén presentes en la dimensión
        for (String nivel : niveles) {
            if (!this.getHeaders().contains(nivel)) {
                throw new NivelNoPresenteException("El nivel '" + nivel + "' no existe en la tabla.");
            }
        }

        // Guardo el nombre de la clave primaria
        this.primaryKey = primaryKey;

        // Guardo la información sobre los niveles
        Map<String, List<String>> map_niveles = new HashMap<>();
        for (String nivel : niveles) {
            map_niveles.put(nivel, this.obtenerValoresUnicos(nivel));
        }

        // Añado la información a niveles
        this.niveles = map_niveles;   
    }
 
    /**
     * Constructor de la clase Dimension.
     * Inicializa la tabla de dimensión con información general,
     * lista de niveles y clave primaria.
     *
     * @param nombre El nombre de la dimensión.
     * @param niveles La lista de niveles en la dimensión.
     * @param primaryKey La clave primaria de la dimensión.
     * @param data Los datos de la tabla. Debe ser una lista de listas, donde cada lista interna representa una fila.
     * @param headers Los encabezados de la tabla. Debe ser una lista de cadenas, donde cada cadena representa un nombre de columna.
     * @throws ColumnaNoPresenteException Si la clave primaria pasada como argumento no existe en la dimensión.
     * @throws NivelNoPresenteException Si alguno de los niveles pasados en la lista de niveles no existe en la dimensión.
     */
    public Dimension (String nombre , List<String> niveles, String primaryKey, List<List<String>> data, List<String> headers)throws ColumnaNoPresenteException, NivelNoPresenteException {

        // Uso el constructor base para la información general
        super(nombre, data, headers);

        // Verifico que la clave primaria exista en la dimensión
        if (!this.getHeaders().contains(primaryKey)) {
            throw new ColumnaNoPresenteException("La clave primaria '" + primaryKey + "' no existe en la tabla.");
        }

        // Verifico que los niveles estén presentes en la dimensión
        for (String nivel : niveles) {
            if (!this.getHeaders().contains(nivel)) {
                throw new NivelNoPresenteException("El nivel '" + nivel + "' no existe en la tabla.");
            }
        }

        // Guardo el nombre de la clave primaria
        this.primaryKey = primaryKey;

        // Guardo la información sobre los niveles
        Map<String, List<String>> map_niveles = new HashMap<>();
        for (String nivel : niveles) {
            map_niveles.put(nivel, this.obtenerValoresUnicos(nivel));
        }

        // Añado la información a niveles
        this.niveles = map_niveles;   
    }

    /**
     * Método para mostrar los valores únicos de un nivel específico.
     *
     * @param nivel El nombre del nivel.
     */
    public void mostrarNivel(String nivel) {
        // Verifico si el nivel existe en el mapa
        if (niveles.containsKey(nivel)) {
            System.out.print(nivel + " : [");
            // Obtengo los valores asociados al nivel
            List<String> valores = niveles.get(nivel);
            // Muestro los valores
            for (int i = 0; i < valores.size(); i++) {
                System.out.print(valores.get(i));
                if (i < valores.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        } else {
            System.out.println("El nivel '" + nivel + "' no existe en la dimensión.");
        }
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
}
