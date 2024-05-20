package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Cubo.CuboOLAP;
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

        // Pruebo la operación rollUp
        cuboPrueba.rollUp(
            new ArrayList<>(Arrays.asList("anio", "quarter")),
            new ArrayList<>(Arrays.asList("valor_total")),
            "max"
        );

        cuboPrueba.ver(10, new ArrayList<>(Arrays.asList("anio", "quarter", "valor_total")));
        
    }
}
