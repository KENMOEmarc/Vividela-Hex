package ken.vivid.domain.port.input;

import ken.vivid.domain.dto.AuthResult;

public interface LoginUseCase {

    AuthResult login(LoginCommand loginCommand);
}
