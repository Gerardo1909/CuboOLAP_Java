package PruebasCubo;

// Esta clase se encarga de inicializar el cubo para luego poder realizar pruebas 
// correctamente
public class Inicializador {
    public static void main(String[] args) throws Exception {

        // Corro primero la generación de los datos
        // NO MODIFICAR
        CreacionPrueba.main(args);
        
        // Ejecuto los archivos que quiero probar
        RollUpPrueba.main(args);
    }
}
