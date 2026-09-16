package ken.vivid.adapter.input.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ken.vivid.domain.entities.User;
import ken.vivid.domain.dto.Role;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto(
        Long id,
        String userName,
        String email,
        String phone,
        String firstName,
        String lastName,
        Role role,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.getActive())
                .build();
    }
}
