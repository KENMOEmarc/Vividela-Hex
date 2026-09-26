package ken.vivid.adapter.input.web.output.persistence.jpaRepositories.product;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.product.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    Optional<ProductJpaEntity> findByName(String name);

    boolean existsByName(String name);
}
