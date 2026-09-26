package ken.vivid.adapter.input.web.output.persistence.jpaEntities.auth;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "revoked_tokens")
public class RevokedTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private LocalDateTime revokedAt;

    public RevokedTokenJpaEntity() {
    }

    public RevokedTokenJpaEntity(String token) {
        this.token = token;
        this.revokedAt = LocalDateTime.now();
    }

    String getToken() {
        return token;
    }

}
