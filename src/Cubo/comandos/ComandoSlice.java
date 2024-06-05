package Cubo.comandos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import Cubo.CuboOLAP;
import Cubo.tablas.Dimension;
import Cubo.tablas.Hecho;

/**
 * Esta clase implementa el comando Slice para la clase {@link CuboOLAP}.
 * "Corta" los hechos según la dimensión, nivel y valor especificados, resultando un cubo 
 * de menor dimensionalidad pero con información según los criterios especificados.
 * Implementa la interfaz {@link ComandoCubo}.
 */
public class ComandoSlice implements ComandoCubo {

    private Hecho tabla_operacion;
    private Dimension dimension;
    private String nivel;
    private String valor_corte;
    private List<ComandoSlice> historial_slice;

    /**
     * Constructor para la clase ComandoSlice.
     *
     * @param tabla_operacion La tabla de hechos que se utilizará para llevar a cabo la operación.
     * @param dimension La dimensión en la que se va a cortar.
     * @param nivel El nivel dentro de la dimensión en la que se va a cortar.
     * @param valor_corte El valor para filtrar.
     * @param historial_slice El historial de operaciones Slice aplicados sobre la instancia de 'CuboOLAP' que 
     *                        invoca esta clase.
     */
    public ComandoSlice(Hecho tabla_operacion, Dimension dimension, String nivel,
                        String valor_corte, List<ComandoSlice> historial_slice) {
        this.dimension = dimension;
        this.valor_corte = valor_corte;
        this.tabla_operacion = tabla_operacion;
        this.nivel = nivel;
        this.historial_slice = historial_slice;
    }

    /**
     * Ejecuta el comando Slice.
     * Ejecuta la operación de corte en tabla_operacion', 
     * y almacena el resultado en la misma 'tabla_operacion', alterando el estado del cubo.
     */
    @Override
    public void ejecutar(){

        // Añado al historial el comando antes de ejecutarlo
        this.historial_slice.add(this);

        // Primero obtengo el índice del nivel por el cual se va a filtrar
        Integer indice_nivel = this.tabla_operacion.getHeaders().indexOf(this.nivel);

        // Genero la tabla que contendrá a la operación resultante 
        List<List<String>> operacion_resultante = new ArrayList<>();

        // Ahora itero por cada fila de 'tabla_operacion' y me quedo con aquellas que cumplan con la condición de corte
        for (List<String> fila : this.tabla_operacion.getData()){

            // Me paro en aquellas filas que coincidan con el valor de corte
            if (fila.get(indice_nivel).equals(this.valor_corte)) {

                // Creo una nueva fila que será la fila filtrada sin los niveles de la dimensión de corte
                List<String> nueva_fila = new ArrayList<>(fila);

                // Ahora debo eliminar todos los niveles de la dimensión que corto
                for (String columna : this.dimension.getHeaders()) {
                    // Obtengo el índice del nivel a eliminar
                    int indice_nivel_eliminar = this.tabla_operacion.getHeaders().indexOf(columna);
                    if (indice_nivel_eliminar != -1 && indice_nivel_eliminar < nueva_fila.size()) {
                        nueva_fila.set(indice_nivel_eliminar, null);  // Marcamos el elemento para eliminación
                    }
                }

                // Remover todos los elementos marcados (null)
                nueva_fila.removeIf(Objects::isNull);

                // Añadir la fila filtrada a la operación resultante
                operacion_resultante.add(nueva_fila);

            }
        }

        // Genero una lista para guardar los headers de la operacion resultante
        List<String> headers_operacion = new ArrayList<>(this.tabla_operacion.getHeaders());
        headers_operacion.removeAll(this.dimension.getHeaders());

        // Finalmente modifico 'tabla_operacion'
        try {
            this.tabla_operacion = new Hecho(this.tabla_operacion.getNombre(), operacion_resultante, headers_operacion, this.tabla_operacion.getHechos());
        } catch (Exception e) {
            // Esta excepcion no debería ocurrir, ya que la tabla de hechos original debería ser válida
            System.err.println(e.getMessage());
        }

    }

    /**
     * Devuelve la 'tabla_operacion' del cubo con el método ya aplicado.
     *
     * @return Un objeto de tipo hecho que representa la tabla sobre la cual se ejecutan las operaciones del cubo.
     */
    public Hecho getResultado() {
        return this.tabla_operacion;
    }

    /**
     * Devuelve el historial de operaciones Slice aplicadas sobre la instancia de 'CuboOLAP' que invoca esta clase
     * con la instancia que se encargó de ejecutar este método ya añadida.
     *
     * @return Una lista con datos de tipo ComandoSlice que representan las operaciones Slice efectuadas sobre el cubo.
     */
    public List<ComandoSlice> getHistorial(){
        return this.historial_slice;
    }

}
