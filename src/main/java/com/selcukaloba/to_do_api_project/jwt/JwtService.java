package com.selcukaloba.to_do_api_project.jwt;

import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    //access token üretmek, tokenı okumak, geçerli mi kontrol etmek
    @Value("${todo.app.jwt.secret}")
    private String SECRET_KEY;

    public String generateToken(String username)
    {
        return Jwts.builder()
                .subject(username) //token sahibi
                .issuedAt(new Date()) //verilme tarihi
                .expiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 2)) //geçerlilik süresi
                .signWith(getKey()) //imza
                .compact(); //birleştirme
    }

    public SecretKey getKey() //keyi jwtsin anlayacağı Key nesnesine çevirmek
    {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public<T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
       final Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
                return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, String username)
    {
        final String extractedUsername = extractUsername(token);
        Date expiredDate = extractClaim(token, Claims::getExpiration);

        boolean isUsernameHeaderValid = extractedUsername.equals(username);
        boolean isTokenExpired = expiredDate.before(new Date());

        return (!isTokenExpired && isUsernameHeaderValid);
    }
}
