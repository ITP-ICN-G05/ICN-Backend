# MongoDB Setup Guide

This guide will help you set up and run MongoDB for the ICN Backend project.

## Prerequisites

- Docker and Docker Compose installed
- Git (to clone the repository)

## Quick Start

### 1. Start MongoDB Services

Navigate to the MongoDB infrastructure directory:

```bash
cd infra/mongo
```

Start the MongoDB services using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- **MongoDB Server** on port `27017`
- **Mongo Express** (Web UI) on port `8081`

### 2. Verify Services

Check that both containers are running:

```bash
docker-compose ps
```

You should see both `icn-mongo` and `icn-mongo-express` containers in "Up" status.

### 3. Access MongoDB

#### Option A: Command Line (mongosh)

Connect to MongoDB using the application user:

```bash
docker exec -it icn-mongo mongosh --username icn_app --password icn_app_pass --authenticationDatabase icn_dev icn_dev
```

#### Option B: Web Interface

Open your browser and go to: http://localhost:8081

Login credentials for Mongo Express:
- **Server**: `mongo`
- **Username**: `admin`
- **Password**: `admin123`

## Configuration

### Environment Variables

The MongoDB setup uses the following environment variables (defined in `.env` file):

```env
# Root Admin Credentials
MONGO_INITDB_ROOT_USERNAME=admin
MONGO_INITDB_ROOT_PASSWORD=admin123
MONGODB_PORT=27017

# Application Database Configuration
MONGODB_DB=icn_dev
MONGODB_APP_USERNAME=icn_app
MONGODB_APP_PASSWORD=icn_app_pass
```

### Database Schema

The MongoDB instance is automatically configured with:

**Database**: `icn_dev`

**Collection**: `companies`

**Schema Validation**: Companies must have required fields:
- `name` (string)
- `location` (object with address and geo coordinates)
- `sectors` (array of strings)
- `capabilities` (array of strings)

**Indexes**:
- Geographic index on `location.geo` (2dsphere)
- Individual indexes on `sectors` and `capabilities`
- Compound indexes for ownership and size filtering
- Text search index on `name` and `productsServices`

## Sample Data

### Insert Test Data

```javascript
db.companies.insertOne({
  name: "Test Company",
  location: {
    address: "123 Test St, Melbourne VIC",
    geo: {
      type: "Point",
      coordinates: [144.9631, -37.8136]
    }
  },
  sectors: ["Technology"],
  capabilities: ["Software Development"],
  ownership: {
    femaleOwned: false,
    firstNationsOwned: false,
    verified: true
  },
  size: {
    category: "SME",
    employees: 25
  },
  productsServices: ["Web Applications"],
  createdAt: new Date(),
  updatedAt: new Date()
})
```

### Query Examples

```javascript
// Find all companies
db.companies.find()

// Find companies in Technology sector
db.companies.find({"sectors": "Technology"})

// Find companies near a location (within 10km)
db.companies.find({
  "location.geo": {
    $near: {
      $geometry: {
        type: "Point",
        coordinates: [144.9631, -37.8136]
      },
      $maxDistance: 10000
    }
  }
})

// Find female-owned companies
db.companies.find({"ownership.femaleOwned": true})

// Text search
db.companies.find({$text: {$search: "steel manufacturing"}})
```

## Troubleshooting

### Common Issues

**1. Port Already in Use**
```
Error: Port 27017 is already in use
```
Solution: Stop any existing MongoDB services or change the port in docker-compose.yml

**2. Permission Denied**
```
Error: Permission denied
```
Solution: Make sure Docker is running and you have sufficient permissions

**3. Connection Refused**
```
Error: Connection refused
```
Solution: 
- Check if containers are running: `docker-compose ps`
- Check container logs: `docker-compose logs mongo`

### Reset Database

To completely reset the database:

```bash
# Stop services
docker-compose down

# Remove data volume
docker volume rm mongo_mongo_data

# Restart services (will reinitialize)
docker-compose up -d
```

### Container Logs

View MongoDB logs:
```bash
docker-compose logs mongo
```

View Mongo Express logs:
```bash
docker-compose logs mongo-express
```

## Development Tips

1. **Data Persistence**: Data is stored in Docker volume `mongo_mongo_data`
2. **Backup**: Use `mongodump` to backup data
3. **Monitoring**: Use Mongo Express web interface for easy data viewing
4. **Performance**: Geographic queries are optimized with 2dsphere index

## API Integration

For Spring Boot integration, use these connection details:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://icn_app:icn_app_pass@localhost:27017/icn_dev?authSource=icn_dev
```

## Security Notes

- Default credentials are for development only
- Change passwords before production deployment
- Consider using MongoDB Atlas for production
- Enable SSL/TLS for production environments

## Useful Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View running containers
docker-compose ps

# Connect to MongoDB shell
docker exec -it icn-mongo mongosh

# View logs
docker-compose logs -f mongo

# Backup database
docker exec icn-mongo mongodump --db icn_dev --archive > backup.archive

# Restore database
docker exec -i icn-mongo mongorestore --archive < backup.archive
```
