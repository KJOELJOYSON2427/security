package com.sivalabs.messages.security;

import com.sivalabs.messages.Exception.SecurityExceptionHadler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class MultiSecurity {



    private  final SecurityExceptionHadler securityExceptionHadler;

    public MultiSecurity(SecurityExceptionHadler securityExceptionHadler) {
        this.securityExceptionHadler = securityExceptionHadler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.addAllowedOrigin("http://localhost:3000"); // Use your frontend URL here
                    cfg.setAllowCredentials(true);
                    cfg.addAllowedMethod("*");
                    cfg.addAllowedHeader("*");
                    return cfg;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/", "/login").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 ->
                        oauth2
                                .defaultSuccessUrl("/", true)
                                .userInfoEndpoint(userInfo -> userInfo.oidcUserService()) // Optional: if using OIDC
                )
                .logout(logout ->
                        logout
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                )
                .exceptionHandling(
                        securityExceptionHadler
                );

        return http.build();
    }
}
