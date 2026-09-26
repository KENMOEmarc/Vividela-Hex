package ken.vivid.adapter.input.web.output.persistence.jpaEntities.product;

import jakarta.persistence.*;
import ken.vivid.domain.dto.RegistrationType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "product_registrations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegistrationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "employee_user_id", nullable = false)
    private Long employeeUserId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type", nullable = false)
    private RegistrationType registrationType;

    @Column(length = 255)
    private String notes;

    @Column(name = "registered_at")
    private Instant registeredAt;
}
