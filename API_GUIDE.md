# ICN Backend API Guide for Frontend Developers

This guide provides comprehensive documentation for all available API endpoints in the ICN Backend system. The backend is built with Spring Boot 3 and provides RESTful JSON APIs for frontend applications.

## 📋 Table of Contents
- [Base Configuration](#base-configuration)
- [User Management APIs](#user-management-apis)
- [Organisation/Company APIs](#organisationcompany-apis)
- [Data Models](#data-models)
- [Response Formats](#response-formats)
- [Error Handling](#error-handling)
- [Frontend Integration Examples](#frontend-integration-examples)

## 🔧 Base Configuration

### Server Details
- **Base URL**: `http://localhost:8082/api`
- **Port**: 8082
- **Context Path**: `/api`
- **Response Format**: JSON
- **Database**: MongoDB (primary), HSQLDB (embedded)

### Headers
All requests should include:
```http
Content-Type: application/json
Accept: application/json
```

## 👤 User Management APIs

### 1. User Login
**Endpoint**: `GET /api/user`

**Description**: Authenticate user with email and password

**Parameters**:
- `email` (required): User's email address
- `password` (required): User's password

**Example Request**:
```bash
GET /api/user?email=user@example.com&password=mypassword
```

**Success Response** (202 Accepted):
```json
{
  "name": "John Doe",
  "VIP": 1,
  "cards": [
    {
      // Organisation card data
    }
  ]
}
```

**Error Response** (409 Conflict):
```http
HTTP/1.1 409 Conflict
invalid user account
```

### 2. Update User Information
**Endpoint**: `PUT /api/user`

**Description**: Update user profile information

**Request Body**:
```json
{
  "id": "user123",
  "VIP": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "password": "newpassword",
  "cards": ["org1", "org2"]
}
```

**Success Response** (201 Created):
```http
HTTP/1.1 201 Created
```

**Error Response** (409 Conflict):
```http
HTTP/1.1 409 Conflict
item update failed
```

### 3. Send Email Validation Code
**Endpoint**: `GET /api/user/getCode`

**Description**: Generate and send validation code to user's email

**Parameters**:
- `email` (required): User's email address

**Example Request**:
```bash
GET /api/user/getCode?email=user@example.com
```

**Success Response** (202 Accepted):
```http
HTTP/1.1 202 Accepted
```

**Error Response** (500 Internal Server Error):
```http
HTTP/1.1 500 Internal Server Error
something wrong with the server
```

### 4. Create User Account
**Endpoint**: `POST /api/user/create`

**Description**: Register a new user account with email validation

**Request Body**:
```json
{
  "email": "newuser@example.com",
  "name": "Jane Smith",
  "password": "securepassword",
  "code": "123456"
}
```

**Success Response** (201 Created):
```http
HTTP/1.1 201 Created
```

**Error Response** (409 Conflict):
```http
HTTP/1.1 409 Conflict
invalid validation code
```

### 5. User Payment (Not Implemented)
**Endpoint**: `POST /api/user/payment`

**Description**: Handle user payment (currently not implemented)

**Request Body**:
```json
{
  // UserPayment object (empty in current implementation)
}
```

**Response** (503 Service Unavailable):
```http
HTTP/1.1 503 Service Unavailable
```

## 🏢 Organisation/Company APIs

### 1. Search Organisations
**Endpoint**: `GET /api/organisation/general`

**Description**: Search for organisations with filters and pagination

**Parameters**:
- `location` (required): Location filter
- `filterParameters` (required): Map of filter criteria
- `searchString` (optional): Search text
- `skip` (optional): Number of records to skip for pagination
- `limit` (optional): Maximum number of records to return

**Example Request**:
```bash
GET /api/organisation/general?location=Melbourne&filterParameters={"sector":"technology"}&searchString=software&skip=0&limit=10
```

**Success Response** (200 OK):
```json
[
  {
    // OrganisationCard objects
  }
]
```

### 2. Get Organisations by IDs
**Endpoint**: `GET /api/organisation/generalByIds`

**Description**: Retrieve specific organisations by their IDs

**Parameters**:
- `ids` (required): Array of organisation IDs

**Example Request**:
```bash
GET /api/organisation/generalByIds?ids=org123&ids=org456
```

**Success Response** (200 OK):
```json
[
  {
    // OrganisationCard objects
  }
]
```

### 3. Get Organisation Details
**Endpoint**: `GET /api/organisation/specific`

**Description**: Get detailed information about a specific organisation

**Parameters**:
- `organisationId` (required): Organisation ID
- `user` (required): User identifier

**Example Request**:
```bash
GET /api/organisation/specific?organisationId=org123&user=user456
```

**Success Response** (202 Accepted):
```json
{
  "_id": "org123",
  "detailedItemID": "DET001",
  "itemName": "Company Name",
  "itemID": "ITM001",
  "detailedItemName": "Detailed Company Name",
  "sectorMappingID": "SEC001",
  "sectorName": "Technology",
  "Subtotal": 100
}
```

**Error Response** (409 Conflict):
```http
HTTP/1.1 409 Conflict
unable to get company details
```

## 📊 Data Models

### User Object
```json
{
  "id": "string",
  "VIP": "integer",
  "email": "string",
  "name": "string",
  "password": "string",
  "cards": ["string"]
}
```

### UserFull Object (Login Response)
```json
{
  "name": "string",
  "VIP": "integer",
  "cards": [
    {
      // OrganisationCard objects
    }
  ]
}
```

### InitialUser Object (Registration)
```json
{
  "email": "string",
  "name": "string",
  "password": "string",
  "code": "string"
}
```

### Organisation Object
```json
{
  "_id": "string",
  "detailedItemID": "string",
  "itemName": "string",
  "itemID": "string",
  "detailedItemName": "string",
  "sectorMappingID": "string",
  "sectorName": "string",
  "Subtotal": "integer"
}
```

### OrganisationCard Object
```json
{
  // Simplified organisation data (implementation details vary)
}
```

## 📝 Response Formats

### Standard HTTP Status Codes
- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `202 Accepted`: Request accepted and processed
- `409 Conflict`: Request conflicts with current state
- `500 Internal Server Error`: Server error occurred
- `503 Service Unavailable`: Service temporarily unavailable

### Error Response Format
Errors are typically returned with appropriate HTTP status codes and descriptive headers:
```http
HTTP/1.1 409 Conflict
error-description-header: detailed error message
```

## ⚠️ Error Handling

### Common Error Scenarios
1. **Invalid Credentials**: 409 Conflict with "invalid user account" header
2. **Update Failures**: 409 Conflict with "item update failed" header
3. **Validation Code Issues**: 409 Conflict with "invalid validation code" header
4. **Server Errors**: 500 Internal Server Error with "something wrong with the server" header
5. **Service Unavailable**: 503 Service Unavailable for unimplemented features

### Frontend Error Handling Best Practices
```javascript
// Example error handling in JavaScript
try {
  const response = await fetch('/api/user', {
    method: 'GET',
    params: { email: 'user@example.com', password: 'password' }
  });
  
  if (response.status === 409) {
    // Handle authentication error
    console.error('Invalid credentials');
  } else if (response.status === 500) {
    // Handle server error
    console.error('Server error occurred');
  } else if (response.ok) {
    const userData = await response.json();
    // Process successful response
  }
} catch (error) {
  console.error('Network error:', error);
}
```

## 🔌 Frontend Integration Examples

### React/JavaScript Example
```javascript
const API_BASE_URL = 'http://localhost:8082/api';

// User login
async function loginUser(email, password) {
  const response = await fetch(`${API_BASE_URL}/user?email=${email}&password=${password}`);
  if (response.ok) {
    return await response.json();
  }
  throw new Error('Login failed');
}

// Search organisations
async function searchOrganisations(location, filters, searchText) {
  const params = new URLSearchParams({
    location: location,
    searchString: searchText || ''
  });
  
  // Add filter parameters
  Object.entries(filters).forEach(([key, value]) => {
    params.append(`filterParameters[${key}]`, value);
  });
  
  const response = await fetch(`${API_BASE_URL}/organisation/general?${params}`);
  return await response.json();
}

// Create new user
async function createUser(userData) {
  const response = await fetch(`${API_BASE_URL}/user/create`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData)
  });
  
  return response.ok;
}
```

### React Native Example
```javascript
// React Native with Expo
import Constants from 'expo-constants';

const API_BASE_URL = `http://localhost:8082/api`;

// User authentication service
class AuthService {
  static async login(email, password) {
    try {
      const response = await fetch(`${API_BASE_URL}/user`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        // Note: In production, use POST with body instead of GET with params for credentials
      });
      
      if (response.status === 202) {
        return await response.json();
      } else {
        throw new Error('Authentication failed');
      }
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }
}
```

## 🔒 Security Considerations

### Current Limitations
- **No Authentication**: APIs are currently publicly accessible
- **No CORS Configuration**: Cross-origin requests may fail
- **Password Security**: Passwords are sent in plain text (should use HTTPS in production)
- **No Rate Limiting**: APIs are not protected against abuse

### Recommended Frontend Practices
1. Always use HTTPS in production
2. Store sensitive data (like user tokens) securely
3. Implement client-side validation before API calls
4. Handle network errors gracefully
5. Implement proper loading states

## 🚀 Development Setup

### Local Development
1. Ensure the backend is running on `http://localhost:8082`
2. Start your frontend development server
3. Configure your API client to use the base URL
4. Test API connectivity with a simple health check

### Environment Configuration
Create environment-specific configuration:

```javascript
// config.js
const config = {
  development: {
    API_BASE_URL: 'http://localhost:8082/api'
  },
  production: {
    API_BASE_URL: 'https://your-production-api.com/api'
  }
};

export default config[process.env.NODE_ENV || 'development'];
```

## 📈 Future Enhancements

### Planned Features
- JWT authentication
- CORS configuration
- API versioning
- Swagger/OpenAPI documentation
- Rate limiting
- Enhanced error responses
- WebSocket support for real-time features

### Migration Notes
When these features are implemented, you may need to:
1. Add authentication headers to requests
2. Update error handling for new response formats
3. Implement token refresh logic
4. Update API endpoints if versioning is introduced

---

## 📞 Support

For API-related questions or issues:
1. Check the backend logs at `app.log`
2. Verify MongoDB connection status
3. Ensure all required parameters are provided
4. Review this documentation for correct usage patterns

**Note**: This API is currently in MVP (Minimum Viable Product) state. Some features may be incomplete or require additional implementation.