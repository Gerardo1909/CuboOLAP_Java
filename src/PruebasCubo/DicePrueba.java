package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Cubo.CuboOLAP;
import Cubo.tablas.Dimension;

public class DicePrueba {
    public static void main(String[] args) throws Exception {
        // Importo el cubo generado anteriormente
        CuboOLAP cuboPrueba = CuboPruebaManager.getCuboPrueba();
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
        Map<String, List<String>> criteriosFechas = new HashMap<>();
        criteriosFechas.put("anio", Arrays.asList("2017"));

        Map<String, List<String>> criteriosPuntoVenta = new HashMap<>();
        criteriosPuntoVenta.put("provincia", Arrays.asList("California"));

        Map<Dimension, Map<String, List<String>>> criterios = new HashMap<>();
        criterios.put(dimFechas, criteriosFechas);
        criterios.put(dimPuntoVenta, criteriosPuntoVenta);

        // Ejecuto el método
        cuboPrueba.dice(criterios);

        cuboPrueba.ver(20, new ArrayList<>(Arrays.asList("anio", "provincia", "costo")));
    }
}
