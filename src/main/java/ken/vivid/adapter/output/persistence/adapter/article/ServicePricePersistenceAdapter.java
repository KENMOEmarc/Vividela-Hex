package ken.vivid.adapter.output.persistence.adapter.article;

import ken.vivid.adapter.output.persistence.jpaEntities.article.ServicePriceJpaEntity;
import ken.vivid.adapter.output.persistence.jpaRepositories.article.ServicePriceJpaRepository;
import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.service.ServicePrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServicePricePersistenceAdapter implements ken.vivid.application.port.output.serviceprice.LoadServicePrice,
        ken.vivid.application.port.output.serviceprice.LoadServicePriceCatalog,
        ken.vivid.application.port.output.serviceprice.SaveServicePrice,
        ken.vivid.application.port.output.serviceprice.DeleteServicePrice {

    private final ServicePriceJpaRepository jpaRepository;

    @Override
    public Optional<BigDecimal> loadActivePrice(ClothingType clothingType, ServiceType service) {
        return jpaRepository.findByClothingTypeAndServiceAndActiveTrue(clothingType, service)
                .map(ServicePriceJpaEntity::getPrice);
    }

    @Override
    public Optional<ken.vivid.domain.entities.service.ServicePrice> loadById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ken.vivid.domain.entities.service.ServicePrice> loadAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<ServicePrice> loadAllActive() {
        return jpaRepository.findAll().stream()
                .filter(ServicePriceJpaEntity::isActive)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ken.vivid.domain.entities.service.ServicePrice> loadByClothingType(ClothingType clothingType) {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getClothingType() == clothingType)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByClothingTypeAndService(ClothingType clothingType, ServiceType service) {
        return jpaRepository.findByClothingTypeAndServiceAndActiveTrue(clothingType, service).isPresent()
                || jpaRepository.findAll().stream()
                    .anyMatch(entity -> entity.getClothingType() == clothingType && entity.getService() == service);
    }

    @Override
    public ken.vivid.domain.entities.service.ServicePrice save(ken.vivid.domain.entities.service.ServicePrice servicePrice) {
        ServicePriceJpaEntity entity = toEntity(servicePrice);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    private ken.vivid.domain.entities.service.ServicePrice toDomain(ServicePriceJpaEntity entity) {
        return ken.vivid.domain.entities.service.ServicePrice.create(
                entity.getId(),
                entity.getClothingType(),
                entity.getService(),
                entity.getPrice(),
                entity.isActive());
    }

    private ServicePriceJpaEntity toEntity(ken.vivid.domain.entities.service.ServicePrice servicePrice) {
        return ServicePriceJpaEntity.builder()
                .id(servicePrice.getId())
                .clothingType(servicePrice.getClothingType())
                .service(servicePrice.getService())
                .price(servicePrice.getPrice())
                .active(servicePrice.isActive())
                .build();
    }
}
