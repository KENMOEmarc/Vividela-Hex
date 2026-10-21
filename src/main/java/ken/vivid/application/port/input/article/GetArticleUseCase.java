package ken.vivid.application.port.input.article;

import ken.vivid.domain.entities.article.Article;

import java.util.List;

public interface GetArticleUseCase {
    Article getById(Long id);
    List<Article> getAll();
    List<Article> getByOrderId(Long orderId);
}
