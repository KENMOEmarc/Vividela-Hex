package ken.vivid.domain.entities;

public record AuthResult(String token, long expiresInMillis, User user) {
}
