# ICN-Backend

The **backend server** for the ICN Navigator project.  
It is built with **Spring Boot 3**, packaged via **Maven**, and designed to run in a Docker-friendly environment.

---

## 🚀 Features
- RESTful API endpoints to serve the frontend (React web + React Native mobile).
- Configurable **HSQLDB** data source (via `application.yml`).
- Logging configured with **Logback** (`logback-spring.xml`).
- Integrated with **Pebble templates** for server-side views.
- MongoDB driver included (for future data persistence).

---

## 🛠️ Tech Stack
- Java 18
- Spring Boot 3.0.0
- Maven build tool
- HSQLDB (embedded database)
- Pebble template engine
- MongoDB Java Driver
- Logback logging framework

---

## 📂 Project Structure

```
ICN-Backend/
├── src/main/java/com/gof/ICNBack/Application.java  # Spring Boot entry point
├── src/main/resources/
│   ├── application.yml                              # App + datasource config
│   ├── logback-spring.xml                           # Logging configuration
├── pom.xml                                          # Maven dependencies & build
└── README.md
```

---

## ⚙️ Configuration

### `application.yml`
- Sets app name (`ICN_Backend` by default).  
- Configures HSQLDB file-based DB (`testdb`).  
- JDBC driver: `org.hsqldb.jdbc.JDBCDriver`.  
- Connection pool tuned with HikariCP (timeouts, pool size).

### `logback-spring.xml`
- Console logging with custom pattern.  
- Rolling file logging to `app.log` (max size 1MB).  
- Root log level: INFO.

---

## 🏃 Running the Application

### Prerequisites
- Java 18+
- Maven 3.8+

### Build & Run
```bash
# Clone the repository
git clone git@github.com:ITP-ICN-G05/ICN-Backend.git
cd ICN-Backend

# Build the project
mvn clean install

# Run the Spring Boot app
mvn spring-boot:run
```

The server starts on **http://localhost:8080** by default.

## 🔌 API Usage
A sample test endpoint can be added in a controller:

```java
@RestController
@RequestMapping("/api")
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot Backend!";
    }
}
```

Frontend apps (`ICN-Frontend` and `ICN-Mobile`) should point their API base URL to:
```
http://<backend-host>:8080/api
```

## 🐳 Docker (Optional)
You can package the app as a Docker image:

```bash
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=icn-backend
docker run -p 8080:8080 icn-backend
```

## 🤝 Contributing
1. Fork the repo & clone locally.
2. Create a feature branch: `git checkout -b feature/xyz`.
3. Commit changes: `git commit -m "feat: add xyz"`.
4. Push branch: `git push origin feature/xyz`.
5. Open a Pull Request.