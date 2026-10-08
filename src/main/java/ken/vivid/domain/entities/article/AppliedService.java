package ken.vivid.domain.entities.article;

import ken.vivid.domain.dto.ServiceType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A service applied to an article, with the price frozen at the time it was
 * applied (maps to a row of {@code article_services}). Later changes in the
 * price list never alter it.
 */
public record AppliedService(ServiceType service, BigDecimal appliedPrice) {

    public AppliedService {
        if (service == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        if (appliedPrice == null || appliedPrice.signum() < 0) {
            throw new IllegalArgumentException("Applied price cannot be null or negative");
        }
        appliedPrice = appliedPrice.setScale(2, RoundingMode.HALF_UP);
    }
}
