package com.sivalabs.messages.convertor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class GoogleAuthoritiesMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities;
    }

    public Collection<? extends  GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities,String email){
        HashSet<? extends GrantedAuthority> mappedAuthorities = new HashSet<>(authorities);


        if(email !=null && email.toLowerCase().contains("123456789")){
            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        }else{

        }
    }
}
