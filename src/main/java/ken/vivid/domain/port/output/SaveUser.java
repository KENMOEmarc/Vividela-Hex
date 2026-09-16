package ken.vivid.domain.port.output;

import ken.vivid.domain.entities.User;

public interface SaveUser {
    User save(User user);
}
