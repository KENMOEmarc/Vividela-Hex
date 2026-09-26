package ken.vivid.adapter.input.web.output.persistence.jpaRepositories.product;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.product.ProductRegistrationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRegistrationJpaRepository extends JpaRepository<ProductRegistrationJpaEntity, Long> {
}
