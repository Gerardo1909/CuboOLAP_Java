package PruebasCubo;

import java.util.List;
import Cubo.ImplementacionCubo.Cubo;
import Cubo.exportacionArchivos.ExportadorCSV;
import Cubo.tablasCubo.Dimension;

public class SlicePrueba {
    public static void main(String[] args) throws Exception {
        // Importo las dimensiones generadas anteriormente
        List<Dimension> dimensiones = CuboPruebaManager.getDimensionesCuboPrueba();
        if (dimensiones.size() < 3) {
            System.out.println("Las dimensiones no están configuradas correctamente.");
            return;
        }

        Dimension dimFechas = dimensiones.get(0);
        Dimension dimProducto = dimensiones.get(1);
        Dimension dimPuntoVenta = dimensiones.get(2);

        // Importo el cubo generado anteriormente
        Cubo cuboPrueba = CuboPruebaManager.getCuboPrueba();
        if (cuboPrueba == null) {
            System.out.println("El cubo no está configurado correctamente.");
            return;
        }

        // Pruebo la operación Slice
        cuboPrueba.slice(
            dimFechas,
            "anio",
            "2017"
        );

        // Exporto el cubo para verificar el resultado
        ExportadorCSV exportadorCSV = new ExportadorCSV(';');
        cuboPrueba.exportar("exportaciones/prueba_slice.csv", exportadorCSV);
    }
}
