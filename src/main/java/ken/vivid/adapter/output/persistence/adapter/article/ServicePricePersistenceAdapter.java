package ken.vivid.adapter.output.persistence.adapter.article;

import ken.vivid.adapter.output.persistence.jpaRepositories.article.ServicePriceJpaRepository;
import ken.vivid.application.port.output.article.LoadServicePrice;
import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServicePricePersistenceAdapter implements LoadServicePrice {

    private final ServicePriceJpaRepository jpaRepository;

    @Override
    public Optional<BigDecimal> loadActivePrice(ClothingType clothingType, ServiceType service) {
        return jpaRepository.findByClothingTypeAndServiceAndActiveTrue(clothingType, service)
                .map(entity -> entity.getPrice());
    }
}
