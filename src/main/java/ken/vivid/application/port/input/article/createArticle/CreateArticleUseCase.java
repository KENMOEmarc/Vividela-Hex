package ken.vivid.application.port.input.article.createArticle;

import ken.vivid.domain.entities.article.Article;

public interface CreateArticleUseCase {
    Article create(CreateArticleCommand command);
}
