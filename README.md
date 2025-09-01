# ICN Backend

A Spring Boot backend server for the ICN Navigator application, designed to run in a Docker environment.

## Overview

ICN Backend is a Java-based REST API server built with Spring Boot 3.0.0. It provides backend services for the ICN Navigator application, featuring:

- **Spring Boot Web**: RESTful API endpoints
- **Spring Boot JDBC**: Database connectivity and management
- **Pebble Templates**: Server-side templating engine
- **MongoDB Support**: NoSQL database integration
- **Static Resource Serving**: File serving capabilities

## Prerequisites

- **Java 18** or higher
- **Maven 3.6+** for dependency management
- **Docker** (for containerized deployment)
- **MongoDB** (for production database)

## Technology Stack

- **Framework**: Spring Boot 3.0.0
- **Java Version**: 18
- **Build Tool**: Maven
- **Database**: 
  - Development: HSQLDB (in-memory/file-based)
  - Production: MongoDB
- **Template Engine**: Pebble 3.2.0
- **Container**: Docker-ready

## Project Structure

```
ICN-Backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/gof/ICNBack/
│       │       └── Application.java
│       └── resources/
│           ├── application.yml
│           └── logback-spring.xml
├── pom.xml
├── README.md
└── .gitignore
```

## Getting Started

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ICN-Backend
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`

### Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t icn-backend .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 icn-backend
   ```

## Configuration

The application uses `application.yml` for configuration. Key settings include:

- **Application Name**: Configurable via `APP_NAME` environment variable
- **Database**: HSQLDB for development, MongoDB for production
- **Connection Pool**: HikariCP with optimized settings
- **Static Resources**: Served from `/static/**` path

### Environment Variables

- `APP_NAME`: Application name (default: ICN_Backend)
- Database connection parameters (configure as needed)

## API Endpoints

The application provides REST API endpoints for the ICN Navigator frontend. Static resources are served from the `/static/` path.

## Database

### Development
- Uses HSQLDB (in-memory/file-based)
- Database file: `testdb`
- Username: `sa`
- No password required

### Production
- MongoDB integration ready
- Configure connection parameters in `application.yml`

## Dependencies

### Core Dependencies
- `spring-boot-starter-web`: Web application support
- `spring-boot-starter-jdbc`: Database connectivity
- `pebble-spring-boot-starter`: Template engine
- `mongo-java-driver`: MongoDB driver

### Build Dependencies
- Spring Boot Parent POM
- Maven Compiler Plugin (Java 18)

## Development

### Adding New Features
1. Create new controller classes in the appropriate package
2. Add service layer for business logic
3. Configure database models and repositories
4. Update configuration as needed

### Logging
The application uses Logback for logging configuration, defined in `logback-spring.xml`.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

[Add your license information here]

## Support

For issues and questions, please contact the development team or create an issue in the repository.
