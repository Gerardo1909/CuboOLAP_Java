package Cubo.implementacionCubo;

/**
 * <p>
 * Esta interfaz define la ejecución de los métodos en la clase {@link Cubo}.
 * </p>
 * 
 * <p>
 * Está inspirada en el patrón de diseño "Command", en donde cada método tiene su propia clase que 
 * se encarga de su implementación.
 * </p>
 */
interface ComandoCubo {
    /**
     * Firma para la ejecución de los métodos en la clase {@link Cubo}.
     */
    public void ejecutar();
}
