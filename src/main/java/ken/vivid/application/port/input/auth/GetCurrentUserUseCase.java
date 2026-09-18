package ken.vivid.application.port.input.auth;

import ken.vivid.domain.entities.User;

public interface GetCurrentUserUseCase {
    User getCurrentUser(String email);
}
