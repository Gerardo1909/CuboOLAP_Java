package PruebasCubo;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Cubo.ImplementacionCubo.Cubo;
import Cubo.exportacionArchivos.ExportadorCSV;
import Cubo.tablasCubo.Dimension;

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
        Cubo cuboPrueba = CuboPruebaManager.getCuboPrueba();
        if (cuboPrueba == null) {
            System.out.println("El cubo no está configurado correctamente.");
            return;
        }


        // Armo el mapa que le voy a pasar al método como argumento
        Map<Dimension, String> criteriosAgregacion = new LinkedHashMap<>();
        criteriosAgregacion.put(dimFechas, "anio");
        criteriosAgregacion.put(dimPuntoVenta, "region");
        criteriosAgregacion.put(dimProducto, "categoria");


        // Pruebo la operación RollUp
        cuboPrueba.rollUp(
            criteriosAgregacion,
            Arrays.asList("valor_total", "costo", "valor_unitario", "cantidad"),
            "sum"
        );

        // Visualizo por consola
        List<String> columnasAVer = Arrays.asList("anio", "region", "categoria", "valor_total", "costo", "valor_unitario", "cantidad");
        cuboPrueba.proyectar(10, columnasAVer);

        // Pruebo la exportacion del cubo
        ExportadorCSV exportadorCSV = new ExportadorCSV(';');
        cuboPrueba.exportar("exportaciones/prueba_rollUp.csv", exportadorCSV);
    }
}
