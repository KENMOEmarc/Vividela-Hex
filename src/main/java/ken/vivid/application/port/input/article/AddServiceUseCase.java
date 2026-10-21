package ken.vivid.application.port.input.article;

import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.article.Article;

public interface AddServiceUseCase {
    Article addService(Long articleId, ServiceType service);
}
