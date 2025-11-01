package com.real_estate.auth_service.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.real_estate.auth_service.dto.*;
import com.real_estate.auth_service.entity.User;
import com.real_estate.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final FirebaseService firebaseService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        try {
            // Verify Firebase token to ensure the user is authenticated
            String firebaseUid = firebaseService.getUidFromToken(request.getIdToken());
            
            // Extract additional user info from Firebase token
            String emailFromToken = firebaseService.getEmailFromToken(request.getIdToken());
            String nameFromToken = firebaseService.getNameFromToken(request.getIdToken());
            String pictureFromToken = firebaseService.getPictureFromToken(request.getIdToken());
            
            // Use Firebase token data if available, otherwise use request data
            String finalEmail = emailFromToken != null ? emailFromToken : request.getEmail();
            String finalName = nameFromToken != null ? nameFromToken : request.getName();
            String finalPicture = pictureFromToken != null ? pictureFromToken : request.getImageUrl();
            
            // Check if user already exists
            if (userRepository.existsByFirebaseUid(firebaseUid)) {
                log.warn("User already exists with Firebase UID: {}", firebaseUid);
                throw new RuntimeException("User already registered. Please login instead.");
            }
            
            if (userRepository.existsByEmail(finalEmail)) {
                log.warn("User already exists with email: {}", finalEmail);
                throw new RuntimeException("User already registered with this email. Please login instead.");
            }
            
            // Create new user
            User user = User.builder()
                    .firebaseUid(firebaseUid)
                    .name(finalName)
                    .email(finalEmail)
                    .role(request.getRole())
                    .imageUrl(finalPicture)
                    .status(User.Status.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            User savedUser = userRepository.save(user);
            
            log.info("User registered successfully: {}", savedUser.getEmail());
            
            return AuthResponse.builder()
                    .success(true)
                    .message("User registered successfully")
                    .user(mapToUserResponse(savedUser))
                    .accessToken(request.getIdToken()) // Return Firebase token as access token
                    .build();
                    
        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication error during registration", e);
            throw new RuntimeException("Invalid Firebase token: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Registration failed", e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            // Verify Firebase token
            String firebaseUid = firebaseService.getUidFromToken(request.getIdToken());
            
            // Find user in database
            Optional<User> userOpt = userRepository.findByFirebaseUid(firebaseUid);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found. Please register first.");
            }
            
            User user = userOpt.get();
            if (user.getStatus() != User.Status.ACTIVE) {
                throw new RuntimeException("User account is not active");
            }
            
            log.info("User logged in successfully: {}", user.getEmail());
            
            return AuthResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .user(mapToUserResponse(user))
                    .accessToken(request.getIdToken())
                    .build();
                    
        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication error during login", e);
            throw new RuntimeException("Invalid Firebase token: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Login failed", e);
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
    
    public AuthResponse logout(String idToken) {
        try {
            // Verify token to ensure it's valid
            firebaseService.verifyIdToken(idToken);
            
            log.info("User logged out successfully");
            
            return AuthResponse.builder()
                    .success(true)
                    .message("Logout successful")
                    .build();
                    
        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication error during logout", e);
            throw new RuntimeException("Invalid Firebase token: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new RuntimeException("Logout failed: " + e.getMessage(), e);
        }
    }
    
    public UserResponse getProfile(String idToken) {
        try {
            String firebaseUid = firebaseService.getUidFromToken(idToken);
            
            Optional<User> userOpt = userRepository.findByFirebaseUid(firebaseUid);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            
            return mapToUserResponse(userOpt.get());
            
        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication error during profile fetch", e);
            throw new RuntimeException("Invalid Firebase token: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Get profile failed", e);
            throw new RuntimeException("Get profile failed: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public UserResponse updateProfile(String idToken, UpdateProfileRequest request) {
        try {
            String firebaseUid = firebaseService.getUidFromToken(idToken);
            
            Optional<User> userOpt = userRepository.findByFirebaseUid(firebaseUid);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            
            User user = userOpt.get();
            user.setName(request.getName());
            user.setImageUrl(request.getImageUrl());
            user.setUpdatedAt(LocalDateTime.now());
            
            User updatedUser = userRepository.save(user);
            
            log.info("User profile updated successfully: {}", updatedUser.getEmail());
            
            return mapToUserResponse(updatedUser);
            
        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication error during profile update", e);
            throw new RuntimeException("Invalid Firebase token: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Update profile failed", e);
            throw new RuntimeException("Update profile failed: " + e.getMessage(), e);
        }
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firebaseUid(user.getFirebaseUid())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .imageUrl(user.getImageUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
