package com.joel.messages_service.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyCloakJwtAuthenticationConvertor implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Converter<Jwt, Collection<GrantedAuthority>> delegate = new JwtGrantedAuthoritiesConverter();





    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        System.out.println("come to mimr");
        List<GrantedAuthority> authorityList = extractRoles(jwt);
        Collection<GrantedAuthority> authorities = delegate.convert(jwt);
        System.out.println("come to mimr2"+ authorities);
        if(authorities != null){
              authorityList.addAll(authorities);
            System.out.println("come to mimr2"+ authorityList);
        }
        return new JwtAuthenticationToken(jwt, authorityList);
    }

    private List<GrantedAuthority> extractRoles(Jwt jwt){
        Map<String,Object> realmAccess =(Map<String,Object>) jwt.getClaims().get("realm_access");
        if(realmAccess== null ||  realmAccess.isEmpty()){
            return  List.of();
        }
        List<String> roles = (List<String>) realmAccess.get("roles");
        if(roles == null || roles.isEmpty()){
            roles =List.of("ROLE_USER"); //defaukt user role


        }
        return  roles.stream().
                  filter(role -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    }
}



