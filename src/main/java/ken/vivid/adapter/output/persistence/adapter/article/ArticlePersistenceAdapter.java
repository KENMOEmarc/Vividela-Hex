package ken.vivid.adapter.output.persistence.adapter.article;

import ken.vivid.adapter.output.persistence.jpaEntities.article.ArticleJpaEntity;
import ken.vivid.adapter.output.persistence.jpaEntities.article.ArticleServiceJpaEmbeddable;
import ken.vivid.adapter.output.persistence.jpaRepositories.article.ArticleJpaRepository;
import ken.vivid.application.port.output.article.DeleteArticle;
import ken.vivid.application.port.output.article.LoadArticle;
import ken.vivid.application.port.output.article.SaveArticle;
import ken.vivid.domain.entities.article.AppliedService;
import ken.vivid.domain.entities.article.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArticlePersistenceAdapter implements LoadArticle, SaveArticle, DeleteArticle {

    private final ArticleJpaRepository repository;

    @Override
    public Optional<Article> loadById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Article> loadAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Article> loadByOrderId(Long orderId) {
        return repository.findByOrderIdOrderByIdAsc(orderId).stream().map(this::toDomain).toList();
    }

    @Override
    public Article save(Article article) {
        return toDomain(repository.save(toEntity(article)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private Article toDomain(ArticleJpaEntity entity) {
        List<AppliedService> services = entity.getServices() == null ? List.of() :
                entity.getServices().stream()
                        .map(service -> new AppliedService(service.getService(), service.getAppliedPrice()))
                        .toList();

        return Article.create(
                entity.getId(),
                entity.getOrderId(),
                entity.getClothingType(),
                entity.getSize(),
                entity.getFabric(),
                entity.getColor(),
                entity.getDistinction(),
                entity.getStatus(),
                services
        );
    }

    private ArticleJpaEntity toEntity(Article article) {
        ArticleJpaEntity entity = ArticleJpaEntity.builder()
                .id(article.getId())
                .orderId(article.getOrderId())
                .clothingType(article.getClothingType())
                .size(article.getSize())
                .fabric(article.getFabric())
                .color(article.getColor())
                .distinction(article.getDistinction())
                .status(article.getStatus())
                .build();

        if (article.getServices() != null) {
            List<ArticleServiceJpaEmbeddable> services = article.getServices().stream()
                    .map(service -> ArticleServiceJpaEmbeddable.builder()
                            .service(service.service())
                            .appliedPrice(service.appliedPrice())
                            .build())
                    .toList();
            entity.setServices(services);
        }

        return entity;
    }
}
