package ken.vivid.adapter.input.web.output.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import ken.vivid.application.port.output.auth.TokenGenerator;
import ken.vivid.domain.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenAdapter implements TokenGenerator {

    private static final int MIN_SECRET_LENGTH_BYTES = 32;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMillis;

    private SecretKey signingKey;

    @PostConstruct
    void validateAndInitSecret() {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_LENGTH_BYTES) {
            throw new IllegalStateException(
                    "The jwt.secret must contain at least " + MIN_SECRET_LENGTH_BYTES + " octets (HS256)");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        log.info("JWT signing key initialized successfully");
    }

    @Override
    public String generateToken(User user) {
        Date now = new Date();
        String token = Jwts.builder()
                .subject(user.getUserName())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis))
                .signWith(signingKey)
                .compact();
        log.debug("Generated JWT for user={}", user.getUserName());
        return token;
    }

    @Override
    public Optional<String> validateAndExtractEmail(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(signingKey).build()
                    .parseSignedClaims(token).getPayload();
            log.debug("Validated JWT for subject={}", claims.getSubject());
            return Optional.of(claims.getSubject());
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public long getExpirationMillis() {
        return expirationMillis;
    }
}
