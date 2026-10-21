package ken.vivid.adapter.output.persistence.jpaRepositories.article;

import ken.vivid.adapter.output.persistence.jpaEntities.article.ServicePriceJpaEntity;
import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicePriceJpaRepository extends JpaRepository<ServicePriceJpaEntity, Long> {
    Optional<ServicePriceJpaEntity> findByClothingTypeAndServiceAndActiveTrue(
            ClothingType clothingType, ServiceType service);
}
