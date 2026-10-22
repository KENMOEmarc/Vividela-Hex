package ken.vivid.adapter.output.persistence.jpaRepositories.article;

import ken.vivid.adapter.output.persistence.jpaEntities.article.ArticleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleJpaRepository extends JpaRepository<ArticleJpaEntity, Long> {
    List<ArticleJpaEntity> findByOrderIdOrderByIdAsc(Long orderId);
}
