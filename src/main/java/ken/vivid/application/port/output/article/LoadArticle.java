package ken.vivid.application.port.output.article;

import ken.vivid.domain.entities.article.Article;

import java.util.List;
import java.util.Optional;

public interface LoadArticle {
    Optional<Article> loadById(Long id);
    List<Article> loadAll();
    List<Article> loadByOrderId(Long orderId);
}
