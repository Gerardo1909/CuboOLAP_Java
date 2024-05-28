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
    private Map<Dimension, ComandoRollUp> historial_rollup;
    private Deque<ComandoDice> historial_dice;

    public ComandoDrillDown(Hecho tabla_operacion, Dimension dimension_expansion, 
                            String nivel_expansion, Map<Dimension, ComandoRollUp> historial_rollup, Deque<ComandoDice> historial_dice) {

        // Inicializo los atributos que se verán implicados en la operación
        this.dimension_expansion = dimension_expansion;
        this.tabla_operacion = tabla_operacion;
        this.nivel_expansion = nivel_expansion;
        this.historial_rollup = historial_rollup;
        this.historial_dice = historial_dice;
    }

    @Override
    public void ejecutar() throws TablaException {

        // Obtengo el historial de operaciones RollUp de la dimensión que quiero desagrupar
        ComandoRollUp ultimoRollUp = this.historial_rollup.get(this.dimension_expansion);
    
        // Recupero el estado del cubo antes del último RollUp en esta dimensión
        this.tabla_operacion = ultimoRollUp.getTablaAOperacion();
    
        // Recupero y reejecuto todos los Slice y Dice realizados
        Deque<ComandoDice> ultimosCortes = this.historial_dice;
    
        // Verifico que la pila de operaciones de corte no esté vacía
        if (!ultimosCortes.isEmpty()) {
            for (ComandoDice corteAplicado : ultimosCortes) {
                // Obtengo los criterios de corte aplicados
                Map<Dimension, Map<String, List<String>>> criteriosCorte = corteAplicado.getCriterios();
    
                // Genero una nueva instancia de ComandoDice con los criterios de corte actuales
                ComandoDice corteAAplicar = new ComandoDice(this.tabla_operacion, criteriosCorte, this.historial_dice);
    
                // Ejecuto la operación de corte
                corteAAplicar.ejecutar();
    
                // Actualizo la tabla de operación con el resultado del corte
                this.tabla_operacion = corteAAplicar.getResultado();
            }
        }
    
        // Desagregación de la dimensión objetivo
        ComandoRollUp desagregacion = new ComandoRollUp(this.tabla_operacion, this.dimension_expansion, 
                                                        this.nivel_expansion, "sum", this.historial_rollup);
        desagregacion.ejecutar();
        this.tabla_operacion = desagregacion.getResultado();
    
        // Regreso a las agrupaciones anteriores de otras dimensiones si fueron desagrupadas
        boolean empiezoAIterar = false;
        for (Map.Entry<Dimension, ComandoRollUp> entry : this.historial_rollup.entrySet()) {
            if (entry.getKey().equals(this.dimension_expansion)) {
                empiezoAIterar = true;
                continue;
            }
            if (empiezoAIterar) {
                // Reaplico las operaciones RollUp en dimensiones subyacentes
                ComandoRollUp histComando = entry.getValue();
                ComandoRollUp comandoDim = new ComandoRollUp(this.tabla_operacion, histComando.getDimensionReduccion(),
                                                             histComando.getNivelReduccion(), "sum", this.historial_rollup);
                comandoDim.ejecutar();
                this.tabla_operacion = comandoDim.getResultado();
            }
        }
    }

    public Hecho getResultado() {
        return this.tabla_operacion;
    }

    public Map<Dimension, ComandoRollUp> getHistRollUp(){
        return this.historial_rollup;
    }

    public Deque<ComandoDice> getHistDice(){
        return this.historial_dice;
    }

}
    

