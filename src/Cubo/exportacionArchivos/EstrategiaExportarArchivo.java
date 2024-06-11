package Cubo.exportacionArchivos;

import java.io.IOException;
import java.util.List;

/**
 * Este interfaz define una estrategia para exportar archivos.
 * Proporciona la firma de un método para exportar datos en formato de lista de listas de cadena a una ubicación determinada.
 */
public interface EstrategiaExportarArchivo {
    /**
     * Exporta los datos proporcionados a un archivo en la ruta especificada.
     * 
     * @param ruta_archivo la ruta completa del archivo donde se exportarán los datos. No debe ser nula o vacía.
     * @param data Una lista de listas de cadenas que representa el contenido del archivo.
     * @throws IOException Si se produce un error de E/S al leer el archivo.
     */
    public void exportarArchivo(String ruta_archivo, List<List<String>> data) throws IOException;
}
