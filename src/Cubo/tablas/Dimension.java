package Cubo.tablas;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Cubo.lectura_archivos.EstrategiaLecturaArchivo;
import Cubo.lectura_archivos.LectorCSV;

public class Dimension extends Tabla {

    private Map<String, List<String>> niveles;

    //Constructor privado para que solo se instancie desde los métodos de lectura
    private Dimension(String nombre, List<List<String>> data, List<String> headers){
        super(nombre, data, headers);
    }
    
    public static Dimension CrearDesdeCSV(String ruta_archivo, String nombre, List<String> niveles) throws IOException{

        // Instancio el lector CSV
        EstrategiaLecturaArchivo estrategia = new LectorCSV();

        // Guardo el archivo leido
        List<List<String>> archivo = estrategia.leerArchivo(ruta_archivo);

        //Guardo los headers
        List<String> headers = archivo.get(0);
        archivo.remove(0);

        //Guardo la data 
        List<List<String>> data = archivo;

        // Instancio la dimensión
        Dimension dimension = new Dimension(nombre, data, headers);

        // Guardo la información sobre los niveles
        Map<String, List<String>> map_niveles = new HashMap<>();
        for (String nivel : niveles) {
            map_niveles.put(nivel, dimension.obtenerValoresUnicos(nivel));
        }

        // Añado la información a niveles
        dimension.niveles = map_niveles;

        return dimension;
    }

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

}
