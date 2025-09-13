// Runs automatically on first container start
const dbName = process.env.MONGODB_DB || 'icn_dev';
const appUser = process.env.MONGODB_APP_USERNAME || 'icn_app';
const appPass = process.env.MONGODB_APP_PASSWORD || 'icn_app_pass';

// Switch to the application database
const db = db.getSiblingDB(dbName);

// Create application user with read/write permissions
db.createUser({
  user: appUser,
  pwd: appPass,
  roles: [{ role: "readWrite", db: dbName }]
});

// JSON Schema validation for "companies"
db.createCollection("companies", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["name", "location", "sectors", "capabilities"],
      properties: {
        name: { bsonType: "string", description: "Company name" },
        location: {
          bsonType: "object",
          required: ["address", "geo"],
          properties: {
            address: { bsonType: "string" },
            geo: {
              bsonType: "object",
              required: ["type", "coordinates"],
              properties: {
                type: { enum: ["Point"] },
                coordinates: {
                  bsonType: "array",
                  items: [{ bsonType: "double" }, { bsonType: "double" }],
                  description: "[lon, lat]"
                }
              }
            }
          }
        },
        sectors: { bsonType: "array", items: { bsonType: "string" } },
        capabilities: { bsonType: "array", items: { bsonType: "string" } },
        ownership: {
          bsonType: "object",
          properties: {
            femaleOwned: { bsonType: "bool" },
            firstNationsOwned: { bsonType: "bool" },
            verified: { bsonType: "bool" }
          }
        },
        size: {
          bsonType: "object",
          properties: {
            category: { enum: ["SME", "Enterprise", null] },
            employees: { bsonType: ["int", "long", "null"] },
            revenue: { bsonType: ["double", "null"] }
          }
        },
        productsServices: { bsonType: "array", items: { bsonType: "string" } },
        createdAt: { bsonType: "date" },
        updatedAt: { bsonType: "date" }
      }
    }
  }
});

// Core indexes for optimal query performance
db.companies.createIndex({ "location.geo": "2dsphere" });
db.companies.createIndex({ "sectors": 1 });
db.companies.createIndex({ "capabilities": 1 });
db.companies.createIndex({ "ownership.femaleOwned": 1, "ownership.firstNationsOwned": 1, "ownership.verified": 1 });
db.companies.createIndex({ "size.category": 1 });
db.companies.createIndex({ "name": 1, "location.address": 1 }, { unique: false, collation: { locale: "en", strength: 2 } });

// Optional text search index for name and products/services
db.companies.createIndex({ name: "text", productsServices: "text" });
