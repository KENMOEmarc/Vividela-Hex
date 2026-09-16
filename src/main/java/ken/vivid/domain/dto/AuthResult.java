package ken.vivid.domain.dto;

import ken.vivid.domain.entities.User;

public record AuthResult(String token,
                         long expiresInMillis,
                         User user) {
}
