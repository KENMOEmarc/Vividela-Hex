package ken.vivid.application.port.input.auth.updateUser;

public record ChangePasswordCommand(
        Long userId,
        String currentPassword,
        String newPassword,
        String confirmPassword
) {
}
