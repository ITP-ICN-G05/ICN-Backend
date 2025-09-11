# MongoDB Quick Start

## 🚀 Get Started in 3 Steps

### Step 1: Start MongoDB
```bash
cd infra/mongo
docker-compose up -d
```

### Step 2: Verify Setup
```bash
docker-compose ps
```

Expected output:
```
NAME                IMAGE                 COMMAND         STATUS          PORTS
icn-mongo           mongo:7              "docker-ent..."  Up 2 minutes    0.0.0.0:27017->27017/tcp
icn-mongo-express   mongo-express:1.0.2  "/sbin/tini ..." Up 2 minutes    0.0.0.0:8081->8081/tcp
```

### Step 3: Connect & Test
Open browser: http://localhost:8081

**Login:**
- Username: `admin`
- Password: `admin123`

## 📊 Test Data

Connect to MongoDB and insert sample data:

```bash
docker exec -it icn-mongo mongosh --username icn_app --password icn_app_pass --authenticationDatabase icn_dev icn_dev
```

```javascript
// Insert test company
db.companies.insertOne({
  name: "Tech Innovators",
  location: {
    address: "123 Collins St, Melbourne VIC",
    geo: { type: "Point", coordinates: [144.9631, -37.8136] }
  },
  sectors: ["Technology"],
  capabilities: ["Software Development", "AI"],
  ownership: { femaleOwned: true, verified: true },
  size: { category: "SME", employees: 50 },
  productsServices: ["Mobile Apps", "Web Development"],
  createdAt: new Date(),
  updatedAt: new Date()
})

// Query all companies
db.companies.find().pretty()
```

## 🔧 Common Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs mongo

# Reset database
docker-compose down
docker volume rm mongo_mongo_data
docker-compose up -d

# Connect to MongoDB shell
docker exec -it icn-mongo mongosh --username icn_app --password icn_app_pass --authenticationDatabase icn_dev icn_dev
```

## 📋 Database Info

- **Database:** `icn_dev`
- **Collection:** `companies`
- **App User:** `icn_app` / `icn_app_pass`
- **Admin User:** `admin` / `admin123`
- **MongoDB Port:** `27017`
- **Web UI Port:** `8081`

## ❗ Troubleshooting

**Services not starting?**
```bash
docker-compose logs
```

**Port conflicts?**
Check if ports 27017 or 8081 are already in use.

**Connection issues?**
Ensure Docker is running and containers are up.

---

For detailed documentation, see [README.md](./README.md)
