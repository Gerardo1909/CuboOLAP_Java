package Cubo.exportacionArchivos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Esta clase se encarga de exportación de archivos en formato CSV.
 * </p>
 * 
 * <p>
 * Implementa la interfaz {@link EstrategiaExportarArchivo}.
 * </p>
 */
public class ExportadorCSV implements EstrategiaExportarArchivo {
    
    // Atributos de la clase ExportadorCSV
    private char separador;

    /**
     * <p>
     * <b>Constructor para la clase ExportadorCSV.</b>
     * </p>
     * 
     * <p>
     * Se encarga de guardar el separador que se usará para la exportación del archivo CSV.
     * </p>
     * 
     * @param separador El carácter utilizado para separar los campos en el archivo CSV.
     */
    public ExportadorCSV(char separador) {
        this.separador = separador;
    }

    /**
     * Exporta la matriz de datos proporcionada en formato CSV a la ruta especificada.
     *
     * @param rutaArchivo La ruta donde se desea guardar el archivo CSV a exportar.
     * @param data Una matriz de datos de tipo cadena que representa la información interna del archivo
     *             CSV a exportar.
     * 
     * @throws IOException Si ocurre un error de entrada/salida al leer los datos del archivo CSV.
     */
    @Override
    public void exportarArchivo(String rutaArchivo, List<List<String>> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (List<String> fila : data) {
                writer.write(String.join(String.valueOf(separador), fila));
                writer.newLine();
            }
        }
    }
}
