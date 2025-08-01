package com.sivalabs.messages.convertor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.*;

public class KeycloakAuthoritiesMapper implements GrantedAuthoritiesMapper {
    private static final Logger log = LoggerFactory.getLogger(KeycloakAuthoritiesMapper.class);

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mapAuthorities = new HashSet<>();
        
        authorities.forEach(authority->{
            if(authority instanceof SimpleGrantedAuthority){
                log.info("called simple ");
                mapAuthorities.add(authority);
            } else if (authority instanceof OidcUserAuthority oidcUserAuthority) {

                OidcIdToken idToken = oidcUserAuthority.getIdToken();
                Map<String, Object> claims=idToken.getClaims();
                Map<String,Object> realmAccess = (Map<String,Object>)claims.get("realm_access");

                if (realmAccess != null && !realmAccess.isEmpty()) {

                    List<String> roles = (List<String>) realmAccess.get("roles");
                    var list = roles.stream()
                            .filter(role -> role.startsWith("ROLE_"))
                            .map(SimpleGrantedAuthority::new).toList();
                    mapAuthorities.addAll(list);
                }
            }else if(authority instanceof OAuth2UserAuthority auth2UserAuthority){
                Map<String, Object> userAttributes=auth2UserAuthority.getAttributes();
                Object rolesObj = userAttributes.get("roles");

                if (rolesObj instanceof List<?>) {
                    List<?> roles = (List<?>) rolesObj;

                    for (Object role : roles) {
                        if (role instanceof String roleStr) {
                            // Optionally prefix with "ROLE_" if your app expects that
                            if (roleStr.startsWith("ROLE_")) {
                                mapAuthorities.add(new SimpleGrantedAuthority(roleStr));
                            } else {
                                mapAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleStr));
                            }
                        }
                    }
                }

                // Optional: handle single role as String
                else if (rolesObj instanceof String singleRole) {
                    mapAuthorities.add(new SimpleGrantedAuthority("ROLE_" + singleRole));
                }
            }
        });
        return mapAuthorities;
    }
}
