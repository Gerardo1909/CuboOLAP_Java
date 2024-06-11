package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Cubo.CuboOLAP;
import Cubo.exportacion_archivos.ExportadorCSV;
import Cubo.tablas.Dimension;

public class DrillDownPrueba {
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

        // Para probar el DrillDown primero se debe hacer un RollUp
        // por lo tanto genero el mapa para pasar como argumento
        Map<Dimension, String> criterios_reduccion = new LinkedHashMap<>();
        criterios_reduccion.put(dimFechas, "anio");
        criterios_reduccion.put(dimPuntoVenta, "region");
        criterios_reduccion.put(dimProducto, "categoria");

        // ejecuto el RollUp
        cuboPrueba.rollUp(
            criterios_reduccion,
            Arrays.asList("valor_total"),
            "sum"
        );

        // Pruebo el método dice para ver como se comporta y si mantiene el filtro
        Map<String, List<String>> criteriosFechas = new LinkedHashMap<>();
        criteriosFechas.put("anio", Arrays.asList("2018"));

        Map<Dimension, Map<String, List<String>>> criterios = new LinkedHashMap<>();
        criterios.put(dimFechas, criteriosFechas);
        // Ejecuto el método
        cuboPrueba.dice(criterios);

        // Ahora pruebo a desagrupar las dimensiones a su nivel más fino
        Map<Dimension, String> criterios_expansion = new LinkedHashMap<>();
        criterios_expansion.put(dimFechas, "fecha");
        criterios_expansion.put(dimPuntoVenta, "punto_venta");
        criterios_expansion.put(dimProducto, "producto");

        // Pruebo la operación DrillDown
        cuboPrueba.drillDown(criterios_expansion);

        // Pruebo la exportacion del cubo
        ExportadorCSV exportadorCSV = new ExportadorCSV(';');
        cuboPrueba.exportar("exportaciones/prueba_DrillDown.csv", exportadorCSV);

    }
}