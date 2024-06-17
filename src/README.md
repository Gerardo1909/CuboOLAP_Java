# Documentación de la clase `Cubo`

## Descripción

La clase `Cubo` es el componente central de nuestra librería y proporciona las funcionalidades esenciales para trabajar con cubos OLAP. Esta clase permite realizar diversas operaciones de análisis sobre las tablas que componen el cubo, es decir, las tablas de **dimensiones** y **hechos**.

Además, facilita la carga de tablas de dimensiones y hechos mediante instancias de las clases `Hecho` y `Dimension`, así como la exportación de la información del cubo al formato preferido. Esto convierte a nuestra librería en una herramienta ideal para aplicar operaciones y posteriormente analizar los resultados obtenidos utilizando otros medios.

### Operaciones Disponibles

La clase `Cubo` ofrece una variedad de operaciones OLAP que facilitan el análisis multidimensional de los datos:

- **Roll-up**: Agrega los datos de una o más dimensiones a un nivel superior de la jerarquía de niveles.
- **Slice**: Filtra los datos en una dimensión específica en un nivel determinado y elimina dicha dimensión.
- **Drill-down**: Desglosa los datos de una o más dimensiones a un nivel más detallado de la jerarquía de niveles.
- **Dice**: Filtra los datos en varias dimensiones a la vez.

## Método  `crearCuboOLAP` para crear nuevos cubos OLAP

Este método creacional permite obtener una nueva instancia de la clase `Cubo`, que representa un cubo OLAP que está listo para ser operable. Solo es necesario
asignarle un nombre, asignar una tabla de hechos y tablas de dimensiones para ejecutar el método.

### Parámetros del Método

1. **nombreCubo**: `String`
   - **Descripción**: El nombre del cubo OLAP.
   - **Requisitos**: Debe ser un string válido que represente el nombre que deseas asignar al cubo.

2. **tablaHechos**: `Hecho`
   - **Descripción**: La tabla de hechos que contiene las medidas asociadas a las dimensiones del cubo.
   - **Requisitos**: Debe ser una instancia válida de la clase `Hecho` que esté asociada a las tablas de dimensiones, es decir, que contiene claves 
                     foráneas que la asocian a las mismas.

3. **tablasDimensiones**: `List<Dimension>`
   - **Descripción**: Una lista de dimensiones que definen las diferentes perspectivas para analizar los datos de la tabla de hechos.
   - **Requisitos**: Debe ser una lista válida de instancias de la clase `Dimension`, donde cada una está asociada a la tabla de hechos.

### Excepciones Lanzadas

1. **DimensionNoAsociadaException**
   - **Descripción**: Esta excepción se lanza si alguna de las dimensiones pasadas en `tablasDimensiones` no está asociada a la tabla de hechos
                      que se pasó anteriormente.
   - **Cómo Evitarla**: Asegúrate de verificar que todas las dimensiones en la lista `tablasDimensiones` estén asociadas a la tabla de hechos.

### Retorno del Método

- **CuboOLAP**: `Cubo`
  - **Descripción**: Una nueva instancia de la clase `Cubo`, la cual representa un cubo OLAP que está listo para el análisis.

### Ejemplo de Uso

```java
// Primero siempre generar las instancias de tipo Hecho y Dimension
Dimension dim1 = Dimension.crearTablaDimension("dim1",...);
Dimension dim2 = Dimension.crearTablaDimension("dim2",...);
.
.
.
Hecho hecho = Hecho.crearTablaHechos("tabla_hechos", ...);

// Generamos la lista de dimensiones necesaria para la creación del cubo
List<Dimension> dimensiones = Arrays.asList(dim1, dim2);

// Una vez hecho, ahora se puede instanciar un objeto Cubo
Cubo cubo = Cubo.crearCuboOLAP("NombreCubo", hecho, dimensiones);
```

## Proyecciones sobre el cubo

El método `proyectar` permite visualizar la información de una instancia de `Cubo` en un formato tabular en cualquier momento desde su inicialización. Recibe una lista con las columnas del cubo que se desean visualizar y un número que indica la cantidad de filas a proyectar.

### Parámetros del Método

1. **cantFilas**: `int`
   - **Descripción**: El número de filas que se desean proyectar.
   - **Requisitos**: Debe ser un número entero positivo que indique la cantidad de filas a mostrar. Además debe ser un número
                     menor a la longitud de información disponible en el cubo.

2. **nombresColumnas**: `List<String>`
   - **Descripción**: La lista de nombres de las columnas que se desean visualizar.
   - **Requisitos**: Debe ser una lista válida de nombres de columnas que existen entre las tablas disponibles del cubo al momento de su invocación.

### Excepciones Lanzadas

1. **ColumnaNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguna de las columnas especificadas no está presente en los encabezados disponibles del cubo.
   - **Cómo Evitarla**: Verifica que todas las columnas especificadas existen en las tablas que viven dentro del cubo antes de llamar al método `proyectar`.

2. **FilaFueraDeRangoException**
   - **Descripción**: Se lanza si el número de filas especificado excede el número de filas disponibles en la información interna del cubo.
   - **Cómo Evitarla**: Asegúrate de que el número de filas solicitado no sea mayor que el número de filas disponibles en la información interna
                        del cubo.


### Ejemplo de Uso

```java
// Definimos el número de filas y las columnas a visualizar
int cantFilas = 10;
List<String> nombresColumnas = Arrays.asList("columna1", "columna2", "columna3");

// Proyectamos los datos del cubo
cubo.proyectar(cantFilas, nombresColumnas);
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

1. **rutaGuardadoArchivo**: `String`
   - **Descripción**: La ruta de destino donde se guardará el archivo.
   - **Requisitos**: Debe ser una ruta válida donde se pueda escribir el archivo.

2. **estrategiaExportacion**: `EstrategiaExportarArchivo`
   - **Descripción**: La estrategia usada para exportar la informacion del cubo.
   - **Requisitos**: Debe ser una instancia válida de una clase que implemente la interfaz `EstrategiaExportarArchivo`.

### Excepciones Lanzadas

1. **IOException**
   - **Descripción**: Esta excepción se lanza si ocurre un error de entrada/salida durante el proceso de exportación de datos.
   - **Cómo Evitarla**: Asegúrate de que la ruta de destino es accesible y de que tienes permisos de escritura.

### Ejemplo de Uso

```java
// Primero debemos crear una objeto que implemente la interfaz 'EstrategiaExportarArchivo',
// en este caso la clase 'LectorCSV' cumple con ese requerimiento
LectorCSV lectorCSV = new LectorCSV(';');

// Generamos la ruta donde se guardará el archivo exportado
String rutaGuardadoArchivo = "/ruta/a/archivo.csv";

// Ejecutamos el método 
cubo.exportar(rutaGuardadoArchivo, estrategiaExportacion);
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

El método `rollUp` permite realizar una operación de roll-up en una instancia de `Cubo`, lo que implica una agregación de los datos a un nivel superior en la jerarquía de los niveles de las dimensiones
presentes en el cubo. 

### Parámetros del Método

1. **criteriosAgregacion**: `Map<Dimension, String>`
   - **Descripción**: Un mapa de criterios que contiene como clave la dimensión sobre la cual se quiere aplicar la agregación
                      y como valor el nivel que se toma como criterio para aplicar la misma.
   - **Requisitos**: Debe ser un mapa válido que contenga dimensiones y niveles que estén presentes en el cubo.

2. **hechosSeleccionados**: `List<String>`
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
   - **Descripción**: Esta excepción se lanza si alguna dimensión especifcada en `criteriosAgregacion` no está en el cubo.
   - **Cómo Evitarla**: Verifica que todas las dimensiones especificadas estén presentes en el cubo antes de invocar al método.

3. **NivelNoPresenteException**
   - **Descripción**: Esta excepción se lanza si algún nivel especificado en `criteriosAgregacion` no está presente en el cubo.
   - **Cómo Evitarla**: Verifica que todos los niveles especificados estén presentes en las dimensiones asociadas al cubo al momento de invocar al método.

4. **HechoNoPresenteException**
   - **Descripción**: Esta excepción se lanza si algún hecho especificado en `hechosSeleccionados` no está presente en el cubo.
   - **Cómo Evitarla**: Verifica que todos los hechos especificados existen en la tabla de hechos asociada al cubo antes de llamar al método.

5. **ArgumentosInoperablesException**
   - **Descripción**: Esta excepción se lanza si el mapa `criteriosAgregacion` está vacío a la hora de invocar al método.
   - **Cómo Evitarla**: Verifica que el mapa contenga las condiciones de agregación antes de invocar al método.

### Ejemplo de Uso

```java
// Primero generamos un mapa con los criterios de reducción
Map<Dimension, String> criteriosAgregacion = new LinkedHashMap<>();
criteriosAgregacion.put(dim1, "nivelY");
criteriosAgregacion.put(dim2, "nivelX");

// Seleccionamos los hechos que queremos agrupar
List<String> hechosSeleccionados = Arrays.asList("hecho1", "hecho2");

// Realizamos la operación de roll-up con agregación "sum"
cubo.rollUp(criteriosAgregacion, hechosSeleccionados, "sum");

// Luego podemos proyectar el resultado de la operación
int cantFilas = 10;
List<String> columnasProyeccion = Arrays.asList("nivelY", "nivelX","hecho1", "hecho2");
cubo.proyectar(cantFilas, columnasProyeccion);
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

El método `slice` permite realizar una operación de corte (slice) en una instancia de `Cubo`, filtrando los datos en una dimensión específica a un nivel determinado y por un valor de corte. 

Esta operación una vez se ejecuta y filtra los datos según los criterios especificados, elimina **completamente** la dimensión que se vió implicada en la operación, reduciendo así la dimensionalidad del cubo cada vez que se ejecuta.

### Parámetros del Método

1. **dimension**: `Dimension`
   - **Descripción**: La dimensión en la que se va a realizar la operación de slice.
   - **Requisitos**: Debe ser una instancia válida de la clase `Dimension` que esté presente en el cubo al momento de invocar al método.

2. **nivel**: `String`
   - **Descripción**: El nivel en la dimensión en el que se va a realizar la operación de slice.
   - **Requisitos**: Debe ser un string válido que represente un nivel existente en la dimensión especificada.

3. **valorCorte**: `String`
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
// Defininimos el nivel y valor de corte para la operación de slice
String nivel = "nivel1";
String valorCorte = "valor1";

// Realizamos la operación de slice, ver que usamos una de las dimensiones
// usadas anteriormente para inicializar el cubo
cubo.slice(dim1, nivel, valorCorte);

// Luego podemos proyectar el resultado de la operación, verificando que efectivamente
// se eliminó la dimensión que se vió implicada en la operación y viendo como esto afectó 
// al resto de la información del cubo
int cantFilas = 10;
List<String> columnasProyeccion = Arrays.asList("nivelX_dim2", "nivelX_dim3", ....);
cubo.proyectar(cantFilas, columnasProyeccion);
```
### Estructura del Resultado

El método `slice` luego de haberse ejecutado modifica el estado interno del cubo, filtrando la información según los criterios especificados 
y eliminando completamente la dimensión que se vió implicada en la operación, reduciendo así la dimensionalidad del cubo.

El cubo resultante contará con el resto de dimensiones pero filtradas según los criterios especificos. Una forma ideal de visualizar estos resultados 
podría ser usando el método `exportar` de la clase `Cubo` para exportar el cubo y analizar la información restante.

## Método `dice`

Este método realiza una operación de "dice" en el cubo, que filtra los datos del cubo según los criterios especificados. Notar que su 
característica principal es que puede contener varios criterios de corte a diferencia del método `Slice`, además que **no reduce la dimensionalidad del cubo**.

### Parámetros del Método

1. **criteriosDice**: `Map<Dimension, Map<String, List<String>>>`
   - **Descripción**: Un mapa que contiene las dimensiones, niveles y valores a incluir en el método `dice`.
   - **Estructura**:
     - La clave del mapa es una instancia de la clase `Dimension`.
     - El valor asociado es otro mapa donde:
       - La clave es un `String` que representa el nivel de la dimensión seleccionada.
       - El valor es una lista de `String` que representa los valores que se quieren permitir a ese nivel.

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
// Definimos los criterios que usaremos para filtrar
Map<String, List<String>> criteriosNivel1 = new LinkedHashMap<>();
criteriosNivel1.put("Nivel1", Arrays.asList("Valor1", "Valor2"));

Map<String, List<String>> criteriosNivel2 = new LinkedHashMap<>();
criteriosNivel2.put("Nivel2", Arrays.asList("Valor3", "Valor4"));

// Creamos el mapa que contiene como clave las dimensiones creadas anteriormente
// para inicializar el cubo y como valor contiene los criterios de filtrado
Map<Dimension, Map<String, List<String>>> criterios = new LinkedHashMap<>();
criterios.put(dim1, criteriosNivel1);
criterios.put(dim2, criteriosNivel2);

// Ejecutamos el método dice
cubo.dice(criterios);

// Luego podemos proyectar el resultado de la operación
int cantFilas = 10;
List<String> columnasProyeccion = Arrays.asList("Nivel1", "Nivel2", ....);
cubo.proyectar(cantFilas, columnasProyeccion);
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

## Método `drillDown`

El método `drillDown` permite realizar una operación de drill-down en una instancia de `Cubo`, lo que implica desagregar los datos a un nivel inferior en la jerarquía de los niveles de las dimensiones presentes en el cubo.

Es importante notar que para ejecutar este método se tuvo que haber antes ejecutado el método `rollUp` sobre el cubo, ya que no se puede desagregar algo que ya está desagregado. Además los hechos que se verán 
luego de aplicar el método `drillDown` sobre el cubo serán los mismos que se vieron implicados en la última operación `rollUp` aplicada sobre el cubo.

### Parámetros del Método

1. **criteriosDesagregacion**: `Map<Dimension, String>`
   - **Descripción**: Un mapa de criterios que contiene como clave la dimensión sobre la cual se quiere aplicar la operación de desagregación y como valor el nivel hasta donde se quiere desagregar.
   - **Requisitos**: Debe ser un mapa válido que contenga dimensiones y niveles que estén presentes en el cubo.

### Excepciones Lanzadas

1. **ArgumentosInoperablesException**
   - **Descripción**: Esta excepción se lanza si el mapa de criterios de desagregación está vacío.
   - **Cómo Evitarla**: Verifica que el mapa contenga las condiciones de desagregación antes de invocar el método.

2. **DimensionNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguna dimensión especificada no está presente en el cubo.
   - **Cómo Evitarla**: Verifica que todas las dimensiones especificadas estén presentes en el cubo antes de invocar el método.

3. **NivelNoPresenteException**
   - **Descripción**: Esta excepción se lanza si algún nivel especificado no está presente en alguna dimensión.
   - **Cómo Evitarla**: Verifica que todos los niveles especificados estén presentes en las dimensiones asociadas al cubo al momento de invocar el método.

4. **NivelDesagregadoException**
   - **Descripción**: Esta excepción se lanza si algún nivel especificado ya está desagregado en el cubo.
   - **Cómo Evitarla**: Verifica que los niveles a desagregar no estén ya desagregados en el cubo antes de llamar al método.

### Ejemplo de Uso

```java
// Primero generamos un mapa con los criterios de desagregación
Map<Dimension, String> criteriosDesagregacion = new LinkedHashMap<>();
criteriosDesagregacion.put(dim1, "nivelInferiorY");
criteriosDesagregacion.put(dim2, "nivelInferiorX");

// Realizamos la operación de drill-down
cubo.drillDown(criteriosDesagregacion);

// Luego podemos proyectar el resultado de la operación
int cantFilas = 10;
List<String> columnasProyeccion = Arrays.asList("nivelInferiorY", "nivelInferiorX", "hecho1", "hecho2");
cubo.proyectar(cantFilas, columnasProyeccion);
```