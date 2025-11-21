package com.example.oms.auth.util;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // ==========================================
    // SECTION 1: CONFIGURATION & SETUP
    // The foundation: Secrets and Timing
    // ==========================================

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Step 0: Prepare the "King's Ring" (Secret Key).
     * Converts the text password from application.properties into a cryptographic Key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ==========================================
    // SECTION 2: GENERATION (Login Flow)
    // The "Printer": Creates new ID Cards
    // ==========================================

    /**
     * Step 1: Generate Token
     * Call this when the user successfully logs in.
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = Map.of("role", role);
        return buildToken(claims, email, jwtExpiration);
    }

    /**
     * Internal Helper to pack the data and stamp the seal.
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)                                     // 1. Pack the custom data (Role)
                .setSubject(subject)                                        // 2. Write the Email
                .setIssuedAt(new Date(System.currentTimeMillis()))          // 3. Stamp "Created Now"
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 4. Stamp "Valid Until..."
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)        // 5. SIGN IT (The Wax Seal)
                .compact();
    }

    // ==========================================
    // SECTION 3: VALIDATION (Request Flow)
    // The "Guard": Checks ID Cards at the door
    // ==========================================

    /**
     * Step 2: Validate Token
     * Call this for every request to check if the user is allowed in.
     */
    public boolean validateToken(String token, String userEmail) {
        final String username = extractUsername(token);
        // Check 1: Does the token belong to this user?
        // Check 2: Is the token still alive?
        return (username.equals(userEmail) && !isTokenExpired(token));
    }

    // ==========================================
    // SECTION 4: EXTRACTION TOOLS
    // The "Readers": Pulling specific data out
    // ==========================================

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    // ==========================================
    // SECTION 5: INTERNAL ENGINES
    // The "Mechanics": The heavy lifting code
    // ==========================================

    /**
     * THE CORE ENGINE
     * This tries to open the token using the Secret Key.
     * If the Key doesn't match (Hacker), this throws an Exception.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // <--- Critical Security Check
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generic Helper: "Open the suitcase and give me X"
     */
    // public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    //     final Claims claims = extractAllClaims(token);
    //     return claimsResolver.apply(claims);
    // }
}