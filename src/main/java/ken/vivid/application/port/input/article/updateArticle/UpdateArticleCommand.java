package ken.vivid.application.port.input.article.updateArticle;

public record UpdateArticleCommand(Long articleId, String size, String fabric, String color, String distinction) {
}
