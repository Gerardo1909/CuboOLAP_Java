# Documentación de la clase `CuboOLAP`

## Descripción

La clase `CuboOLAP` es el componente central de nuestra librería y proporciona las funcionalidades esenciales para trabajar con cubos OLAP. Esta clase permite realizar diversas operaciones de análisis sobre las tablas que componen el cubo, es decir, las **dimensiones** y los **hechos**.

Además, facilita la carga de tablas de dimensiones y hechos mediante instancias de las clases `Hecho` y `Dimension`, así como la exportación de la información del cubo al formato preferido. Esto convierte a nuestra librería en una herramienta ideal para aplicar operaciones y posteriormente analizar los resultados obtenidos utilizando otros medios.

### Operaciones Disponibles

La clase `CuboOLAP` ofrece una variedad de operaciones que facilitan el análisis multidimensional de los datos:

- **Roll-up**: Agrega los datos a un nivel superior de la jerarquía.
- **Slice**: Filtra los datos en una dimensión específica en un nivel determinado y elimina dicha dimensión.
- **Drill-down**: Desglosa los datos a un nivel más detallado de la jerarquía.
- **Dice**: Filtra los datos en varias dimensiones a la vez.


## Constructor de la clase 

El constructor de esta clase permite crear una instancia de tipo `CuboOLAP` proporcionando el nombre del cubo, una tabla de hechos y una lista de tablas de dimensiones.

### Parámetros del Constructor

1. **nombre**: `String`
   - **Descripción**: El nombre del cubo OLAP.
   - **Requisitos**: Debe ser un string válido que represente el nombre que deseas asignar al cubo.

2. **hecho**: `Hecho`
   - **Descripción**: La tabla de hechos que contiene los datos principales del cubo.
   - **Requisitos**: Debe ser una instancia válida de la clase `Hecho`, que contiene los datos necesarios para llevar a cabo las operaciones del cubo.

3. **dimensiones**: `List<Dimension>`
   - **Descripción**: Una lista de dimensiones que definen las diferentes perspectivas para analizar los datos de la tabla de hechos.
   - **Requisitos**: Debe ser una lista válida de instancias de la clase `Dimension`, donde cada dimensión tiene una clave primaria que debe estar presente en la tabla de hechos.

### Excepciones Lanzadas

1. **ClaveNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguna clave primaria de las dimensiones proporcionadas no está presente en los encabezados de la tabla de hechos.
   - **Cómo Evitarla**: Asegúrate de que todas las claves primarias de las dimensiones están incluidas como encabezados en la tabla de hechos antes de llamar al constructor.

### Ejemplo de Uso

```java
// Primero siempre generar las instancias de tipo Hecho y Dimension
Hecho hecho = new Hecho("tabla_hechos", ...);
Dimension dimension1 = new Dimension("dim1", "primaryKey1", ...);
Dimension dimension2 = new Dimension("dim2", "primaryKey2", ...);
List<Dimension> dimensiones = Arrays.asList(dimension1, dimension2);

// Una vez hecho, ahora se puede instanciar un objeto CuboOLAP
CuboOLAP cubo = new CuboOLAP("NombreCubo", hecho, dimensiones);
```

## Proyecciones sobre el cubo

El método `proyectar` permite visualizar la información de una instancia de `CuboOLAP` en un formato tabular en cualquier momento desde su inicialización. Recibe una lista con las columnas del cubo que se desean visualizar y un número que indica la cantidad de filas a proyectar.

### Parámetros del Método

1. **n_filas**: `int`
   - **Descripción**: El número de filas que se desean proyectar.
   - **Requisitos**: Debe ser un número entero positivo que indique la cantidad de filas a mostrar.

2. **columnas**: `List<String>`
   - **Descripción**: La lista de nombres de las columnas que se desean visualizar.
   - **Requisitos**: Debe ser una lista válida de nombres de columnas que existen entre las tablas disponibles del cubo al momento de su invocación.

### Excepciones Lanzadas

1. **ColumnaNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguna de las columnas especificadas no está presente en los encabezados disponibles del cubo.
   - **Cómo Evitarla**: Verifica que todas las columnas especificadas existen en las tablas que viven dentro del cubo antes de llamar al método `proyectar`.

2. **FilaFueraDeRangoException**
   - **Descripción**: Se lanza si el número de filas especificado excede el número de filas disponibles en la tabla.
   - **Cómo Evitarla**: Asegúrate de que el número de filas solicitado no sea mayor que el número de filas disponibles en la tabla.


### Ejemplo de Uso

```java
// Definimos el número de filas y las columnas a visualizar
int n_filas = 10;
List<String> columnas = Arrays.asList("columna1", "columna2", "columna3");

// Proyectamos los datos del cubo
cubo.proyectar(n_filas, columnas);
```

### Estructura del Resultado

El método `proyectar` imprime las columnas seleccionadas y las filas correspondientes en un formato tabular. Los datos se muestran en bloques de columnas, con un máximo de 4 columnas por bloque, si hay más columnas que el límite de 4 por bloque, se muestra una indicación de puntos suspensivos para las columnas adicionales.

A continuación 2 ejemplos de como se vería la impresión que genera el método `proyectar` por consola:

##### Ejemplo #1 (despúes de aplicar la operación dice)

```sh
anio                          provincia                     costo
2018                          Florida                       352.4
2018                          Florida                       826.29
2018                          Florida                       826.29
2018                          Florida                       826.29
2018                          Florida                       1239.44
2018                          Florida                       826.29
2018                          Florida                       413.15
2018                          Florida                       1239.44
2018                          Florida                       826.29
2018                          Florida                       362.97
```

##### Ejemplo #2 (despúes de aplicar la operación rollUp)

```sh
anio                          quarter                       region                        pais                          ...
2018                          3                             North America                 United States                 ...
2019                          3                             North America                 United States                 ...
2018                          3                             North America                 United States                 ...
2018                          4                             North America                 United States                 ...
2019                          1                             North America                 United States                 ...
2019                          2                             North America                 United States                 ...
2018                          3                             North America                 United States                 ...
2019                          3                             North America                 United States                 ...
2019                          4                             North America                 United States                 ...
2020                          1                             North America                 United States                 ...

categoria                     subcategoria                  cantidad
Components                    Road Frames                   1933.0
Components                    Road Frames                   1008.0
Bikes                         Road Bikes                    3845.0
Bikes                         Road Bikes                    3393.0
Bikes                         Road Bikes                    2917.0
Bikes                         Road Bikes                    3283.0
Components                    Wheels                        1458.0
Clothing                      Socks                         817.0
Clothing                      Socks                         710.0
Clothing                      Socks                         405.0
```

## Exportación de la información del cubo

El método `exportar` permite exportar los datos del cubo a un archivo utilizando una estrategia específica de exportación.

### Parámetros del Método

1. **ruta_guardado**: `String`
   - **Descripción**: La ruta de destino donde se guardará el archivo.
   - **Requisitos**: Debe ser una ruta válida donde se pueda escribir el archivo.

2. **estrategia_exportar**: `EstrategiaExportarArchivo`
   - **Descripción**: La estrategia usada para exportar la informacion del cubo.
   - **Requisitos**: Debe ser una instancia válida de una clase que implemente la interfaz `EstrategiaExportarArchivo`.

### Excepciones Lanzadas

1. **IOException**
   - **Descripción**: Esta excepción se lanza si ocurre un error de entrada/salida durante el proceso de exportación de datos.
   - **Cómo Evitarla**: Asegúrate de que la ruta de destino es accesible y de que tienes permisos de escritura.

### Ejemplo de Uso

```java
// Primero debemos crear una objeto que implemente la interfaz 'EstrategiaExportarArchivo'
EstrategiaExportarArchivo estrategia_exportar = new EstrategiaCSV();

// Generamos la ruta donde se guardará el archivo exportado
String ruta_guardado = "/ruta/a/archivo.csv";

// Ejecutamos el método 
cubo.exportar(ruta_guardado, estrategia_exportar);
```

## Reinicio del estado del cubo a través del método `reiniciar`

El método `reiniciar`  permite reiniciar el cubo a su estado original, restaurando tanto las tablas de dimensiones como las tablas de hechos a su estado inicial y limpiando los historiales de operaciones.

Este método fue creado para facilitar la exploración de distintos aspectos de las tablas de dimensiones y de hechos después de trabajar con alguna dimensión o hecho específico, evitando la necesidad de generar una nueva instancia del cubo para ejecutar otras operaciones.

### Ejemplo de Uso

```java
// Ejecutamos el método
cubo.reiniciar();

// Podemos imprimir por consola algún aviso sobre el reinicio
System.out.println("El cubo ha sido reiniciado a su estado original.");

// Podemos proyectar de nuevo información del cubo que anteriormente fue eliminada
cubo.proyectar(...)
```

## Método `rollUp`

El método `rollUp` permite realizar una operación de roll-up en una instancia de `CuboOLAP`, lo que implica una agregación de los datos a un nivel superior en la jerarquía de dimensiones. 

### Parámetros del Método

1. **criterios_reduccion**: `Map<Dimension, String>`
   - **Descripción**: Un mapa de criterios que contiene como clave la dimensión sobre la cual se quiere aplicar reducción 
                      y como valor el nivel que se toma como criterio para aplicar la misma.
   - **Requisitos**: Debe ser un mapa válido que contenga dimensiones y niveles que estén presentes en el cubo.

2. **hechos_seleccionados**: `List<String>`
   - **Descripción**: La lista de hechos a incluir en la operación de roll-up.
   - **Requisitos**: Debe ser una lista válida de nombres de hechos que existen en la tabla de hechos asociada al cubo.

3. **agregacion**: `String`
   - **Descripción**: La operación de agregación a realizar.
   - **Requisitos**: Solo se soportan las siguientes operaciones: `"sum"`, `"max"`, `"min"`, `"count"`. Debe escribirse tal cual al pasar el argumento.

### Excepciones Lanzadas

1. **AgregacionNoSoportadaException**
   - **Descripción**: Esta excepción se lanza si la operación de agregación seleccionada no está entre las disponibles.
   - **Cómo Evitarla**: Asegúrate de pasar una de las operaciones soportadas: `"sum"`, `"max"`, `"min"`, `"count"`.

2. **DimensionNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguna dimensión especifcada en `criterios_reduccion` no está en el cubo.
   - **Cómo Evitarla**: Verifica que todas las dimensiones especificadas estén presentes en el cubo antes de invocar al método.

3. **NivelNoPresenteException**
   - **Descripción**: Esta excepción se lanza si algún nivel especificado en `criterios_reduccion` no está presente en el cubo.
   - **Cómo Evitarla**: Verifica que todos los niveles especificados estén presentes en las dimensiones asociadas al cubo al momento de invocar al método.

4. **HechoNoPresenteException**
   - **Descripción**: Esta excepción se lanza si algún hecho especificado en `hechos_seleccionados` no está presente en el cubo.
   - **Cómo Evitarla**: Verifica que todos los hechos especificados existen en la tabla de hechos asociada al cubo antes de llamar al método.

5. **ArgumentosInoperablesException**
   - **Descripción**: Esta excepción se lanza si el mapa `criterios_reduccion` está vacío a la hora de invocar al método.
   - **Cómo Evitarla**: Verifica que el mapa contenga las condiciones de agregación antes de invocar al método.

### Ejemplo de Uso

```java
// Primero generamos un mapa con los criterios de reducción
Map<Dimension, String> criterios_reduccion = new LinkedHashMap<>();
criterios_reduccion.put(dim1, "nivelY");
criterios_reduccion.put(dim2, "nivelX");

// Seleccionamos los hechos que queremos agrupar
List<String> hechos_seleccionados = Arrays.asList("hecho1", "hecho2");

// Realizamos la operación de roll-up con agregación "sum"
cubo.rollUp(criterios_reduccion, hechos_seleccionados, "sum");

// Luego podemos proyectar el resultado de la operación
int n_filas = 10;
List<String> columnas_proy = Arrays.asList("nivelY", "nivelX","hecho1", "hecho2");
cubo.proyectar(n_filas, columnas_proy);
```
### Estructura del Resultado

El método `rollUp` luego de haberse ejecutado modifica el estado interno del cubo, manteniendo solo aquellas dimensiones y hechos que fueron agrupadas. A continuación aquí hay una impresión por consola usando el método `proyectar` luego de ejecutar la operación:

```sh
anio                          region                        categoria                     valor_total
2018                          North America                 Components                    3273942.150000018
2019                          North America                 Components                    4285236.790000052
2018                          North America                 Bikes                         1.8827625290000148E7
2019                          North America                 Bikes                         2.0723330779999968E7
2019                          North America                 Clothing                      683028.1099999952
2020                          North America                 Clothing                      219205.850000001
2020                          North America                 Bikes                         7895007.350000155
2020                          North America                 Components                    1074260.7400000016
2018                          North America                 Accessories                   84699.5800000001
2019                          North America                 Accessories                   212287.50000000105
```

## Método `slice`

El método `slice` permite realizar una operación de corte (slice) en una instancia de `CuboOLAP`, filtrando los datos en una dimensión específica a un nivel determinado y por un valor de corte. 

Esta operación una vez se ejecuta y filtra los datos según los criterios especificados, elimina **completamente** la dimensión que se vió implicada en la operación, reduciendo así la dimensionalidad del cubo cada vez que se ejecuta.

### Parámetros del Método

1. **dimension**: `Dimension`
   - **Descripción**: La dimensión en la que se va a realizar la operación de slice.
   - **Requisitos**: Debe ser una instancia válida de la clase `Dimension` que esté presente en el cubo al momento de invocar al método.

2. **nivel**: `String`
   - **Descripción**: El nivel en la dimensión en el que se va a realizar la operación de slice.
   - **Requisitos**: Debe ser un string válido que represente un nivel existente en la dimensión especificada.

3. **valor_corte**: `String`
   - **Descripción**: El valor de corte para la operación de slice.
   - **Requisitos**: Debe ser un string válido que represente un valor presente en el nivel especificado de la dimensión.

### Excepciones Lanzadas

1. **DimensionNoPresenteException**
   - **Descripción**: Esta excepción se lanza si la dimensión especificada no está presente en el cubo.
   - **Cómo Evitarla**: Asegúrate de que la dimensión pasada como argumento esté incluida en el cubo al momento de invocar al método.

2. **NivelNoPresenteException**
   - **Descripción**: Esta excepción se lanza si el nivel especificado no está presente presente en el cubo al momento de invocar al método.
   - **Cómo Evitarla**: Verifica que el nivel especificado existe entre una de las dimensiones presentes en el cubo al invocar el método.

3. **ValorNoPresenteException**
   - **Descripción**: Esta excepción se lanza si el valor de corte no está presente en el cubo para el nivel seleccionado de la dimensión al momento de invocar al método.
   - **Cómo Evitarla**: Asegúrate de que el valor de corte existe en el cubo para el nivel especificado al momento de invocar al método.

4. **SliceException**
   - **Descripción**: Esta excepción se lanza si a la dimensión pasada como argumento se le aplicó la operación Slice anteriormente al momento de invocar al método.
   - **Cómo Evitarla**: Intenta usando el método `reiniciar` si quieres volver a aplicar alguna operación de Slice a la dimensión pasada como argumento.


### Ejemplo de Uso

```java
// Defininimos la dimensión, nivel y valor de corte para la operación de slice
Dimension dimension = new Dimension("dim1", ...);
String nivel = "nivel1";
String valor_corte = "valor1";

// Realizamos la operación de slice
cubo.slice(dimension, nivel, valor_corte);

// Luego podemos proyectar el resultado de la operación, verificando que efectivamente
// se eliminó la dimensión que se vió implicada en la operación y viendo como esto afectó 
// al resto de la información del cubo
int n_filas = 10;
List<String> columnas_proy = Arrays.asList("nivelX_dim2", "nivelX_dim3", ....);
cubo.proyectar(n_filas, columnas_proy);
```
### Estructura del Resultado

El método `slice` luego de haberse ejecutado modifica el estado interno del cubo, filtrando la información según los criterios especificados 
y eliminando completamente la dimensión que se vió implicada en la operación, reduciendo así la dimensionalidad del cubo.

El cubo resultante contará con el resto de dimensiones pero filtradas según los criterios especificos. Una forma ideal de visualizar estos resultados 
podría ser usando el método `exportar` de la clase `CuboOLAP` para exportar el cubo y analizar la información restante.

## Método `dice`

Este método realiza una operación de "dice" en el cubo, que filtra los datos del cubo según los criterios especificados. Notar que su 
característica principal es que puede contener varios criterios de corte a diferencia del método `Slice`, además que **no reduce la dimensionalidad del cubo**.

### Parámetros del Método

1. **criterios**: `Map<Dimension, Map<String, List<String>>>`
   - **Descripción**: Un mapa que contiene las dimensiones, niveles y valores a incluir en el método `dice`.
   - **Estructura**:
     - La clave del mapa es una instancia de la clase `Dimension`.
     - El valor asociado es otro mapa donde:
       - La clave es un `String` que representa el nivel de la dimensión seleccionada.
       - El valor es una lista de `String` que representa los valores de corte en ese nivel.

### Excepciones Lanzadas

1. **DimensionNoPresenteException**
  - **Descripción**: Se lanza si la dimensión especificada no está presente en el cubo al momento de invocar al método.
  - **Cómo Evitarla**: Verifica que todas las dimensiones especificadas están incluidas en el cubo antes de invocar al método.

2. **NivelNoPresenteException**
  - **Descripción**: Se lanza si el nivel especificado no está presente en el cubo al momento de invocar al método.
  - **Cómo Evitarla**: Asegúrate de que todos los niveles especificados estén presentes en el cubo al momento de invocar al método.

3. **ValorNoPresenteException**
  - **Descripción**: Se lanza si el valor de corte no está presente en el cubo para el nivel seleccionado de la dimensión al momento de invocar al método.
  - **Cómo Evitarla**: Comprueba que todos los valores de corte están presentes en los niveles correspondientes de las dimensiones antes de realizar la operación,
                       además asegurate de no haber antes filtrado antes al cubo al momento de invocar al método.

### Ejemplo de Uso

```java
// Creamos primero instancias de la clase Dimension
Dimension dimension1 = new Dimension("Dim1", Arrays.asList("Nivel1"), "primaryKey1", estrategiaLectura, "ruta/archivo1.csv");
Dimension dimension2 = new Dimension("Dim2", Arrays.asList("Nivel2"), "primaryKey2", estrategiaLectura, "ruta/archivo2.csv");

// Definimos los criterios que usaremos para filtrar
Map<String, List<String>> criteriosNivel1 = new LinkedHashMap<>();
criteriosNivel1.put("Nivel1", Arrays.asList("Valor1", "Valor2"));

Map<String, List<String>> criteriosNivel2 = new LinkedHashMap<>();
criteriosNivel2.put("Nivel2", Arrays.asList("Valor3", "Valor4"));

Map<Dimension, Map<String, List<String>>> criterios = new LinkedHashMap<>();
criterios.put(dimension1, criteriosNivel1);
criterios.put(dimension2, criteriosNivel2);

// Ejecutamos el método dice
cubo.dice(criterios);

// Luego podemos proyectar el resultado de la operación
int n_filas = 10;
List<String> columnas_proy = Arrays.asList("Nivel1", "Nivel2", ....);
cubo.proyectar(n_filas, columnas_proy);
```

### Estructura del Resultado

El método `dice` luego de haberse ejecutado modifica el estado interno del cubo, filtrando según los criterios especificados en el método. A continuación aquí hay una impresión por consola usando el método `proyectar` luego de ejecutar la operación:

```sh
anio                provincia           costo
2017                California          5694.28
2017                California          9490.47
2017                California          3796.19
2017                California          3796.19
2017                California          27.17
2017                California          10.19
2017                California          13.59
2017                California          11.41
2017                California          1912.15
2017                California          413.15
2017                California          826.29
```