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

    public List<String> getHechos(){
        return new ArrayList<>(this.hechos);
    }

    public Hecho getHechoCopy() {
        // Método para hacer una copia profunda del objeto actual
        return new Hecho(this.getNombre(), this.getData(), this.getHeaders(), this.getHechos());
    }

}
