package ken.vivid.application.port.input.article.updateArticle;

import ken.vivid.domain.entities.article.Article;

public interface UpdateArticleUseCase {
    Article update(UpdateArticleCommand command);
}
