package ken.vivid.adapter.output.persistence.jpaRepositories.product;

import ken.vivid.adapter.output.persistence.jpaEntities.product.StockJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StockJpaRepository extends JpaRepository<StockJpaEntity, Long> {

    List<StockJpaEntity> findByProductIdAndCurrentQuantityGreaterThanOrderByExpirationDateAsc(
            Long productId, BigDecimal quantity);

    //Find stock with all product
    @Query("SELECT s FROM StockJpaEntity s LEFT JOIN FETCH s.productId")
    List<StockJpaEntity> findAllWithProduct();

    @Query("SELECT COALESCE(SUM(s.currentQuantity), 0) FROM StockJpaEntity s WHERE s.productId = :productId")
    BigDecimal sumCurrentQuantityByProductId(@Param("productId") Long productId);
}
