package com.sivalabs.messages.security;

import com.sivalabs.messages.Exception.SecurityExceptionHadler;
import com.sivalabs.messages.convertor.GoogleAuthoritiesMapper;
import com.sivalabs.messages.convertor.KeycloakAuthoritiesMapper;
import com.sivalabs.messages.convertor.MicrosoftAuthoritiesMapper;
import com.sivalabs.messages.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class MultiSecurity {



    private  final SecurityExceptionHadler securityExceptionHadler;
  private final OidcUserService oidcUserService;
  private  final GoogleAuthoritiesMapper googleAuthoritiesMapper;
    private final KeycloakAuthoritiesMapper keycloakAuthoritiesMapper;
  private  final MicrosoftAuthoritiesMapper microsoftAuthoritiesMapper;
    private final  authenticationSuccessHandler authenticationSuccessHandler;
  private  final UserRepo userRepo;
    public MultiSecurity(SecurityExceptionHadler securityExceptionHadler, OidcUserService oidcUserService, GoogleAuthoritiesMapper googleAuthoritiesMapper, KeycloakAuthoritiesMapper keycloakAuthoritiesMapper, MicrosoftAuthoritiesMapper microsoftAuthoritiesMapper, UserRepo userRepo,authenticationSuccessHandler authenticationSuccessHandler) {
        this.securityExceptionHadler = securityExceptionHadler;

        this.oidcUserService = oidcUserService;
        this.googleAuthoritiesMapper = googleAuthoritiesMapper;
        this.keycloakAuthoritiesMapper = keycloakAuthoritiesMapper;
        this.microsoftAuthoritiesMapper = microsoftAuthoritiesMapper;
        this.userRepo = userRepo;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
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

                                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(
                                        new CustomOidcUser(
                                                oidcUserService,
                                                userRepo,
                                                googleAuthoritiesMapper,
                                                keycloakAuthoritiesMapper,
                                                microsoftAuthoritiesMapper)))
                                .successHandler(authenticationSuccessHandler)
                                .defaultSuccessUrl("/", false)// Optional: if using OIDC
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
