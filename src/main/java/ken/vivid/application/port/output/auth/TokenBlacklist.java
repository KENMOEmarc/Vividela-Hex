package ken.vivid.application.port.output.auth;

public interface TokenBlacklist {

    void revoke(String token);

    boolean isRevoked(String token);
}
