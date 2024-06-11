# Documentación del módulo `PruebasCubo`

## Descripción

Este módulo Java tiene el propósito de ser usado para probar el correcto funcionamiento de la clase `Cubo`. 

## Componentes

### **Clase `Inicializador`**:

Define un método estático `main` que se encarga de primero inicializar una instancia de `Cubo` para luego poder ejecutar los archivos que realizan pruebas
sobre los métodos de la misma, este orden es el que permite el correcto funcionamiento de las pruebas.

### **Clase `CuboPruebaManager`**:

Es la clase encargada de almacenar a una instancia de `Cubo` junto con sus datos relacionados para poder realizar pruebas sobre la misma en otros módulos posteriormente.

### **Clases `MetodoPrueba`**:

Son distintas clases que se encargan de probar los métodos de la clase `Cubo`.

## ¿Cómo se usa?

Primero debemos entrar al archivo de la clase `Inicializador`, veremos algo de este estilo:

```java
public class Inicializador {
    public static void main(String[] args) throws Exception {

        // Corro primero la generación de los datos
        // NO MODIFICAR
        CreacionPrueba.main(args);
        
        // Ejecuto los archivos que quiero probar
        MetodoPrueba.main(args);
    }
}
```

La parte de `CreacionPrueba.main(args)` no debe ser modificada, cuando queramos hacer una prueba sobre algún método solo debemos 
reemplazar la parte de `MetodoPrueba.main(args)` por la clase de prueba que represente al método que queremos probar.

Luego de hacer ese cambio lo siguiente es abrir una nueva terminal y ejecutar los siguientes comandos en el orden que se indica:

```sh
// Compilamos el inicializador 
javac -d bin -sourcepath src src/PruebasCubo/Inicializador.java

// Ejecutamos el inicializador 
java -cp bin PruebasCubo.Inicializador
```

Luego de haber ejecutado los comandos correctamente podremos visualizar el resultado de la prueba del método en la consola. **Ver** que de no ejecutar
estos comandos antes e ir directamente al ejecutor del IDE que estemos usando hará que **obtengamos un error de ejecución**.