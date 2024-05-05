package Cubo.tablas;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Cubo.lectura_archivos.EstrategiaLecturaArchivo;


public class Dimension extends Tabla {

    private Map<String, List<String>> niveles;

    public Dimension(String nombre, List<String> niveles, EstrategiaLecturaArchivo estrategia_lectura, String ruta_archivo) throws IOException{
        
        // Uso el constructor base para la información general
        super(nombre, estrategia_lectura, ruta_archivo);

        // Guardo la información sobre los niveles
        Map<String, List<String>> map_niveles = new HashMap<>();
        for (String nivel : niveles) {
            map_niveles.put(nivel, this.obtenerValoresUnicos(nivel));
        }

        // Añado la información a niveles
        this.niveles = map_niveles;     
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
