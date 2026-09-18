package ken.vivid.application.port.output.auth;

import ken.vivid.domain.entities.User;

public interface SaveUser {
    User save(User user);
}
