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

    /**
     * Lista de hechos en la tabla.
     */
    private List<String> hechos;

    /**
     * Constructor de la clase Hecho.
     * Inicializa la tabla de hechos con la información general y su la lista de hechos.
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

    // Constructor privado, para uso dentro de los métodos de la clase
    private Hecho(String nombre, List<List<String>> data, List<String> headers, List<String> hechos){
        super(nombre, data, headers);
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
     *
     * @return Una copia profunda del objeto actual.
     */
    public Hecho getHechoCopy() {
        return new Hecho(this.getNombre(), this.getData(), this.getHeaders(), this.getHechos());
    }

}
