package ken.vivid.domain.entities;

import ken.vivid.domain.dto.MeasurementUnit;
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
    @DisplayName("creates a valid product and exposes its invariants")
    void createProductShouldBuildAValidProduct() {
        Instant creation = Instant.now().minusSeconds(120);

        Product product = Product.createProduct(1L, "Farine T55", new BigDecimal("10.00"),
                MeasurementUnit.KG, creation, creation);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Farine T55");
        // BigDecimal: always compare with isEqualByComparingTo (10 != 10.00 for equals)
        assertThat(product.getThresholdValue()).isEqualByComparingTo("10");
        assertThat(product.getMeasurementUnit()).isEqualTo(MeasurementUnit.KG);
        assertThat(product.getCreatedAt()).isEqualTo(creation);
        assertThat(product.getUpdatedAt()).isEqualTo(creation);
    }

    @Test
    @DisplayName("rejects a null identifier")
    void createProductShouldRejectNullId() {
        Instant now = Instant.now().minusSeconds(1);

        assertThatThrownBy(() -> Product.createProduct(null, "Farine", BigDecimal.TEN,
                MeasurementUnit.KG, now, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID");
    }

    @ParameterizedTest(name = "invalid name: \"{0}\"")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("rejects a null or blank name")
    void createProductShouldRejectBlankOrNullName(String invalidName) {
        Instant now = Instant.now().minusSeconds(1);

        assertThatThrownBy(() -> Product.createProduct(1L, invalidName, BigDecimal.TEN,
                MeasurementUnit.KG, now, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name");
    }

    @Test
    @DisplayName("rejects a null threshold")
    void createProductShouldRejectNullThreshold() {
        Instant now = Instant.now().minusSeconds(1);

        assertThatThrownBy(() -> Product.createProduct(1L, "Farine", null,
                MeasurementUnit.KG, now, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Threshold");
    }

    @Test
    @DisplayName("rejects a negative threshold")
    void createProductShouldRejectNegativeThreshold() {
        Instant now = Instant.now().minusSeconds(1);

        assertThatThrownBy(() -> Product.createProduct(1L, "Farine", new BigDecimal("-0.01"),
                MeasurementUnit.KG, now, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Threshold");
    }

    @Test
    @DisplayName("accepts a threshold equal to zero (allowed lower bound)")
    void createProductShouldAcceptZeroThreshold() {
        Product product = aProduct().withThresholdValue(BigDecimal.ZERO).build();

        assertThat(product.getThresholdValue()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("rejects a null measurement unit")
    void createProductShouldRejectNullMeasurementUnit() {
        Instant now = Instant.now().minusSeconds(1);

        assertThatThrownBy(() -> Product.createProduct(1L, "Farine", BigDecimal.TEN,
                null, now, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Measurement unit");
    }

    @Test
    @DisplayName("rejects a creation date in the future")
    void createProductShouldRejectCreationDateInTheFuture() {
        Instant future = Instant.now().plusSeconds(3600);

        assertThatThrownBy(() -> Product.createProduct(1L, "Farine", BigDecimal.TEN,
                MeasurementUnit.KG, future, Instant.now().minusSeconds(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Created at");
    }

    @Test
    @DisplayName("rejects an update date in the future")
    void createProductShouldRejectUpdateDateInTheFuture() {
        Instant now = Instant.now().minusSeconds(1);
        Instant future = Instant.now().plusSeconds(3600);

        assertThatThrownBy(() -> Product.createProduct(1L, "Farine", BigDecimal.TEN,
                MeasurementUnit.KG, now, future))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Updated at");
    }

    @Test
    @DisplayName("the identifier is immutable, the other fields are mutable")
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
