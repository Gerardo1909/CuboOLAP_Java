package Cubo.exportacionArchivos;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Esta interfaz define un método para la exportación de archivos en diferentes formatos.
 * </p>
 * 
 * <p>
 * Está inspirada en el patrón de diseño "Strategy", en donde cada forma de exportar archivos
 * tiene una clase que se encarga de su implementación.
 * </p>
 */
public interface EstrategiaExportarArchivo {
    /**
     * Exporta la matriz de datos proporcionada a la ruta especificada.
     * 
     * @param rutaArchivo La ruta de destino donde se guardará el archivo.
     * @param data Una matriz de datos de tipo cadena que representa la información interna del archivo.
     * 
     * @throws IOException Si ocurre un error de entrada/salida al exportar la información de la matriz.
     */
    public void exportarArchivo(String rutaArchivo, List<List<String>> data) throws IOException;
}
