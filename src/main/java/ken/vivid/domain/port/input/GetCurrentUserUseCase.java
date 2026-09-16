package ken.vivid.domain.port.input;

import ken.vivid.domain.entities.User;

public interface GetCurrentUserUseCase {
    User getCurrentUser(String email);
}
