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
    private Hecho data;

    public CuboOLAP(String nombre, Hecho hecho, List<Dimension> dimensiones){

        // Verifico que en la tabla de hechos estén todas las claves primarias de las dimensiones
        for (Dimension dimension : dimensiones){

            if (!hecho.getHeaders().contains(dimension.getPrimaryKey())){
                throw new IllegalArgumentException("La clave primaria "+ dimension.getPrimaryKey() + " no está en la tabla de hechos");
            }

        }

        // Una vez verificado asigno los valores
        this.dimensiones = List.copyOf(dimensiones);
        this.hecho = hecho;
        this.nombre = nombre;

        // Y ahora en data guardo una gran tabla resultado de hacer merge a la tabla de hechos por cada dimensión,
        // esto servirá para realizar las operaciones
        Hecho hechos_merged = hecho.mergeDimension(dimensiones.get(0), dimensiones.get(0).getPrimaryKey());
        for (int i = 1; i < dimensiones.size(); i++){
            hechos_merged = hechos_merged.mergeDimension(dimensiones.get(i), dimensiones.get(i).getPrimaryKey());
        }
        this.data = hechos_merged;
    }

    public Map<List<String>, List<Double>> rollUp(List<String> criterio_reduccion) {

        // Genero una instancia de RollUp
        ComandoRollUp comando = new ComandoRollUp(this.data, criterio_reduccion);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación
        return comando.getResultado();

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
