import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import manejo_cubo.Dimension;
import manejo_cubo.Hecho;
import manejo_cubo.CuboOLAP;
import manejo_archivos.Joiner;

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


        //Instancio las clases correspondientes
        Hecho ventas = new Hecho(rutaVentas, "Ventas");
        Dimension fechas = new Dimension(rutaFechas, "Fechas", niveles_fechas);
        Dimension productos = new Dimension(rutaProductos, "Productos", niveles_Productos);
        Dimension puntos_venta = new Dimension(rutaPuntosVenta, "Puntos de Venta", niveles_PuntosVenta);

        // Pruebo la nueva función inner_join
        List<List<String>> resultado = Joiner.innerJoin(ventas.getArchivoHecho(), fechas.getArchivoDimension(), "id_fecha");

        List<String> fila1 = resultado.get(0);

        // Imprimo todos los campos de fila1 para probar
        for (String campo : fila1) {
            System.out.println(campo);
        }

    }
}
