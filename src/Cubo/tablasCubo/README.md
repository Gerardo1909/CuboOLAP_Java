# Documentación del módulo `tablasCubo`

## Descripción

El módulo `tablasCubo` fue creado con el propósito de facilitar la gestión de los archivos de **hechos** y **dimensiones**, generando así los tipos de datos
`Hecho` y `Dimension`. Hemos optado por una estrucutra de herencia de clases en donde implementamos una clase abstracta `Tabla` que encapsula el comportamiento 
común de las clases ya mencionadas. 

## Clase `Tabla`

La clase abstracta `Tabla` fue creada para representar datos en formato tabular y agrupar métodos cómunes entre los tipos de datos que adoptan esta
estructura.

### Método `ver`

El método `ver` permite mostrar una parte especificada de los datos internos de una tabla mediante una impresión por consola.

#### Parámetros del Método

1. **cantFilas**: `int`
   - **Descripción**: El número de filas a mostrar.
   - **Requisitos**: Debe ser un número entero positivo y no mayor al número de filas disponibles en la tabla.

2. **columnas**: `List<String>`
   - **Descripción**: Una lista que contiene los nombres de las columnas a mostrar.
   - **Requisitos**: Debe ser una lista válida de nombres de columnas que existen en la tabla.

#### Excepciones Lanzadas

1. **ColumnaNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguna de las columnas solicitadas no está presente en la tabla.
   - **Cómo Evitarla**: Verifica que todos los nombres de las columnas especificadas estén presentes en los encabezados de la tabla antes de invocar el método.

2. **FilaFueraDeRangoException**
   - **Descripción**: Esta excepción se lanza si el número solicitado de filas a mostrar es mayor a las disponibles en la tabla.
   - **Cómo Evitarla**: Asegúrate de que el número de filas solicitadas no exceda el número de filas disponibles en la tabla.

#### Ejemplo de Uso

```java
// Primero debemos instanciar una clase que extienda de tabla
Tabla claseExtTabla = ....

// Número de filas a mostrar
int cantFilas = 10;

// Lista de columnas a mostrar
List<String> columnas = Arrays.asList("columna1", "columna2", "columna3");

// Realizamos la operación de ver
claseExtTabla.ver(cantFilas, columnas);
```
#### Estructura del Resultado

El método `ver` imprime las columnas seleccionadas y las filas correspondientes en un formato tabular. Los datos se muestran en bloques de columnas, con un máximo de 4 columnas por bloque, si hay más columnas que el límite de 4 por bloque, se muestra una indicación de puntos suspensivos para las columnas adicionales. 

A continuación 2 ejemplos de como se vería la impresión que genera el método `ver` por consola:

##### Ejemplo #1

```sh
columna1             columna2             columna3             
valor                valor                valor              
valor                valor                valor              
valor                valor                valor              
valor                valor                valor              
valor                valor                valor               
```

##### Ejemplo #2

```sh
columna1                      columna2                    columna3                columna4                ...
valor                         valor                       valor                   valor                   ...
valor                         valor                       valor                   valor                   ...
valor                         valor                       valor                   valor                   ...
valor                         valor                       valor                   valor                   ...

columna5                      columna6                  
valor                         valor                   
valor                         valor                   
valor                         valor                  
valor                         valor                   
```

### Método `getNombre`

El método `getNombre` devuelve el nombre de la tabla.

#### Retorno del Método

- **Nombre de la tabla**: `String`
  - **Descripción**: El nombre de la tabla.

#### Ejemplo de Uso

```java
// Primero debemos instanciar una clase que extienda de tabla
Tabla claseExtTabla = ....

// Obtenemos el nombre de la tabla
String nombreTabla = claseExtTabla.getNombre();
```

### Método `getHeaders`

El método `getHeaders` devuelve una copia de la lista que contiene los encabezados de la tabla.

#### Retorno del Método

- **Encabezados de la tabla**: `List<String>`
  - **Descripción**: Una copia de la lista de encabezados de la tabla.

#### Ejemplo de Uso

```java
// Primero debemos instanciar una clase que extienda de tabla
Tabla claseExtTabla = ....

// Obtenemos los encabezados de la tabla
List<String> encabezados = claseExtTabla.getHeaders();
```

### Método `getDatosTabla`

El método `getDatosTabla` devuelve una copia de la matriz que contiene la información interna de la tabla.

#### Retorno del Método

- **Matriz de información interna**: `List<List<String>>`
  - **Descripción**: Una copia de la matriz que contiene los datos internos de la tabla.

#### Ejemplo de Uso

```java
// Primero debemos instanciar una clase que extienda de tabla
Tabla claseExtTabla = ....

// Obtenemos los datos de la tabla
List<List<String>> datosTabla = claseExtTabla.getDatosTabla();
```

### Método `getColumna`

El método `getColumna` obtiene la información interna de una columna presente en la tabla.

#### Parámetros del Método

1. **nombre_columna**: `String`
   - **Descripción**: Nombre de la columna que se va a obtener.
   - **Requisitos**: Debe ser un string válido que represente el nombre de una columna presente
                     en la tabla.

#### Excepciones Lanzadas

1. **ColumnaNoPresenteException**
   - **Descripción**: Esta excepción se lanza si la columna especificada no existe en la tabla.
   - **Cómo Evitarla**: Verifica que el nombre de la columna exista entre los encabezados de la tabla antes de invocar el método.

#### Retorno del Método

- **Columna de la tabla**: `List<String>`
  - **Descripción**: Una lista que contiene los valores presentes en la columna seleccionada de esta tabla, sin incluir el nombre que la representa.

#### Ejemplo de Uso

```java
// Primero debemos instanciar una clase que extienda de tabla
Tabla claseExtTabla = ....

// Obtenemos los valores de una columna específica
List<String> valoresColumna = claseExtTabla.getColumna("nombre_columna");
```

## Clase `Dimension`

La clase `Dimension` representa una tabla de dimensión, la cual es un componente de una instancia de `Cubo`. Hereda directamente de la clase `Tabla`, por 
lo cual cuenta con todos los métodos descritos anteriormente.

### Método `crearTablaDimension` para crear nuevas tablas de dimensiones

El método `crearTablaDimension` crea una nueva tabla de dimensión, instancia de la clase `Dimension`, con un nombre, niveles asociados, y toda su información interna.

#### Parámetros del Método

1. **nombre**: `String`
   - **Descripción**: El nombre de la tabla de dimensión.
   - **Requisitos**: Debe ser un string válido que represente el nombre de la tabla de dimensión.

2. **niveles**: `List<String>`
   - **Descripción**: Una lista que representa los niveles de la dimensión presentes en la tabla. 
   - **Requisitos**: Los niveles deben ser pasados en orden de jerarquía, siendo el nivel más alto/abstracto el primero en la lista y el más fino el último en la lista.

3. **primaryKey**: `String`
   - **Descripción**: La clave primaria de la tabla de dimensión.
   - **Requisitos**: Debe ser un string válido que represente la clave primaria de la tabla de dimensión.

4. **estrategiaLectura**: `EstrategiaLecturaArchivo`
   - **Descripción**: La estrategia de lectura de archivos a utilizar. 
   - **Requisitos**: Debe ser una instancia válida de una clase que implemente la interfaz `EstrategiaLecturaArchivos`.

5. **rutaArchivo**: `String`
   - **Descripción**: La ruta donde se ubica el archivo a leer.
   - **Requisitos**: Debe ser una ruta válida donde se pueda leer el archivo.

#### Excepciones Lanzadas

1. **IOException**
   - **Descripción**: Esta excepción se lanza si ocurre un error de entrada/salida al leer los datos de la tabla.
   - **Cómo Evitarla**: Asegúrate de que el archivo exista y sea accesible en la ruta especificada antes de invocar el método.

2. **ClaveNoPresenteException**
   - **Descripción**: Esta excepción se lanza si la clave primaria indicada no está presente en la tabla de dimensión.
   - **Cómo Evitarla**: Verifica que la clave primaria especificada exista entre los encabezados de la tabla de dimensión antes de invocar el método.

3. **NivelNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguno de los niveles especificados no está presente en la tabla de dimensión.
   - **Cómo Evitarla**: Verifica que todos los niveles especificados existan entre los encabezados de la tabla de dimensión antes de invocar el método.

#### Retorno del Método

- **Tabla de dimensión**: `Dimension`
  - **Descripción**: Una nueva instancia de la clase `Dimension`, la cual representa una tabla de dimensión.

#### Ejemplo de Uso

```java
// Definimos los parámetros para crear una tabla de dimensión
String nombre = "TablaDimensión";
List<String> niveles = Arrays.asList("Nivel1", "Nivel2", "Nivel3"); // Recordar que deben ser pasados en orden de jerarquía!!
String primaryKey = "ID";
EstrategiaLecturaArchivo estrategiaLectura = new EstrategiaLecturaCSV(",");
String rutaArchivo = "/ruta/a/archivo.csv";

// Creamos la tabla de dimensión
Dimension tablaDimension = Dimension.crearTablaDimension(nombre, niveles, primaryKey, estrategiaLectura, rutaArchivo);
```

### Método `getPrimaryKey`

El método `getPrimaryKey` devuelve la clave primaria de la tabla de dimensión.

#### Retorno del Método

- **Clave primaria**: `String`
  - **Descripción**: La clave primaria de la tabla de dimensión.

#### Ejemplo de Uso

```java
// Primero debemos generar una tabla de dimensión
Dimension tablaDimension = ....

// Obtenemos la clave primaria de la tabla de dimensión
String primaryKey = tablaDimension.getPrimaryKey();
```

### Método `getNiveles`

El método `getNiveles` devuelve una copia del mapa que contiene como clave el nombre de un nivel de la tabla de dimensión y como valor la lista de valores únicos en ese nivel.

#### Retorno del Método

- **Niveles dimensión**: `Map<String, List<String>>`
  - **Descripción**: Una copia del mapa que contiene como clave el nombre del nivel y como valor la lista de valores únicos en ese nivel de la dimensión.

#### Ejemplo de Uso

```java
// Primero debemos generar una tabla de dimensión
Dimension tablaDimension = ....

// Obtenemos los niveles de la dimensión
Map<String, List<String>> niveles = tablaDimension.getNiveles();
```

### Método `getIndicesNiveles`

El método `getIndicesNiveles` devuelve una copia del mapa que contiene como clave el nombre de un nivel de la tabla de  dimensión y como valor un índice que representa su nivel de jerarquía, siendo el 0 el nivel más alto en la misma.

#### Retorno del Método

- **Indices Niveles**: `Map<String, Integer>`
  - **Descripción**: Una copia del mapa que contiene como clave el nombre del nivel y como valor un índice que representa su nivel de jerarquía en la dimensión.

#### Ejemplo de Uso

```java
// Primero debemos generar una tabla de dimensión
Dimension tablaDimension = ....

// Obtenemos los índices de los niveles de la dimensión
Map<String, Integer> indicesNiveles = tablaDimension.getIndicesNiveles();
```

## Clase `Hecho`

La clase `Hecho` representa una tabla de hechos, la cual es un componente de una instancia de `Cubo`. Hereda directamente de la clase `Tabla`, por 
lo cual cuenta con todos los métodos descritos anteriormente.

### Método `crearTablaHechos` para crear nuevas tablas de hechos

El método `crearTablaHechos` crea una nueva tabla de hechos, instancia de la clase `Hecho`, con un nombre y toda su información interna.

#### Parámetros del Método

1. **nombre**: `String`
   - **Descripción**: El nombre de la tabla de hechos.
   - **Requisitos**: Debe ser un string válido que represente el nombre de la tabla de dimensión..

2. **hechos**: `List<String>`
   - **Descripción**: Una lista que representa los hechos presentes en la tabla.
   - **Requisitos**: Debe ser una lista válida de strings que contenga los hechos presentes en la tabla.

3. **clavesForaneasDims**: `Map<Dimension, String>`
   - **Descripción**: Un mapa que representa las claves foráneas de las dimensiones presentes en la tabla. Como clave
                      posee una instancia de la clase `Dimension` y como valor tiene un string que representa la clave foránea
                      que asocia dicha dimensión a la tabla de hechos que se está creando.
   - **Requisitos**: Debe ser un mapa válido y no vacío que contenga cada clave foránea de dimensiones asociadas a la tabla.

4. **estrategiaLectura**: `EstrategiaLecturaArchivo`
   - **Descripción**: La estrategia de lectura de archivos a utilizar.
   - **Requisitos**: Debe ser una instancia válida de una clase que implemente la interfaz `EstrategiaLecturaArchivos`.

5. **rutaArchivo**: `String`
   - **Descripción**: La ruta donde se ubica el archivo a leer.
   - **Requisitos**: Debe ser una ruta válida donde se pueda leer el archivo.

#### Excepciones Lanzadas

1. **IOException**
   - **Descripción**: Esta excepción se lanza si ocurre un error de entrada/salida al leer los datos de la tabla.
   - **Cómo Evitarla**: Asegúrate de que el archivo exista y sea accesible en la ruta especificada antes de invocar el método.

2. **HechoNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguno de los hechos especificados no está presente en la tabla de hechos.
   - **Cómo Evitarla**: Verifica que todos los nombres de los hechos especificados existan entre los encabezados de la tabla de hechos antes de invocar el método.

3. **ClaveNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguna de las claves foráneas de las dimensiones no está presente en la tabla de hechos.
   - **Cómo Evitarla**: Verifica que todas las claves foráneas de las dimensiones especificadas existan entre los encabezados de la tabla de hechos antes de invocar el método.

#### Retorno del Método

- **Tabla de hechos**: `Hecho`
  - **Descripción**: Una nueva instancia de la clase `Hecho`, la cual representa una tabla de hechos.

#### Ejemplo de Uso

```java
// Definimos los parámetros para crear una tabla de hechos
String nombre = "TablaHechos";
List<String> hechos = Arrays.asList("hecho1", "hecho2", "hecho3");
Map<Dimension, String> clavesForaneasDims = new HashMap<>();
clavesForaneasDims.put(dim1, "claveForanea1");
clavesForaneasDims.put(dim2, "claveForanea2");
EstrategiaLecturaArchivo estrategiaLectura = new EstrategiaLecturaCSV();
String rutaArchivo = "/ruta/a/archivo.csv";

// Creamos la tabla de hechos
Hecho tablaHechos = Hecho.crearTablaHechos(nombre, hechos, clavesForaneasDims, estrategiaLectura, rutaArchivo);
```

### Método `getHechos`

El método `getHechos` devuelve una copia de la lista de hechos presentes en la tabla de hechos.

#### Retorno del Método

- **Lista de hechos**: `List<String>`
  - **Descripción**: Una copia de la lista de hechos presentes en la tabla de hechos.

#### Ejemplo de Uso

```java
// Primero debemos generar una tabla de hechos
Hecho tablaHechos = ....

// Obtenemos la lista de hechos de la tabla de hechos
List<String> listaHechos = tablaHechos.getHechos();
```

### Método `getClavesForaneasDims`

El método `getClavesForaneasDims` devuelve una copia del mapa que contiene las claves foráneas de dimensiones presentes en la tabla de hechos.

#### Retorno del Método

- **Claves foráneas**: `Map<Dimension, String>`
  - **Descripción**: Una copia del mapa que contiene las claves foráneas de dimensiones presentes en la tabla de hechos.

#### Ejemplo de Uso

```java
// Primero debemos generar una tabla de hechos
Hecho tablaHechos = ....

// Obtenemos el mapa de claves foráneas de dimensiones
Map<Dimension, String> clavesForaneas = tablaHechos.getClavesForaneasDims();
```