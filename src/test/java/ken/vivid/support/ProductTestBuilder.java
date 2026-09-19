package ken.vivid.support;

import ken.vivid.domain.dto.MeasurementUnit;
import ken.vivid.domain.entities.Product;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Test builder for {@link Product}.
 * <p>
 * Avoids repeating the 6 positional parameters of {@code Product.createProduct}
 * in each test: only the fields relevant to the scenario under test are
 * overridden, the others remain on valid default values.
 */
public final class ProductTestBuilder {

    private Long id = 1L;
    private String name = "Test product";
    private BigDecimal thresholdValue = BigDecimal.TEN;
    private MeasurementUnit measurementUnit = MeasurementUnit.KG;
    private Instant createdAt = Instant.now().minusSeconds(3600);
    private Instant updatedAt = Instant.now().minusSeconds(60);

    private ProductTestBuilder() {
    }

    public static ProductTestBuilder aProduct() {
        return new ProductTestBuilder();
    }

    public ProductTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ProductTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductTestBuilder withThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
        return this;
    }

    public ProductTestBuilder withThresholdValue(String thresholdValue) {
        return withThresholdValue(new BigDecimal(thresholdValue));
    }

    public ProductTestBuilder withMeasurementUnit(MeasurementUnit measurementUnit) {
        this.measurementUnit = measurementUnit;
        return this;
    }

    public ProductTestBuilder withCreatedAt(Instant creation) {
        this.createdAt = creation;
        return this;
    }

    public ProductTestBuilder withUpdatedAt(Instant update) {
        this.updatedAt = update;
        return this;
    }

    public Product build() {
        return Product.createProduct(id, name, thresholdValue, measurementUnit, createdAt, updatedAt);
    }

}
