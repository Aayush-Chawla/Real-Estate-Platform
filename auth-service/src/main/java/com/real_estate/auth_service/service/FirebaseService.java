package com.real_estate.auth_service.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseService {
    
    private final FirebaseAuth firebaseAuth;
    
    /**
     * Verifies Firebase ID token and returns decoded token
     * @param idToken Firebase ID token to verify
     * @return Decoded Firebase token
     * @throws FirebaseAuthException if token is invalid
     */
    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        try {
            log.debug("Verifying Firebase ID token");
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            log.debug("Firebase ID token verified successfully for UID: {}", decodedToken.getUid());
            return decodedToken;
        } catch (FirebaseAuthException e) {
            log.error("Failed to verify Firebase ID token: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Extracts Firebase UID from ID token
     * @param idToken Firebase ID token
     * @return Firebase UID
     * @throws FirebaseAuthException if token is invalid
     */
    public String getUidFromToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = verifyIdToken(idToken);
        return decodedToken.getUid();
    }
    
    /**
     * Extracts email from Firebase ID token
     * @param idToken Firebase ID token
     * @return User email
     * @throws FirebaseAuthException if token is invalid
     */
    public String getEmailFromToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = verifyIdToken(idToken);
        return decodedToken.getEmail();
    }
    
    /**
     * Extracts name from Firebase ID token
     * @param idToken Firebase ID token
     * @return User name
     * @throws FirebaseAuthException if token is invalid
     */
    public String getNameFromToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = verifyIdToken(idToken);
        return decodedToken.getName();
    }
    
    /**
     * Extracts picture URL from Firebase ID token
     * @param idToken Firebase ID token
     * @return User picture URL
     * @throws FirebaseAuthException if token is invalid
     */
    public String getPictureFromToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = verifyIdToken(idToken);
        return decodedToken.getPicture();
    }
    
    /**
     * Checks if Firebase ID token is valid
     * @param idToken Firebase ID token
     * @return true if token is valid, false otherwise
     */
    public boolean isValidToken(String idToken) {
        try {
            verifyIdToken(idToken);
            return true;
        } catch (FirebaseAuthException e) {
            log.debug("Invalid Firebase token: {}", e.getMessage());
            return false;
        }
    }
}
