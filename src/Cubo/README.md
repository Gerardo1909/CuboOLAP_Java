# Documentación de la clase `CuboOLAP`

## Descripción

La clase `CuboOLAP` es la pieza central de nuestra librería y proporciona las funcionalidades esenciales para trabajar con cubos OLAP. Esta clase permite realizar diversas operaciones de análisis sobre las tablas que componen el cubo, es decir, las **dimensiones** y los **hechos**.

### Operaciones Disponibles

La clase `CuboOLAP` ofrece una variedad de operaciones que facilitan el análisis multidimensional de los datos:

- **Roll-up**: Agrega los datos a un nivel superior de la jerarquía.
- **Slice**: Filtra los datos en una dimensión específica en un nivel determinado.
- **Drill-down**: Desglosa los datos a un nivel más detallado de la jerarquía.
- **Dice**: Filtra los datos en varias dimensiones a la vez.

### Visualización del Estado del Cubo

Además de las operaciones de análisis, la clase `CuboOLAP` permite visualizar el estado del cubo después de realizar una operación. Esta funcionalidad es muy útil para los científicos de datos y analistas, ya que proporciona una forma rápida y eficiente de inspeccionar los resultados de las operaciones realizadas.

## Constructor de la clase 

El constructor de esta clase te permite crear una instancia de tipo `CuboOLAP` proporcionando el nombre del cubo, una tabla de hechos y una lista de dimensiones.

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

2. **ColumnaNoPresenteException**
   - **Descripción**: Aunque no se muestra en el fragmento del constructor, este tipo de excepción puede ser lanzada si hay problemas con las columnas en la tabla de hechos o las dimensiones durante la operación de merge.
   - **Cómo Evitarla**: Verifica que todas las columnas necesarias estén presentes y que no haya conflictos durante el merge de la tabla de hechos con las dimensiones.

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

## Visualización del cubo luego de aplicar una operación

El método `ver` permite visualizar los datos en una instancia de `CuboOLAP` después de haber aplicado una operación sobre la misma. Este método muestra un número específico de filas y columnas seleccionadas.

### Parámetros del Método

1. **n_filas**: `int`
   - **Descripción**: El número de filas que se desea visualizar. Si se pasa un número grande de filas 
                      (entiendáse por grande un número mayor a la longitud de las filas implicadas en la operación)
                      se mostrarán todas las filas que resultaron de la operación aplicada.
   - **Requisitos**: Debe ser un número entero positivo que indique la cantidad de filas a mostrar.

2. **columnas**: `List<String>`
   - **Descripción**: La lista de nombres de las columnas que se desean visualizar. Si se le especifica como argumento el valor 
                      `null` entonces se seleccionarán todas las columnas implicadas en la operación.
   - **Requisitos (Si el parámetro no es `null`)**: Debe ser una lista válida de nombres de columnas que existen entre 
                                                las tablas disponibles del cubo para el momento de su invocación.

### Excepciones Lanzadas

1. **ColumnaNoPresenteException (Si el parámetro "columnas" no es `null`)**
   - **Descripción**: Esta excepción se lanza si alguna de las columnas especificadas no está presente en los encabezados disponibles del cubo.
   - **Cómo Evitarla**: Verifica que todas las columnas especificadas existen en las tablas que viven dentro del cubo antes de llamar al método.

### Consideraciones Previas

- **Operaciones Previas**: Es necesario haber aplicado alguna operación al cubo antes de utilizar este método. Si no se ha realizado ninguna operación, se notificará al usuario y el método no continuará.

### Ejemplo de Uso #1

```java
// Aplicamos alguna operación sobre el cubo
cubo.operar(param1, param2...);

// Definimos el número de filas y las columnas a visualizar, idealmente las 
// columnas deberían haberse visto implicadas en la operación anterior
int n_filas = 10;
List<String> columnas = Arrays.asList("columna1", "columna2", "columna3");

// Visualizamos los datos del cubo
cubo.ver(n_filas, columnas);
```

### Ejemplo de Uso #2

```java
// Aplicamos alguna operación sobre el cubo
cubo.operar(param1, param2...);

// Definimos el número de filas que queremos visualizar
int n_filas = 10;

// Visualizamos los datos del cubo
cubo.ver(n_filas, null);
```

### Ejemplo de Uso #3

```java
// Aplicamos alguna operación sobre el cubo
cubo.operar(param1, param2...);

// Seleccionamos algunas columnas (podemos optar por dejarlo en null)
List<String> columnas = Arrays.asList("columna1", "columna2", "columna3");

// Definimos el número de filas que queremos visualizar,
// de esta forma mostrará todas las filas implicadas en la operación
// (incluso si el número es menor a 'n_filas')
int n_filas = 1000000000000000;

// Visualizamos los datos del cubo
cubo.ver(n_filas, columnas);
```

### Estructura del Resultado

El método `ver` para el caso de una instancia de `CuboOLAP` imprimirá por consola las columnas seleccionadas hasta el número de filas indicado luego
de haber realizado alguna operación sobre el cubo. A continuación un ejemplo de su salida luego de aplicar el método `Slice` a una instancia de `CuboOLAP`:

```java
anio                mes                 ciudad              costo
2020                3                   Alhambra            6.72
2020                3                   Alhambra            36.99
2020                3                   Alhambra            3247.53
2020                3                   Auburn              55.14
2020                5                   Baldwin Park        157.06
2020                2                   Baldwin Park        26.18
2020                5                   Baldwin Park        157.06
2020                5                   Baldwin Park        189.99
2020                3                   Camarillo           409.25
2020                3                   Camarillo           35.96
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
   - **Requisitos**: Debe ser una lista válida de nombres de hechos que existen en la tabla de hechos del cubo.

3. **agregacion**: `String`
   - **Descripción**: La operación de agregación a realizar.
   - **Requisitos**: Solo se soportan las siguientes operaciones: `"sum"`, `"max"`, `"min"`, `"count"`. Debe escribirse tal cual al pasar el argumento.

### Excepciones Lanzadas

1. **AgregacionNoSoportadaException**
   - **Descripción**: Esta excepción se lanza si la operación de agregación seleccionada no está entre las disponibles.
   - **Cómo Evitarla**: Asegúrate de pasar una de las operaciones soportadas: `"sum"`, `"max"`, `"min"`, `"count"`.

2. **DimensionNoPresenteException**
   - **Descripción**: Esta excepción se lanza si alguna dimensión especifcada en `criterios_reduccion` no está presente en las dimensiones del cubo.
   - **Cómo Evitarla**: Verifica que todas las dimensiones especificadas existan en las dimensiones del cubo antes de llamar al método.

3. **NivelNoPresenteException**
   - **Descripción**: Esta excepción se lanza si algún nivel especificado en `criterios_reduccion` no está presente en las dimensiones del cubo.
   - **Cómo Evitarla**: Verifica que todos los niveles especificados existen en las dimensiones del cubo antes de llamar al método.

4. **HechoNoPresenteException**
   - **Descripción**: Esta excepción se lanza si algún hecho especificado en `hechos_seleccionados` no está presente en la tabla de hechos.
   - **Cómo Evitarla**: Verifica que todos los hechos especificados existen en la tabla de hechos antes de llamar al método.

5. **TablaException**
   - **Descripción**: Esta excepción se lanza si ocurre un error inesperado al invocar el comando.
   - **Cómo Evitarla**: Maneja las excepciones correctamente y asegúrate de que la tabla de hechos y las dimensiones estén bien configuradas.

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

// Luego podemos visualizar el resultado de la operación
int n_filas = 10;
cubo.ver(n_filas, null);
```
### Estructura del Resultado

El método `rollUp` luego de haberse ejecutado modifica el atributo `proyeccion_cubo`, lo cual indica que el cubo está disponible para su 
visualización. A continuación un ejemplo un ejemplo de su salida por pantalla a través del método `ver` después de haberlo ejecutado en 
una instancia de `CuboOLAP`:

```java
anio                quarter             mes                 region              pais                provincia           cantidad
2019                4                   12                  Europe              France              Seine Saint Denis   17.0
2019                2                   4                   North America       United States       Mississippi         9.0
2019                3                   8                   North America       United States       Arizona             15.0
2019                1                   1                   North America       United States       Michigan            128.0
2018                1                   3                   North America       Canada              Ontario             212.0
2018                3                   8                   North America       United States       Missouri            89.0
2017                3                   7                   North America       Canada              British Columbia    93.0
2017                3                   9                   North America       Canada              Quebec              48.0
2020                2                   5                   Europe              Germany             Saarland            16.0
2018                4                   10                  North America       United States       Alabama             3.0
2018                1                   2                   North America       United States       Utah                36.0
2018                4                   10                  North America       United States       Minnesota           6.0
2020                1                   1                   North America       United States       Colorado            114.0
2020                1                   2                   North America       United States       California          509.0
2018                2                   6                   North America       United States       Illinois            111.0
2018                4                   11                  North America       United States       Mississippi         191.0
2019                3                   8                   North America       United States       Utah                356.0
2019                4                   11                  North America       Canada              British Columbia    320.0
2018                2                   5                   North America       Canada              Manitoba            15.0
2020                1                   2                   Europe              Germany             Bayern              129.0
2018                3                   9                   North America       United States       Oregon              88.0
2019                3                   9                   North America       Canada              Ontario             1018.0
```

## Método `slice`

El método `slice` permite realizar una operación de corte (slice) en una instancia de `CuboOLAP`, filtrando los datos en una dimensión específica a un nivel determinado y por un valor de corte.

### Parámetros del Método

1. **dimension**: `Dimension`
   - **Descripción**: La dimensión en la que se va a realizar la operación de slice.
   - **Requisitos**: Debe ser una instancia válida de la clase `Dimension` que esté presente en el cubo.

2. **nivel**: `String`
   - **Descripción**: El nivel en la dimensión en el que se va a realizar la operación de slice.
   - **Requisitos**: Debe ser un string válido que represente un nivel existente en la dimensión especificada.

3. **valor_corte**: `String`
   - **Descripción**: El valor de corte para la operación de slice.
   - **Requisitos**: Debe ser un string válido que represente un valor presente en el nivel especificado de la dimensión.

### Excepciones Lanzadas

1. **DimensionNoPresenteException**
   - **Descripción**: Esta excepción se lanza si la dimensión especificada no está presente en el cubo.
   - **Cómo Evitarla**: Asegúrate de que la dimensión pasada como argumento esté incluida en el cubo antes de llamar al método.

2. **NivelNoPresenteException**
   - **Descripción**: Esta excepción se lanza si el nivel especificado no está presente en la dimensión.
   - **Cómo Evitarla**: Verifica que el nivel especificado existe en la dimensión antes de llamar al método.

3. **ValorNoPresenteException**
   - **Descripción**: Esta excepción se lanza si el valor de corte no está presente en el nivel seleccionado de la dimensión.
   - **Cómo Evitarla**: Asegúrate de que el valor de corte existe en el nivel especificado antes de llamar al método.

4. **TablaException**
   - **Descripción**: Esta excepción se lanza si ocurre un error inesperado al invocar el comando.
   - **Cómo Evitarla**: Maneja las excepciones correctamente y asegúrate de que los datos estén bien configurados.

### Ejemplo de Uso

```java
// Defininimos la dimensión, nivel y valor de corte para la operación de slice
Dimension dimension = new Dimension("dim1", ...);
String nivel = "nivel1";
String valor_corte = "valor1";

// Realizamos la operación de slice
cubo.slice(dimension, nivel, valor_corte);

// Luego podemos visualizar el resultado de la operación
int n_filas = 10;
cubo.ver(n_filas, null);
```
### Estructura del Resultado

El método `slice` luego de haberse ejecutado modifica el atributo `proyeccion_cubo`, lo cual indica que el cubo está disponible para su 
visualización. A continuación un ejemplo un ejemplo de su salida por pantalla a través del método `ver` después de haberlo ejecutado en 
una instancia de `CuboOLAP`:

```java
anio                mes                 ciudad              costo
2017                11                  Baldwin Park        5694.28
2017                11                  Baldwin Park        9490.47
2017                11                  Baldwin Park        3796.19
2017                11                  Baldwin Park        3796.19
2017                8                   Baldwin Park        27.17
2017                11                  Baldwin Park        10.19
2017                8                   Baldwin Park        13.59
2017                11                  Baldwin Park        11.41
2017                11                  Baldwin Park        1912.15
2017                12                  Barstow             413.15
```

## Método `dice`

Este método realiza una operación de "dice" en el cubo, que filtra los datos del cubo según los criterios especificados. Notar que su 
característica principal es que puede contener varios criterios de corte a diferencia del método `Slice`.

### Parámetros del Método

1. **criterios**: `Map<Dimension, Map<String, List<String>>>`
   - **Descripción**: Un mapa que contiene las dimensiones, niveles y valores a incluir en el método `dice`.
   - **Estructura**:
     - La clave del mapa es una instancia de la clase `Dimension`.
     - El valor asociado es otro mapa donde:
       - La clave es un `String` que representa el nivel de la dimensión seleccionada.
       - El valor es una lista de `String` que representa los valores de corte en ese nivel.

### Excepciones Lanzadas

- **TablaException**
  - **Descripción**: Se lanza si se produce un error inesperado al ejecutar el comando.
  - **Cómo Evitarla**: Asegúrate de que los datos del cubo están en un estado correcto antes de realizar la operación de "dice".

- **DimensionNoPresenteException**
  - **Descripción**: Se lanza si la dimensión especificada no está presente en el cubo.
  - **Cómo Evitarla**: Verifica que todas las dimensiones especificadas están incluidas en el cubo antes de llamar al método.

- **NivelNoPresenteException**
  - **Descripción**: Se lanza si el nivel especificado no está presente en la dimensión.
  - **Cómo Evitarla**: Asegúrate de que todos los niveles especificados están presentes en las dimensiones correspondientes.

- **NivelNoPresenteException**
  - **Descripción**: Se lanza si el valor de corte no está presente en el nivel seleccionado de la dimensión.
  - **Cómo Evitarla**: Comprueba que todos los valores de corte están presentes en los niveles correspondientes de las dimensiones antes de realizar la operación.

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

// Luego podemos visualizar el resultado de la operación
int n_filas = 10;
cubo.ver(n_filas, null);
```

### Estructura del Resultado

El método `dice` luego de haberse ejecutado modifica el atributo `proyeccion_cubo`, lo cual indica que el cubo está disponible para su 
visualización. A continuación un ejemplo un ejemplo de su salida por pantalla a través del método `ver` después de haberlo ejecutado en 
una instancia de `CuboOLAP`:

```java
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
2017                California          31.72
2017                California          2654.12
2017                California          1769.42
2017                California          4423.54
2017                California          413.15
2017                California          826.29
2017                California          826.29
2017                California          413.15
2017                California          24.06
```