package com.sivalabs.messages.Security;


import com.sivalabs.messages.Exception.RestAuthenticationEntryPoint;
import com.sivalabs.messages.cookie.HttpCookieAuthorizatioRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{


    @Autowired
    private HttpCookieAuthorizatioRequestRepository httpCookieAuthorizatioRequestRepository;



    @Bean
    public HttpCookieAuthorizatioRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieAuthorizatioRequestRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.
                cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(
                        AbstractHttpConfigurer::disable
                )
                .httpBasic(
                        AbstractHttpConfigurer::disable
                )
                .exceptionHandling(ex->
                        ex.authenticationEntryPoint(
                                new RestAuthenticationEntryPoint()                        ))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/error", "/favicon.ico", "/**/*.png", "/**/*.gif",
                        "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.css", "/**/*.js")
                .permitAll()
                .requestMatchers("/auth/**", "/oauth2/**")
                .permitAll()
                .anyRequest()
                .authenticated()
        ).
                oauth2Login(
                ).authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(

                )
    }

}
