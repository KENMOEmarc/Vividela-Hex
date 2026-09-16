package ken.vivid.domain.port.input;

import ken.vivid.domain.dto.AuthResult;
import ken.vivid.domain.entities.User;

public interface RegisterUseCase {

    AuthResult register(StoreCommand storeCommand);

    User store(StoreCommand storeCommand);
}
