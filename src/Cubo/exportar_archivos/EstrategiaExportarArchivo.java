package Cubo.exportar_archivos;

import java.io.IOException;
import java.util.List;

public interface EstrategiaExportarArchivo {
    public void exportarArchivo(String ruta_archivo, List<List<String>> data) throws IOException;
}
