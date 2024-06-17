package Cubo.implementacionCubo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Cubo.tablasCubo.Dimension;
import Cubo.tablasCubo.Hecho;
import Cubo.tablasCubo.Tabla;

/** 
 * <p>
 * Esta clase representa la estructura interna de una instancia de la clase
 * {@link Cubo}.
 * </p>
 * 
 * <p>
 * Esta clase extiende de la clase {@link Tabla}.
 * </p>
 */
class CuerpoCubo extends Tabla{

    // Atributos de la clase CuerpoCubo
    private final List<String> hechosCubo;


    // Métodos para la creación de instancias de la clase CuerpoCubo

    /**
     * Método para crear el <b>cuerpo interno de un cubo</b>, el cual es una instancia de CuerpoCubo.
     *
     * @param tablaHechos La tabla de hechos asociada a las dimensiones del cubo que representa esta instancia.
     * @param tablasDimensiones Una lista que contiene todas las dimensiones asociadas al cubo que representa esta instancia.
     * 
     * @return EL cuerpo del cubo configurado y preparado para realizar operaciones.
     */
    public static CuerpoCubo configurarCubo(Hecho tablaHechos, List<Dimension> tablasDimensiones, Map<Dimension, String> clavesForaneasDims){

        // Inicializo el cuerpo del cubo obteniendo la información de la tabla de hechos
        CuerpoCubo cuerpoCubo = new CuerpoCubo(tablaHechos.getDatosTabla(), tablaHechos.getHeaders(), tablaHechos.getHechos());

        // Fusiono la tabla de hechos con cada una de las tablas de dimensiones
        for (Dimension dimension : tablasDimensiones) {
            String claveForanea = clavesForaneasDims.get(dimension);
            cuerpoCubo.merge(cuerpoCubo, claveForanea, dimension, dimension.getPrimaryKey());
        }
        
        // Elimino las columnas de las claves primarias y foráneas de las dimensiones
        // ya que una vez fusionadas con la tabla de hechos estas no sirven más
        for (Dimension dimension : tablasDimensiones){
            cuerpoCubo.eliminarColumna(dimension.getPrimaryKey());
            cuerpoCubo.eliminarColumna(clavesForaneasDims.get(dimension));
        }

        // Retorno el cuerpo del cubo configurado
        return cuerpoCubo;
    }

    /**
     * <p>
     * <b>Constructor para la clase CuerpoCubo.</b>
     * </p>
     * 
     * <p>
     * Se encarga de inicializar el "cuerpo" del cubo que representa esta instancia, guardando
     * toda la información interna del mismo.
     * </p>
     *
     * @param datosTabla La matriz de información interna del cubo que representa esta instancia.
     * @param headers Una lista que contiene los encabezados de las tablas contenidas en el cubo que representa esta instancia.
     * @param hechosCubo Una lista que contiene los nombres de los hechos presentes en el cubo que representa esta instancia.
     */
    protected CuerpoCubo(List<List<String>> datosTabla, List<String> headers, List<String> hechosCubo) {
        super("Estructura interna del cubo",datosTabla, headers);
        this.hechosCubo = hechosCubo;
    }


    // Métodos de ayuda para la clase

    /**
     * Método para fusionar la información contenida en dos objetos de tipo tabla.
     * 
     * @param tablaIzq Tabla izquierda a fusionar.
     * @param claveTablaIzq Clave de la tabla izquierda.
     * @param tablaDer Tabla derecha a fusionar.
     * @param claveTablaDer Clave de la tabla derecha.
     * 
     */
    private void merge(Tabla tablaIzq, String claveTablaIzq, Tabla tablaDer, String claveTablaDer) {

        // Obtengo los índices de las claves correspondientes en ambas tablas
        int indiceClaveIzq = tablaIzq.getHeaders().indexOf(claveTablaIzq);
        int indiceClaveDer = tablaDer.getHeaders().indexOf(claveTablaDer);
    
        // Preparo la lista de headers y datos resultantes
        List<String> headersResultado = new ArrayList<>(tablaIzq.getHeaders());
        headersResultado.addAll(tablaDer.getHeaders());
        List<List<String>> datosResultado = new ArrayList<>();
    
        // Genero un mapa que me ayudará a agrupar las filas de la 
        // tabla izquierda según el valor de su clave
        Map<String, List<List<String>>> mapaIzq = new LinkedHashMap<>();

        // Itero sobre la tabla izquierda y voy agrupando las filas
        for (List<String> fila : tablaIzq.getDatosTabla()) {
            if (fila == null) continue;
            String clave = fila.get(indiceClaveIzq);
            mapaIzq.computeIfAbsent(clave, k -> new ArrayList<>()).add(fila);
        }
    
        // Itero sobre la tabla derecha y voy agrupando sus filas
        // junto con las filas de la tabla izquierda
        for (List<String> filaDer : tablaDer.getDatosTabla()) {
            if (filaDer == null) continue;
            String clave = filaDer.get(indiceClaveDer);
            List<List<String>> filasIzq = mapaIzq.get(clave);
            if (filasIzq != null) {
                for (List<String> filaIzq : filasIzq) {
                    List<String> filaCombinada = new ArrayList<>(filaIzq);
                    filaCombinada.addAll(filaDer);
                    datosResultado.add(filaCombinada);
                }
            }
        }
    
        // Actualizo la información interna de esta instancia
        this.datosTabla = datosResultado;
        this.headers = headersResultado;
    }

    /**
     * <p>
     * Elimina todas las columnas de la tabla que tengan el nombre especificado.
     * </p>
     * 
     * <p>
     * Si no se encuentra ninguna columna con el nombre especificado, no se hace nada
     * sobre la tabla.
     * </p>
     *
     * @param nombreColumna El nombre de la columna que se va a eliminar.
     */
    private void eliminarColumna(String nombreColumna){

        // Encuentro todos los indices de las columnas que coinciden con el nombre
        List<Integer> indicesColumnas = new ArrayList<>();
        for (int i = 0; i < this.headers.size(); i++) {
            if (this.headers.get(i).equals(nombreColumna)) {
                indicesColumnas.add(i);
            }
        }

        // Si no se encontraron índices, no se hace nada
        if (indicesColumnas.isEmpty()) {
            return;
        }

        // Elimino los encabezados de las columnas en orden inverso para evitar problemas de desplazamiento
        Collections.sort(indicesColumnas, Collections.reverseOrder());
        for (int indiceColumna : indicesColumnas) {
            this.headers.remove(indiceColumna);
        }

        // Elimino los datos de las columnas en cada fila
        for (List<String> fila : this.datosTabla) {
            for (int indiceColumna : indicesColumnas) {
                if (fila.size() > indiceColumna) {
                    fila.remove(indiceColumna);
                }
            }
        }
    }


    // Getters de la clase

    /**
     * Obtiene los hechos presentes en el cubo 
     * que representa esta instancia.
     *
     * @return Una lista que contiene los nombres
     *         de los hechos presentes en el cubo.
     */
    public List<String> getHechosCubo() {
        return new ArrayList<>(hechosCubo);
    }    

    /**
     * Devuelve una copia profunda del cuerpo del cubo
     * que representa esta instancia.
     *
     * @return Una copia profunda del cuerpo del cubo.
     */
    public CuerpoCubo getCuerpoCopy(){
        return new CuerpoCubo(this.getDatosTabla(), this.getHeaders(), this.getHechosCubo());
    }

}
