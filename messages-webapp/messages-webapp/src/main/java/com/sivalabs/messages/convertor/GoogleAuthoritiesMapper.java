package com.sivalabs.messages.convertor;

import com.sivalabs.messages.User;
import com.sivalabs.messages.repository.UserRepo;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Component
public class GoogleAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private final UserRepo
            userRepo;
    @Setter
    private String email; // This will be set before mapping

    public GoogleAuthoritiesMapper(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (email == null) {
            log.info("i am google");
            throw new IllegalStateException("Email must be set before mapping authorities");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in database: " + email));

        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }
}
