import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Hecho ventas = Hecho.CrearDesdeCSV(rutaVentas, "Ventas", hechosVentas);
        Dimension fechas = Dimension.CrearDesdeCSV(rutaFechas, "Fechas", niveles_fechas);
        Dimension productos = Dimension.CrearDesdeCSV(rutaProductos, "Productos", niveles_Productos);
        Dimension puntos_venta = Dimension.CrearDesdeCSV(rutaPuntosVenta, "Puntos de Venta", niveles_PuntosVenta);

        // Pruebo el método ver()
        ventas.ver(5, ventas.getHeaders());

    }
}
