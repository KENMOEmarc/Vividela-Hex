package ken.vivid.adapter.input.web.output.persistence.jpaEntities.product;

import jakarta.persistence.*;
import ken.vivid.domain.dto.MovementType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    /**
     * Colonne présente en base (FK vers `treatments`) mais non portée par le
     * domaine `StockMovement` actuel : StockService.consume() référence le
     * traitement uniquement de façon textuelle dans `notes`. Laissée
     * nullable et non mappée ici tant que le module `treatment` (absent du
     * code fourni, cf. l'analyse précédente) n'existe pas.
     */
    @Column(name = "treatment_id")
    private Long treatmentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(length = 255)
    private String notes;

    @Column(name = "movement_date")
    private Instant movementDate;
}
