# Documentación del módulo `lecturaArchivos`

## Descripción

Este módulo Java implementa **el patrón de diseño Strategy** para definir una familia de algoritmos que representan métodos de lectura de archivos, encapsular cada uno y hacerlos intercambiables. Este patrón de diseño permite que el algoritmo varíe independientemente de las clases que lo utilizan.

## Componentes

### **Interfaz `EstrategiaLecturaArchivo`**:

Define el método `leerArchivo(String rutaArchivo)` que todas las clases que representen modos de lectura de archivos deben implementar. Este método es responsable de leer el contenido del archivo especificado y devolverlo en formato tabular para su posterior manipulación.

#### Parámetros del Método

1. **rutaArchivo**: `String`
   - **Descripción**: La ruta del archivo que se va a leer.
   - **Requisitos**: Debe ser una ruta válida a un archivo que se desea leer.

#### Excepciones Lanzadas

1. **IOException**
    - **Descripción**: Se lanza si se produce un error de entrada/salida al leer el archivo.
    - **Cómo Evitarla**: Asegúrate de que el archivo en la ruta especificada esté accesible y sea legible.

#### Retorno del Método

- **Matriz de datos**: `List<List<String>>`
  - **Descripción**: Una lista de listas de cadenas que representa el contenido del archivo.
  - **Estructura**: Cada lista interna representa una línea en el archivo, y cada cadena en la lista interna representa un campo.

### **Clase `LectorCSV`**

Esta clase implementa la interfaz `EstrategiaLecturaArchivo` y representa una estrategia de lectura de archivos CSV. La clase `LectorCSV` lee el contenido del archivo CSV y lo devuelve como una lista de listas de cadenas, donde cada lista interior representa una fila del archivo CSV y cada cadena representa un valor en la fila, simulando así el formato tabular esperado.

#### Constructor de la Clase `LectorCSV`

El constructor de esta clase te permite crear una instancia de tipo `LectorCSV`, este mismo recibe un dato de tipo `char` que indica el separador elegido para leer los archivos.

#### Parámetros del Constructor

1. **separador**: `char`
   - **Descripción**: El carácter utilizado para separar los campos en el archivo CSV.
   - **Requisitos**: Debe ser un carácter válido que se usará como delimitador en el archivo CSV.

#### Ejemplo de Uso

```java
// Creamos una instancia de LectorCSV con un separador de coma
LectorCSV lector = new LectorCSV(',');

// O podemos crear una instancia de LectorCSV con un separador de punto y coma
LectorCSV lector = new LectorCSV(';');
```

## ¿Cómo funciona?

Una vez creada una instancia de una clase que implemente la interfaz `EstrategiaLecturaArchivo` se procede a invocar el método `leerArchivo(String rutaArchivo)` que recibe la ruta de ubicación del archivo y devuelve una lista de listas de cadenas que representan la información en formato tabular. A continuación un ejemplo de su uso: 

```java
// Creamos una instancia del lector deseado, en este caso elegimos el 
// LectorCSV
LectorCSV lector = new LectorCSV(',');

// Guardamos la ruta de la ubicación del archivo
String rutaArchivo = "ruta/al/archivo.formato";

// Ejecutamos el método 
List<List<String>> archivo = Lector.leerArchivo(rutaArchivo);
```

## Beneficios

El patrón de diseño Strategy aplicado en este módulo provee los siguientes beneficios en el código:

* **Familias de algoritmos relacionados:**: El patrón Strategy nos permite definir un grupo de formas diferentes de hacer una tarea, como en nuestro caso es leer archivos. Estas diferentes formas de hacer la tarea se agrupan juntas como una familia, y cada forma es como un miembro de esa familia. Esto nos permite reutilizar estas diferentes formas de hacer la tarea en diferentes situaciones.

* **Alternativa a la subclasificación**:  En lugar de crear muchas clases diferentes para manejar diferentes situaciones, podríamos querer tener una sola clase que pueda comportarse de diferentes maneras según lo que necesitemos. Esta es una forma de hacer las cosas, pero puede hacer que nuestra clase sea más difícil de entender y mantener. Además, no podemos cambiar la forma en que se comporta la clase en tiempo de ejecución. El patrón Strategy nos permite encapsular cada comportamiento en su propia clase separada, lo que facilita el cambio de comportamiento en cualquier momento sin afectar al resto del código.

* **Eliminación de condicionales**: Cuando tenemos diferentes comportamientos en una clase, tendemos a usar muchos condicionales para elegir qué comportamiento utilizar en un momento dado. Esto puede hacer que el código sea difícil de leer y mantener. Con el patrón Strategy, podemos evitar estas declaraciones condicionales colocando cada comportamiento en su propia clase. Esto hace que el código sea más limpio y fácil de entender, ya que cada clase se centra en un solo comportamiento.

## Referencias 

**"Design Patterns: Elements of Reusable Object-Oriented
Software" Gamma, Erich; Helm, Richard; Johnson, Ralph;
Vlissides, John Addison-Wesley Professional (1st edition,
1994)**

*  **"Strategy" (página 349)**