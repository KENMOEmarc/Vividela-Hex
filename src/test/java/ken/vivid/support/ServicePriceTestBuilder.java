package ken.vivid.support;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.serviceprice.ServicePrice;

import java.math.BigDecimal;

/**
 * Test builder for {@link ServicePrice}.
 * <p>
 * Defaults to an active entry: WASH for a SHIRT at 500, with id 1.
 */
public final class ServicePriceTestBuilder {

    private Long id = 1L;
    private ClothingType clothingType = ClothingType.SHIRT;
    private ServiceType service = ServiceType.WASH;
    private BigDecimal price = new BigDecimal("500");
    private boolean active = true;

    private ServicePriceTestBuilder() {
    }

    public static ServicePriceTestBuilder aServicePrice() {
        return new ServicePriceTestBuilder();
    }

    public ServicePriceTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ServicePriceTestBuilder withClothingType(ClothingType clothingType) {
        this.clothingType = clothingType;
        return this;
    }

    public ServicePriceTestBuilder withService(ServiceType service) {
        this.service = service;
        return this;
    }

    public ServicePriceTestBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ServicePriceTestBuilder withPrice(String price) {
        return withPrice(new BigDecimal(price));
    }

    public ServicePriceTestBuilder inactive() {
        this.active = false;
        return this;
    }

    public ServicePrice build() {
        return ServicePrice.create(id, clothingType, service, price, active);
    }
}
