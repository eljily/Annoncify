package com.sibrahim.annoncify.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwt ;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            log.debug("No JWT token found in Authorization header.");
            filterChain.doFilter(request,response);
            return;
        }
        jwt = authHeader.substring(7);
        log.info("JWT token extracted from Authorization header: {}", jwt);
        String userName = jwtService.extractUsername(jwt);
        log.info("Extracted username from JWT token: {}", userName);
        if (userName != null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if (userDetails != null) {
                log.info("UserDetails loaded from UserDetailsService for username: {}", userName);
                if (jwtService.isTokenValid(jwt,userDetails)){
                    log.info("JWT token is valid for username: {}", userName);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null
                                    ,userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("User authenticated successfully: {}", userName);
                } else {
                    log.error("JWT token is not valid for username: {}", userName);
                }
            } else {
                log.error("Failed to load UserDetails for username: {}", userName);
            }
        } else {
            log.warn("User is already authenticated or username is null.");
        }
        filterChain.doFilter(request,response);
    }
}
