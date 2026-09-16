package ken.vivid.adapter.output.persistence.repositories;

import ken.vivid.adapter.output.persistence.entities.ProductRegistrationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRegistrationJpaRepository extends JpaRepository<ProductRegistrationJpaEntity, Long> {
}
