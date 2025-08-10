package com.sivalabs.messages.Security;

import com.sivalabs.messages.Exception.ResourceNotFoundException;
import com.sivalabs.messages.SpringWithDbOAuth.UserPrincipal;
import com.sivalabs.messages.model.User;
import com.sivalabs.messages.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email:" + email)
                );
        return UserPrincipal.create(user);
    }


    @Transactional
    public UserDetails loadUserById(Long id){
          User user=userRepository.findById(id).orElseThrow(
                  ()-> new ResourceNotFoundException("User","id", id)
          );

          return UserPrincipal.create(user);
    }
}
