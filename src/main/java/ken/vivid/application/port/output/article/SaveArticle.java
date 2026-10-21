package ken.vivid.application.port.output.article;

import ken.vivid.domain.entities.article.Article;

public interface SaveArticle {
    Article save(Article article);
}
