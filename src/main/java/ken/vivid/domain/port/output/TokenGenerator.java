package ken.vivid.domain.port.output;

import ken.vivid.domain.entities.User;

import java.util.Optional;

public interface TokenGenerator {

    String generateToken(User user);

    Optional<String> validateAndExtractEmail(String token);

    long getExpirationMillis();

}
