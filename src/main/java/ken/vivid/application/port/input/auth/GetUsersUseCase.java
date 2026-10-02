package ken.vivid.application.port.input.auth;

import ken.vivid.domain.entities.User;

import java.util.List;

public interface GetUsersUseCase {
    User getUserById(Long id);
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    List<User> getAllUsers();

    List<User> getAllCustomers();
}
