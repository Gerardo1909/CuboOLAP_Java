package Cubo.tablasCubo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import Cubo.excepciones.excepcionesDimension.ClaveNoPresenteException;
import Cubo.excepciones.excepcionesHechos.HechoNoPresenteException;
import Cubo.lecturaArchivos.EstrategiaLecturaArchivo;

/**
 * <p>
 * Esta clase representa una tabla de hechos.
 * </p>
 * 
 * <p>
 * Esta clase extiende de la clase {@link Tabla}.
 * </p>
 */
public class Hecho extends Tabla {
    
    // Atributos de la clase Hecho
    private final List<String> hechos;
    private final Map<Dimension, String> clavesForaneasDims;


    // Métodos para la creación de instancias de la clase Hecho

    /**
     * </p>
     * Método para crear una <b>tabla de hechos</b>, la cual es instancia de la clase Hecho.
     * </p>
     * 
     * </p>
     * Crea una nueva tabla de hechos con un nombre y toda su información interna.
     * </p>
     * 
     * @param nombre El nombre de la tabla de hechos.
     * @param hechos Una lista que representa los hechos presentes en la tabla.
     * @param clavesForaneasDims Un mapa que representa las claves foráneas de las dimensiones presentes en la tabla.
     * @param estrategiaLectura La estrategia de lectura de archivos a utilizar
     * @param rutaArchivo La ruta donde se ubica el archivo a leer.
     * 
     * @throws IOException Si ocurre un error de entrada/salida al leer los datos de la tabla.
     * @throws HechoNoPresenteException Si alguno de los hechos especificados no está presente en la tabla de hechos.
     * @throws ClaveNoPresenteException Si alguna de las claves foráneas de las dimensiones no está presente en la tabla de hechos.
     * 
     * @return Una nueva instancia de la clase Hecho, la cual representa una tabla de hechos.
     */
    public static Hecho crearTablaHechos(String nombre, List<String> hechos, Map<Dimension, String> clavesForaneasDims, 
                                         EstrategiaLecturaArchivo estrategiaLectura, String rutaArchivo) throws IOException{
        
        // Guardo la información de la tabla de hechos
        List<List<String>> archivo_hecho = estrategiaLectura.leerArchivo(rutaArchivo);
        List<String> headers_hecho = archivo_hecho.get(0);
        archivo_hecho.remove(0);

        // Verifico que los hechos estén presentes en la tabla 
        for (String hecho : hechos) {
            if (!headers_hecho.contains(hecho)) {
                throw new HechoNoPresenteException("El hecho '" + hecho + "' no esta presente en la tabla de hechos.");
            }
        }

        // Verifico que las claves foraneas estén presentes en la tabla
        for (Map.Entry<Dimension, String> entry : clavesForaneasDims.entrySet()) {
            Dimension dim = entry.getKey();
            String claveForanea = entry.getValue();
            if (!headers_hecho.contains(claveForanea)) {
                throw new ClaveNoPresenteException("La clave foranea '" + claveForanea + "' de la dimension '" + dim.getNombre() + "' no esta presente en la tabla de hechos.");
            }
        }

        // Retorno una nueva tabla de hechos
        return new Hecho(nombre, archivo_hecho, headers_hecho, hechos, clavesForaneasDims);
    }

    /**
     * <p>
     * <b>Constructor para la clase Hecho.</b>
     * </p>
     * 
     * <p>
     * Se encarga de inicializar una nueva tabla de hechos con un nombre, una matriz de información interna,
     * encabezados que representan las columnas y los hechos y claves foráneas de dimensiones presentes en la misma.
     * </p>
     * 
     * @param nombre El nombre de la tabla de hechos.
     * @param datosTabla Una matriz que representa la información interna de la tabla.
     * @param headers Una lista que representa los encabezados de la tabla.
     * @param hechos Una lista que representa los hechos presentes en la tabla.
     * @param clavesForaneasDims Un mapa que representa las claves foráneas de las dimensiones presentes en la tabla.
     */
    private Hecho(String nombre, List<List<String>> datosTabla, List<String> headers, List<String> hechos, Map<Dimension, String> clavesForaneasDims){

        // Uso el constructor base para la información general
        super(nombre, datosTabla, headers);

        // Guardo la información propia de la tabla de hechos
        this.hechos = hechos;
        this.clavesForaneasDims = clavesForaneasDims;
    }


    // Métodos de la clase Hecho

    /**
     * @return Una copia de la lista de hechos presentes en esta tabla.
     */
    public List<String> getHechos(){
        return new ArrayList<>(this.hechos);
    }

    /**
     * @return Una copia del mapa que contiene a las claves foráneas de dimensiones presentes
     * en esta tabla.
     */
    public Map<Dimension, String> getClavesForaneasDims(){
        return new HashMap<>(this.clavesForaneasDims);
    }
    
    /**
     * <p>
     * Devuelve una representación en forma de cadena de esta tabla.
     * </p>
     * 
     * <p>
     * La representación incluye el nombre de la tabla, la cantidad de registros y las columnas
     * presentes en la misma.
     * </p>
     *
     * @return La representación en forma de cadena de esta tabla.
     */
    @Override
    public String toString() {
        String informacionTablaHechos = "TABLA DE HECHOS <" + this.getNombre() + ">\n";
        informacionTablaHechos += "\n CANTIDAD DE REGISTROS: " + this.getDatosTabla().size() + "\n";
        informacionTablaHechos += " COLUMNAS:"+ "\n";
        for (String header : this.getHeaders()) {
            informacionTablaHechos += "  - " + header + "\n";
        }
        return informacionTablaHechos;
    }

}
