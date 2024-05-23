# Documentación del módulo `comandos`

## Descripción

Este módulo Java implementa **el patrón de diseño Command** para encapsular operaciones sobre una instancia de `CuboOLAP`. El patrón permite encapsular los métodos como objetos, logrando desacoplar el código y haciendo fáciles de mantener y extender a los métodos de la clase `CuboOLAP`. 

## Componentes

### **Interfaz `ComandoCubo`**:

Define el método `ejecutar()` que implementan las clases de los métodos, dicho método es el que se ejecuta internamente dentro de una instancia de `CuboOLAP` cada vez que se invoca uno de sus métodos.

### **Clases que implementan `ComandoCubo`**:

Dentro de este módulo viven 4 clases que representan las operaciones disponibles en las intancias de `CuboOLAP`, todas implementan la interfaz `ComandoCubo` y por lo tanto cada una tiene un método `ejecutar()`. 

Los parámetros definidos en la clase `CuboOLAP` para invocar los métodos, desde la perspectiva de estas clases representan atributos que son trabajados dentro de cada una para lograr el resultado esperado. A continuación una lista de las clases que viven dentro de este módulo:

* `ComandoDice`
* `ComandoDrillDown`
* `ComandoRollUp`
* `ComandoSlice`

Cada una se encargar de implementar el comportamiento esperado de su método correspondiente y devolver el resultado del mismo mediante un **getter**, que dentro de cada clase tiene la forma de `getResultado()`.

## ¿Cómo funciona?

Cada vez que desde una clase `Main` se crea una instancia de `CuboOLAP` y se invoca a uno de sus métodos internamente se llevan a cabo los siguientes pasos:

1. Se reciben los argumentos una vez invocado el método y se hacen los chequeos correspondientes para evitar resultados inesperados.

2. Dentro del método invocado se crea un objeto que implementa `ComandoCubo` y a su constructor se le pasan los argumentos ya verificados.

3. Se ejecuta el método `ejecutar()` y dentro de la clase que implementa `ComandoCubo` se lleva a cabo la operación.

4. Finalmente mediante el getter `getResultado()` se obtiene el resultado de la operación y se almacena dentro del atributo `proyeccion_cubo`
   para su posterior visualización.

## Beneficios

El patrón de diseño Command aplicado en este módulo provee los siguientes beneficios en el código:

* **Desacoplamiento**: Command desacopla el objeto que invoca la operación del que sabe cómo realizarla.

* **Objetos de primera clase**: Los comandos son objetos de primera clase. Se pueden manipular y extender como cualquier otro objeto.

* **Facilidad para agregar nuevos métodos**: Es fácil agregar nuevos comandos porque no es necesario cambiar las clases existentes.

## Referencias 

**"Design Patterns: Elements of Reusable Object-Oriented
Software" Gamma, Erich; Helm, Richard; Johnson, Ralph;
Vlissides, John Addison-Wesley Professional (1st edition,
1994)**

*  **"Command" (página 263)**