package com.skillbox.auth.security.jwt;

import com.skillbox.auth.security.AppUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Slf4j
@Component
public class JwtTokenUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Long lifetime;

    public String generateToken(AppUserDetails appUserDetails) {
        return Jwts.builder().subject(appUserDetails.getUsername()).issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + lifetime))
                .signWith(getSigningKey(secret))
                .claim("firstName", appUserDetails.getFirstName())
                .claim("lastName", appUserDetails.getLastName())
                .claim("userId", appUserDetails.getId())
                .compact();


    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder().subject(username).issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + lifetime))
                .signWith(getSigningKey(secret))
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parser().verifyWith(getSigningKey(secret)).build();
            jwtParser.parse(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }


    public String getUserName(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public List<String> getUserRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    private Claims getAllClaimsFromToken(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
