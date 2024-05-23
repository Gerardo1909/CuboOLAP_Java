package Cubo.tablas;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import Cubo.excepciones.excepciones_hechos.HechoNoPresenteException;
import Cubo.lectura_archivos.EstrategiaLecturaArchivo;

/**
 * Esta clase representa una tabla de hechos en un cubo de datos.
 * Hereda de la clase Tabla y añade atributos y métodos específicos para las tablas de hechos.
 */
public class Hecho extends Tabla {
    
    private List<String> hechos;

    /**
     * Constructor de la clase Hecho.
     * Inicializa la tabla de hechos con la información general y su lista de hechos.
     *
     * @param nombre El nombre de la tabla de hechos.
     * @param hechos La lista de hechos en la tabla.
     * @param estrategia_lectura La estrategia de lectura de archivos.
     * @param ruta_archivo La ruta del archivo de datos.
     * @throws IOException Si hay un error de E/S al leer el archivo.
     * @throws HechoNoPresenteException Si alguno de los hechos indicados no está presente en la tabla de hechos.
     */
    public Hecho(String nombre, List<String> hechos ,EstrategiaLecturaArchivo estrategia_lectura, String ruta_archivo) throws IOException, HechoNoPresenteException{
        
        // Uso el constructor base para la información general
        super(nombre, estrategia_lectura, ruta_archivo);

        // Verifico que los hechos pasados por argumento estén presentes en la tabla de hechos
        for (String hecho : hechos) {
            if (!this.getHeaders().contains(hecho)) {
                throw new HechoNoPresenteException("El hecho " + hecho + " no está presente en la tabla de hechos");
            }
        }
 
        //Guardo la información de los hechos
        this.hechos = hechos;
    }

    /**
     * Constructor de la clase Hecho.
     * Inicializa la tabla de hechos con datos, encabezados y su lista de hechos.
     *
     * @param nombre El nombre de la tabla de hechos.
     * @param data Los datos de la tabla. Debe ser una lista de listas, donde cada lista interna representa una fila.
     * @param headers Los encabezados de la tabla. Debe ser una lista de cadenas, donde cada cadena representa un nombre de columna.
     * @param hechos La lista de hechos en la tabla.
     * @throws HechoNoPresenteException Si alguno de los hechos indicados no está presente en la tabla de hechos.
     */
    public Hecho(String nombre, List<List<String>> data, List<String> headers, List<String> hechos) throws HechoNoPresenteException{

        // Uso el constructor base para la información general
        super(nombre, data, headers);

        // Verifico que los hechos pasados por argumento estén presentes en la tabla de hechos
        for (String hecho : hechos) {
            if (!this.getHeaders().contains(hecho)) {
                throw new HechoNoPresenteException("El hecho " + hecho + " no está presente en la tabla de hechos");
            }
        }

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

    /**
     * Método para hacer una copia profunda del objeto actual.
     * @throws HechoNoPresenteException
     * @return Una copia profunda del objeto actual.
     */
    public Hecho getHechoCopy() throws HechoNoPresenteException{
        return new Hecho(this.getNombre(), this.getData(), this.getHeaders(), this.getHechos());
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
