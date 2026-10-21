package ken.vivid.support;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.article.AppliedService;
import ken.vivid.domain.entities.article.Article;
import ken.vivid.domain.dto.ArticleStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Test builder for {@link Article}.
 * <p>
 * Defaults describe a valid PENDING shirt of order 10 carrying a single WASH
 * service at 500. Only the fields relevant to a scenario are overridden.
 */
public final class ArticleTestBuilder {

    private Long id = 1L;
    private Long orderId = 10L;
    private ClothingType clothingType = ClothingType.SHIRT;
    private String size = "M";
    private String fabric = "COTTON";
    private String color = "Blue";
    private String distinction = null;
    private ArticleStatus status = ArticleStatus.PENDING;
    private List<AppliedService> services = new ArrayList<>(List.of(applied(ServiceType.WASH, "500")));

    private ArticleTestBuilder() {
    }

    public static ArticleTestBuilder anArticle() {
        return new ArticleTestBuilder();
    }

    /** Shortcut to build an {@link AppliedService} from a plain price string. */
    public static AppliedService applied(ServiceType service, String price) {
        return new AppliedService(service, new BigDecimal(price));
    }

    public ArticleTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ArticleTestBuilder withOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public ArticleTestBuilder withClothingType(ClothingType clothingType) {
        this.clothingType = clothingType;
        return this;
    }

    public ArticleTestBuilder withSize(String size) {
        this.size = size;
        return this;
    }

    public ArticleTestBuilder withFabric(String fabric) {
        this.fabric = fabric;
        return this;
    }

    public ArticleTestBuilder withColor(String color) {
        this.color = color;
        return this;
    }

    public ArticleTestBuilder withDistinction(String distinction) {
        this.distinction = distinction;
        return this;
    }

    public ArticleTestBuilder withStatus(ArticleStatus status) {
        this.status = status;
        return this;
    }

    public ArticleTestBuilder withServices(List<AppliedService> services) {
        this.services = services;
        return this;
    }

    public Article build() {
        return Article.create(id, orderId, clothingType, size, fabric, color, distinction, status, services);
    }
}
