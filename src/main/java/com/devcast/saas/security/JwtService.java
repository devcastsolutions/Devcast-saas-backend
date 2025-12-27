package com.devcast.saas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private static final String USER_ID_CLAIM = "userId";

    public String generateToken(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                log.error("❌ Cannot generate token: username is null or empty");
                return null;
            }

            Map<String, Object> claims = new HashMap<>();
            String token = createToken(claims, username, jwtExpiration);

            if (token == null || token.trim().isEmpty()) {
                log.error("❌ Token generation failed: created token is null or empty");
                return null;
            }

            log.debug("✅ Token generated successfully for user: {}", username);
            return token;
        } catch (Exception e) {
            log.error("❌ Error generating token for user: {} - {}", username, e.getMessage(), e);
            return null;
        }
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        try {
            if (subject == null || subject.trim().isEmpty()) {
                log.error("❌ Cannot create token: subject is null or empty");
                return null;
            }

            if (expiration <= 0) {
                log.error("❌ Cannot create token: expiration time is invalid: {}", expiration);
                return null;
            }

            SecretKey signingKey = getSigningKey();
            if (signingKey == null) {
                log.error("❌ Cannot create token: signing key is null");
                return null;
            }

            long currentTime = System.currentTimeMillis();
            Date issuedAt = new Date(currentTime);
            Date expirationDate = new Date(currentTime + expiration);

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expirationDate)
                    .signWith(signingKey, SignatureAlgorithm.HS256)
                    .compact();

            log.debug("✅ Token created successfully for subject: {}", subject);
            return token;

        } catch (Exception e) {
            log.error("❌ Error creating token for subject: {} - {}", subject, e.getMessage(), e);
            return null;
        }
    }


    private SecretKey getSigningKey() {
        try {
            if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
                log.error("❌ JWT secret is null or empty");
                return null;
            }

            // Ensure the key is at least 256 bits (32 bytes)
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

            if (keyBytes.length < 32) {
                log.warn("⚠️ JWT secret is too short ({}), using secure key generator", keyBytes.length);
                // For development only - in production, use a proper 256-bit key
                return Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }

            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            log.debug("✅ Signing key generated successfully");
            return key;
        } catch (Exception e) {
            log.error("❌ Error generating signing key: {}", e.getMessage());
            return null;
        }
    }

    public String generateRefreshToken(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                log.error("❌ Cannot generate refresh token: username is null or empty");
                return null;
            }

            Map<String, Object> claims = new HashMap<>();
            String token = createToken(claims, username, refreshExpiration);

            if (token == null || token.trim().isEmpty()) {
                log.error("❌ Refresh token generation failed: created token is null or empty");
                return null;
            }

            log.debug("✅ Refresh token generated successfully for user: {}", username);
            return token;
        } catch (Exception e) {
            log.error("❌ Error generating refresh token for user: {} - {}", username, e.getMessage(), e);
            return null;
        }
    }


    public String extractUsername(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.warn("⚠️ Cannot extract username: token is null or empty");
                return null;
            }

            String username = extractClaim(token, Claims::getSubject);
            if (username == null || username.trim().isEmpty()) {
                log.warn("⚠️ Extracted username is null or empty from token");
                return null;
            }

            log.debug("✅ Username extracted successfully: {}", username);
            return username;
        } catch (Exception e) {
            log.error("❌ Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.warn("⚠️ Cannot extract claim: token is null or empty");
                return null;
            }

            if (claimsResolver == null) {
                log.warn("⚠️ Cannot extract claim: claimsResolver is null");
                return null;
            }

            final Claims claims = extractAllClaims(token);
            if (claims == null) {
                log.warn("⚠️ Cannot extract claim: claims are null");
                return null;
            }

            T result = claimsResolver.apply(claims);
            log.debug("✅ Claim extracted successfully");
            return result;
        } catch (Exception e) {
            log.error("❌ Error extracting claim from token: {}", e.getMessage());
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.warn("⚠️ Cannot extract claims: token is null or empty");
                return null;
            }

            SecretKey signingKey = getSigningKey();
            if (signingKey == null) {
                log.error("❌ Cannot extract claims: signing key is null");
                return null;
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.debug("✅ Claims extracted successfully");
            return claims;
        } catch (Exception e) {
            log.error("❌ Error extracting all claims from token: {}", e.getMessage());
            return null;
        }
    }
}
