package ken.vivid.domain.port.output;

public interface TokenBlacklist {

    void revoke(String token);

    boolean isRevoked(String token);
}
