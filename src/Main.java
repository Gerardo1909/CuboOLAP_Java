import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Cubo.CuboOLAP;
import Cubo.lectura_archivos.LectorCSV;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

public class Main {
    public static void main(String[] args) throws Exception {
        
        //Obtengo las rutas de los archivos
        String rutaFechas = "../../datasets/fechas.csv";
        String rutaProductos = "../../datasets/productos.csv";
        String rutaPuntosVenta = "../../datasets/puntos_venta.csv";
        String rutaVentas = "../../datasets/ventas.csv";

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

        // Armo una lista con las dimensiones 
        List<Dimension> dimensiones = new ArrayList<>();
        dimensiones.add(fechas);
        dimensiones.add(productos);
        dimensiones.add(puntos_venta);

        // Armo un nuevo Cubo
        CuboOLAP cubito = new CuboOLAP("Cubito", ventas, dimensiones);

        // Pruebo la operación rollUp
        Map<List<String>, List<Double>> resultadoRollUp = cubito.rollUp(new ArrayList<>(Arrays.asList("anio", "categoria", "region", "pais")), "Suma");

        // Veo el resultado por consola
        for (Map.Entry<List<String>, List<Double>> entry : resultadoRollUp.entrySet()) {
            List<String> clave = entry.getKey();
            if (clave.get(0).equals("2017")) {
                System.out.println("Grupo: " + clave + ", Hechos: " + entry.getValue().toString());
            }
        }
    
    
    }
}
