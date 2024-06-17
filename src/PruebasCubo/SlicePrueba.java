package PruebasCubo;

import java.util.List;
import Cubo.ImplementacionCubo.Cubo;
import Cubo.exportacionArchivos.ExportadorCSV;
import Cubo.tablasCubo.Dimension;

public class SlicePrueba {
    public static void main(String[] args) throws Exception {
        // Configuración del cubo para la prueba
        List<Dimension> dimensiones = CuboPruebaManager.getDimensionesCuboPrueba();
        if (dimensiones.size() < 3) {
            System.out.println("Las dimensiones no están configuradas correctamente.");
            return;
        }
        Dimension dimFechas = dimensiones.get(0);
        Dimension dimProducto = dimensiones.get(1);
        Dimension dimPuntoVenta = dimensiones.get(2);
        Cubo cuboPrueba = CuboPruebaManager.getCuboPrueba();
        if (cuboPrueba == null) {
            System.out.println("El cubo no está configurado correctamente.");
            return;
        }

        //-------------- PRUEBA DEL MÉTODO --------------// 

        // Pruebo la operación Slice
        cuboPrueba.slice(
            dimFechas,
            "anio",
            "2020"
        );

        // Pruebo la exportacion del cubo
        ExportadorCSV exportadorCSV = new ExportadorCSV(';');
        cuboPrueba.exportar("exportaciones/prueba_Slice.csv", exportadorCSV);
    }
}
