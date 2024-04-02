package com.sibrahim.annoncify.security;

import com.sibrahim.annoncify.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private String secret="2456789ASDFGHJKXCVBNM56XCVBNMDFGH23456NSDFGHYUBVC45678NBNCC9JHRT";

    public String extractUsername(String jwt){
        return extractClaim(jwt,Claims::getSubject);
    }

    public boolean isTokenValid(String jwt,UserDetails userDetails){
        String username = extractUsername(jwt);
        return  (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpirationTime(jwt).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpirationTime(String jwt){
        return extractClaim(jwt,Claims::getExpiration);
    }

    public String generateToken(User userDetails){
        return generateToken(userDetails,new HashMap<>());
    }

    public String generateToken(User userDetails, Map<String,Object> extraClaims){
        String authoritiesString = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setClaims(Map.of("name",userDetails.getName(),
                        "email",userDetails.getEmail(),
                        "id",userDetails.getId(),
                        "roles",authoritiesString))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()*1000*60*24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String jwt){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private <T> T extractClaim(String jwt, Function<Claims,T> claimDecoder){
        Claims claim = extractAllClaims(jwt);
        return
                claimDecoder.apply(claim);
    }

    private Key getSigningKey() {
        byte[] keyByte = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyByte);
    }
}
