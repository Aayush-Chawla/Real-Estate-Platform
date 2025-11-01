package com.real_estate.auth_service.dto;

import com.real_estate.auth_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    private String idToken;
    
    private String name;
    
    private String email;
    
    private User.Role role;
    
    private String imageUrl;
}
