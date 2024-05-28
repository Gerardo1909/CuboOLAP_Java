package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Cubo.CuboOLAP;
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

        // Primero armo el mapa que le voy a pasar al método rollUp
        // recordar que primero hay que agrupar para desagrupar
        Map<Dimension, String> criterios_reduccion = new LinkedHashMap<>();
        criterios_reduccion.put(dimFechas, "anio");

        // Ejecuto el RollUp
        cuboPrueba.rollUp(
            criterios_reduccion,
            "sum"
        );

        // Lo proyecto
        List<String> columnas_proyeccion_rollUp = new ArrayList<>(Arrays.asList("anio", "valor_unitario"));
        cuboPrueba.proyectar(columnas_proyeccion_rollUp , 10);
        System.out.println();

        // Vuevlo a generar el mapa pero con los criterios de desagregación 
        // para el DrillDown
        Map<Dimension, String> criterios_expansion = new LinkedHashMap<>();
        criterios_expansion.put(dimFechas, "mes");

        // Ejecuto el DrillDown
        cuboPrueba.drillDown(criterios_expansion);

        // Hago la proyección a mes
        List<String> columnas_proyeccion_drillDown = new ArrayList<>(Arrays.asList("anio", "quarter", "mes", "valor_unitario"));

        // Visualizo el resultado
        cuboPrueba.proyectar(columnas_proyeccion_drillDown, 10);
    }
}