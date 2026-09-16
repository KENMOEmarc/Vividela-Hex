package ken.vivid.domain.port.input;

public record LoginCommand(String email, String rawPassword) {
}
