package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Cubo.ImplementacionCubo.Cubo;
import Cubo.exportacionArchivos.ExportadorCSV;
import Cubo.tablasCubo.Dimension;

public class DicePrueba {
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

        // Armo el mapa que le voy a pasar al método como argumento
        Map<String, List<String>> criteriosFechas = new LinkedHashMap<>();
        criteriosFechas.put("anio", Arrays.asList("2018"));
        Map<String, List<String>> criteriosPuntoVenta = new LinkedHashMap<>();
        criteriosPuntoVenta.put("provincia", Arrays.asList("Florida"));
        Map<String, List<String>> criteriosProductos = new LinkedHashMap<>();
        criteriosProductos.put("categoria", Arrays.asList("Bikes"));      
        Map<Dimension, Map<String, List<String>>> criterios = new LinkedHashMap<>();
        criterios.put(dimFechas, criteriosFechas);
        criterios.put(dimPuntoVenta, criteriosPuntoVenta);
        criterios.put(dimProducto, criteriosProductos);

        // Pruebo la operación Dice
        cuboPrueba.dice(criterios);

        // Visualizo por consola
        cuboPrueba.proyectar(10, new ArrayList<>(Arrays.asList("anio", "provincia", "categoria" ,"costo", "valor_total", "cantidad", "valor_unitario")));

        // Pruebo la exportacion del cubo
        ExportadorCSV exportadorCSV = new ExportadorCSV(';');
        cuboPrueba.exportar("exportaciones/prueba_Dice.csv", exportadorCSV);
    
    }
}
