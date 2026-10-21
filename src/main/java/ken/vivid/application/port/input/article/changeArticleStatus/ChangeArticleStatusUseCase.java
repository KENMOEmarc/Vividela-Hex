package ken.vivid.application.port.input.article.changeArticleStatus;

import ken.vivid.domain.entities.article.Article;

public interface ChangeArticleStatusUseCase {
    Article changeStatus(ChangeArticleStatusCommand command);
}
