# Documentación del módulo `exportacionArchivos`

## Descripción

Este módulo, al igual que el módulo de [`lecturaArchivos`](../lecturaArchivos/README.md), implementa **el patrón de diseño Strategy** para definir una familia de algoritmos que representan métodos de exportación de archivos.

## Componentes

### **Interfaz `EstrategiaExportarArchivo`**

Define el método `exportarArchivo(String rutaArchivo, List<List<String>> data)` que todas las clases que representen modos de exportación de archivos deben implementar. Este método es responsable de exportar los datos proporcionados a un archivo en la ruta especificada.

#### Parámetros del Método

1. **rutaArchivo**: `String`
   - **Descripción**: La ruta completa del archivo donde se exportarán los datos. No debe ser nula o vacía.
   - **Requisitos**: Debe ser una ruta válida a un archivo que se desea crear o sobrescribir.

2. **data**: `List<List<String>>`
   - **Descripción**: Una lista de listas de cadenas que representa el contenido del archivo.
   - **Requisitos**: Cada lista interna representa una fila y cada cadena representa una columna. No debe ser nula, aunque puede estar vacía.

#### Excepciones Lanzadas

1. **IOException**
   - **Descripción**: Se lanza si se produce un error de entrada/salida al escribir el archivo.
   - **Cómo Evitarla**: Asegúrate de que la ruta especificada sea accesible y que tengas permisos suficientes para escribir en ella.

### **Clase `ExportadorCSV`**

Esta clase implementa la interfaz `EstrategiaExportarArchivo` y representa una estrategia de exportación de datos a un archivo CSV. La clase `ExportadorCSV` exporta los datos a un archivo CSV utilizando un separador específico para delimitar los campos.

#### Constructor de la Clase `ExportadorCSV`

El constructor de esta clase permite crear una instancia de tipo `ExportadorCSV` y recibe un carácter que se utilizará como separador en el archivo CSV.

#### Parámetros del Constructor

1. **separador**: `char`
   - **Descripción**: El carácter utilizado para separar los campos en el archivo CSV.
   - **Requisitos**: Debe ser un carácter válido que se usará como delimitador en el archivo CSV.

#### Ejemplo de Uso

```java
// Creamos una instancia de ExportadorCSV con un separador de coma
ExportadorCSV exportador = new ExportadorCSV(',');

// O podemos crear una instancia de ExportadorCSV  con un separador de punto y coma
ExportadorCSV exportador = new LectorCSV(';');
```

## ¿Cómo funciona?

Una vez creada una instancia de una clase que implemente la interfaz `EstrategiaExportarArchivo` se procede a invocar el método `exportarArchivo(String rutaArchivo, List<List<String>> data))` que recibe la ruta de ubicación donde se quiere guardar el archivo y una lista de listas de tipo `String` con la información que se desea guardar: 

```java
// Creamos una instancia del exportador deseado, en este caso elegimos el 
// ExportadorCSV
ExportadorCSV exportador = new ExportadorCSV(',');

// Guardamos la ruta de la ubicación donde queremos guardar el archivo
String rutaArchivo = "ruta/al/archivo.formato";

// Preparamos los datos a exportar
List<List<String>> datos = Arrays.asList(
    Arrays.asList("Nombre", "Apellido", "Edad"),
    Arrays.asList("Juan", "Pérez", "30"),
    Arrays.asList("María", "González", "25")
);

// Ejecutamos el método 
exportador.exportarArchivo("ruta/al/archivo.csv", datos);
```

## Beneficios

El patrón de diseño Strategy aplicado en este módulo provee beneficios que ya fueron expuestos en la [documentación del módulo `lecturaArchivos`](../lecturaArchivos/README.md).

## Referencias 

**"Design Patterns: Elements of Reusable Object-Oriented
Software" Gamma, Erich; Helm, Richard; Johnson, Ralph;
Vlissides, John Addison-Wesley Professional (1st edition,
1994)**

*  **"Strategy" (página 349)**