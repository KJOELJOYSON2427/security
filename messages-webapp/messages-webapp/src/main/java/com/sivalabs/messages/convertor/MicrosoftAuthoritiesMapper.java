package com.sivalabs.messages.convertor;


import com.sivalabs.messages.User;
import com.sivalabs.messages.repository.UserRepo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class MicrosoftAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private final UserRepo userRepo;
    private String email; // This must be set before mapping

    public MicrosoftAuthoritiesMapper(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (email == null) {
            throw new IllegalStateException("Email must be set before mapping authorities");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in database: " + email));

        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }
}
