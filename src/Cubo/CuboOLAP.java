package Cubo;

import java.util.List;
import java.util.Map;
import Cubo.comandos.ComandoDice;
import Cubo.comandos.ComandoDrillDown;
import Cubo.comandos.ComandoRollUp;
import Cubo.comandos.ComandoSlice;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

public class CuboOLAP {

    private final List<Dimension> dimensiones;
    private final Hecho hecho;
    private final String nombre;

    public CuboOLAP(String nombre, Hecho hecho, List<Dimension> dimensiones){

        this.dimensiones = List.copyOf(dimensiones);
        this.hecho = hecho;
        this.nombre = nombre;

    }

    public void rollUp(String dimension) {
        new ComandoRollUp(dimension).ejecutar();
    }

    public void drillDown(String dimension) {
        new ComandoDrillDown(dimension).ejecutar();
    }

    public void slice(String dimension, String value) {
        new ComandoSlice(dimension, value).ejecutar();
    }

    public void dice(Map<String, List<String>> filters) {
        new ComandoDice(filters).ejecutar();
    }
    
}
