package ken.vivid.application.port.input.auth.saveUser;

public record LoginCommand(String email, String rawPassword) {
}
