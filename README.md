# Sistema de Votaciones (Poll Elections)

API RESTful para gestionar un sistema de votaciones. Permite registrar votantes y candidatos, emitir votos (un único voto por votante) y consultar estadísticas de los resultados.

## Tecnologías

- Java 21
- Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA)
- MySQL 8
- Maven
- Lombok

## Estructura del proyecto

```
src/main/java/com/Elections/poll_elections/
├── controller/    # Endpoints REST
├── service/       # Reglas de negocio y validaciones
├── repository/    # Acceso a datos (Spring Data JPA)
├── model/         # Entidades JPA: Voter, Candidate, Vote
├── dto/           # Objetos de entrada/salida (VoteRequest, VoteStatistics, CandidateStats)
└── exception/     # Excepciones personalizadas y manejador global de errores
```

## Requisitos previos

- [JDK 21](https://adoptium.net/) o superior
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [MySQL 8](https://dev.mysql.com/downloads/) (o XAMPP, que incluye MySQL)
- Un cliente HTTP: [Postman](https://www.postman.com/) o `curl`

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone [URL_DE_TU_REPOSITORIO]
cd poll-elections
```

### 2. Iniciar MySQL

Si usas XAMPP, abre el **Control Panel** y haz clic en **Start** en la fila de MySQL.

### 3. Crear la base de datos y el usuario

Entra a MySQL con el usuario `root`:

```bash
mysql -u root -p
```

Ejecuta los siguientes comandos:

```sql
CREATE DATABASE poll_elections CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> Las tablas se crean automáticamente al iniciar la aplicación, así que no necesitas ejecutar scripts SQL.

### 4. Configurar la conexión (opcional)

La configuración está en `src/main/resources/application.properties`:

```properties
server.port=8080
server.servlet.context-path=/api

spring.datasource.url=jdbc:mysql://localhost:3306/poll_elections?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

```
### 5. Ejecutar la aplicación

Desde IntelliJ IDEA, ejecuta la clase `PollElectionsApplication`.

O desde la terminal, en la carpeta del proyecto:

```bash
mvn spring-boot:run
```

Cuando veas `Tomcat started on port 8080 (http) with context path '/api'`, la API está lista en:

```
http://localhost:8080/api
```

## Modelo de datos

| Entidad | Campos | Notas |
|---|---|---|
| **Voter** | `id`, `name`, `email`, `hasVoted` | `email` es único. `hasVoted` inicia en `false`. |
| **Candidate** | `id`, `name`, `party`, `votes` | `party` es opcional. `votes` inicia en `0`. |
| **Vote** | `id`, `voter`, `candidate`, `createdAt` | Relaciona un votante con un candidato. |

## Endpoints

Todas las rutas empiezan con `/api`.

### Votantes

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/voters` | Registrar un votante | 201 |
| GET | `/voters` | Listar votantes | 200 |
| GET | `/voters/{id}` | Obtener un votante | 200 / 404 |
| DELETE | `/voters/{id}` | Eliminar un votante | 204 / 404 / 409 |

### Candidatos

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/candidates` | Registrar un candidato | 201 |
| GET | `/candidates` | Listar candidatos | 200 |
| GET | `/candidates/{id}` | Obtener un candidato | 200 / 404 |
| DELETE | `/candidates/{id}` | Eliminar un candidato | 204 / 404 / 409 |

### Votos

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/votes` | Emitir un voto | 201 / 404 / 409 |
| GET | `/votes` | Listar todos los votos | 200 |
| GET | `/votes/statistics` | Estadísticas de la votación | 200 |

## Reglas de negocio y validaciones

- **Un votante no puede ser candidato y viceversa.** La comparación se hace por nombre, sin importar mayúsculas.
- **Cada votante solo puede votar una vez.** Al emitir un voto se marca `hasVoted = true`.
- **Los votos se cuentan automáticamente.** Al emitir un voto se incrementa el conteo del candidato.
- **No se puede eliminar un votante que ya votó** ni un candidato que tiene votos.
- **Los emails de votantes son únicos.**
- **Los IDs de votante y candidato deben existir** al emitir un voto.

### Códigos de error

| Código | Cuándo ocurre |
|---|---|
| 400 Bad Request | Faltan campos obligatorios (por ejemplo, nombre vacío) |
| 404 Not Found | El recurso solicitado no existe |
| 409 Conflict | Email repetido, persona ya registrada con el otro rol, votante que ya votó, o eliminación no permitida |

## Ejemplos de uso

### Con Postman

Los ejemplos completos están en la colección `Poll_Elections.postman_collection.json` incluida en el repositorio. Para usarla:

1. Abre Postman y haz clic en **Import**.
2. Selecciona el archivo `Poll_Elections.postman_collection.json`.
3. La variable `baseUrl` ya viene configurada como `http://localhost:8080/api`.

## Ejemplo de respuesta de estadísticas

`GET /api/votes/statistics`

```json
{
  "totalVotes": 1,
  "votersWhoVoted": 1,
  "candidates": [
    { "candidateId": 1, "name": "Carlos", "party": "Partido Verde", "votes": 1, "percentage": 100.0 },
    { "candidateId": 2, "name": "María", "party": "Partido Azul", "votes": 0, "percentage": 0.0 }
  ]
}
```

## Decisiones de diseño

- **Arquitectura por capas:** controllers → services → repositories. Las reglas de negocio viven en los services, no en los controllers.
- **DTOs para la salida de estadísticas:** la respuesta tiene exactamente la forma que pide la prueba, sin exponer campos internos.
- **Transacciones en la emisión de votos:** si algo falla a mitad del proceso, todos los cambios se revierten, así que nunca queda un voto sin marcar al votante o viceversa.
- **Manejo global de errores:** `@RestControllerAdvice` devuelve respuestas JSON consistentes con `timestamp`, `status` y `error`.

## Limitaciones conocidas

- **Comparación por nombre para la regla votante/candidato.** El modelo `Candidate` no tiene email ni documento de identidad, así que la regla usa el nombre. En un sistema real se usaría un identificador único, como el número de documento.
- **Credenciales en el archivo de configuración.** Para un entorno de producción, las credenciales deberían moverse a variables de entorno.
- **Autenticación no implementada.** Los endpoints están abiertos. La autenticación básica o con JWT está pendiente como mejora opcional.
- **Paginación y filtrado no implementados.** Se listan todos los registros.

## Autor

Luis Guillermo Piedrahita Gomez — www.linkedin.com/in/luis-guillermo-piedrahita-gomez
