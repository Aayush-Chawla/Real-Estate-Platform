package com.real_estate.auth_service.repository;

import com.real_estate.auth_service.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByFirebaseUid(String firebaseUid);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByFirebaseUid(String firebaseUid);
    
    boolean existsByEmail(String email);
}

