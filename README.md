# My Job API

API REST para la gestión de usuarios construida con Spring Boot 4 y Java.

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

## Seguridad de Datos

- **Contraseñas**: Hasheadas con BCrypt. Anotadas con `@JsonProperty(access = WRITE_ONLY)` para garantizar que nunca se expongan en las respuestas JSON.
- **Validaciones**: Integración de Jakarta Validation (`@NotBlank`, `@Email`, `@Size`).
- **Manejo Centralizado de Errores**: Respuestas JSON estructuradas (`ErrorResponse`) con códigos HTTP adecuados (`400`, `404`, `409`, `500`).

## Ejecución de Pruebas

Para ejecutar la suite completa de pruebas unitarias:

```bash
./mvnw test -Dtest="UserServiceTest,UserControllerTest,UserRoleTest,GlobalExceptionHandlerTest"
```
