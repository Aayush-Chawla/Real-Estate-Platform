# Auth Service

Firebase-based Authentication Microservice for Real Estate Platform

## Features

- **User Registration & Login**: Uses Firebase Auth for credential management
- **MongoDB Integration**: Stores additional user details in MongoDB Atlas
- **Role-based Authorization**: Supports BUYER, SELLER, ADMIN roles
- **Profile Management**: Fetch and update user profiles
- **Eureka Service Registration**: Registers as auth-service with Eureka Server

## Current Implementation Status

✅ **Firebase Integration Complete**: This service now uses the **real Firebase Admin SDK** for authentication and token verification. All Firebase functionality is fully implemented and ready for production use.

## Setup Instructions

### 1. MongoDB Atlas Configuration

1. Create a MongoDB Atlas cluster
2. Create a database named `real-estate-auth`
3. Update the connection string in `application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/real-estate-auth?retryWrites=true&w=majority
   ```

### 2. Eureka Server

Ensure Eureka Server is running on `http://localhost:8761`

### 3. Firebase Configuration (Required)

1. **Create Firebase Project**:
   - Go to https://console.firebase.google.com/
   - Create a new project or select existing project
   - Enable Authentication in Firebase Console
   - Set up Authentication providers (Email/Password, Google, etc.)

2. **Generate Service Account Key**:
   - Go to Project Settings → Service Accounts
   - Click "Generate new private key"
   - Download the JSON file

3. **Configure Service Account**:
   - Rename the downloaded file to `firebase-service-account.json`
   - Place it in `src/main/resources/` directory
   - Update `application.properties` with your Firebase project ID:
     ```properties
     firebase.project-id=your-actual-firebase-project-id
     ```

4. **Security Rules** (Optional):
   - Configure Firebase Security Rules for your project
   - Set up proper authentication rules in Firebase Console

## API Endpoints

### POST /auth/register
Register a new user with Firebase authentication.

**Request Body:**
```json
{
  "firebaseUid": "firebase-id-token",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "BUYER",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": "user-id",
    "firebaseUid": "firebase-uid",
    "name": "John Doe",
    "email": "john@example.com",
    "role": "BUYER",
    "imageUrl": "https://example.com/image.jpg",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  "accessToken": "firebase-id-token"
}
```

### POST /auth/login
Login with Firebase ID token.

**Request Body:**
```json
{
  "idToken": "firebase-id-token"
}
```

### POST /auth/logout
Logout user (requires Authorization header).

**Headers:**
```
Authorization: Bearer firebase-id-token
```

### GET /auth/profile
Get user profile (requires Authorization header).

**Headers:**
```
Authorization: Bearer firebase-id-token
```

### PUT /auth/profile
Update user profile (requires Authorization header).

**Headers:**
```
Authorization: Bearer firebase-id-token
```

**Request Body:**
```json
{
  "name": "Updated Name",
  "imageUrl": "https://example.com/new-image.jpg"
}
```

## MongoDB Schema

The service uses the following schema for the `users` collection:

```json
{
  "id": "ObjectId",
  "firebaseUid": "String (unique)",
  "name": "String",
  "email": "String (unique)",
  "role": "BUYER | SELLER | ADMIN",
  "imageUrl": "String (optional)",
  "status": "ACTIVE | SUSPENDED | DEACTIVATED",
  "createdAt": "Date",
  "updatedAt": "Date"
}
```

## Running the Service

1. Ensure all dependencies are installed:
   ```bash
   mvn clean install
   ```

2. Run the service:
   ```bash
   mvn spring-boot:run
   ```

The service will start on port 8081 and register with Eureka Server.

## Testing the Service

### Test Registration
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firebaseUid": "test-token-123",
    "name": "Test User",
    "email": "test@example.com",
    "role": "BUYER",
    "imageUrl": "https://example.com/avatar.jpg"
  }'
```

### Test Login
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "idToken": "test-token-123"
  }'
```

### Test Profile (requires login first)
```bash
curl -X GET http://localhost:8081/auth/profile \
  -H "Authorization: Bearer test-token-123"
```

## Security

- Firebase handles password hashing and session management
- Spring Boot verifies Firebase ID tokens on protected routes
- No manual JWT generation - uses Firebase tokens directly
- Role-based access control supported through user roles

## Firebase Integration Features

### ✅ **Complete Firebase Admin SDK Integration**
- **Real Token Verification**: Uses Firebase Admin SDK to verify ID tokens
- **User Data Extraction**: Automatically extracts user info from Firebase tokens
- **Security Filter**: Spring Security filter validates Firebase tokens on protected routes
- **Error Handling**: Comprehensive Firebase exception handling
- **Logging**: Detailed logging for authentication events

### 🔧 **Firebase Service Capabilities**
- `verifyIdToken()` - Verifies Firebase ID tokens
- `getUidFromToken()` - Extracts Firebase UID
- `getEmailFromToken()` - Extracts user email
- `getNameFromToken()` - Extracts user name
- `getPictureFromToken()` - Extracts user profile picture
- `isValidToken()` - Checks token validity

### 🛡️ **Security Features**
- Firebase token validation on all protected endpoints
- Automatic user data synchronization from Firebase
- Role-based access control with Firebase authentication
- Secure token handling and validation

## Development Notes

- ✅ **Production Ready**: Full Firebase Admin SDK integration complete
- ✅ **All endpoints functional**: Ready for integration testing
- ✅ **Service discovery**: Registers with Eureka Server
- ✅ **MongoDB integration**: User metadata stored in MongoDB Atlas
- ✅ **Security**: Firebase token-based authentication implemented
