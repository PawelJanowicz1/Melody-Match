package org.example.melodymatch.system;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class JwtUtil {

    private static final String SECRET_KEY = "8Zz5tw0Ionm3XPZZfN0NOml3z9FMfmpg";

    public static String extractRoleFromJwt(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }
}