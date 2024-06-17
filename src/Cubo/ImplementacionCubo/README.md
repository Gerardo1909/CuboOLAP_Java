# Documentación del módulo `implementacionCubo`

Este módulo, como su nombre indica, agrupa todas las clases necesarias para implementar la clase `Cubo`. En él se incluyen todos los componentes que gestionan las funcionalidades de la clase, desde las clases que representan las operaciones OLAP hasta la clase que define la estructura interna del cubo.

Aunque las clases contenidas en este módulo no son públicas para los usuarios de la librería (excepto la clase `Cubo`), esta documentación es útil para comprender el manejo de las operaciones internas y los beneficios de utilizar esta estructura modular.

## Representación de la estructura interna del cubo

La clase `CuerpoCubo` representa la estructura interna de un cubo OLAP y extiende la clase `Tabla`, por lo tanto se puede pensar a dicha estructura como una **"gran tabla"** que contiene a todas las tablas de dimensiones y tabla de hechos que se incluyen en el cubo. Esta clase es crucial para la gestión y manipulación de los datos del cubo, integrando la tabla de hechos y las tablas de dimensiones. 

### Creación del Cubo OLAP

Cuando se crea una instancia de la clase `Cubo`, se llama al método estático `CuerpoCubo.configurarCubo` perteneciente a `CuerpoCubo` para inicializar su estructura interna. 

Este método se encarga de fusionar la tabla de hechos que se pasó como argumento junto con todas las dimensiones que están en la lista `tablasDimensiones`,
logrando así este efecto de **"gran tabla"** que luego será útil para aplicar todas las operaciones que la librería ofrece.

### Efecto en las Operaciones OLAP

Una vez configurado, `CuerpoCubo` permite la ejecución de diversas operaciones OLAP, como `Roll-Up`, `Drill-Down`, `Slice` y `Dice`. Estas operaciones manipulan la estructura interna del cubo, permitiendo un análisis multidimensional de los datos.

Cada operación OLAP modifica la estructura interna del cubo, ya sea reduciendo o aumentando la cantidad de información. En muchas de estas operaciones, se involucran todas las dimensiones o solo algunas. Para lograr el comportamiento esperado, el enfoque de la **"gran tabla"** es perfecto, ya que permite seleccionar únicamente las columnas y filas necesarias para ejecutar la operación correctamente, eliminando comportamientos inesperados.

### Restauración del Estado Original

Cada instancia de la clase `Cubo` guarda una copia de la instancia de `CuerpoCubo` que representa su estructura interna. Esto permite reiniciar el cubo cuando el usuario necesite explorar otros aspectos de las tablas de hechos y dimensiones contenidas en él, ofreciendo un flujo de trabajo dinámico y eliminando la necesidad de crear un nuevo cubo OLAP cada vez que se requiera observar diferentes aspectos del mismo.

### Resumen

En resumen, `CuerpoCubo` es fundamental para la representación y manipulación de la estructura interna de un cubo OLAP. Su rol abarca desde la inicialización y configuración del cubo hasta la gestión de operaciones OLAP complejas, asegurando que los datos se puedan analizar de manera eficiente y flexible.

## Métodos OLAP representados como "Comandos"

Para implementar los métodos OLAP que ofrece la clase `Cubo`, optamos por usar **el patrón de diseño Command** para encapsular las operaciones que se realizan sobre una instancia de `Cubo`. El patrón permite encapsular los métodos como objetos, logrando desacoplar el código y haciendo fáciles de mantener y extender a los mismos. 

### Componentes

#### **Interfaz `ComandoCubo`**:

Define el método `ejecutar()` que implementan las clases de los métodos, dicho método es el que se ejecuta internamente dentro de una instancia de `Cubo` cada vez que se invoca uno de sus métodos.

#### **Clases que implementan `ComandoCubo`**:

Dentro de este módulo viven 4 clases que representan las operaciones disponibles en las intancias de `Cubo`, todas implementan la interfaz `ComandoCubo` y por lo tanto cada una tiene un método `ejecutar()`. Además cada una tiene asociado un **historial** el cual está presente en una instancia de la clase `Cubo` y que como su nombre sugiere, se encarga de llevar registro de las operaciones realizadas, esto con el motivo de realizar operaciones del estilo drill-down que requieren la desagrupación de la información.

Los parámetros definidos en la clase `Cubo` para invocar los métodos, desde la perspectiva de estas clases representan atributos que son trabajados dentro de cada una para lograr el resultado esperado. A continuación una lista de las clases que viven dentro de este módulo:

* `ComandoDice`
* `ComandoDrillDown`
* `ComandoRollUp`
* `ComandoSlice`

Cada una se encargar de implementar el comportamiento esperado de su método correspondiente y devolver el resultado del mismo mediante un **getter**, que dentro de cada clase tiene el nombre de `getResultado()`.

### ¿Cómo funciona?

Cada vez que desde una clase `Main` se crea una instancia de `Cubo` y se invoca a uno de sus métodos internamente se llevan a cabo los siguientes pasos:

1. Se reciben los argumentos una vez invocado el método y se hacen los chequeos correspondientes para evitar resultados inesperados.

2. Dentro del método invocado se crea un objeto que implementa la interfaz `ComandoCubo` y a su constructor se le pasan los argumentos ya verificados.

3. Se ejecuta el método `ejecutar()` y dentro de la clase que implementa `ComandoCubo` se lleva a cabo la operación.

4. Mediante el getter `getHistorial()` se obtiene el historial de operaciones asociado al método que la instancia de `Cubo` invocó, ahora conteniendo dentro de él al objeto que se encargó de ejecutar la operación.

5. Finalmente mediante el getter `getResultado()` se obtiene el resultado de la operación que no es más que una versión alterada de `CuerpoCubo`, dicho resultado se almacena en la tabla que se usa para efectuar las operaciones (que también es una instancia de `CuerpoCubo`), logrando así el efecto de cambio en la información del cubo.

### Beneficios

El patrón de diseño Command aplicado en este módulo provee los siguientes beneficios en el código:

* **Desacoplamiento**: Command desacopla el objeto que invoca la operación del que sabe cómo realizarla.

* **Objetos de primera clase**: Los comandos son objetos de primera clase. Se pueden manipular y extender como cualquier otro objeto.

* **Facilidad para agregar nuevos métodos**: Es fácil agregar nuevos comandos porque no es necesario cambiar las clases existentes.

### Referencias 

**"Design Patterns: Elements of Reusable Object-Oriented
Software" Gamma, Erich; Helm, Richard; Johnson, Ralph;
Vlissides, John Addison-Wesley Professional (1st edition,
1994)**

*  **"Command" (página 263)**