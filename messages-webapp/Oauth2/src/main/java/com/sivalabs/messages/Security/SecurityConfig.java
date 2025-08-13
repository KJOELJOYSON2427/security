package com.sivalabs.messages.Security;

import com.sivalabs.messages.Exception.RestAuthenticationEntryPoint;
import com.sivalabs.messages.Filter.TokenAuthenticationFilter;
import com.sivalabs.messages.cookie.HttpCookieAuthorizatioRequestRepository;

import com.sivalabs.messages.failureHandler.OAuth2AuthenticationFailureHandler;


import com.sivalabs.messages.successHandler.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
@Autowired
private  CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

   @Autowired
   @Lazy
   private OAuth2AuthenticationSuccessHandler Auth2AuthorizationSuccessHandler;
   @Autowired
   @Lazy
   private OAuth2AuthenticationFailureHandler Auth2AuthenticationFailureHandler;
    @Bean
    public HttpCookieAuthorizatioRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieAuthorizatioRequestRepository();
    }


    //normal password/Username from React


    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


      @Bean
      public TokenAuthenticationFilter tokenAuthenticationFilter(){
        return  new TokenAuthenticationFilter();
      }

    @Bean
    PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }


     @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
          return config.getAuthenticationManager();
     }





    @Value("${app.cors.allowedOrigins}")
    private String[] allowedOrigins;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new RestAuthenticationEntryPoint())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/favicon.ico", "/**/*.png", "/**/*.gif",
                                "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.css", "/**/*.js")
                        .permitAll()
                        .requestMatchers("/auth/**", "/oauth2/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                        )
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .redirectionEndpoint(redirection ->
                                redirection.baseUri("/login/oauth2/code/*")
                        )

                        .successHandler(Auth2AuthorizationSuccessHandler)
                        .failureHandler(Auth2AuthenticationFailureHandler));


        //custom JWT Filter
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }




    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        configuration.setAllowedMethods(Arrays.asList("GET","PUT","POST","PATCH","DELETE","OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source= new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
}
