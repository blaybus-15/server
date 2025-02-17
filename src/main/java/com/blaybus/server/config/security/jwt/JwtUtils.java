package com.blaybus.server.config.security.jwt;

import com.blaybus.server.service.UserDetailsImpl;
import com.blaybus.server.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final UserDetailsServiceImpl userDetailsService;
    private final String jwtSecret;
    private final int jwtAccessExpiration;
    private final int jwtRefreshExpiration;
    private final Key key;

    public JwtUtils(UserDetailsServiceImpl userDetailsService,
                    @Value("${carelink.control.jwtSecret}") String jwtSecret,
                    @Value("${carelink.control.jwtAccessExpiration}") int jwtAccessExpiration,
                    @Value("${carelink.control.jwtRefreshExpiration}") int jwtRefreshExpiration
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtSecret = jwtSecret;
        this.jwtAccessExpiration = jwtAccessExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration;
        this.key = Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                // 추가로 id 클레임에 Member id 추가
                .claim("id", userPrincipal.getId())
                .claim("role", String.join(",", roles))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtAccessExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication verifyAndGetAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("role", String.class));

            return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return null;
    }

    public String getUsernameByToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        return claims.getSubject();
    }

    public String getMemberIdByToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        Object idObj = claims.get("id");
        return idObj.toString();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();
            return true;
        } catch (Exception e) {
            logger.error("Refresh token error while validating token: {}", e.getMessage());
            return false;
        }
    }
}
