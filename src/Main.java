import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Cubo.CuboOLAP;
import Cubo.lectura_archivos.LectorCSV;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

public class Main {
    public static void main(String[] args) throws Exception {
        
        //Obtengo las rutas de los archivos
        String rutaFechas = "datasets/fechas.csv";
        String rutaProductos = "datasets/productos.csv";
        String rutaPuntosVenta = "datasets/puntos_venta.csv";
        String rutaVentas = "datasets/ventas.csv";

        // Obtengo los niveles de cada dimensión
        List<String> niveles_fechas = new ArrayList<>(Arrays.asList("fecha", "dia", "mes", "anio", "quarter"));
        List<String> niveles_Productos = new ArrayList<>(Arrays.asList("producto", "subcategoria", "categoria"));
        List<String> niveles_PuntosVenta = new ArrayList<>(Arrays.asList("punto_venta", "ciudad", "provincia", "pais", "region"));
        List<String> hechosVentas = new ArrayList<>(Arrays.asList("cantidad", "valor_unitario", "valor_total", "costo"));

        //Para la instanciación de clases primero inicializo un LectorCSV
        LectorCSV lectorCSV = new LectorCSV(';');

        //Instancio las clases correspondientes
        Hecho ventas = new Hecho("Ventas",hechosVentas ,lectorCSV,rutaVentas);
        Dimension fechas = new Dimension("Fechas", niveles_fechas,"id_fecha", lectorCSV, rutaFechas);
        Dimension productos = new Dimension("Productos", niveles_Productos, "id_producto",lectorCSV, rutaProductos);
        Dimension puntos_venta = new Dimension("Puntos de venta", niveles_PuntosVenta, "id_punto_venta",lectorCSV, rutaPuntosVenta);

        // Armo una lista con las dimensiones 
        List<Dimension> dimensiones = new ArrayList<>();
        dimensiones.add(fechas);
        dimensiones.add(productos);
        dimensiones.add(puntos_venta);

        // Armo un nuevo Cubo
        CuboOLAP cubito = new CuboOLAP("Cubito", ventas, dimensiones);

        // Pruebo la operación rollUp
        cubito.rollUp(
            new ArrayList<>(Arrays.asList("anio", "quarter")),
            new ArrayList<>(Arrays.asList("valor_total")),
            "max"
        );

        // Pruebo la operación Slice
        cubito.slice(
            puntos_venta,
            "anio",
            "2020"
        );


        cubito.ver(100, new ArrayList<>(Arrays.asList("anio", "mes", "ciudad", "costo")));
    }
}
