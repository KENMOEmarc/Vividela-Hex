package ken.vivid.adapter.output.persistence.jpaEntities.article;

import jakarta.persistence.*;
import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "service_prices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePriceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "clothing_type", nullable = false)
    private ClothingType clothingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "service", nullable = false)
    private ServiceType service;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active;
}
