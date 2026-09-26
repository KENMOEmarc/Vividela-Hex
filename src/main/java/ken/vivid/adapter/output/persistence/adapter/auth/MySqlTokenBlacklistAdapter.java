package ken.vivid.adapter.output.persistence.adapter.auth;

import ken.vivid.adapter.output.persistence.jpaEntities.auth.RevokedTokenJpaEntity;
import ken.vivid.adapter.output.persistence.jpaRepositories.auth.RevokedTokenJpaRepository;
import ken.vivid.application.port.output.auth.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MySqlTokenBlacklistAdapter implements TokenBlacklist {

    private final RevokedTokenJpaRepository repository;

    @Override
    public void revoke(String token) {
        if (!repository.existsByToken(token)) {
            log.info("Revoking JWT in blacklist");
            repository.save(new RevokedTokenJpaEntity(token));
            return;
        }
        log.debug("JWT already present in blacklist");
    }

    @Override
    public boolean isRevoked(String token) {
        boolean revoked = repository.existsByToken(token);
        if (revoked) {
            log.debug("Rejected revoked JWT");
        }
        return revoked;
    }
}
