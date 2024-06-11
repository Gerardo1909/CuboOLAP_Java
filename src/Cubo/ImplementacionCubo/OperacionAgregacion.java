package Cubo.ImplementacionCubo;

import java.util.List;

/**
 * Enum que contiene las operaciones de agregación permitidas en el método RollUp.
 */
public enum OperacionAgregacion {
    /**
     * Operación de suma.
     */
    SUM("sum") {
        /**
         * Aplica la operación de suma a una lista de valores.
         * @param values La lista de valores.
         * @return El resultado de la suma.
         */
        @Override
        public double aplicar(List<Double> values) {
            return values.stream().mapToDouble(Double::doubleValue).sum();
        }
    },
    /**
     * Operación de máximo.
     */
    MAX("max") {
        /**
         * Aplica la operación de máximo a una lista de valores.
         * @param values La lista de valores.
         * @return El máximo valor.
         */
        @Override
        public double aplicar(List<Double> values) {
            return values.stream().mapToDouble(Double::doubleValue).max().orElse(Double.MIN_VALUE);
        }
    },
    /**
     * Operación de mínimo.
     */
    MIN("min") {
        /**
         * Aplica la operación de mínimo a una lista de valores.
         * @param values La lista de valores.
         * @return El mínimo valor.
         */
        @Override
        public double aplicar(List<Double> values) {
            return values.stream().mapToDouble(Double::doubleValue).min().orElse(Double.MAX_VALUE);
        }
    },
    /**
     * Operación de conteo.
     */
    COUNT("count") {
        /**
         * Aplica la operación de conteo a una lista de valores.
         * @param values La lista de valores.
         * @return El número de elementos en la lista.
         */
        @Override
        public double aplicar(List<Double> values) {
            return values.size();
        }
    };

    private final String operacion;

    /**
     * Constructor de OperacionAgregacion.
     * @param operacion El nombre de la operación.
     */
    OperacionAgregacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * Obtiene el nombre de la operación.
     * @return El nombre de la operación.
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * Aplica la operación de agregación a una lista de valores.
     * @param values La lista de valores.
     * @return El resultado de la operación de agregación.
     */
    public abstract double aplicar(List<Double> values);

    /**
     * Verifica si una operación de agregación es válida.
     * 
     * @param op la operación de agregación a verificar
     * @return true si la operación es válida, false de lo contrario
     */
    public static boolean esOperacionValida(String op) {
        for (OperacionAgregacion operacion : OperacionAgregacion.values()) {
            if (operacion.getOperacion().equals(op)) {
                return true;
            }
        }
        return false;
    }


}
