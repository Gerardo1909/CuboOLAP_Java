package manejo_cubo;

import manejo_archivos.Archivo;
import java.io.IOException;

public final class Hecho {

    private final String nombre_hecho;
    private final Archivo archivo_hecho;

    public Hecho(String ruta_archivo_hecho, String nombre_hecho) throws IOException{

        this.nombre_hecho = ruta_archivo_hecho;
        this.archivo_hecho = new Archivo(ruta_archivo_hecho, true); //Debe tener headers ya que es un hecho

    }

    public Archivo getArchivoHecho() {
        return this.archivo_hecho;
    }

    public String getNombreHecho() {
        return this.nombre_hecho;
    }

}
