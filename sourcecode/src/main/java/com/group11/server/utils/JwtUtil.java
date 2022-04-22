package com.group11.server.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final String SECRET_KEY = "sJewTcr45et";

    /**
     * This method extracts username using token and getSubject function.
     *
     * @param token Jwt token of current session
     * @return Username of the current user
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * This method extracts expiration date using token and getExpiration function.
     *
     * @param token Jwt token of current session
     * @return Date that token expires
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * This method extracts claim information using token and given function
     *
     * @param token          Jwt token of current session
     * @param claimsResolver function of Claims class that is mapped to retrieve information
     * @return The information that is retrieved using token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * This method extracts all claims using token
     *
     * @param token Jwt token of current session
     * @return Claims of the given token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * This method checks whether the given token is expired or not.
     *
     * @param token Jwt token of current session
     * @return Whether the given token is expired or not
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * This method generates a new Jwt token for given user details.
     *
     * @param userDetails User information details such as username and password
     * @return New Jwt token for current session
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * This method creates a new Jwt token using Jwts builder with claims and username.
     *
     * @param claims   Claims as hashmap
     * @param username Username of the current user
     * @return New Jwt token for current session
     */
    private String createToken(Map<String, Object> claims, String username) {
        long date = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(date))
                .setExpiration(new Date(date + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    /**
     * This method validates the given token with given user details.
     *
     * @param token       Jwt token of the current session
     * @param userDetails Details of the current user
     * @return Whether token is valid or not
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
