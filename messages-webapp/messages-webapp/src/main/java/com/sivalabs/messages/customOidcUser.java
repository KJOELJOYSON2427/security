package com.sivalabs.messages;

import com.sivalabs.messages.convertor.GoogleAuthoritiesMapper;
import com.sivalabs.messages.convertor.KeycloakAuthoritiesMapper;
import com.sivalabs.messages.convertor.MicroSoftAuthoritiesMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomOidcUser implements OAuth2UserService<OidcUserRequest, OidcUser> {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // e.g. "google" or "keycloak"

        // Load the user from the default OidcUserService
        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);

        // Select a GrantedAuthoritiesMapper based on the IdP
        GrantedAuthoritiesMapper mapper = switch (registrationId) {
            case "google" -> new GoogleAuthoritiesMapper();
            case "keycloak" -> new KeycloakAuthoritiesMapper();
            case "microsoft" -> new MicroSoftAuthoritiesMapper();
            default -> authorities -> authorities; // fallback: don't map
        };

        // Map authorities
        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(oidcUser.getAuthorities());

        // Return new DefaultOidcUser with mapped authorities
        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
