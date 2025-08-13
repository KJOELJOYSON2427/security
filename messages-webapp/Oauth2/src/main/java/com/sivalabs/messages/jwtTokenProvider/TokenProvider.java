package com.sivalabs.messages.jwtTokenProvider;


import com.sivalabs.messages.SpringWithDbOAuth.UserPrincipal;
import com.sivalabs.messages.Security.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private final AppProperties appProperties;
    private final Key key; // Store the signing key

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.key = Keys.hmacShaKeyFor(
                appProperties.auth().tokenSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createToken(Authentication authentication) {

        Object principal = authentication.getPrincipal();
        String userId;

        if (principal instanceof UserPrincipal) {
            userId = Long.toString(((UserPrincipal) principal).getId());
        } else if (principal instanceof DefaultOidcUser) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
            userId = oidcUser.getSubject();
            if (userId == null || userId.isEmpty()) {
                userId = (String) oidcUser.getAttributes().get(StandardClaimNames.EMAIL);
            }
        } else {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass());
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.auth().tokenExpirationMsec());

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // Modern parser
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex) {
            logger.error("Invalid JWT signature or malformed token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return false;
    }
}
