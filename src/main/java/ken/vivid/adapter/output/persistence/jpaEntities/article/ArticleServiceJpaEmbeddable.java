package ken.vivid.adapter.output.persistence.jpaEntities.article;

import jakarta.persistence.*;
import ken.vivid.domain.dto.ServiceType;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleServiceJpaEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType service;

    @Column(name = "applied_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal appliedPrice;
}
