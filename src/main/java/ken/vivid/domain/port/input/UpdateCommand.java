package ken.vivid.domain.port.input;

import ken.vivid.domain.dto.Role;

public record UpdateCommand(Long targetUserId,
                            Long actingUserId,
                            Role actingUserRole,
                            String firstName,
                            String lastName,
                            String userName,
                            String email,
                            String phone) {
}
