package ken.vivid.adapter.output.persistence.adapter.product;

import ken.vivid.adapter.output.persistence.jpaEntities.product.ProductJpaEntity;
import ken.vivid.adapter.output.persistence.jpaRepositories.product.ProductJpaRepository;
import ken.vivid.application.port.output.product.DeleteProduct;
import ken.vivid.application.port.output.product.LoadProduct;
import ken.vivid.application.port.output.product.SaveProduct;
import ken.vivid.domain.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements LoadProduct, SaveProduct, DeleteProduct {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Optional<Product> loadById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Product> loadByName(String name) {
        return jpaRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public List<Product> loadAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity saved = jpaRepository.save(toEntity(product));
        return toDomain(saved);
    }

    @Override
    public void delete(Long id) {
       jpaRepository.deleteById(id);
    }

    private Product toDomain(ProductJpaEntity entity) {
        return Product.createProduct(
                entity.getId(),
                entity.getName(),
                entity.getThresholdValue(),
                entity.getMeasurementUnit(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ProductJpaEntity toEntity(Product product) {
        return ProductJpaEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .thresholdValue(product.getThresholdValue())
                .measurementUnit(product.getMeasurementUnit())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
