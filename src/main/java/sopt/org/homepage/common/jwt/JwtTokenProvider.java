package sopt.org.homepage.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sopt.org.homepage.exception.TokenException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Service
public class JwtTokenProvider {

    private final String adminSecretKey;

    public JwtTokenProvider(
            @Value("${jwt.admin}") String adminSecretKey
    ) {
        this.adminSecretKey = adminSecretKey;
    }

    public boolean validateTokenExpiration(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException | SignatureException e) {
            return false;
        }
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        return new UsernamePasswordAuthenticationToken(getId(token), null, null);
    }

    public Long getId(String token) {
        try {
            val claims = getClaimsFromToken(token);
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException | SignatureException e) {
            throw new TokenException("INVALID_TOKEN");
        }
    }

    private Claims getClaimsFromToken(String token) {
        String encodedKey = encodeKey(adminSecretKey);
        byte[] keyBytes = Decoders.BASE64.decode(encodedKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String resolveToken(HttpServletRequest request) {
        val headerAuth = request.getHeader("Authorization");
        return (StringUtils.hasText(headerAuth)) ? headerAuth : null;
    }

    private String encodeKey(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}