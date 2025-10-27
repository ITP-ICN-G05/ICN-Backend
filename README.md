# ICN-Backend

The backend server for the **ICN Navigator** project.  
It is built with **Spring Boot 3**, packaged via **Maven**, and designed to run in a **Docker-friendly** environment.

---

## 🚀 Features
- RESTful API endpoints to serve the frontend (React web + React Native mobile)
- MongoDB data source (configured via `application.yml`)
- Logging configured with **Logback**
- Integrated with **Pebble templates** and **Freemarker** for email templates
- Email service integration with **Gmail SMTP**
- **Google Maps Geocoding API** integration

---

## 🛠️ Tech Stack
- **Java 18**
- **Spring Boot 3.0.0**
- **Maven** build tool
- **MongoDB** (primary database)
- **Pebble** template engine
- **Freemarker** template engine (for emails)
- **Spring Data MongoDB**
- **Spring Mail**
- **Spring Web**
- **JUnit 5 & Mockito** for testing

---

## 🗄️ Database Setup
This project uses **MongoDB Atlas** as the primary database.

### MongoDB Configuration
The application connects to MongoDB Atlas using the connection string in `application.yml` at `spring.data.mongodb.uri`

---

## 📂 Project Structure
```
ICN-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/gof/ICNBack/
│   │   │   ├── Application.java                    # Spring Boot entry point
│   │   │   ├── Web/                         # REST controllers
│   │   │   ├── Entity/                              # Entity models
│   │   │   ├── DataSource/                         # MongoDB repositories
│   │   │   └── Service/                            # Business logic
│   │   └── resources/
│   │       ├── application.yml                     # App configuration
│   │       ├── templates/                          # Email templates (.ftl)
│   │       └── logback-spring.xml                  # Logging configuration
│   └── test/
│   │   ├── java/
│   │   │   ├── IntergrationTest/
│   │   │   ├── UnitTest/                          
│   │   │   ├── Utils/                      
│   │   └── resources/
│   │       ├── application_test.yml                # App configuration test-env
│   │       └── templates/                          # Email templates (.ftl)
├── pom.xml                                         # Maven dependencies & build
└── README.md                                       # Project documentation
```

---

## ⚙️ Configuration

### `application.yml`
- Application name: `ICN_Back`
- Server port: `8082` with context path `/api`
- MongoDB Atlas connection
- Email configuration (Gmail SMTP)
- Google Maps Geocoding API settings
- Pebble template configuration

### Environment Variables Required
| Variable | Description                |
|-----------|----------------------------|
| `EMAIL` | account for sending emails |
| `EMAIL_PASS` | email app password         |
| `GOOGLE_MAPS_API_KEY` | Google Maps API key        |

---

## 🏃 Running the Application

### Prerequisites
- Java 18+
- Maven 3.8+
- MongoDB Atlas account or local MongoDB instance

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

The server starts on `http://localhost:8082` by default with API base path `/api`.

### Set Environment Variables
Before running, set the required environment variables:

```bash
export EMAIL=your-email@gmail.com
export EMAIL_PASS=your-app-password
export GOOGLE_MAPS_API_KEY=your-google-maps-api-key
```

---

## 🔌 API Usage
Frontend apps (**ICN-Frontend** and **ICN-Mobile**) should point their API base URL to:

```
http://localhost:8082/api
```

---

## 🐳 Docker (Optional)

You can package the app as a Docker image:

```bash
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=icn-backend
docker run -p 8082:8082   -e EMAIL=your-email   -e EMAIL_PASS=your-password   -e GOOGLE_MAPS_API_KEY=your-key   icn-backend
```

---

## 📧 Email Service
- Uses **Gmail SMTP** with SSL
- Sends emails using **Freemarker templates**
- Configurable email timeout and verification code length

---

## 🗺️ Google Maps Integration
- Geocoding service for address conversion
- Configurable batch size and request delays
- API key required via environment variable

---

## 🤝 Contributing
1. Fork the repo & clone locally
2. Create a feature branch:
   ```bash
   git checkout -b feature/xyz
   ```
3. Commit changes:
   ```bash
   git commit -m "feat: add xyz"
   ```
4. Push branch:
   ```bash
   git push origin feature/xyz
   ```
5. Open a **Pull Request**
