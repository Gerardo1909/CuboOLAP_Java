package Cubo.comandos;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import Cubo.CuboOLAP;
import Cubo.excepciones.excepciones_tabla.TablaException;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

/**
 * Esta clase implementa el comando DrillDown para la clase {@link CuboOLAP}.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoDrillDown implements ComandoCubo {

    private Hecho tabla_operacion;
    private Dimension dimension_expansion;
    private String nivel_expansion;

    public ComandoDrillDown(Hecho tabla_operacion, Dimension dimension_expansion, String nivel_expansion) {

        // Inicializo los atributos que se verán implicados en la operación
        this.dimension_expansion = dimension_expansion;
        this.tabla_operacion = tabla_operacion;
        this.nivel_expansion = nivel_expansion;
    }

    @Override
    public void ejecutar() throws TablaException {

        // Obtengo el historial de operaciones RollUp de la dimensión que quiero desagrupar
        ComandoRollUp ultimoRollUp = ComandoRollUp.historial_operaciones.get(this.dimension_expansion);
    
        // Recupero el estado del cubo antes del último RollUp en esta dimensión
        this.tabla_operacion = ultimoRollUp.getTablaAOperacion();
    
        // Recupero y reejecuto todos los Slice y Dice realizados
        Deque<ComandoDice> ultimosCortes = ComandoDice.historial_operaciones;
    
        // Verifico que la pila de operaciones de corte no esté vacía
        if (!ultimosCortes.isEmpty()) {
            for (ComandoDice corteAplicado : ultimosCortes) {
                // Obtengo los criterios de corte aplicados
                Map<Dimension, Map<String, List<String>>> criteriosCorte = corteAplicado.getCriterios();
    
                // Genero una nueva instancia de ComandoDice con los criterios de corte actuales
                ComandoDice corteAAplicar = new ComandoDice(this.tabla_operacion, criteriosCorte);
    
                // Ejecuto la operación de corte
                corteAAplicar.ejecutar();
    
                // Actualizo la tabla de operación con el resultado del corte
                this.tabla_operacion = corteAAplicar.getResultado();
            }
        }
    
        // Desagregación de la dimensión objetivo
        ComandoRollUp desagregacion = new ComandoRollUp(this.tabla_operacion, this.dimension_expansion, this.nivel_expansion, "sum");
        desagregacion.ejecutar();
        this.tabla_operacion = desagregacion.getResultado();
    
        // Regreso a las agrupaciones anteriores de otras dimensiones si fueron desagrupadas
        boolean empiezoAIterar = false;
        for (Map.Entry<Dimension, ComandoRollUp> entry : ComandoRollUp.historial_operaciones.entrySet()) {
            if (entry.getKey().equals(this.dimension_expansion)) {
                empiezoAIterar = true;
                continue;
            }
            if (empiezoAIterar) {
                // Reaplico las operaciones RollUp en dimensiones subyacentes
                ComandoRollUp histComando = entry.getValue();
                ComandoRollUp comandoDim = new ComandoRollUp(this.tabla_operacion, histComando.getDimensionReduccion(),
                                                             histComando.getNivelReduccion(), "sum");
                comandoDim.ejecutar();
                this.tabla_operacion = comandoDim.getResultado();
            }
        }
    }

    public Hecho getResultado() {
        return this.tabla_operacion;
    }
}
    

