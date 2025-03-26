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
