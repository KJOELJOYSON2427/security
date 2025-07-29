package com.joel.messages_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
//@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET,"/api/messages").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/messages/archive").hasRole("ADMIN").anyRequest().authenticated())
                .sessionManagement(c-> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(new KeyCloakJwtAuthenticationConvertor())   //jwtAuthenticationConverter()
                        )
                ).csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());

        return http.build();
    }
 //2ways i have configured

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var realmAccess = (Map<String, Object>) jwt.getClaim("realm_access");

            if (realmAccess == null || realmAccess.get("roles") == null) {
                return List.of(); // No roles
            }

            var roles = (List<String>) realmAccess.get("roles");

            return roles.stream()
                    .map(role -> "ROLE_" + role) // Add Spring prefix
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        return converter;
    }

}
