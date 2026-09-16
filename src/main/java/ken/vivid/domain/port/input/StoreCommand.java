package ken.vivid.domain.port.input;

import ken.vivid.domain.dto.Role;

public record StoreCommand(Long id, String firstName,
                           String lastName,
                           String userName,
                           String email,
                           String phone,
                           String rawPassword,
                           Role role){
}
