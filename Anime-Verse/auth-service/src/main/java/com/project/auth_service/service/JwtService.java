package com.project.auth_service.service;

import com.project.auth_service.domain.dtos.TokenInfo;
import com.project.auth_service.domain.dtos.TokenVerificationResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${security.jwt.expiration}")
    private long expirationMsAccess;

    @Value("${security.jwt.refresh-expiration}")
    private long expirationMsRefresh;

    @Value("${SECURITY_JWT_SECRET}")
    private String secret;

    public String generateToken(UUID id , String role , long expireTime){
        Map<String , String> claims = new HashMap<>();
        claims.put("role" , role);

        return Jwts.builder()
                .claims(claims)
                .subject(id.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key())
                .compact();
    }

    public TokenVerificationResponse getTokens(UUID id , String role){
        String AccessToken = createAccessToken(id , role);
        Date TimeStampAccessToken = new Date(System.currentTimeMillis() + expirationMsAccess);
        String RefreshToken = createRefreshToken(id , role);
        Date TimeStampRefreshToken = new Date(System.currentTimeMillis() + expirationMsRefresh);

        return TokenVerificationResponse
                .builder()
                .AccessToken(AccessToken)
                .TimeStampAccessToken(TimeStampAccessToken)
                .RefreshToken(RefreshToken)
                .TimeStampRefreshToken(TimeStampRefreshToken)
                .build();

    }

    public String createAccessToken(UUID id , String role){
        return generateToken(id , role , expirationMsAccess);
    }

    public String createRefreshToken(UUID id , String role){
        return generateToken(id , role , expirationMsRefresh);
    }

    public SecretKey key(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public TokenInfo extractClaim(String token){
        final Claims claims = extractAllClaims(token);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setId(claims.getSubject());
        tokenInfo.setRoles(claims.get("role", String.class));
        tokenInfo.setExpirationAt(claims.getExpiration());

        return tokenInfo;
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
