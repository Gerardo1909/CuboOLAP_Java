package PruebasCubo;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Cubo.ImplementacionCubo.Cubo;
import Cubo.exportacionArchivos.ExportadorCSV;
import Cubo.tablasCubo.Dimension;

public class DrillDownPrueba {
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

        // Para probar el DrillDown primero se debe hacer un RollUp
        Map<Dimension, String> criteriosAgregacion = new LinkedHashMap<>();
        criteriosAgregacion.put(dimFechas, "anio");
        criteriosAgregacion.put(dimPuntoVenta, "region");
        criteriosAgregacion.put(dimProducto, "categoria");
        cuboPrueba.rollUp(
            criteriosAgregacion,
            Arrays.asList("valor_total"),
            "sum"
        );

        // Armo el mapa que le voy a pasar al método como argumento
        Map<Dimension, String> criteriosDesagregacion = new LinkedHashMap<>();
        criteriosDesagregacion.put(dimPuntoVenta, "punto_venta");
        criteriosDesagregacion.put(dimProducto, "producto");
        criteriosDesagregacion.put(dimFechas, "fecha");

        // Pruebo la operación DrillDown
        cuboPrueba.drillDown(criteriosDesagregacion);

        // Visualizo por consola
        List<String> columnasAVer = Arrays.asList("anio", "quarter", "mes", "fecha", "region", "categoria", "valor_total");
        cuboPrueba.proyectar(10, columnasAVer);
    
        // Pruebo la exportacion del cubo
        ExportadorCSV exportadorCSV = new ExportadorCSV(';');
        cuboPrueba.exportar("exportaciones/prueba_DrillDown.csv", exportadorCSV);
    }
}