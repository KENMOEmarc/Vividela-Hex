package ken.vivid.domain.port.input;

import ken.vivid.domain.entities.User;

public interface ChangePasswordUseCase {
    User changePassword(ChangePasswordCommand command);
}
