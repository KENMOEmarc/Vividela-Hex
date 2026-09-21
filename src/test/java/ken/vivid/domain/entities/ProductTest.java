package ken.vivid.domain.entities;

import ken.vivid.domain.dto.MeasurementUnit;
import ken.vivid.domain.entities.product.Product;
import ken.vivid.support.ProductTestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static ken.vivid.support.ProductTestBuilder.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the domain entity {@link Product}:
 * no mocks, no framework, only JUnit 5 + AssertJ.
 * <p>
 * Goal: ensure that a {@code Product} can never exist in an
 * invalid state.
 */
@DisplayName("Product (domain)")
class ProductTest {

    @Test
    @DisplayName("Creates a valid product and exposes its invariants")
    void createProductShouldBuildAValidProduct() {
        Instant creation = Instant.now().minusSeconds(120);

        Product product = aProduct().withId(1L).withName("Farine T55").withThresholdValue(new BigDecimal("10.00"))
                .withMeasurementUnit(MeasurementUnit.KG).withCreatedAt(creation).withUpdatedAt(creation).build();

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Farine T55");
        // BigDecimal: always compare with isEqualByComparingTo (10 != 10.00 for equals)
        assertThat(product.getThresholdValue()).isEqualByComparingTo("10");
        assertThat(product.getMeasurementUnit()).isEqualTo(MeasurementUnit.KG);
        assertThat(product.getCreatedAt()).isEqualTo(creation);
        assertThat(product.getUpdatedAt()).isEqualTo(creation);
    }

    @Test
    @DisplayName("Rejects a null identifier")
    void createProductShouldRejectNullId() {
        assertThatThrownBy(() -> ProductTestBuilder.aProduct().withId(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID");
    }

    @ParameterizedTest(name = "invalid name: \"{0}\"")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Rejects a null or blank name")
    void createProductShouldRejectBlankOrNullName(String invalidName) {
        assertThatThrownBy(() -> ProductTestBuilder.aProduct().withName(invalidName).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name");
    }

    @Test
    @DisplayName("Rejects a null threshold")
    void createProductShouldRejectNullThreshold() {
        assertThatThrownBy(() -> ProductTestBuilder.aProduct().withThresholdValue((BigDecimal) null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Threshold");
    }

    @Test
    @DisplayName("Rejects a negative threshold")
    void createProductShouldRejectNegativeThreshold() {
        assertThatThrownBy(() -> ProductTestBuilder.aProduct().
                withThresholdValue(new BigDecimal("-0.01")).build()
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Threshold");
    }

    @Test
    @DisplayName("Accepts a threshold equal to zero (allowed lower bound)")
    void createProductShouldAcceptZeroThreshold() {
        Product product = aProduct().withThresholdValue(BigDecimal.ZERO).build();
        assertThat(product.getThresholdValue()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Rejects a null measurement unit")
    void createProductShouldRejectNullMeasurementUnit() {
        Instant now = Instant.now().minusSeconds(1);

        assertThatThrownBy(() -> ProductTestBuilder.aProduct().withMeasurementUnit(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Measurement unit");
    }

    @Test
    @DisplayName("Rejects a creation date in the future")
    void createProductShouldRejectCreationDateInTheFuture() {
        Instant future = Instant.now().plusSeconds(3600);

        assertThatThrownBy(() -> ProductTestBuilder.aProduct().withCreatedAt(future).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Created at");
    }

    @Test
    @DisplayName("Rejects an update date in the future")
    void createProductShouldRejectUpdateDateInTheFuture() {
        Instant future = Instant.now().plusSeconds(3600);

        assertThatThrownBy(() -> ProductTestBuilder.aProduct().withUpdatedAt(future).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Updated at");
    }

    @Test
    @DisplayName("The identifier is immutable, the other fields are mutable")
    void createProductShouldOnlyUpdateMutableFields() {
        Product product = aProduct().withId(7L).withName("Old name").build();
        Instant update = Instant.now();

        product.setName("New name");
        product.setThresholdValue(new BigDecimal("25"));
        product.setMeasurementUnit(MeasurementUnit.LITER);
        product.setUpdatedAt(update);

        assertThat(product.getId()).isEqualTo(7L); // final: no setter exposed
        assertThat(product.getName()).isEqualTo("New name");
        assertThat(product.getThresholdValue()).isEqualByComparingTo("25");
        assertThat(product.getMeasurementUnit()).isEqualTo(MeasurementUnit.LITER);
        assertThat(product.getUpdatedAt()).isEqualTo(update);
    }
}
