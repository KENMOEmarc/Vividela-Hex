package ken.vivid.application.port.input.auth.saveUser;

import ken.vivid.domain.entities.AuthResult;

public interface LoginUseCase {

    AuthResult login(LoginCommand loginCommand);
}
