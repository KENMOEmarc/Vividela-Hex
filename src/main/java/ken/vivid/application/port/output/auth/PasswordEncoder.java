package ken.vivid.application.port.output.auth;

public interface PasswordEncoder {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
