package com.sivalabs.messages.successHandler;

import com.sivalabs.messages.Exception.BadRequestException;
import com.sivalabs.messages.Security.AppProperties;
import com.sivalabs.messages.cookie.CookieUtils;
import com.sivalabs.messages.cookie.HttpCookieAuthorizatioRequestRepository;
import com.sivalabs.messages.jwtTokenProvider.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.sivalabs.messages.cookie.HttpCookieAuthorizatioRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private final TokenProvider tokenProvider;
    @Autowired
    private final AppProperties appProperties;
    @Autowired
    private final HttpCookieAuthorizatioRequestRepository httpCookieAuthorizatioRequestRepository;

    public OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, AppProperties appProperties, HttpCookieAuthorizatioRequestRepository httpCookieAuthorizatioRequestRepository) {
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
        this.httpCookieAuthorizatioRequestRepository = httpCookieAuthorizatioRequestRepository;
    }


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        String token = tokenProvider.createToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientURI = URI.create(uri);

        return appProperties.oauth2().authorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientURI.getHost())
                            && authorizedURI.getPort() == clientURI.getPort();
                });
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieAuthorizatioRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
