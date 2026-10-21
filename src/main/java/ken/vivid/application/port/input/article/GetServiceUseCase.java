package ken.vivid.application.port.input.article;

import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.article.AppliedService;

import java.util.List;

public interface GetServiceUseCase {
    AppliedService getService(Long articleId, ServiceType service);
    List<AppliedService> getAllServices(Long articleId);
}
