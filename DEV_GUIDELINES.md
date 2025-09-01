# Backend Developer Guidelines (Spring Boot)

These guidelines outline the expectations for contributors working on the ICN backend (Spring Boot) repository. They draw on industry best-practices and the Medium article "Developer Guidelines", adapting the concepts for Java-based server projects. Following them helps ensure a maintainable codebase and smooth collaboration.

## Repository Structure

### Current Structure
Based on the README, the ICN-Backend currently has:
```
ICN-Backend/
├── src/main/java/com/gof/ICNBack/
│   └── Application.java             # Spring Boot entry point
├── src/main/resources/
│   ├── application.yml              # Configuration
│   └── logback-spring.xml          # Logging configuration
├── pom.xml                          # Maven dependencies
└── README.md                        # Documentation
```

### Recommended Structure Additions
To align with best practices, add the following to your repository:

```
├── .github/
│   └── workflows/                   # ADD: GitHub Actions CI/CD
│       ├── build.yml
│       ├── test.yml
│       └── artifacts.yml
├── .gitignore                       # UPDATE: Use gitignore.io
├── .pre-commit-config.yaml          # ADD: Pre-commit hooks
├── Makefile                         # ADD: Standardized commands
├── src/main/java/com/gof/ICNBack/
│   ├── controller/                  # ADD: REST controllers
│   │   └── UserController.java
│   ├── service/                     # ADD: Business logic layer
│   │   └── UserService.java
│   ├── repository/                  # ADD: Data access layer
│   │   └── UserRepository.java
│   ├── model/                       # ADD: Domain models
│   │   └── User.java
│   ├── config/                      # ADD: Configuration classes
│   │   └── SwaggerConfig.java
│   └── exception/                   # ADD: Exception handling
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application-dev.yml          # ADD: Development profile
│   ├── application-prod.yml         # ADD: Production profile
│   └── db/migration/                # ADD: Database migrations (Flyway)
│       └── V1__initial_schema.sql
├── src/test/java/                   # ADD: Test files
│   └── com/gof/ICNBack/
│       ├── controller/
│       ├── service/
│       └── integration/
├── docs/                            # ADD: Documentation
│   ├── api-reference.md
│   ├── database-schema.md
│   └── deployment.md
├── Dockerfile                       # ADD: Docker configuration
└── docker-compose.yml               # ADD: Local development setup
```

The `ADD` tags indicate new items to create, while `UPDATE` suggests improving existing files.

## Makefile

Provide a Makefile with standardised targets to automate common tasks. Every project should include targets for help, clean, setup-env, tests, open-tests-results, validate and build:

```makefile
.PHONY: help clean setup-env tests open-tests-results validate build run

help:                        ## Show available targets
	@grep -E '^[a-zA-Z_-]+:.*?##' $(MAKEFILE_LIST) | \
	awk 'BEGIN {FS = ":.*?##"; printf "Available targets:\n"} {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

clean:                       ## Remove build artefacts and temp files
	./mvnw clean
	rm -rf target .mvn/wrapper .idea *.iml logs/

setup-env:                   ## Set up a local development environment
	./mvnw install -DskipTests
	@echo "Environment ready. Run 'make run' to start the application"

tests:                       ## Run unit and integration tests
	./mvnw test

open-tests-results:          ## Open code coverage report in browser
	open target/site/jacoco/index.html

validate:                    ## Run pre-commit validations (code style, formatting)
	pre-commit run --all-files

build:                       ## Build the Spring Boot application
	./mvnw clean package -DskipTests

run:                         ## Run the Spring Boot application
	./mvnw spring-boot:run
```

## .gitignore

Use a `.gitignore` file tailored to Java, Spring Boot, and your operating system. Generate it using gitignore.io. Essential entries:

```gitignore
# Compiled class files
*.class
target/

# Log files
*.log
logs/

# Package Files
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# Maven
.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
.settings/
.classpath
.project

# OS
.DS_Store
Thumbs.db

# Application
application-local.yml
*.env
```

## Pre-commit Hooks

Adopt pre-commit hooks to catch issues early. The `pre-commit-config.yaml` should include:

```yaml
repos:
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.4.0
    hooks:
      - id: end-of-file-fixer
      - id: trailing-whitespace
      - id: check-yaml
      - id: check-added-large-files
      - id: check-merge-conflict
      - id: detect-private-key
      
  - repo: https://github.com/macisamuele/language-formatters-pre-commit-hooks
    rev: v2.8.0
    hooks:
      - id: pretty-format-java
        args: [--autofix]
        
  - repo: https://github.com/jumanjihouse/pre-commit-hook-yamlfmt
    rev: 0.2.2
    hooks:
      - id: yamlfmt
```

Run `pre-commit install` after cloning to ensure the hooks run on every commit.

## Documentation

Store documentation inside a dedicated `documentation/` folder. Use a static site generator such as MkDocs or Sphinx to publish the docs. At minimum, include:

- **Getting Started** – overview of the service, prerequisites (Java 18, Maven), how to build and run locally
- **API Reference** – describe each REST endpoint with method, path, request parameters, response format, and authentication requirements
- **Code Examples** – show sample requests (cURL) or client library usage
- **Configuration** – list environment variables and configuration properties
- **Testing & Debugging** – explain how to run tests, view logs and debug issues

## Continuous Integration (CI)

Implement continuous integration with GitHub Actions. CI should build, test and validate the project on every push. Use three workflows under `.github/workflows/`:

### build.yml
```yaml
name: Build and Validate
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 18
        uses: actions/setup-java@v3
        with:
          java-version: '18'
          distribution: 'temurin'
          cache: maven
      - name: Run pre-commit
        uses: pre-commit/action@v3.0.0
      - name: Build with Maven
        run: ./mvnw clean compile
```

### test.yml
```yaml
name: Test
on:
  push:
    branches: [main, develop]
  pull_request:

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 18
        uses: actions/setup-java@v3
        with:
          java-version: '18'
          distribution: 'temurin'
          cache: maven
      - name: Run tests
        run: ./mvnw test
      - name: Generate code coverage
        run: ./mvnw jacoco:report
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
```

### artifacts.yml
```yaml
name: Build Artifacts
on:
  push:
    branches: [main]

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 18
        uses: actions/setup-java@v3
        with:
          java-version: '18'
          distribution: 'temurin'
      - name: Build JAR
        run: ./mvnw clean package -DskipTests
      - name: Build Docker image
        run: |
          docker build -t icn-backend:${{ github.sha }} .
          # Push to registry if configured
```

## Development Strategy

Feature development should always occur on branches and be merged via Pull Requests (PRs). Follow these guidelines:

1. **Clear goal** – understand the feature or bugfix before writing code
2. **Feature branch** – create a branch named after the feature (e.g., `feature/add-user-endpoint`)
3. **Descriptive commits** – write meaningful commit messages explaining what changed
4. **Regularly rebase** – keep your branch up to date with main to minimise merge conflicts
5. **Write tests** – accompany your changes with unit/integration tests
6. **Code reviews** – open a PR and request at least one reviewer. Be open to feedback and iterate as needed
7. **Documentation** – update documentation when the feature alters behaviour or API contracts
8. **Release notes** – summarise significant changes for stakeholders

## Database Configuration

The project uses HSQLDB for development and can be configured for other databases in production:

### application.yml
```yaml
spring:
  application:
    name: ICN_Backend
  datasource:
    url: jdbc:hsqldb:file:testdb
    driver-class-name: org.hsqldb.jdbc.JDBCDriver
    username: SA
    password:
    hikari:
      connection-timeout: 20000
      maximum-pool-size: 5
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Database Migrations
Use Flyway or Liquibase to manage schema changes:
- **Automate** – integrate migrations into the build pipeline
- **Atomic changes** – each migration should perform one logical change
- **Naming conventions** – use consistent, descriptive file names (e.g., `V1__create_users_table.sql`)

## REST API Design

Expose a well-designed REST API and include Swagger/OpenAPI in the project:

### Swagger Configuration
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.gof.ICNBack"))
            .paths(PathSelectors.any())
            .build();
    }
}
```

### API Guidelines
- HTTP verbs aligned with CRUD operations (GET, POST, PUT, DELETE)
- Clear resource-oriented URLs (e.g., `/api/users/{id}`)
- Standardised response structures with meaningful HTTP status codes
- API versioning in the path (e.g., `/api/v1/...`)
- Consistent error responses

### Sample Controller
```java
@RestController
@RequestMapping("/api/v1")
public class UserController {
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        // Implementation
    }
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // Implementation
    }
}
```

## Deployment Strategy

### Build Artefacts
Every commit merged into `main` should produce deployable artefacts:
- JAR file for direct deployment
- Docker image for containerised deployment

### Dockerfile
```dockerfile
FROM openjdk:18-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Versioning
Adopt calendar versioning (CalVer):
- Format: `YYYY.MM.INC` (e.g., `2025.01.1`)
- Increment resets each month
- Tag releases using GitHub Actions

### Development Isolation
Support deployment of feature branches into isolated dev environments:
- Use Spring profiles for different environments
- Parameterise database connections
- Separate configuration for dev/staging/production

## Logging

Configure comprehensive logging using Logback:

### logback-spring.xml
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeBasedRollingPolicy">
            <maxFileSize>1MB</maxFileSize>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## Summary

These backend developer guidelines promote a well-structured, consistent and high-quality Spring Boot codebase. By organising the repository with standard files, automating builds and tests, enforcing code style and commit discipline, documenting thoroughly, managing migrations and APIs carefully, and adopting robust deployment practices, the team improves maintainability and reduces friction. Following these guidelines encourages collaboration, faster onboarding and smoother releases for the ICN Navigator backend.