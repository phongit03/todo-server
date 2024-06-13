package com.todoapp.todo_server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.security.WeakKeyException;
import jakarta.websocket.Decoder;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTGenerator {
    public String generateToken(Authentication authentication) throws WeakKeyException {
        System.out.println("Generating Token....");
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
        System.out.println("Dates init!!");
        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(getSigninKey());
        return builder.compact();
    }

    public String getUsernameFromJWT(String token) throws WeakKeyException {
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT expired!");
        }
    }

    private SecretKey getSigninKey() throws WeakKeyException {

        try {
            System.out.println("Creating key....");
            byte[] secreteBytes = Decoders.BASE64.decode(SecurityConstants.JWT_SECRETES);
            SecretKey key = Keys.hmacShaKeyFor(secreteBytes);
            System.out.println("Key Created: "+key);
            return key;
        } catch (WeakKeyException e) {
            throw new WeakKeyException("Your secret key is invalid!");
        }
    }

}
