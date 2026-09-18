package ken.vivid.application.port.input.auth.saveUser;

import ken.vivid.domain.entities.AuthResult;
import ken.vivid.domain.entities.User;

public interface RegisterUseCase {

    AuthResult register(StoreCommand storeCommand);

    User store(StoreCommand storeCommand);
}
