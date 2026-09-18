package ken.vivid.application.port.input.auth.updateUser;

import ken.vivid.domain.dto.Role;

public record UpdateCommand(Long targetUserId,
                            Role actingUserRole,
                            String firstName,
                            String lastName,
                            String userName,
                            String email,
                            String phone) {
}
