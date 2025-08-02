package com.sivalabs.messages.Exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class SecurityExceptionHadler implements Customizer<ExceptionHandlingConfigurer<HttpSecurity>> {
    public void customize(ExceptionHandlingConfigurer<HttpSecurity> configurer){
        configurer.authenticationEntryPoint(unauthorizedEntryPoint())
                .accessDeniedHandler(forbiddenAccessHandler());
    }


    private AuthenticationEntryPoint  unauthorizedEntryPoint(){
        return  ( request,  response,  ex)->{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        };
    }        private AccessDeniedHandler forbiddenAccessHandler(){
            return  (HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) -> {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            };
        }

}
