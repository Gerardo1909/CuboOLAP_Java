# Documentación del módulo `tablas`

## Descripción

El módulo `tablas` fue creado con el propósito de facilitar la gestión de los archivos de **hechos** y **dimensiones**, generando así los tipos de datos
`Hecho` y `Dimension`. Hemos optado por una estrucutra de herencia de clases en donde implementamos una clase abstracta `Tabla` que encapsula el comportamiento 
común de las clases ya mencionadas. 

## Clase `Tabla`

La clase abstracta `Tabla` representa una estructura de datos en formato tabular. Esta clase proporciona métodos para realizar operaciones comunes como agrupar, fusionar, obtener valores únicos y visualizar los datos. Además, implementa la interfaz `Visualizable` para facilitar la visualización de la tabla.

### Método estático `groupBy`

 Agrupa filas de un objeto de tipo `Tabla` según columnas específicas.

#### Parámetros del Método

1. **tabla_operacion**: `Tabla`
   - **Descripción**: La tabla en la que se realizará la operación de agrupación.
   - **Requisitos**: Debe ser una instancia válida de la clase `Tabla`.

2. **columnas_agrupacion**: `List<String>`
   - **Descripción**: Columnas por las que se va a agrupar.
   - **Requisitos**: Debe ser una lista válida de nombres de columnas presentes en la tabla de operación.

3. **columnas_a_agrupar**: `List<String>`
   - **Descripción**: Columnas que se van a agrupar.
   - **Requisitos**: Debe ser una lista válida de nombres de columnas presentes en la tabla de operación.

#### Excepciones Lanzadas

1. **ColumnaNoPresenteException**
   - **Descripción**: Se lanza si alguna de las columnas especificadas no existe en la tabla.
   - **Cómo Evitarla**: Asegúrate de que todas las columnas especificadas estén presentes en la tabla antes de llamar al método `groupBy`.

#### Ejemplo de Uso

```java
// Generamos las listas que contienen a las columnas involucradas en el método
List<String> columnas_agrupacion = Arrays.asList("col1", "col2"...);
List<String> columnas_a_agrupar = Arrays.asList("col_x", "col_y"...);

// Llamo al método, debemos haber instanciado anteriormente a un tipo de dato Tabla
Map<List<String>, List<List<String>>> resultado = TipoTabla.groupBy(tabla_operacion, columnas_agrupacion, columnas_a_agrupar);
```

#### Estructura del Resultado

Se devuelve un mapa donde las claves son listas de valores de las columnas de agrupación y los valores son listas de listas de datos agrupados.

- **Clave del Mapa**: `List<String>`
  - **Descripción**: Una lista de valores que representan la combinación única de valores de las columnas de agrupación.
  - **Ejemplo**: Si las columnas de agrupación son ["A", "B"] y la combinación de valores para una fila es ["valor1", "valor2"], entonces la clave será la lista ["valor1", "valor2"].

- **Valor del Mapa**: `List<List<String>>`
  - **Descripción**: Una lista de listas de datos agrupados correspondientes a las columnas que se están agrupando.
  - **Ejemplo**: Si las columnas a agrupar son ["C", "D"] y los valores de esas columnas para una fila son ["dato1", "dato2"], entonces la lista correspondiente será ["dato1", "dato2"].

Internamente la estructura que se devuelve tiene una forma de este estilo:

```java
{
    ["valor1", "valor2"] -> [["dato1", "dato2"], ["dato3", "dato4"]],
    ["valor3", "valor4"] -> [["dato5", "dato6"]]
}
```

### Método estático `merge`

Fusiona dos objetos de tipo `Tabla` basándose en una columna específica.

#### Parámetros del Método

1. **tabla_izq**: `Tabla`
   - **Descripción**: La tabla izquierda que será modificada para contener el resultado de la fusión.
   - **Requisitos**: Debe ser una instancia válida de la clase `Tabla`.

2. **tabla_der**: `Tabla`
   - **Descripción**: La tabla derecha que se fusionará con la tabla izquierda.
   - **Requisitos**: Debe ser una instancia válida de la clase `Tabla`.

3. **on**: `String`
   - **Descripción**: El nombre de la columna por la que se realizará la fusión.
   - **Requisitos**: Debe ser una columna que exista en ambas tablas.

#### Excepciones Lanzadas

1. **ColumnaNoPresenteException**
   - **Descripción**: Se lanza si la columna especificada no existe en alguna de las tablas.
   - **Cómo Evitarla**: Asegúrate de que la columna especificada esté presente en ambas tablas antes de llamar al método `merge`.

#### Ejemplo de Uso

```java
// Instanciamos dos datos de tipo Tabla
TipoTabla tabla_izq = new TipoTabla("tabla_izq", ...);
TipoTabla tabla_der = new TipoTabla("tabla_der", ...);

// Guardamos el nombre de la columna por la cual se junta
String col_on = "col_1";

// Llamamos al método merge
TipoTabla.merge(tabla_izq, tabla_der, col_on);
```

#### Estructura del Resultado

El método `merge` no devuelve un valor, pero modifica la tabla izquierda (`tabla_izq`) para que contenga el resultado de la fusión. Los datos y los encabezados de la tabla izquierda se actualizan para incluir los datos fusionados de ambas tablas.

- **Atributos Modificados de `tabla_izq`**:
  - **data**: `List<List<String>>`
    - **Descripción**: Las filas resultantes de la fusión de ambas tablas.
    - **Ejemplo**: Si la fusión de dos filas produce `["valor1", "valor2", "valor3", "valor4"]`, esta será una de las filas en `data`.
  - **headers**: `List<String>`
    - **Descripción**: Los encabezados resultantes de la combinación de los encabezados de ambas tablas.
    - **Ejemplo**: Si los encabezados de `tabla_izq` son `["A", "B"]` y los de `tabla_der` son `["C", "D"]`, los nuevos encabezados serán `["A", "B", "C", "D"]`.

### Método `obtenerValoresUnicos`

Toma una columna de una instancia de tipo `Tabla` y devuelve en una lista los valores únicos de dicha columna.

#### Parámetros del Método

1. **columna**: `String`
   - **Descripción**: El nombre de la columna de la que se van a obtener los valores únicos.
   - **Requisitos**: Debe ser un nombre de columna válido que exista en la tabla.

#### Excepciones Lanzadas

1. **ColumnaNoPresenteException**
   - **Descripción**: Se lanza si la columna especificada no existe en la tabla.
   - **Cómo Evitarla**: Asegúrate de que la columna especificada esté presente en la tabla antes de llamar al método `obtenerValoresUnicos`.

#### Ejemplo de Uso

```java
// Llamamos al método 
List<String> valoresUnicos = TipoTabla.obtenerValoresUnicos("nombreColumna");

// Podemos trabajar con la lista obtenida e imprimirla por consola
for (String valor : valoresUnicos) {
      System.out.println(valor);
  }
```

#### Estructura del Resultado

El método `obtenerValoresUnicos` devuelve una lista de valores únicos de la columna especificada.

- **Valor Devuelto**: `List<String>`
  - **Descripción**: Una lista de valores únicos que se encuentran en la columna especificada.
  - **Ejemplo**: Si la columna contiene los valores `["a", "b", "a", "c"]`, la lista devuelta será `["a", "b", "c"]`.

### Método `ver`

El método `ver` permite visualizar por consola la estructura de una instancia de tipo `Tabla` una vez este último sea instanciado.

#### Parámetros del Método

1. **n_filas**: `int`
   - **Descripción**: El número de filas que se desean ver.
   - **Requisitos**: Debe ser un número entero positivo que no exceda el número de filas disponibles en la tabla.

2. **columnas**: `List<String>`
   - **Descripción**: Una lista de nombres de columnas que se desean visualizar.
   - **Requisitos**: Debe ser una lista válida de nombres de columnas presentes en la tabla.

#### Excepciones Lanzadas

1. **ColumnaNoPresenteException**
   - **Descripción**: Se lanza si alguna de las columnas especificadas no existe en la tabla.
   - **Cómo Evitarla**: Asegúrate de que todas las columnas especificadas estén presentes en la tabla antes de llamar al método `ver`.

2. **FilaFueraDeRangoException**
   - **Descripción**: Se lanza si el número de filas especificado excede el número de filas disponibles en la tabla.
   - **Cómo Evitarla**: Asegúrate de que el número de filas solicitado no sea mayor que el número de filas disponibles en la tabla.

#### Ejemplo de Uso

```java
// Llamamos al método
tabla.ver(5, Arrays.asList("columna1", "columna2", "columna3"));
```

#### Estructura del Resultado

El método `ver` imprime las columnas seleccionadas y los datos correspondientes en un formato tabular. Cada columna tiene 
un ancho fijo para mantener el alineamiento adecuado. A continuación un ejemplo de su salida por consola:

```java
columna1             columna2             columna3             
valor11              valor12              valor13              
valor21              valor22              valor23              
valor31              valor32              valor33              
valor41              valor42              valor43              
valor51              valor52              valor53   
```

## Clase `Dimension`

La clase `Dimension` representa una tabla de dimensión, la cual es un componente de una instancia de `CuboOLAP`. Hereda directamente de la clase `Tabla`, por 
lo cual cuenta con todos los métodos descritos anteriormente.

### Constructor de la clase #1

El constructor de esta clase te permite crear una instancia de tipo `Dimension` proporcionando el nombre de la misma, su clave primaria para conectarla a una tabla de hechos y una lista de los niveles que contiene.

#### Parámetros del Constructor

1. **nombre**: `String`
   - **Descripción**: El nombre de la dimensión.
   - **Requisitos**: Debe ser un string válido que representa el nombre de la dimensión.

2. **niveles**: `List<String>`
   - **Descripción**: La lista de niveles de la dimensión.
   - **Requisitos**: Debe ser una lista de nombres de niveles que existen en la tabla de la dimensión.

3. **primaryKey**: `String`
   - **Descripción**: La clave primaria de la dimensión.
   - **Requisitos**: Debe ser un nombre de columna válido que existe en la tabla de la dimensión y se usa como clave primaria.

4. **estrategia_lectura**: `EstrategiaLecturaArchivo`
   - **Descripción**: La estrategia usada para leer el archivo de datos.
   - **Requisitos**: Debe ser una instancia válida de una clase que implemente la interfaz `EstrategiaLecturaArchivo`.

5. **ruta_archivo**: `String`
   - **Descripción**: La ruta del archivo de datos.
   - **Requisitos**: Debe ser una ruta válida a un archivo que contiene los datos de la dimensión.

#### Excepciones Lanzadas

1. **IOException**
   - **Descripción**: Se lanza si hay un error de E/S al leer el archivo.
   - **Cómo Evitarla**: Asegúrate de que el archivo en la ruta especificada esté accesible y sea legible.

2. **ColumnaNoPresenteException**
   - **Descripción**: Se lanza si la clave primaria especificada no existe en la tabla de la dimensión.
   - **Cómo Evitarla**: Verifica que la clave primaria esté presente en los encabezados de la tabla antes de pasarla al constructor.

3. **NivelNoPresenteException**
   - **Descripción**: Se lanza si uno de los niveles especificados en la lista de niveles no existe en la tabla de la dimensión.
   - **Cómo Evitarla**: Asegúrate de que todos los niveles especificados estén presentes en los encabezados de la tabla antes de pasarlos al constructor.

#### Ejemplo de Uso

```java
// Primero debemos crear una objeto que implemente la interfaz 'EstrategiaLecturaArchivo'
  EstrategiaLecturaArchivo estrategia = new EstrategiaLecturaArchivo(param);

// Llamamos al constructor
Dimension dimension = new Dimension(
        "NombreDimension",
        Arrays.asList("Nivel1", "Nivel2"),
        "PrimaryKey",
        estrategia,
        "ruta/al/archivo.csv"
  );
```

### Constructor de la clase #2

Este constructor inicializa una instancia de la clase `Dimension`, configurando la tabla de dimensión con información general, una lista de niveles y una clave primaria.

#### Parámetros del Constructor

1. **nombre**: `String`
   - **Descripción**: El nombre de la dimensión.
   - **Requisitos**: Debe ser una cadena válida que representa el nombre de la dimensión.

2. **niveles**: `List<String>`
   - **Descripción**: La lista de niveles en la dimensión.
   - **Requisitos**: Debe ser una lista válida de cadenas, donde cada cadena representa un nivel.

3. **primaryKey**: `String`
   - **Descripción**: La clave primaria de la dimensión.
   - **Requisitos**: Debe ser una cadena válida que representa la clave primaria y debe estar presente en los encabezados de la tabla.

4. **data**: `List<List<String>>`
   - **Descripción**: Los datos de la tabla. Cada lista interna representa una fila de la tabla.
   - **Requisitos**: Debe ser una lista válida de listas de cadenas, donde cada lista interna contiene los valores de una fila.

5. **headers**: `List<String>`
   - **Descripción**: Los encabezados de la tabla. Cada cadena representa un nombre de columna.
   - **Requisitos**: Debe ser una lista válida de cadenas que representan los nombres de las columnas.

#### Excepciones Lanzadas

- **ColumnaNoPresenteException**
  - **Descripción**: Se lanza si la clave primaria pasada como argumento no existe en la dimensión.
  - **Cómo Evitarla**: Verifica que la clave primaria especificada esté incluida en los encabezados de la tabla antes de llamar al constructor.

- **NivelNoPresenteException**
  - **Descripción**: Se lanza si alguno de los niveles pasados en la lista de niveles no existe en la dimensión.
  - **Cómo Evitarla**: Asegúrate de que todos los niveles especificados estén incluidos en los encabezados de la tabla antes de llamar al constructor.

#### Ejemplo de Uso

```java
// Generamos los datos de la tabla
List<List<String>> data = Arrays.asList(
    Arrays.asList("1", "A", "X"),
    Arrays.asList("2", "B", "Y"),
    Arrays.asList("3", "C", "Z")
);

// Guardamos su headers y niveles en listas 
List<String> headers = Arrays.asList("ID", "Name", "Category");
List<String> niveles = Arrays.asList("Name", "Category");

// Guardamos la clave primaria de la tabla dimensión
String primaryKey = "ID";

// Finalmente, generamos una instancia de la clase mediante el constructor
Dimension dimension = new Dimension("Dim1", niveles, primaryKey, data, headers);
```

## Clase `Hecho`

La clase `Hecho` representa una tabla de hechos, la cual es un componente de una instancia de `CuboOLAP`. Hereda directamente de la clase `Tabla`, por 
lo cual cuenta con todos los métodos descritos anteriormente.

### Constructor de la clase #1

El constructor de esta clase te permite crear una instancia de tipo `Hecho` proporcionando el nombre de la misma y una lista con los hechos contenidos en ella.

#### Parámetros del Constructor

1. **nombre**: `String`
   - **Descripción**: El nombre de la tabla de hechos.
   - **Requisitos**: Debe ser un string válido que representa el nombre de la tabla de hechos.

2. **hechos**: `List<String>`
   - **Descripción**: La lista de hechos en la tabla.
   - **Requisitos**: Debe ser una lista de nombres de hechos que existen en la tabla de hechos.

4. **estrategia_lectura**: `EstrategiaLecturaArchivo`
   - **Descripción**: La estrategia usada para leer el archivo de datos.
   - **Requisitos**: Debe ser una instancia válida de una clase que implemente la interfaz `EstrategiaLecturaArchivo`.

4. **ruta_archivo**: `String`
   - **Descripción**: La ruta del archivo de datos.
   - **Requisitos**: Debe ser una ruta válida a un archivo que contiene los datos de la tabla de hechos.

#### Excepciones Lanzadas

1. **IOException**
   - **Descripción**: Se lanza si hay un error de E/S al leer el archivo.
   - **Cómo Evitarla**: Asegúrate de que el archivo en la ruta especificada esté accesible y sea legible.

2. **HechoNoPresenteException**
   - **Descripción**: Se lanza si alguno de los hechos especificados no existe en la tabla de hechos.
   - **Cómo Evitarla**: Verifica que todos los hechos especificados estén presentes en los encabezados de la tabla antes de pasarlos al constructor.

#### Ejemplo de Uso

```java
// Primero debemos crear una objeto que implemente la interfaz 'EstrategiaLecturaArchivo'
EstrategiaLecturaArchivo estrategia = new EstrategiaLecturaArchivo();

// Llamamos al constructor
Hecho hecho = new Hecho(
        "NombreTablaHechos",
        Arrays.asList("Hecho1", "Hecho2", "Hecho3"),
        estrategia,
        "ruta/al/archivo.csv"
  );
```

### Constructor de la clase #2

Este constructor inicializa una instancia de la clase `Hecho`, configurando la tabla de hechos con datos, encabezados y una lista de hechos.

#### Parámetros del Constructor

1. **nombre**: `String`
   - **Descripción**: El nombre de la tabla de hechos.
   - **Requisitos**: Debe ser una cadena válida que representa el nombre de la tabla de hechos.

2. **data**: `List<List<String>>`
   - **Descripción**: Los datos de la tabla. Cada lista interna representa una fila de la tabla.
   - **Requisitos**: Debe ser una lista válida de listas de cadenas, donde cada lista interna contiene los valores de una fila.

3. **headers**: `List<String>`
   - **Descripción**: Los encabezados de la tabla. Cada cadena representa un nombre de columna.
   - **Requisitos**: Debe ser una lista válida de cadenas que representan los nombres de las columnas.

4. **hechos**: `List<String>`
   - **Descripción**: La lista de hechos en la tabla.
   - **Requisitos**: Debe ser una lista válida de cadenas, donde cada cadena representa un hecho que debe estar presente en los encabezados de la tabla.

#### Excepciones Lanzadas

- **HechoNoPresenteException**
  - **Descripción**: Se lanza si alguno de los hechos indicados no está presente en la tabla de hechos.
  - **Cómo Evitarla**: Verifica que todos los hechos especificados estén incluidos en los encabezados de la tabla antes de llamar al constructor.

#### Ejemplo de Uso

```java
// Generamos los datos de la tabla
List<List<String>> data = Arrays.asList(
    Arrays.asList("1", "100", "200"),
    Arrays.asList("2", "150", "250"),
    Arrays.asList("3", "200", "300")
);

// Guardamos su headers y hechos en listas 
List<String> headers = Arrays.asList("ID", "Sales", "Profit");
List<String> hechos = Arrays.asList("Sales", "Profit");

// Finalmente, generamos una instancia de la clase mediante el constructor
Hecho hecho = new Hecho("TablaDeHechos", data, headers, hechos);
```