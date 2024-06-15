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
        // Importo el cubo generado anteriormente
        Cubo cuboPrueba = CuboPruebaManager.getCuboPrueba();
        if (cuboPrueba == null) {
            System.out.println("El cubo no está configurado correctamente.");
            return;
        }

        // Importo las dimensiones generadas anteriormente
        List<Dimension> dimensiones = CuboPruebaManager.getDimensionesCuboPrueba();
        if (dimensiones.size() < 3) {
            System.out.println("Las dimensiones no están configuradas correctamente.");
            return;
        }

        Dimension dimFechas = dimensiones.get(0);
        Dimension dimProducto = dimensiones.get(1);
        Dimension dimPuntoVenta = dimensiones.get(2);

        // Defino los criterios para la operación "Dice"
        Map<String, List<String>> criteriosFechas = new LinkedHashMap<>();
        criteriosFechas.put("anio", Arrays.asList("2018"));

        Map<String, List<String>> criteriosPuntoVenta = new LinkedHashMap<>();
        criteriosPuntoVenta.put("provincia", Arrays.asList("Florida"));

        Map<Dimension, Map<String, List<String>>> criterios = new LinkedHashMap<>();
        criterios.put(dimFechas, criteriosFechas);
        criterios.put(dimPuntoVenta, criteriosPuntoVenta);

        // Ejecuto el método
        cuboPrueba.dice(criterios);

        // Visualizo por consola
        cuboPrueba.proyectar(10, new ArrayList<>(Arrays.asList("anio", "provincia", "costo")));

        // Pruebo la exportacion del cubo
        ExportadorCSV exportadorCSV = new ExportadorCSV(';');
        cuboPrueba.exportar("exportaciones/prueba_Dice.csv", exportadorCSV);
    
    }
}
