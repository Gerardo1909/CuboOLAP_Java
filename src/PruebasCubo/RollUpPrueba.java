package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        // Armo el mapa que le voy a pasar al método como argumento
        Map<Dimension, String> criterios_reduccion = new LinkedHashMap<>();
        criterios_reduccion.put(dimFechas, "anio");
        criterios_reduccion.put(dimPuntoVenta, "region");
        criterios_reduccion.put(dimProducto, "categoria");

        // Pruebo la operación RollUp
        cuboPrueba.rollUp(
            criterios_reduccion,
            "sum"
        );

        // Hago una lista de las columnas a visualizar
        List<String> columnas_proyeccion = new ArrayList<>(Arrays.asList("anio", "region", "categoria", "valor_unitario"));

        // Visualizo el resultado
        cuboPrueba.proyectar(columnas_proyeccion, 10);
    }
}
