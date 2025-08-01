package com.sivalabs.messages.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecurityHepler {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public SecurityHepler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public  String getAccessToken(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof OAuth2AuthenticationToken oauthGToken)){
            return null;
        }

        OAuth2AuthorizedClient cli = authorizedClientService.loadAuthorizedClient
                (oauthGToken.getAuthorizedClientRegistrationId(), oauthGToken.getName()
                );


        String accessToken = cli.getAccessToken().getTokenValue();

        return  accessToken;
    }



    //getLoginUserDetails

    public static Map<String,Object> getLoginUserDetails(){


        Map<String, Object> map = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof OAuth2AuthenticationToken oauthGToken)){
            return null;
        }
        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles=authorities.stream().map(GrantedAuthority::getAuthority).toList();
        OidcUserInfo userInfo = principal.getUserInfo();
        map.put("id", userInfo.getSubject());
        map.put("fullName", userInfo.getFullName());
        map.put("email", userInfo.getEmail());
        map.put("username", userInfo.getPreferredUsername());
        map.put("roles", roles);
        return  map;
    }
}
