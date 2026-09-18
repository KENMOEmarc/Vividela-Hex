package ken.vivid.adapter.output.persistence.adapter.auth;

import ken.vivid.adapter.output.persistence.jpaEntities.auth.RevokedTokenJpaEntity;
import ken.vivid.adapter.output.persistence.jpaRepositories.auth.RevokedTokenJpaRepository;
import ken.vivid.application.port.output.auth.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MySqlTokenBlacklistAdapter implements TokenBlacklist {

    private final RevokedTokenJpaRepository repository;

    @Override
    public void revoke(String token) {
        if (!repository.existsByToken(token)) {
            repository.save(new RevokedTokenJpaEntity(token));
        }
    }

    @Override
    public boolean isRevoked(String token) {
        return repository.existsByToken(token);
    }
}
