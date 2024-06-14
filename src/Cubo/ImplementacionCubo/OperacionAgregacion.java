package Cubo.ImplementacionCubo;

import java.util.List;

/**
 * Enum que contiene las operaciones de agregación permitidas en el método RollUp.
 */
enum OperacionAgregacion {
    /**
     * Operación de suma.
     */
    SUM("sum") {
        /**
         * Suma los valores de todos los hechos agregados.
         * 
         * @param hechosAgregados La lista de valores de los hechos implicados en la operación.
         * 
         * @return Un valor que representa a todos los hechos agregados sumados.
         */
        @Override
        public double aplicar(List<Double> hechosAgregados) {
            double sumaHechos = 0;
            for (Double hecho : hechosAgregados) {
                if (hecho != null) {
                    sumaHechos += hecho;
                }
            }
            return sumaHechos;
        }
    },
    /**
     * Operación de hallar máximo.
     */
    MAX("max") {
        /**
         * Encuentra el valor máximo entre los hechos agregados.
         * 
         * @param hechosAgregados La lista de valores de los hechos implicados en la operación.
         * 
         * @return El valor máximo entre los hechos en la lista.
         */
        @Override
        public double aplicar(List<Double> hechosAgregados) {
            double maxHecho = Double.MIN_VALUE;
            for (Double hecho : hechosAgregados) {
                if (hecho != null && hecho > maxHecho) {
                    maxHecho = hecho;
                }
            }
            return maxHecho;
        }
    },
    /**
     * Operación de hallar mínimo.
     */
    MIN("min") {
        /**
         * Encuentra el valor mínimo entre los hechos agregados.
         * 
         * @param hechosAgregados La lista de valores de los hechos implicados en la operación.
         * 
         * @return El valor mínimo entre los hechos en la lista.
         */
        @Override
        public double aplicar(List<Double> hechosAgregados) {
            double minHecho = Double.MAX_VALUE;
            for (Double hecho : hechosAgregados) {
                if (hecho != null && hecho < minHecho) {
                    minHecho = hecho;
                }
            }
            return minHecho;
        }
    },
    /**
     * Operación de conteo.
     */
    COUNT("count") {
        /**
         * Aplica la operación de conteo a los hechos agregados.
         * 
         * @param hechosAgregados La lista de valores de los hechos implicados en la operación.
         * 
         * @return El número de hechos presentes en la lista.
         */
        @Override
        public double aplicar(List<Double> hechosAgregados) {
            return hechosAgregados.size();
        }
    };

    // Atributos del enum OperacionAgregacion
    private final String operacion;

    /**
     * <p>
     * <b>Constructor para el enum OperacionAgregacion.</b>
     * </p>
     * 
     * <p>
     * Asocia un String que representa el nombre de la operación con una instancia de este enum.
     * </p>
     * 
     * @param operacion El nombre de la operación.
     */
    OperacionAgregacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * Obtiene el nombre de la operación que contiene 
     * una instancia de este enum.
     * 
     * @return El nombre de la operación.
     */
    private String getOperacion() {
        return this.operacion;
    }

    /**
     * Aplica la operación de agregación a una lista de valores
     * que representan los hechos seleccionados en la operación
     * rollUp.
     * 
     * @param hechosAgregados La lista de valores de los hechos implicados en la operación.
     * 
     * @return El resultado de la operación de agregación.
     */
    public abstract double aplicar(List<Double> hechosAgregados);

    /**
     * Verifica si la operación de agregación ingresada en formato
     * cadena coincide con una de las operaciones disponibles en este enum.
     * 
     * @param op la operación de agregación a verificar
     * 
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
