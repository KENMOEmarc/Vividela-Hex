package ken.vivid.adapter.input.web.output.persistence.adapter.auth;

import ken.vivid.adapter.input.web.output.persistence.jpaRepositories.auth.RevokedTokenJpaRepository;
import ken.vivid.application.port.output.auth.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * A revoked token can be safely forgotten once its own JWT lifetime has
 * elapsed since it was revoked, because by then the token would be rejected
 * as expired anyway — it no longer needs to be looked up in the blacklist.
 * This wires up the previously unused security.jwt.blacklist-cleanup
 * property (see application.yml) so the revoked_tokens table doesn't grow
 * forever.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RevokedTokenCleanupJob {

    private final RevokedTokenJpaRepository repository;
    private final TokenGenerator tokenGenerator;

    @Scheduled(fixedDelayString = "${security.jwt.blacklist-cleanup.fixed-delay-ms}")
    @Transactional
    public void purgeExpiredRevokedTokens() {
        LocalDateTime cutoff = LocalDateTime.now()
                .minusNanos(tokenGenerator.getExpirationMillis() * 1_000_000L);
        long deleted = repository.deleteByRevokedAtBefore(cutoff);
        if (deleted > 0) {
            log.info("Purged {} expired revoked token(s) from the blacklist", deleted);
        }
    }
}
