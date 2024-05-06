import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Dimension fechas = new Dimension("Fechas", niveles_fechas,"id_fecha", new LectorCSV(), rutaFechas);
        Dimension productos = new Dimension("Productos", niveles_Productos, "id_producto",new LectorCSV(), rutaProductos);
        Dimension puntos_venta = new Dimension("Puntos de venta", niveles_PuntosVenta, "id_punto_venta",new LectorCSV(), rutaPuntosVenta);

        Hecho ventas_merge = ventas.mergeDimension(fechas, "id_fecha");


        // Llamada al método groupBy() para obtener el resultado
        Map<List<String>, List<Double>> resultado = ventas_merge.groupBy(new ArrayList<>(Arrays.asList("id_producto", "id_punto_venta", "anio")));

        // Filtrar las entradas del mapa para obtener solo aquellas con el primer valor de la clave igual a "212"
        for (Map.Entry<List<String>, List<Double>> entry : resultado.entrySet()) {
            List<String> clave = entry.getKey();
            if (clave.get(0).equals("212")) {
                System.out.println("Grupo: " + clave + ", Hechos: " + entry.getValue().toString());
            }
        }
    
    
    }
}
