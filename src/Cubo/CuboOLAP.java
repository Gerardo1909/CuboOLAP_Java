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
import Cubo.tablas.Tabla;
import Cubo.utils.Visualizable;

public class CuboOLAP implements Visualizable {

    private final List<Dimension> dimensiones;
    private final Hecho hecho;
    private final String nombre;
    private Hecho tabla_operacion;
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

        // Y ahora en tabla_operacion guardo una gran tabla resultado de hacer merge a la tabla de hechos por cada dimensión,
        // esto servirá para realizar las operaciones
        Hecho hechos_merged = hecho.getHechoCopy();
        for (Dimension dimension : dimensiones) {
            Tabla.merge(hechos_merged, dimension, dimension.getPrimaryKey());
        }
        this.tabla_operacion = hechos_merged;
    }

    public void rollUp(List<String> criterio_reduccion, List<String> hechos_seleccionados, String agregacion) {

        String agregacion_parsed = agregacion.toLowerCase().trim();

        // Genero una instancia de RollUp
        ComandoRollUp comando = new ComandoRollUp(this.tabla_operacion, criterio_reduccion, hechos_seleccionados, agregacion_parsed);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación y lo guardo en el atributo 'estado_cubo'
        this.estado_cubo = comando.getResultado();
    }

    public void drillDown(String dimension) {
        new ComandoDrillDown(dimension).ejecutar();
    }

    public void slice(Dimension dimension,String nivel ,String valor_corte) {

        // Genero una instancia de Slice
        ComandoSlice comando = new ComandoSlice(this.tabla_operacion,dimension, nivel, valor_corte);

        // Ejecuto la operación
        comando.ejecutar();

        // Obtengo el resultado de la operación y lo guardo en el atributo 'estado_cubo'
        this.estado_cubo = comando.getResultado();

    }

    public void dice(Map<String, List<String>> filters) {
        new ComandoDice(filters).ejecutar();
    }
    
    @Override
    public void ver(int n_filas, List<String> columnas){
    
        // Verifico si el estado_cubo está vacío
        if (this.estado_cubo.isEmpty()) {
            System.out.println("No se han aplicado operaciones sobre el cubo.");
            return;
        }

        // Obtengo los índices de las columnas seleccionadas
        List<Integer> indices_columnas = new ArrayList<>();
        for (String columna : columnas) {
            indices_columnas.add(this.estado_cubo.keySet().iterator().next().indexOf(columna));
        }

        // Filtro la matriz resultante para quedarme solo con las columnas seleccionadas
        List<List<String>> matriz_filtrada = new ArrayList<>();
        for (List<String> fila : this.estado_cubo.values().iterator().next()) {
            List<String> fila_filtrada = new ArrayList<>();
            for (int indice : indices_columnas) {
                fila_filtrada.add(fila.get(indice));
            }
            matriz_filtrada.add(fila_filtrada);
        }


    
        // Muestro las columnas seleecionadas
        for (String columna : columnas) {
            System.out.print(String.format("%-20s", columna));
        }
        System.out.println();
    
        // Iterar sobre las primeras n_filas filas de la matriz resultado
        int contador_filas = 0;
        for (List<String> fila : matriz_filtrada) {
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

