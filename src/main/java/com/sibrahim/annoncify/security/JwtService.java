package com.sibrahim.annoncify.security;

import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.exceptions.GenericException;
import com.sibrahim.annoncify.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    private final UserRepository userRepository;
    private String secret="2456789ASDFGHJKXCVBNM56XCVBNMDFGH23456NSDFGHYUBVC45678NBNCC9JHRT";

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String extractUsername(String jwt){
        log.info("Extracting username from JWT token");
        String username = extractClaim(jwt, Claims::getSubject);
        log.info("Extracted username: {}", username);
        return username;
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

    public String generateToken(UserDetails userDetails){
        return generateToken(userDetails,new HashMap<>());
    }

    public String generateToken(UserDetails userDetails, Map<String,Object> extraClaims){
        User user = userRepository.findUserByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new GenericException("User not found with phone number: " + userDetails.getUsername()));
        String authoritiesString = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        log.error(authoritiesString);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim("userId",user.getId())
                .claim("role",user.getRole())
                .claim("email",user.getEmail())
                .claim("name",user.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 day
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
