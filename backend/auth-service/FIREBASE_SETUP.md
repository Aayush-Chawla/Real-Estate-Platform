# Firebase Integration Setup Guide

## Prerequisites
- Firebase project created
- Firebase Authentication enabled
- Service account key downloaded

## Step-by-Step Setup

### 1. Firebase Project Setup

1. **Create Firebase Project**:
   ```
   https://console.firebase.google.com/
   → Create a project
   → Enter project name (e.g., "real-estate-platform")
   → Enable Google Analytics (optional)
   ```

2. **Enable Authentication**:
   ```
   Firebase Console → Authentication → Get Started
   → Sign-in method → Enable Email/Password
   → Add other providers as needed (Google, Facebook, etc.)
   ```

3. **Generate Service Account Key**:
   ```
   Firebase Console → Project Settings → Service Accounts
   → Generate new private key
   → Download JSON file
   ```

### 2. Service Configuration

1. **Place Service Account File**:
   ```
   Rename downloaded file to: firebase-service-account.json
   Place in: auth-service/src/main/resources/
   ```

2. **Update Configuration**:
   ```properties
   # In application.properties
   firebase.project-id=your-actual-project-id
   firebase.credentials.path=classpath:firebase-service-account.json
   ```

### 3. Testing Firebase Integration

1. **Start the Service**:
   ```bash
   mvn spring-boot:run
   ```

2. **Test with Real Firebase Token**:
   ```bash
   # Get Firebase ID token from your frontend/client
   curl -X POST http://localhost:8081/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "idToken": "REAL_FIREBASE_ID_TOKEN_HERE"
     }'
   ```

### 4. Firebase Security Rules (Optional)

```javascript
// firestore.rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Troubleshooting

### Common Issues

1. **"Failed to initialize Firebase Admin SDK"**:
   - Check if `firebase-service-account.json` exists in `src/main/resources/`
   - Verify the JSON file is valid
   - Check if `firebase.project-id` matches your Firebase project

2. **"Invalid Firebase token"**:
   - Ensure the token is a valid Firebase ID token
   - Check if the token has expired
   - Verify Firebase Authentication is enabled

3. **"User not found"**:
   - User must be registered first via `/auth/register`
   - Check if user exists in MongoDB

### Debug Mode

Enable debug logging:
```properties
logging.level.com.real_estate.auth_service=DEBUG
logging.level.com.google.firebase=DEBUG
```

## Production Considerations

1. **Environment Variables**:
   ```properties
   firebase.project-id=${FIREBASE_PROJECT_ID}
   ```

2. **Service Account Security**:
   - Never commit service account keys to version control
   - Use environment variables or secure key management
   - Rotate keys regularly

3. **Firebase Quotas**:
   - Monitor Firebase usage and quotas
   - Set up billing alerts
   - Consider Firebase pricing plans

## Integration with Frontend

### JavaScript/React Example

```javascript
import { getAuth, signInWithEmailAndPassword } from 'firebase/auth';

const auth = getAuth();

// Login user
signInWithEmailAndPassword(auth, email, password)
  .then((userCredential) => {
    const user = userCredential.user;
    const idToken = user.getIdToken();
    
    // Send token to auth-service
    fetch('/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        idToken: idToken
      })
    });
  });
```

### Android Example

```kotlin
FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = task.result?.user
            user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                val idToken = tokenTask.result?.token
                // Send token to auth-service
            }
        }
    }
```

