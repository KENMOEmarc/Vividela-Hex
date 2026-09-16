package ken.vivid.adapter.output.persistence;

import ken.vivid.adapter.output.persistence.entities.RevokedTokenJpaEntity;
import ken.vivid.adapter.output.persistence.repositories.RevokedTokenJpaRepository;
import ken.vivid.domain.port.output.TokenBlacklist;
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
