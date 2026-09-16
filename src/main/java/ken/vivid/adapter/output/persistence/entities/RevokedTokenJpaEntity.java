package ken.vivid.adapter.output.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "revoked_tokens")
class RevokedTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private LocalDateTime revokedAt;

    protected RevokedTokenJpaEntity() {
    }

    RevokedTokenJpaEntity(String token) {
        this.token = token;
        this.revokedAt = LocalDateTime.now();
    }

    String getToken() {
        return token;
    }

}
