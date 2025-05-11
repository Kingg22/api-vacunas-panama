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
- Complemento a métricas y observabilidad implementando Prometheus protegiendo el acceso a estas.
- Manejar con otros u más datos personales la recuperación de contraseña del usuario, similar a Panamá Digital.
- Migrar el OAuth Server de JWT
  hacia [Auth0](https://auth0.com).
    - Utilizar un [Authentication Flow seguro](https://auth0.com/docs/get-started/authentication-and-authorization-flow#authorization-code-flow-with-enhanced-privacy-protection)
      de Auth0, garantizando la seguridad y confidencialidad de los datos.
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


## :warning: Limitaciones actuales de la API

- Los roles es una entidad que se puede modificar posterior a la creación del enum utilizado por la API al verificar los
  roles y su jerarquía. Se debe modificar a medida que los roles cambien.
- Se valida el formato del email mas no si su dominio existe.
- La v1 de la API está limitada en:
    - ~~No hay endpoint u operaciones asíncronas.~~ Desde v0.11.0 todo el sistema es reactivo asíncrono.
    - No hay filtros de búsquedas.
    - No paginación de resultados.
- De las reglas de vacunación en el proyecto solo implementamos: la edad mínima, intervalo entre primera y segunda
  dosis.
- Aún no se implementan las reglas de negocio para manejar esquemas especiales de vacunación para niños rezagados,
  mujeres embarazadas y otras poblaciones específicas.
- El listado de vacunas (por lo tanto, enfermedades, síntomas y 1 fabricante por vacuna) se mantiene
  el [esquema de vacunación de Panamá 2023 abril](https://www.spp.com.pa/publicaciones/documentos-interes/vacunacion/ESQUEMA-DE-VACUNACION_2023_3Abril.pdf).
- Se debe procurar el uso correcto de las zonas horarias en campos de fechas. Recomendamos UTC.
