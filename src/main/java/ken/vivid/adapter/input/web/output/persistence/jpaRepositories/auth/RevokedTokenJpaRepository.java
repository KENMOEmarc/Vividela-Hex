package ken.vivid.adapter.input.web.output.persistence.jpaRepositories.auth;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.auth.RevokedTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RevokedTokenJpaRepository extends JpaRepository<RevokedTokenJpaEntity, Long> {
    boolean existsByToken(String token);

    long deleteByRevokedAtBefore(LocalDateTime cutoff);
}
