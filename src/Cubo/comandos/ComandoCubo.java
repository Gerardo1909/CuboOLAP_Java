package Cubo.comandos;

import Cubo.excepciones.excepciones_tabla.TablaException;

/**
 * Esta interfaz representa un comando que se puede ejecutar en un cubo.
 * Define un único método, {@link #ejecutar()}, que debe ser implementado por cualquier clase que implemente esta interfaz.
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
