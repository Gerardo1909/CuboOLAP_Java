package PruebasCubo;

import java.util.ArrayList;
import java.util.List;
import Cubo.Cubo;
import Cubo.tablasCubo.Dimension;

// Esta clase sirve para poder probar un cubo de prueba en diferentes clases
public class CuboPruebaManager {
    private static Cubo cuboPrueba;
    private static List<Dimension> dimensionesCuboPrueba = new ArrayList<>();

    public static void setCuboPrueba(Cubo cubo) {
        cuboPrueba = cubo;
    }

    public static void setDimensionesCuboPrueba(List<Dimension> dimensiones) {
        dimensionesCuboPrueba = dimensiones;
    }

    public static void addDimension(Dimension dimension) {
        dimensionesCuboPrueba.add(dimension);
    }

    public static Cubo getCuboPrueba() {
        return cuboPrueba;
    }

    public static List<Dimension> getDimensionesCuboPrueba() {
        return dimensionesCuboPrueba;
    }
}
