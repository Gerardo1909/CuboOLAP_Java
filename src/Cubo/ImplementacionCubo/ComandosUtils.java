package Cubo.implementacionCubo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import Cubo.tablasCubo.Dimension;

/**
 * Clase que contiene métodos de ayuda para la implementación de las clases que implementan {@link ComandoCubo}.
 */
class ComandosUtils {

    // Constructor privado para evitar la instanciación
    private ComandosUtils() {
        throw new AssertionError("Esta clase no debe ser instanciada.");
    }

    /**
     * Este método obtiene la clave de un mapa dado su valor.
     *
     * @param <K> El tipo de la clave del mapa.
     * @param <V> El tipo del valor del mapa.
     * @param mapa El mapa del que se quiere obtener la clave.
     * @param valor El valor por el que se quiere buscar en el mapa.
     * 
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
     * Toma el nivel de una dimensión dada y obtiene todos los niveles
     * más abstractos que él, guardándolos en una lista.
     *
     * @param dimension La dimensión en la que se realizará la operación
     * @param nivel El nivel específico a partir del cual se obtendrán los niveles de operación
     * @param nivelesOperacion La lista donde se guardan los niveles de operación
     * 
     * @return La lista de niveles de operación modificada.
     */
    public static List<String> obtenerNivelesOperacion(Dimension dimension, String nivel, List<String> nivelesOperacion) {
        int indice_nivel = dimension.getIndicesNiveles().get(nivel);
        if (indice_nivel != 0) {
            // Si es distinto de 0, es decir no es el más abstracto, debo incluir en la lista
            // todo los niveles detrás de él
            for (int i = 0; i <= indice_nivel; i++) {
                String nivel_anterior = obtenerClavePorValor(dimension.getIndicesNiveles(), i);
                nivelesOperacion.add(nivel_anterior);
            }
        } else {
            // Si es el más abstracto, simplemente lo agrego a la lista
            nivelesOperacion.add(nivel);
        }
        // Retorno la lista 'nivelesOperacion' modificada
        return nivelesOperacion;
    }

}
