package manejo_cubo;

import manejo_archivos.Archivo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public final class Dimension{

    private final String nombre_dimension;
    private final Archivo archivo_dimension;
    private final Map<String, List<String>> niveles;

    public Dimension(String ruta_archivo_dimension, String nombre_dimension, List<String> niveles)  throws IOException{

        this.archivo_dimension = new Archivo(ruta_archivo_dimension, true); //Debe tener headers ya que es una dimensión
        this.nombre_dimension = nombre_dimension;

        // Creo un map para controlar los niveles
        Map<String, List<String>> map_niveles = new HashMap<>();
        for (String nivel : niveles) {
            map_niveles.put(nivel, this.archivo_dimension.obtenerValoresUnicos(nivel));
        }

        // Añado la información a niveles
        this.niveles = map_niveles;

    }

    public Archivo getArchivoDimension() {
        return this.archivo_dimension;
    }

    public String getNombreDimension() {
        return this.nombre_dimension;
    }

    public void mostrarNivel(String nivel) {
        // Verificar si el nivel existe en el mapa
        if (niveles.containsKey(nivel)) {
            System.out.print(nivel + " : [");
            // Obtener los valores asociados al nivel
            List<String> valores = niveles.get(nivel);
            // Mostrar los valores
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
