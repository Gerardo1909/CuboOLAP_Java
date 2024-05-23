package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Cubo.CuboOLAP;
import Cubo.tablas.Dimension;

public class SlicePrueba {
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

        // Pruebo la operación Slice
        cuboPrueba.slice(
            dimFechas,
            "anio",
            "2017"
        );

        cuboPrueba.ver(10, new ArrayList<>(Arrays.asList("anio", "mes", "ciudad", "costo")));
    }
}
