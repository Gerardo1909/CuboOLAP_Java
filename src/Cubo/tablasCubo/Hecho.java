package Cubo.tablasCubo;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import Cubo.excepciones.excepcionesHechos.HechoNoPresenteException;
import Cubo.lecturaArchivos.EstrategiaLecturaArchivo;

/**
 * Esta clase representa una tabla de hechos en un cubo de datos.
 * Hereda de la clase Tabla y añade atributos y métodos específicos para las tablas de hechos.
 */
public class Hecho extends Tabla {
    
    private List<String> hechos;

    /**
     * Crea una instancia de la clase Hecho con los parámetros especificados.
     * 
     * @param nombre el nombre de la tabla de hechos.
     * @param hechos la lista de hechos a verificar en la tabla de hechos
     * @param estrategia_lectura la estrategia de lectura de archivo a utilizar
     * @param rutaArchivo la ruta del archivo a leer
     * @return una instancia de la clase Hecho.
     * @throws IOException si ocurre un error de lectura del archivo
     * @throws HechoNoPresenteException si alguno de los hechos no está presente en la tabla de hechos
     */
    public static Hecho crearTablaHechos(String nombre, List<String> hechos, EstrategiaLecturaArchivo estrategia_lectura, String rutaArchivo) throws IOException, HechoNoPresenteException {
        
        // Leo el archivo del hecho
        List<List<String>> archivo_hecho = estrategia_lectura.leerArchivo(rutaArchivo);

        // Guardo los headers del hecho
        List<String> headers_hecho = archivo_hecho.get(0);
        archivo_hecho.remove(0);

        // Verifico que los hechos estén presentes en la tabla de hechos
        for (String hecho : hechos) {
            if (!headers_hecho.contains(hecho)) {
                throw new HechoNoPresenteException("El hecho '" + hecho + "' no esta presente en la tabla de hechos.");
            }
        }

        return new Hecho(nombre, archivo_hecho, headers_hecho, hechos);
    }

    /**
     * Constructor de la clase Hecho.
     * Inicializa la tabla de hechos con datos, encabezados y su lista de hechos.
     *
     * @param nombre El nombre de la tabla de hechos.
     * @param data Los datos de la tabla. Debe ser una lista de listas, donde cada lista interna representa una fila.
     * @param headers Los encabezados de la tabla. Debe ser una lista de cadenas, donde cada cadena representa un nombre de columna.
     * @param hechos La lista de hechos en la tabla.
     */
    private Hecho(String nombre, List<List<String>> data, List<String> headers, List<String> hechos){

        // Uso el constructor base para la información general
        super(nombre, data, headers);

        //Guardo la información de los hechos
        this.hechos = hechos;
    }

    /**
     * Obtiene una copia de la lista de hechos en la tabla.
     *
     * @return Una copia de la lista de hechos en la tabla.
     */
    public List<String> getHechos(){
        return new ArrayList<>(this.hechos);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tabla de hechos: ").append(this.getNombre()).append("\n");
        sb.append("Número de filas: ").append(this.getData().size()).append("\n");
        sb.append("Columnas:\n");
        for (String header : this.getHeaders()) {
            sb.append(" - ").append(header).append("\n");
        }
        return sb.toString();
    }

}
