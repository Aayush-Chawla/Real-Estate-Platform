package com.real_estate.auth_service.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final FirebaseAuth firebaseAuth;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new FirebaseAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        log.info("Security configuration applied with Firebase authentication and CORS");
        return http.build();
    }

    

    private class FirebaseAuthenticationFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            String path = request.getRequestURI();

            // Allow CORS preflight through unconditionally
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            // Skip authentication for public endpoints
            if (path.equals("/api/auth/register") || path.equals("/api/auth/login")) {
                filterChain.doFilter(request, response);
                return;
            }

            String authorization = request.getHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ")) {
                log.warn("Missing or invalid authorization header for path: {}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Missing or invalid authorization header\"}");
                return;
            }

            String idToken = authorization.substring(7);

            try {
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);

                // Create a simple authentication object
                FirebaseAuthenticationToken authentication = new FirebaseAuthenticationToken(decodedToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Firebase authentication successful for UID: {}", decodedToken.getUid());
                filterChain.doFilter(request, response);

            } catch (FirebaseAuthException e) {
                log.error("Firebase authentication failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid Firebase token: " + e.getMessage() + "\"}");
            }
        }
    }

    private static class FirebaseAuthenticationToken extends org.springframework.security.authentication.AbstractAuthenticationToken {
        private final FirebaseToken firebaseToken;

        public FirebaseAuthenticationToken(FirebaseToken firebaseToken) {
            super(null);
            this.firebaseToken = firebaseToken;
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return firebaseToken;
        }

        @Override
        public Object getPrincipal() {
            return firebaseToken.getUid();
        }
    }
}