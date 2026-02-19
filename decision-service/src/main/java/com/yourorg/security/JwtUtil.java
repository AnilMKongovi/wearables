package com.yourorg.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class JwtUtil {

    private static final Key key =
        Keys.hmacShaKeyFor("your-256-bit-secret-your-256-bit-secret".getBytes());

    public static Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
