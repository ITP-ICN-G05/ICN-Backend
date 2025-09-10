const dbName = process.env.MONGODB_DB || 'icn_dev';
const dbref = db.getSiblingDB(dbName);
dbref.companies.insertMany([
  {
    name: "Acme Steel",
    location: { address: "123 Smith St, Melbourne VIC", geo: { type: "Point", coordinates: [144.9631, -37.8136] } },
    sectors: ["Steel"],
    capabilities: ["Manufacturer"],
    ownership: { femaleOwned: false, firstNationsOwned: false, verified: true },
    size: { category: "SME", employees: 50 },
    productsServices: ["Beams", "Plates"],
    createdAt: new Date(), updatedAt: new Date()
  }
]);
