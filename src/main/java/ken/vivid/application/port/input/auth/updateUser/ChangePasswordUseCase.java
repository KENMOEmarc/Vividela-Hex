package ken.vivid.application.port.input.auth.updateUser;

import ken.vivid.domain.entities.User;

public interface ChangePasswordUseCase {
    User changePassword(ChangePasswordCommand command);
}
