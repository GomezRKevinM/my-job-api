# My Job API

API REST para la gestión de usuarios y ofertas de empleo construida con Spring Boot 4 y Java.

## Endpoints de Usuario (`/api/v1/user`)

| Método | Endpoint | Descripción | Respuestas |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/user` | Registra un nuevo usuario | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `GET` | `/api/v1/user` | Obtiene todos los usuarios (filtro opcional: `?role=admin`) | `200 OK` |
| `GET` | `/api/v1/user/{id}` | Obtiene un usuario por su UUID | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/user/username/{username}` | Busca un usuario por nombre de usuario | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/user/email/{email}` | Busca un usuario por correo electrónico | `200 OK`, `404 Not Found` |
| `PUT` | `/api/v1/user/{id}` | Actualiza datos de un usuario existente | `200 OK`, `400 Bad Request`, `404 Not Found`, `409 Conflict` |
| `DELETE` | `/api/v1/user/{id}` | Elimina un usuario por su UUID | `204 No Content`, `404 Not Found` |

## Endpoints de Empleo (`/api/v1/empleo`)

| Método | Endpoint | Descripción | Respuestas |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/empleo` | Publica una nueva oferta de empleo | `201 Created`, `400 Bad Request`, `404 Not Found` |
| `GET` | `/api/v1/empleo` | Lista empleos con filtros opcionales (`empresa`, `areaTrabajo`, `nivel`, `categoria`, `userId`) | `200 OK` |
| `GET` | `/api/v1/empleo/{id}` | Obtiene el detalle de un empleo por ID | `200 OK`, `404 Not Found` |
| `PUT` | `/api/v1/empleo/{id}` | Actualiza los datos de una oferta de empleo | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/api/v1/empleo/{id}` | Elimina una oferta de empleo | `204 No Content`, `404 Not Found` |

## Endpoints de Categoría (`/api/v1/categoria`)

| Método | Endpoint | Descripción | Respuestas |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/categoria` | Registra una nueva categoría | `201 Created`, `409 Conflict` |
| `GET` | `/api/v1/categoria` | Lista todas las categorías | `200 OK` |
| `GET` | `/api/v1/categoria/{id}` | Obtiene una categoría por ID | `200 OK`, `404 Not Found` |
| `DELETE` | `/api/v1/categoria/{id}` | Elimina una categoría | `204 No Content`, `404 Not Found` |

## Seguridad y Arquitectura

- **Contraseñas**: Hasheadas con BCrypt y excluidas de las respuestas JSON con `@JsonProperty(access = WRITE_ONLY)`.
- **Relaciones de Dominio**:
  - `User`: Creador de la oferta (`@ManyToOne` hacia `users`).
  - `cargoJefe`: Relación autorreferencial jerárquica (`@ManyToOne` hacia `empleos`).
  - `categorias`: Tabla normalizada y tabla intermedia `empleos_categorias` (`@ManyToMany`), expuesta como colección en el modelo de dominio.
- **Validaciones**: Integración con Jakarta Validation (`@NotBlank`, `@Email`, `@PositiveOrZero`).
- **Manejo Centralizado de Errores**: Respuestas JSON estructuradas (`ErrorResponse`) con códigos HTTP adecuados (`400`, `404`, `409`, `500`).

## Ejecución de Pruebas

Para ejecutar la suite completa de pruebas unitarias:

```bash
./mvnw test -Dtest="UserServiceTest,UserControllerTest,UserRoleTest,GlobalExceptionHandlerTest,NivelEmpleoTest,CategoriaServiceTest,EmpleoServiceTest,EmpleoControllerTest"
```
