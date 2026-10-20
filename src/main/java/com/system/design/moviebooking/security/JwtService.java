package com.system.design.moviebooking.security;

import com.system.design.moviebooking.model.enums.Role;
import com.system.design.moviebooking.model.mainEntity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    private SecretKey key;
    private long expiration;

    public JwtService(@Value("${app.jwt.secret}")  String secret,@Value("${app.jwt.expiration-ms}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public String generateToken(User user) {
        List<String> roles=user.getRole().stream().map(Role::name).toList();
        Date now=new Date();
        return Jwts.builder().subject(String.valueOf(user.getId()))
                .claim("roles",roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+expiration))
                .signWith(key)
                .compact();

    }

    public Claims extractClaims(String token){
       return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}
