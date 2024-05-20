package PruebasCubo;

import java.util.ArrayList;
import java.util.List;
import Cubo.CuboOLAP;
import Cubo.tablas.Dimension;

// Esta clase sirve para poder probar un cubo de prueba en diferentes clases
public class CuboPruebaManager {
    private static CuboOLAP cuboPrueba;
    private static List<Dimension> dimensionesCuboPrueba = new ArrayList<>();

    public static void setCuboPrueba(CuboOLAP cubo) {
        cuboPrueba = cubo;
    }

    public static void setDimensionesCuboPrueba(List<Dimension> dimensiones) {
        dimensionesCuboPrueba = dimensiones;
    }

    public static void addDimension(Dimension dimension) {
        dimensionesCuboPrueba.add(dimension);
    }

    public static CuboOLAP getCuboPrueba() {
        return cuboPrueba;
    }

    public static List<Dimension> getDimensionesCuboPrueba() {
        return dimensionesCuboPrueba;
    }
}
