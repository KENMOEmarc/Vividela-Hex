package ken.vivid.adapter.input.web.payloads;

import ken.vivid.domain.dto.AuthResult;
import ken.vivid.domain.entities.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresIn; // en secondes, comme dans AuthResponse.expiresIn côté auth-backend
    private UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {
        private Long id;
        private String userName;
        private String email;
        private String phone;
        private String firstName;
        private String lastName;
        private String role;
    }

    public static LoginResponse from(AuthResult result) {
        User user = result.user();
        return LoginResponse.builder()
                .accessToken(result.token())
                .tokenType("Bearer")
                .expiresIn(result.expiresInMillis() / 1000) // ms -> secondes
                .user(UserInfo.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}
