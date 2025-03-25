# :syringe: Proyecto de Gestión de Información sobre Vacunación en Panamá - Backend API

![Imagen logo del proyecto](https://github.com/Kingg22/api-vacunas-panama/blob/main/src/main/resources/images/icon.png?raw=true)

## Descripción

Este proyecto tiene como objetivo mejorar el control y la gestión de la información sobre vacunación en las distintas
sedes sanitarias de Panamá, tanto públicas como privadas. La implementación de este sistema permitirá optimizar el
seguimiento de las vacunaciones, mejorar la precisión de los datos y apoyar en la toma de decisiones para el control
sanitario del país. A continuación, se detallan los aspectos clave del proyecto, sus objetivos, alcance y
recomendaciones para su implementación.
Este proyecto consiste en el desarrollo de una API que gestiona la información sobre vacunación en Panamá. La
solución está diseñada utilizando tecnologías como Java con Spring Boot y SQL Server, y se enfoca en varios aspectos
clave, incluyendo la precisión de los datos, la seguridad, la interoperabilidad y la facilidad de uso.

:construction: Proyecto mejorado lentamente en los tiempos libres :construction:

:sparkles: Repositorio Fronted en Flet del proyecto [aquí](https://github.com/patrickvillarroel/flet-vacunas-panama) :dizzy:

### :pushpin: Objetivo del Proyecto

El objetivo principal del proyecto es llevar un mejor control sobre la vacunación en las distintas sedes sanitarias del
país, brindando datos precisos para combatir y tomar medidas necesarias en casos de epidemias y pandemias. Este sistema
contribuirá al Plan Nacional de Salud 2016-2025, específicamente en la Política 2, que se enfoca en ejercer el liderazgo
en salud a nivel nacional y en la integración del sistema de información de salud en las entidades públicas y privadas.

### :dart: Alcance del Proyecto

Cobertura Nacional:
La base de datos será utilizada a nivel nacional por centros médicos estatales y centros privados que deseen participar.

## :warning: Limitaciones actuales de la API

- Los roles es una entidad que se puede modificar posterior a la creación del enum utilizado por la API al verificar los
  roles y su jerarquía. Se debe modificar a medida que los roles cambien.
- Se valida el formato del email mas no si su dominio existe.
- La v1 de la API está limitada en:
    - No hay endpoint u operaciones asíncronas.
    - No hay filtros de búsquedas.
    - No paginación de resultados.
- De las reglas de vacunación en el proyecto solo implementamos: la edad mínima, intervalo entre primera y segunda
  dosis.
- Aún no se implementan las reglas de negocio para manejar esquemas especiales de vacunación para niños rezagados,
  mujeres embarazadas y otras poblaciones específicas.
- El listado de vacunas (por lo tanto, enfermedades, síntomas y 1 fabricante por vacuna) se mantiene
  el [esquema de vacunación de Panamá 2023 abril](https://www.spp.com.pa/publicaciones/documentos-interes/vacunacion/ESQUEMA-DE-VACUNACION_2023_3Abril.pdf).
- Se debe procurar el uso correcto de las zonas horarias en campos de fechas. Recomendamos UTC.

## :hammer_and_wrench:Tecnologías Utilizadas

- [Java](https://www.java.com/es/) - Lenguaje de programación inicial para el desarrollo de la API.
- [Kotlin](https://kotlinlang.org/) – Lenguaje de programación principal del API.
- [SQL Server](https://www.microsoft.com/es-mx/sql-server) - Para la gestión de la base de datos.
- [Spring Boot](https://spring.io/) - Framework de Java, módulos utilizados: web, data, security, OAuth resource server,
  validations, amqp (mensajería), redis cache, testing.
- [Spring Dotenv](https://github.com/paulschwarz/spring-dotenv) - Librería para cargar las variables de entornos para
  Spring.
- [JDBC SQL Driver](https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server?view=sql-server-ver16) -
  Driver para conectar con la base de datos SQL Server en Java.
- [MapStruct](https://mapstruct.org/) - Librería para generar mappers entre DTO y Entities.
- [Lombok](https://projectlombok.org/) - Librería para reducir código con anotaciones.
- [Jackson](https://github.com/FasterXML/jackson) - Librería para parsear a JSON.
- [Error handling Spring Boot](https://github.com/wimdeblauwe/error-handling-spring-boot-starter/) - Librería para
  Spring Boot utilizada para dar Error Response uniformes y Logger de los mismos.
- [Redis](https://redis.io/) - Base de datos en memoria para almacenar datos de forma rápida.
- [RabbitMQ](https://www.rabbitmq.com/) - Broker de mensajería entre microservicios.

Puede encontrar más detalles de las licencias
en [THIRD-PARTY](https://github.com/Kingg22/api-vacunas-panama/blob/main/THIRD-PARTY.txt)

## :pencil: Autores

- **Rey Acosta** – _Java_ – [Kingg22](https://github.com/Kingg22)
- **Allisson Ortega** – _PDF module_ – [Applepie2005](https://github.com/Applepie2005)

## Conclusión

Este proyecto representa un paso significativo hacia la modernización y mejora del control de la vacunación en Panamá. A
través de la implementación de tecnologías modernas y prácticas de desarrollo robustas, se espera mejorar la eficiencia
y efectividad del sistema de salud en el manejo de la información sobre vacunación.

## :loudspeaker: Diferencias de api respecto a desktoapp

### Sistema

- Se adoptó una arquitectura API REST manteniendo la lógica existente.
- Se reemplazó JDBC por Jakarta Persistence para optimizar la persistencia.
- La tokenización mediante JWT sigue vigente, cambio de algorithm a RSA y mayor información de claims.
- Mayor documentación del código y API.
- Adición de test unitarios.
- Se definen patrones alternativos a la cédula de identificación personal de Panamá (en adelante CIP):
    - Recién nacidos no registrado: Registre con la cédula de la madre con las letras RN o múltiples con RN1 máximo 19.
    - No identificados: Registrar con identificador de la sede con las letras "NI-" al inicio.
    - Pasaporte: Tal cual escrito máximo 20 caracteres. Recomendamos reducir el uso de esta opción.

### Base de datos

- Se implementó una [convención de nombres](https://blog.sqlauthority.com/i/dl/SQLServerGuideLines.pdf), brindada
  por [Pinal Dave](https://blog.sqlauthority.com/) en su blog, modificada.
- Los tipos de datos fueron cambiados para mejorar rendimiento y concurrencia.
- Protección de los datos y reglas del negocio incrementada con más constraint y triggers.
- Se centralizó el manejo de usuarios, roles y permisos, delegando la autorización a los sistemas.
- Se añadieron índices para optimizar las búsquedas.
- Las funciones devuelven columnas sin alias; los alias se usan solo en las vistas, además se adicionan columnas id a las vistas.
- Más procedimientos almacenados para facilitar transacciones comunes. Se recomienda crear operaciones propias a los sistemas.
- Se define el campo "licencia" para los fabricantes puedan utilizarlo como CIP de su usuario, esto facilita la búsqueda
  del fabricante con su JWT.
- Más datos de pruebas.

## :high_brightness: Recomendaciones para implementar esta API en producción

- Utilizar un gestor de secretos para los datos en el .env
- Se está analizando traducir la base de datos para estandarizar principalmente los nombres de vacunas dando posibilidad
  a exportar datos.
- Separar el manejo de usuarios a otra base de datos incluyendo el registro de logs y tokens de autorización.
- Complemento a métricas y observabilidad
  implementando [Prometheus exporter](https://docs.spring.io/spring-boot/api/rest/actuator/prometheus.html) protegiendo
  el acceso a estas.
- Manejar con otros u más datos personales la recuperación de contraseña del usuario, similar a Panamá Digital.
- Migrar el OAuth Server de JWT
  hacia [Auth0](https://auth0.com/docs/quickstart/backend/java-spring-security5/interactive).
    - Utilizar un [Authentication Flow seguro](https://auth0.com/docs/get-started/authentication-and-authorization-flow#authorization-code-flow-with-enhanced-privacy-protection)
      de Auth0, garantizando la seguridad y confidencialidad de la data.
    - Con este feature abre la posibilidad de tener todos los JWT con las mismas credenciales entre varios sistemas y
      poder guardarlos en la BD de logs sugerida.
- Precisión en la Edad de los Pacientes: Utilizar eventos programados o jobs de SQL Server Agent para actualizar la edad
  de los pacientes diariamente si lo necesitan.
- Implementar un sistema de autocompletado para evitar duplicidades y mejorar la precisión en la base de datos.
- Crear [diccionarios de texto](https://learn.microsoft.com/es-es/sql/t-sql/statements/create-fulltext-index-transact-sql?view=sql-server-ver16)
    en el idioma de los datos para facilitar este feature y reducir el tiempo de búsquedas.
- Expediente Digital: Integrar reacciones a vacunas, efectos secundarios y contraindicaciones en el expediente digital
  del paciente.
- Esquemas Especiales de Vacunación: Implementar reglas de negocio para manejar esquemas especiales de vacunación para
  niños rezagados, mujeres embarazadas y otras poblaciones específicas.
- Autenticación en dos factores.
- Cifrar los datos sensibles en la base de datos y comunicación de los sistemas.
- Velar por cumplimiento con la ley de protección de datos personales.
- Se está planeando para la v2:
    - Filtrado de respuestas. Con este feature se completa la migración de la versión desktoapp.

### :busts_in_silhouette: Otros sistemas o API's que interactúen con la base de datos:

- Todos los sistemas creados que se conectan a esta API deben mantener el formato de CIP dada.
- Todas las contraseñas deben utilizar BCryptEncoder.
- Validar los datos con pattern y demás, antes de conectar a la base de datos.
- Implementar la autorización basada en los roles y permisos.
- Si utilizan Persistence deben asegurarse que se verifique la estructura de la base de datos, NO modificarla, truncarla
  o generar una nueva.
- Obtener su usuario de acceso a la base de datos con permisos mínimos requeridos.

## :wrench: Clonación y configuración del repositorio

> [!NOTE]
> Para disfrutar de un buen ambiente para **programar en local** necesita:
> [Gradle](https://gradle.org/) viene de forma portable como wrapper.
> [Docker](https://www.docker.com/) y [Git](https://git-scm.com/) son necesarios.
> Java JDK 21 (portable para gradle con [Foojay Toolchains Plugin](https://github.com/gradle/foojay-toolchains)).

Sigue estos pasos para clonar el repositorio:

Abre una terminal y ejecuta los siguientes comandos:

### 1. Clonar el repositorio

```bash
git clone https://github.com/Kingg22/api-vacunas-panama.git
```

### 2. Abrir la carpeta donde fue clonado el repositorio

### 3. Crear un entorno propio para deploy

> [!NOTE]
> Puede utilizar un archivo env o docker secrets, ambas deben utilizar los nombres dados en .env.example al menos que
> modifique application.properties
>
> Debe utilizar 2 archivos env el docker.env para colocar las ip internas de docker y .env para utilizarlo de forma
> externa.
> Si se decide por usar docker secrets puede eliminar la dependencia _spring-dotenv_ y eliminar el uso de env_file en
> docker-compose.yaml

1. Modificar vacunas-init.sql el login colocando un usuario y una contraseña segura al inicio del script.

   _Opcional:_ Crear contraseñas con [BCrypt](https://bcrypt-generator.com/) para los usuarios con roles superiores,
   colocarlo en el parámetro de
   'hash' del procedimiento sp_vacunas_gestionar_usuario, últimas líneas comentadas de vacunas-init.sql
2. Con las credenciales anteriores del login colocarlo como DB_USER y DB_PASSWORD estas credenciales serán usadas por la
   API no utilizar sa.
    - Recordatorio: Al estar en docker la base de datos el puerto externo que coloquemos, inicialmente 1440. No hemos
      podido utilizar el nombre del contendor para conectarse, recomendamos el uso de IP del host.
     - Colocar characterEncoding=UTF-8;useUnicode=true al final de la URL para mostrar los carácteres del español.
3. Crear el par de llaves del certificado RSA. Abra una terminal de git en el proyecto y ejecute:
    ``` bash
    openssl genrsa -out private.pem 4096
    openssl rsa -in private.pem -pubout -out public.pem
    ```
4. Colocar en JWT_PRIVATE y JWT_PUBLIC el contenido de cada archivo correspondiente.

   Utilizando .env:
     1. Se tiene un [problema](https://github.com/cdimascio/dotenv-java/issues/77) con los valores multi líneas y la
       dependencia real que tiene el proyecto (spring dotenv) también la sufre.
    2. Para solucionar el problema anterior debe colocar el contenido generado en las llaves de forma que cada fin de
       línea eliminar ese espacio, debe quedarle un string largo.

   Utilizando Docker secrets: Puede colocar sus archivos al final del docker-compose, estos deben estar en la misma
   carpeta
    ``` dockerfile
    secrets:
      JWT_PUBLIC_KEY:
        file: public.pem
      JWT_PRIVATE_KEY:
        file: private.pem
    ```
   Utilizando classpath (no recomendado): Puede colocar sus archivos en src/resources/certs y application.properties
   deberá
   comentar las líneas de docker y des comentar las líneas siguientes, más detalles en el archivo.

5. Por último para JWT_EXPIRATION_TIME, JWT_REFRESH_TIME, JWT_ISSUER puede personalizarlo a su gusto.
6. Configurar su contraseña sa debe crear un archivo llamado db.env donde **solo coloque la contraseña en texto plano**.
   Esta es necesaria para los scripts configure-db.sh y el test-check.sh del contenedor de la base de datos,
   sin esta no se puede construir la imagen ni verificar si el estado es saludable.
7. Crear la docker images de la API con su IDE o terminal con gradlew.

   ```bash
   ./gradlew clean bootBuildImage -DskipTests=true
   ```
   Este proceso puede demorar un poco la primera vez, se recomienda el flag skip tests para disminuir el tiempo de
   espera.
8. Crear la imagen de la base de datos
   ```` bash
    docker compose --progress=plain build bd-vacunas
    ````
9. Verificar que la base de datos fue creada con éxito en el build de esa imagen:
   Mientras se está creando la imagen podrá ver los mensajes anteriores o
   utilizando [docker desktop](https://www.docker.com/products/docker-desktop/) sección Builds, estará
   api-vacunas-panama con un tag: VACUNAS-APP-BD-VACUNAS
   Dentro del registro de este build verá en la sección de log alguna de estas líneas:
    ``` sql
    (1 row affected)
    -----------------------------------FIN------------------------------------------------
    ...
    Fin de la inicialización vacunas Panamá
    Changed database context to 'master'.
    ```
   O puede ser
    ``` sql
    Starting up database 'vacunas'.
    Parallel redo is started for database 'vacunas' with worker pool size [4].
    Parallel redo is shutdown for database 'vacunas' with worker pool size [4].
    ```

10. Crear las contraseñas para RabbitMQ

    Debe hacer una copia de rabbitmq_definitions_example.json a un archivo rabbitmq_definitions.json
    En las claves "password_hash" utilizando el contenedor rabbitmq:
    ```bash
    rabbitmqctl hash_password <contraseña>
    ```
    las credenciales de producer debe colocarla en RBMQ_USER, RBMQ_PASSWORD (como texto plano, no hash) respectivamente.
    Cambiar la contraseña de guest aunque no tiene permisos definidos en rabbitmq.conf

    _Opcional:_ En el caso del fronted que se conecta a este servicio de mensajería debe usar las credenciales de
    consumer.
    Si ya tiene pensado los nombres para las queues, exchange y routing puede crear en el json y darle permisos
    exclusivos a esa queue, sino spring creará los objetos mencionados con el nombre dado en RabbitMQConfig, se
    recomienda restringir el consumer a leer esa queue.

> [!TIP]
> En resumen debe crear el login de la base de datos (1), crear el certificado RSA para JWT (3), contraseña para sa de
> la base de datos (6), generar la imagen de la base de datos (7, 8, 9), contraseña de RabbitMQ (10).
> El resto de variables tienen valores por defectos para utilizar la API fuera de docker.

### 4. Levantar todos los contenedores:

``` bash
docker compose up -d
```

### 5. Verificación

Con un cliente como [Postman](https://www.postman.com/) puede probar los endpoint con localhost:8080

Observación: Si cambia el puerto que se expone en docker-compose.yaml debe apuntar a este nuevo puerto.

### Verificar modificaciones sin desplegar contenedor de API

1. Levantar la base de datos si no está activa o construir una nueva imagen por cambios en vacunas-init.sql:
    ``` bash
    docker compose up --build -d bd-vacunas
    ```
2. Utilizando el IDE integrado con gradlew o el siguiente comando:
    ```bash
    ./gradlew clean bootRun
    ```
3. Utilizar el cliente Postman o su propio API client al endpoint configurado.

> [!IMPORTANT]
> ¿Qué se espera de su configuración? Utilice docker secrets con los nombres dados, elimine la dependencia de
> spring-dotenv al momento de generar la imagen con spring elimine el .env Para pruebas de la API fuera de docker
> entonces utilice .env Eliminar los valores por defecto de application.properties y rabbitmq_definitions.json, ya que,
> tendrá valores personalizados.

Cualquier error verificar la versión del Java JDK 21, tu archivo .env o docker secrets configurados para cada servicio,
certificado RSA mínima 2056, la base de datos vacunas esté creada con éxito con todos sus objetos y con las credenciales
correctas, el login tiene su usuario con los permisos mínimos requeridos, la contraseña de sa es válida según los
requisitos mínimos de SQL Server y no contenga caracteres especiales (puede causar conflictos con los scripts de
configuración), ningún contenedor está en bucle de reinicio por errores, colocar spring con el flag -Ddebug y des
comentar el bloque de líneas relacionadas a logs en application.properties para encontrar cuál es el error si es el
contenedor de la API, eliminar el health_check en el service bd-vacunas y su dependencia en la API.

> [!NOTE]
> En docker compose utilizamos images latest en todos los servicios, Redis y RabbitMQ pueden cambiarse a su versión sin
> dashboard de manejo para reducir su tamaño y en entornos productivos.
