package Cubo.lectura_archivos;

import java.io.IOException;
import java.util.List;

public interface EstrategiaLecturaArchivo {
    public List<List<String>> leerArchivo(String ruta_archivo) throws IOException;
}
