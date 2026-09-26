package ken.vivid.adapter.input.web.output.persistence.adapter.product;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.product.ProductRegistrationJpaEntity;
import ken.vivid.adapter.input.web.output.persistence.jpaRepositories.product.ProductRegistrationJpaRepository;
import ken.vivid.application.port.output.product.SaveProductRegistration;
import ken.vivid.domain.entities.product.ProductRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRegistrationPersistenceAdapter implements SaveProductRegistration {

    private final ProductRegistrationJpaRepository jpaRepository;

    @Override
    public ProductRegistration save(ProductRegistration registration) {
        log.info("Persisting product registration for productId={} quantity={} type={}",
                registration.getProductId(), registration.getQuantity(), registration.getRegistrationType());
        ProductRegistrationJpaEntity saved = jpaRepository.save(toEntity(registration));
        return toDomain(saved);
    }

    private ProductRegistration toDomain(ProductRegistrationJpaEntity entity) {
        return ProductRegistration.createProductRegistration(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getRegistrationType(),
                entity.getNotes(),
                entity.getRegisteredAt()
        );
    }

    private ProductRegistrationJpaEntity toEntity(ProductRegistration registration) {
        return ProductRegistrationJpaEntity.builder()
                .id(registration.getId())
                .productId(registration.getProductId())
                .quantity(registration.getQuantity())
                .registrationType(registration.getRegistrationType())
                .notes(registration.getNotes())
                .registeredAt(registration.getRegisteredAt())
                .build();
    }
}
