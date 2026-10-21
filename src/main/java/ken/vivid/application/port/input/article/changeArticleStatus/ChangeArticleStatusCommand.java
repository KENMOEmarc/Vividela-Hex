package ken.vivid.application.port.input.article.changeArticleStatus;

import ken.vivid.domain.dto.ArticleStatus;

public record ChangeArticleStatusCommand(Long articleId, ArticleStatus status) {
}
