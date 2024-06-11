package Cubo.exportacionArchivos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Esta clase implementa la interfaz EstrategiaExportarArchivo para exportar datos a un archivo CSV.
 */
public class ExportadorCSV implements EstrategiaExportarArchivo {
    
    private char separador;

    /**
     * Constructor para la clase ExportarCSV.
     * @param separador El carácter utilizado para separar los campos en el archivo CSV.
     */
    public ExportadorCSV(char separador) {
        this.separador = separador;
    }

    /**
     * Exporta los datos a un archivo CSV.
     * @param ruta_archivo La ruta del archivo CSV.
     * @param data Los datos a exportar, representados como una lista de listas de cadenas.
     * @throws IOException Si se produce algún error al escribir el archivo.
     */
    @Override
    public void exportarArchivo(String ruta_archivo, List<List<String>> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta_archivo))) {
            for (List<String> row : data) {
                writer.write(String.join(String.valueOf(separador), row));
                writer.newLine();
            }
        } catch (IOException e) {
            throw e;
        }
    }
}
