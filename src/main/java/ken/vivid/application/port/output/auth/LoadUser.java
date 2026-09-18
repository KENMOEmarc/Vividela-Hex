package ken.vivid.application.port.output.auth;

import ken.vivid.domain.dto.Role;
import ken.vivid.domain.entities.User;

import java.util.Optional;

public interface LoadUser {

    Optional<User> loadByEmailOrUserName(String identifier);

    Optional<User> loadById(Long id);

    Optional<User> loadByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    long countByRole(Role role);
}
