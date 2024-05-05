import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Cubo.lectura_archivos.LectorCSV;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

public class Main {
    public static void main(String[] args) throws Exception {
        
        //Obtengo las rutas de los archivos
        String rutaFechas = "C:\\Users\\gerar\\OneDrive\\Documentos\\Mis Archivos\\Algoritmos 1\\TP_final\\datasets\\fechas.csv";
        String rutaProductos = "C:\\Users\\gerar\\OneDrive\\Documentos\\Mis Archivos\\Algoritmos 1\\TP_final\\datasets\\productos.csv";
        String rutaPuntosVenta = "C:\\Users\\gerar\\OneDrive\\Documentos\\Mis Archivos\\Algoritmos 1\\TP_final\\datasets\\puntos_venta.csv";
        String rutaVentas = "C:\\Users\\gerar\\OneDrive\\Documentos\\Mis Archivos\\Algoritmos 1\\TP_final\\datasets\\ventas.csv";

        // Obtengo los niveles de cada dimensión
        List<String> niveles_fechas = new ArrayList<>(Arrays.asList("fecha", "dia", "mes", "anio", "quarter"));
        List<String> niveles_Productos = new ArrayList<>(Arrays.asList("producto", "subcategoria", "categoria"));
        List<String> niveles_PuntosVenta = new ArrayList<>(Arrays.asList("punto_venta", "ciudad", "provincia", "pais", "region"));
        List<String> hechosVentas = new ArrayList<>(Arrays.asList("cantidad", "valor_unitario", "valor_total", "costo"));


        //Instancio las clases correspondientes
        Hecho ventas = new Hecho("Ventas",hechosVentas ,new LectorCSV(),rutaVentas);
        Dimension fechas = new Dimension("Fechas", niveles_fechas, new LectorCSV(), rutaFechas);
        Dimension productos = new Dimension("Productos", niveles_Productos, new LectorCSV(), rutaProductos);
        Dimension puntos_venta = new Dimension("Puntos de venta", niveles_PuntosVenta, new LectorCSV(), rutaPuntosVenta);

        // Pruebo el método ver()
        ventas.ver(5, ventas.getHeaders());

    }
}
