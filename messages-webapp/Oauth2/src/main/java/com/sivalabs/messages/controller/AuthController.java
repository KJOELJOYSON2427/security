package com.sivalabs.messages.controller;

import com.sivalabs.messages.Exception.BadRequestException;
import com.sivalabs.messages.RequestDAO.LoginRequest;
import com.sivalabs.messages.RequestDAO.SignUpRequest;
import com.sivalabs.messages.Response.ApiResponse.ApiResponse;
import com.sivalabs.messages.Response.AuthResponse;
import com.sivalabs.messages.jwtTokenProvider.TokenProvider;
import com.sivalabs.messages.model.AuthProvider;
import com.sivalabs.messages.model.User;
import com.sivalabs.messages.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private AuthenticationManager authenticationManager;

     @Autowired
     private UserRepository userRepository;
    @Autowired
    private TokenProvider tokenProvider;



    @PostMapping("/login")
    public  ResponseEntity<?>  authentication(@Valid @RequestBody LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       loginRequest.getEmail(),
                       loginRequest.getPassword()
               )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

         String token = tokenProvider.createToken(authentication);

         return ResponseEntity.ok(new AuthResponse(token));

    }




    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
         if(userRepository.existsByEmail(signUpRequest.getEmail())){
              throw new BadRequestException("Email address already in use.");
         }

         //creating a user

        User user = new User();

         user.setEmail(signUpRequest.getEmail());
         user.setPassword(signUpRequest.getPassword());
         user.setName(signUpRequest.getName());

         user.setProvider(AuthProvider.local);

         user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location =
                ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return  ResponseEntity.created(location)
                .body( new ApiResponse(true, "User registered successfully@"));
    }
}
