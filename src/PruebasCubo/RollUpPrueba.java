package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Cubo.CuboOLAP;
import Cubo.exportacion_archivos.ExportadorCSV;
import Cubo.tablas.Dimension;

public class RollUpPrueba {
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
        CuboOLAP cuboPrueba = CuboPruebaManager.getCuboPrueba();
        if (cuboPrueba == null) {
            System.out.println("El cubo no está configurado correctamente.");
            return;
        }

        // Armo el mapa que le voy a pasar al método como argumento
        Map<Dimension, String> criterios_reduccion = new LinkedHashMap<>();
        criterios_reduccion.put(dimFechas, "quarter");
        criterios_reduccion.put(dimPuntoVenta, "pais");
        criterios_reduccion.put(dimProducto, "subcategoria");

        // Pruebo la operación RollUp
        cuboPrueba.rollUp(
            criterios_reduccion,
            Arrays.asList("cantidad"),
            "sum"
        );

        // Visualizo el resultado
        List<String> columnas_visualizacion = new ArrayList<>(Arrays.asList("anio", "quarter", "region", "pais", "categoria", "subcategoria", "cantidad"));
        cuboPrueba.ver(20, columnas_visualizacion);

        // Pruebo la exportacion del cubo
        ExportadorCSV exportadorCSV = new ExportadorCSV(';');
        cuboPrueba.exportar("exportaciones/prueba_rollUp.csv", exportadorCSV);
    }
}
