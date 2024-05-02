package manejo_cubo;

import java.util.List;

public final class CuboOLAP {

    private final List<Dimension> dimensiones;
    private final Hecho hecho;
    private final String nombre_cubo;
    private final int max_dimensiones = 3;

    public CuboOLAP(String nombre_cubo, List<Dimension> dimensiones, Hecho hecho){
        this.nombre_cubo = nombre_cubo;
        this.hecho = hecho;

        if (dimensiones.size() > max_dimensiones){
            throw new IllegalArgumentException("El cubo no puede tener más de 3 dimensiones");
        }

        this.dimensiones = dimensiones;
    }

    public Dimension getDimension(String nombre_dimension) {

        // Recorro la lista de dimensiones
        for (Dimension dimension : dimensiones) {
            if (dimension.getNombreDimension().equals(nombre_dimension)) {
                return dimension;
            }
        }
        
        // sSi llegamos aquí y ninguna dimensión coincide con el nombre especificado
        throw new IllegalArgumentException("La dimensión especificada no existe en el cubo");
    }

    public Hecho getHecho(){
        return this.hecho;
    }

    public String getNombreCubo(){
        return this.nombre_cubo;
    }
    
}
