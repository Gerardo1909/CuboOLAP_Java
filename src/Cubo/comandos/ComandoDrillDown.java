package Cubo.comandos;

import java.util.List;
import java.util.Map;

import Cubo.CuboOLAP;
import Cubo.excepciones.excepciones_tabla.TablaException;

/**
 * Esta clase implementa el comando DrillDown para la clase {@link CuboOLAP}.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoDrillDown implements ComandoCubo {

    private String dimension;
    private Map<List<String>, List<List<String>>> resultado;


    public ComandoDrillDown(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public void ejecutar() throws TablaException {
        // Aquí va la implementación de Drill-down
        System.out.println("Realizando Drill-down en la dimensión: " + dimension);
    }

    public Map<List<String>, List<List<String>>> getResultado() {
        return this.resultado;
    }
}
    

