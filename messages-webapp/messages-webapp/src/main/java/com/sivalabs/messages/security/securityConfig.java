package com.sivalabs.messages.security;


import com.sivalabs.messages.convertor.KeycloakAuthoritiesMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class securityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public securityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(
                c -> c.
                        requestMatchers("/").
                        permitAll()
                        .anyRequest()
                        .authenticated()


        )
                .cors(CorsConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .oauth2Login(httpSecurityOAuth2LoginConfigurer ->
                        httpSecurityOAuth2LoginConfigurer.userInfoEndpoint(
                                userInfo-> userInfo.userAuthoritiesMapper(new KeycloakAuthoritiesMapper())
                        ))
                .logout(
                        logout -> logout
                                .clearAuthentication(true)
                                .invalidateHttpSession(true)
                                .logoutSuccessHandler(oidcLogoutSuccessHandler())
                );


        return http.build();
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler(){
        OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcClientInitiatedLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
        return  oidcClientInitiatedLogoutSuccessHandler;
    }
}