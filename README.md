# NeoLayer Identity Service

A Spring Boot microservice for user authentication and identity management.

## Features

- User registration and login
- JWT token-based authentication
- Role-based access control (RBAC)
- User profile management
- Password encryption using BCrypt
- Refresh token mechanism
- Comprehensive error handling

## Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+

## Database Setup

Create the database:

```sql
CREATE DATABASE neolayer_identity;
```

## Running the Application

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

The service will start on `http://localhost:8080/api`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/refresh` - Refresh access token

### Users

- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

## Request/Response Examples

### Register
```json
POST /api/auth/register
{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "username": "johndoe"
}
```

### Login
```json
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

### Response
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

## Configuration

Key properties in `application.yml`:

```yaml
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 hours
  refresh-expiration: 604800000  # 7 days
```

## Security

- All passwords are encrypted using BCrypt
- JWT tokens are signed with HMAC-SHA512
- Access control is enforced via Spring Security
- CSRF protection is disabled for stateless API

## Project Structure

```
src/main/java/com/neolayer/identity/
├── controller/      # REST endpoints
├── service/         # Business logic
├── entity/          # JPA entities
├── repository/      # Data access
├── security/        # Security configurations
├── dto/             # Data transfer objects
├── config/          # Spring configurations
└── exception/       # Exception handling
```

## License

This project is part of the NeoLayer suite.
