package Cubo.cubo_utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import Cubo.tablas.Dimension;

public abstract class OperacionesComunes {

    /**
     * Este método obtiene la clave de un mapa dado su valor.
     * Itera a través de las entradas del mapa y verifica si el valor coincide con el valor dado.
     * Si se encuentra una coincidencia, devuelve la clave correspondiente.
     *
     * @param <K> El tipo de la clave del mapa.
     * @param <V> El tipo del valor del mapa.
     * @param mapa El mapa del que se quiere obtener la clave.
     * @param valor El valor por el que se quiere buscar en el mapa.
     * @return La clave del mapa que coincide con el valor dado, o null si no se encuentra ninguna coincidencia.
     */
    public static <K, V> K obtenerClavePorValor(Map<K, V> mapa, V valor) {
        for (Map.Entry<K, V> entrada : mapa.entrySet()) {
            if (Objects.equals(entrada.getValue(), valor)) {
                return entrada.getKey();
            }
        }
        return null; 
    }

    /**
     * Obtiene los niveles de operación para una dimensión y nivel específicos.
     *
     * @param dimension la dimensión en la que se realizará la operación
     * @param nivel el nivel específico a partir del cual se obtendrán los niveles de operación
     * @param niveles_operacion la lista donde se guardan los niveles de operación
     * @return la lista de niveles de operación modificada
     */
    public static List<String> obtenerNivelesOperacion(Dimension dimension, String nivel, List<String> niveles_operacion) {

        // Obtengo el índice del nivel
        int indice_nivel = dimension.getIndicesNiveles().get(nivel);

        // Si es distinto de 0, es decir no es el más abstracto, debo incluir en la lista
        // todo los niveles detrás de él
        if (indice_nivel != 0) {
            for (int i = 0; i <= indice_nivel; i++) {

                // Obtengo el nivel por su indice
                String nivel_anterior = obtenerClavePorValor(dimension.getIndicesNiveles(), i);

                // Y lo agrego a la lista
                niveles_operacion.add(nivel_anterior);

            }
        } else {
            // Si es el más abstracto, simplemente lo agrego a la lista
            niveles_operacion.add(nivel);
        }

        // Retorno la lista 'niveles_operacion' modificada
        return niveles_operacion;
        }

}
