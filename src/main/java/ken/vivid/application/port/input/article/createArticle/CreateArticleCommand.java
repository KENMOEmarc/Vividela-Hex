package ken.vivid.application.port.input.article.createArticle;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;

import java.util.List;

public record CreateArticleCommand(Long orderId, ClothingType clothingType, String size, String fabric,
                                   String color, String distinction, List<ServiceType> services) {
}
