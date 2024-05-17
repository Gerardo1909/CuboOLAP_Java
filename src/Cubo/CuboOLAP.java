package Cubo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Cubo.comandos.ComandoDice;
import Cubo.comandos.ComandoDrillDown;
import Cubo.comandos.ComandoRollUp;
import Cubo.comandos.ComandoSlice;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;
import Cubo.utils.Visualizable;

public class CuboOLAP implements Visualizable {

    private final List<Dimension> dimensiones;
    private final Hecho hecho;
    private final String nombre;
    private Hecho data;
    private Map<List<String>, List<List<String>>> estado_cubo;

    public CuboOLAP(String nombre, Hecho hecho, List<Dimension> dimensiones){

        // Verifico que en la tabla de hechos estén todas las claves primarias de las dimensiones
        for (Dimension dimension : dimensiones){

            if (!hecho.getHeaders().contains(dimension.getPrimaryKey())){
                throw new IllegalArgumentException("La clave primaria "+ dimension.getPrimaryKey() + " no está en la tabla de hechos");
            }

        }

        // Una vez verificado asigno los valores
        this.dimensiones = new ArrayList<>(dimensiones);
        this.hecho = hecho;
        this.nombre = nombre;

        // Y ahora en data guardo una gran tabla resultado de hacer merge a la tabla de hechos por cada dimensión,
        // esto servirá para realizar las operaciones
        Hecho hechos_merged = hecho.getHechoCopy();
        for (Dimension dimension : dimensiones) {
            hechos_merged = hechos_merged.mergeDimension(dimension, dimension.getPrimaryKey());
        }
        this.data = hechos_merged;
    }

    public void rollUp(List<String> criterio_reduccion, String agregacion) {

        // Genero una instancia de RollUp
        ComandoRollUp comando = new ComandoRollUp(this.data, criterio_reduccion, agregacion);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación y lo guardo en el atributo 'estado_cubo'
        this.estado_cubo = comando.getResultado();
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
    
    @Override
    public void ver(int n_filas, List<String> columnas){
    
        // Verificar si el estado_cubo está vacío
        if (this.estado_cubo.isEmpty()) {
            System.out.println("El estado del cubo está vacío.");
            return;
        }
    
        // Iterar sobre las columnas seleccionadas y mostrarlas
        for (String columna : columnas) {
            System.out.print(String.format("%-20s", columna));
        }
        System.out.println();
    
        // Iterar sobre las primeras n_filas filas de la matriz resultado
        int contador_filas = 0;
        for (List<String> fila : this.estado_cubo.values().iterator().next()) {
            // Verificar si se ha alcanzado el número máximo de filas
            if (contador_filas >= n_filas) {
                break;
            }
            // Imprimir los datos de la fila actual
            for (String elemento : fila) {
                System.out.print(String.format("%-20s", elemento));
            }
            System.out.println();
            contador_filas++;
        }
    }
}

