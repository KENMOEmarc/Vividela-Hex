package ken.vivid.domain.port.input;

public record ChangePasswordCommand(
        Long userId,
        String currentPassword,
        String newPassword,
        String confirmPassword
) {
}
