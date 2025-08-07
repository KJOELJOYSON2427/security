package com.sivalabs.messages.security;

import com.sivalabs.messages.Role;
import com.sivalabs.messages.User;
import com.sivalabs.messages.convertor.GoogleAuthoritiesMapper;
import com.sivalabs.messages.convertor.KeycloakAuthoritiesMapper;
import com.sivalabs.messages.convertor.MicrosoftAuthoritiesMapper;
import com.sivalabs.messages.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
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

    private static final Logger log = LoggerFactory.getLogger(CustomOidcUser.class);

    private final OidcUserService oidcUserService;
    private final UserRepo userRepo;
    private final GoogleAuthoritiesMapper googleAuthoritiesMapper;
    private final KeycloakAuthoritiesMapper keycloakAuthoritiesMapper;
    private final MicrosoftAuthoritiesMapper microSoftAuthoritiesMapper;

    public CustomOidcUser(OidcUserService oidcUserService, UserRepo userRepo,
                          GoogleAuthoritiesMapper googleAuthoritiesMapper,
                          KeycloakAuthoritiesMapper keycloakAuthoritiesMapper,
                          MicrosoftAuthoritiesMapper microSoftAuthoritiesMapper) {
        this.oidcUserService = oidcUserService;
        this.userRepo = userRepo;
        this.googleAuthoritiesMapper = googleAuthoritiesMapper;
        this.keycloakAuthoritiesMapper = keycloakAuthoritiesMapper;
        this.microSoftAuthoritiesMapper = microSoftAuthoritiesMapper;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("🔐 OAuth2 login from provider: {}", registrationId);

        OidcUser oidcUser = oidcUserService.loadUser(userRequest);

        // Extract email
        String email = switch (registrationId) {
            case "google", "microsoft" -> oidcUser.getAttribute("email");
            case "messages-webapp" -> {
                String keycloakEmail = oidcUser.getAttribute("email");
                if (keycloakEmail == null) {
                    keycloakEmail = oidcUser.getAttribute("preferred_username");
                }
                yield keycloakEmail;
            }
            default -> null;
        };

        log.info("📧 Extracted email: {}", email);

        // Extract name
        String name = switch (registrationId) {
            case "google", "microsoft", "messages-webapp" -> {
                String fullName = oidcUser.getAttribute("name");
                if (fullName == null) fullName = oidcUser.getAttribute("preferred_username");
                yield fullName;
            }
            default -> "Unknown";
        };

        log.info("👤 Extracted name: {}", name);

        // Extract profile picture
        String picture = switch (registrationId) {
            case "google" -> oidcUser.getAttribute("picture");
            case "messages-webapp", "microsoft" -> null;
            default -> null;
        };

        log.info("🖼️ Extracted profile picture: {}", picture);

        // Persist user if email is available
        if (email != null) {
            User user = userRepo.findByEmail(email).orElseGet(() -> {
                log.info("➕ Creating new user for email: {}", email);
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setProvider(registrationId);
                newUser.setRole(Role.ROLE_USER);
                return newUser;
            });

            user.setName(name);
            if (picture != null) {
                user.setImage(picture);
            }

            userRepo.save(user);
            log.info("💾 User saved/updated: {}", email);
        }

        // Map authorities
        Collection<? extends GrantedAuthority> mappedAuthorities = switch (registrationId) {
            case "google" -> {
                log.info("🔄 Mapping authorities using GoogleAuthoritiesMapper");
                googleAuthoritiesMapper.setEmail(email);
                yield googleAuthoritiesMapper.mapAuthorities(oidcUser.getAuthorities());
            }
            case "messages-webapp" -> {
                log.info("🔄 Mapping authorities using KeycloakAuthoritiesMapper");
                yield keycloakAuthoritiesMapper.mapAuthorities(oidcUser.getAuthorities());
            }
            case "microsoft" -> {
                log.info("🔄 Mapping authorities using MicrosoftAuthoritiesMapper");
                microSoftAuthoritiesMapper.setEmail(email);
                yield microSoftAuthoritiesMapper.mapAuthorities(oidcUser.getAuthorities());
            }
            default -> {
                log.warn("⚠️ Unknown registrationId '{}', using default authorities", registrationId);
                yield oidcUser.getAuthorities();
            }
        };

        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
