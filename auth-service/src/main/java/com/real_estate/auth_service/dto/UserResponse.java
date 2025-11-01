package com.real_estate.auth_service.dto;

import com.real_estate.auth_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private String id;
    private String firebaseUid;
    private String name;
    private String email;
    private User.Role role;
    private String imageUrl;
    private User.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

