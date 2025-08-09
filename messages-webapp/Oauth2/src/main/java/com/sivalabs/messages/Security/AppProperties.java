package com.sivalabs.messages.Security;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;


@ConfigurationProperties(prefix = "app")
public record AppProperties(Auth auth, OAuth2 oauth2) {

    public record Auth(String tokenSecret, long tokenExpirationMsec) { }

    public record OAuth2(List<String> authorizedRedirectUris) { }
}