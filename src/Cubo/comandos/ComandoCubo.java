package Cubo.comandos;

import Cubo.excepciones.excepciones_tabla.TablaException;

/**
 * Esta interfaz define la ejecución de métodos en cubos OLAP.
 * Proporciona la firma de un método para ejecutar los métodos de la clase {@link CuboOLAP}.
 */
public interface ComandoCubo {
    /**
     * Ejecuta el comando en el cubo.
     * No se requieren parámetros para este método.
     * El método no devuelve ningún valor, cambia el estado interno del cubo que lo invoca.
     * @throws TablaException Si ocurre algún problema al ejecutar el comando.
     */
    public void ejecutar() throws TablaException;
}
