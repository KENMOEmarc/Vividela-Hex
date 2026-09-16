package ken.vivid.domain.port.input;

import ken.vivid.domain.entities.User;

public interface UpdateUserUseCase {
    User update(UpdateCommand updateCommand);
}
