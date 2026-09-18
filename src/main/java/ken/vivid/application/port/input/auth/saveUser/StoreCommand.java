package ken.vivid.application.port.input.auth.saveUser;

import ken.vivid.domain.dto.Role;

public record StoreCommand(Long id, String firstName,
                           String lastName,
                           String userName,
                           String email,
                           String phone,
                           String rawPassword,
                           Role role){
}
