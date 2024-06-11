package PruebasCubo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Cubo.Cubo;
import Cubo.lecturaArchivos.LectorCSV;
import Cubo.tablasCubo.Dimension;
import Cubo.tablasCubo.Hecho;

public class CreacionPrueba {
    public static void main(String[] args) throws Exception {
        
        // Obtengo las rutas de los archivos
        String rutaFechas = "datasets/fechas.csv";
        String rutaProductos = "datasets/productos.csv";
        String rutaPuntosVenta = "datasets/puntos_venta.csv";
        String rutaVentas = "datasets/ventas.csv";

        // Obtengo los niveles de cada dimensión
        List<String> niveles_fechas = new ArrayList<>(Arrays.asList("anio", "quarter", "mes", "dia", "fecha"));
        List<String> niveles_Productos = new ArrayList<>(Arrays.asList("categoria", "subcategoria", "producto"));
        List<String> niveles_PuntosVenta = new ArrayList<>(Arrays.asList("region", "pais", "provincia", "ciudad", "punto_venta"));
        List<String> hechosVentas = new ArrayList<>(Arrays.asList("cantidad", "valor_unitario", "valor_total", "costo"));

        // Para la instanciación de clases primero inicializo un LectorCSV
        LectorCSV lectorCSV = new LectorCSV(';');

        // Instancio las clases correspondientes
        Hecho ventas = Hecho.crearTablaHechos("Ventas", hechosVentas, lectorCSV, rutaVentas);
        Dimension fechas = Dimension.crearTablaDimension("Fechas", niveles_fechas, "id_fecha", lectorCSV, rutaFechas);
        Dimension productos = Dimension.crearTablaDimension("Productos", niveles_Productos, "id_producto", lectorCSV, rutaProductos);
        Dimension puntos_venta = Dimension.crearTablaDimension("Puntos de venta", niveles_PuntosVenta, "id_punto_venta", lectorCSV, rutaPuntosVenta);

        // Armo una lista con las dimensiones 
        List<Dimension> dimensiones = new ArrayList<>();
        dimensiones.add(fechas);
        dimensiones.add(productos);
        dimensiones.add(puntos_venta);

        // Armo un nuevo Cubo
        Cubo cuboPrueba = Cubo.crearCuboOLAP("Cubo de Prueba", ventas, dimensiones);

        // Guardo el cubo y sus datos asociados para probarlos en otras clases
        CuboPruebaManager.setCuboPrueba(cuboPrueba);
        CuboPruebaManager.setDimensionesCuboPrueba(dimensiones);

        System.out.println("CreacionPrueba: Inicializacion completada.");
    }
}