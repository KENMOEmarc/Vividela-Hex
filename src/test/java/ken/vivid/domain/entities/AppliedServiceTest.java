package ken.vivid.domain.entities;

import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.article.AppliedService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the value object {@link AppliedService}: a service
 * applied to an article with its frozen price. No mocks, no framework.
 */
@DisplayName("AppliedService (domain)")
class AppliedServiceTest {

    @Test
    @DisplayName("Keeps the service and the price it was created with")
    void shouldExposeServiceAndPrice() {
        AppliedService applied = new AppliedService(ServiceType.DRY_CLEAN, new BigDecimal("1500.50"));

        assertThat(applied.service()).isEqualTo(ServiceType.DRY_CLEAN);
        assertThat(applied.appliedPrice()).isEqualByComparingTo("1500.50");
    }

    @ParameterizedTest(name = "{0} is stored as {1}")
    @CsvSource({"500,500.00", "500.5,500.50", "500.00,500.00", "10.004,10.00", "10.005,10.01", "0.999,1.00"})
    @DisplayName("Rounds the price to 2 decimals (HALF_UP), like the DECIMAL(10,2) column")
    void shouldRoundThePriceToTwoDecimals(String given, String expected) {
        AppliedService applied = new AppliedService(ServiceType.WASH, new BigDecimal(given));

        // toString (not isEqualByComparingTo): the scale itself is part of the contract
        assertThat(applied.appliedPrice().toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Considers 500 and 500.00 as the same applied service")
    void shouldBeEqualWhenOnlyTheScaleOfThePriceDiffers() {
        AppliedService withoutDecimals = new AppliedService(ServiceType.WASH, new BigDecimal("500"));
        AppliedService withDecimals = new AppliedService(ServiceType.WASH, new BigDecimal("500.00"));

        assertThat(withoutDecimals).isEqualTo(withDecimals).hasSameHashCodeAs(withDecimals);
    }

    @Test
    @DisplayName("Distinguishes two services or two prices")
    void shouldNotBeEqualWhenServiceOrPriceDiffers() {
        AppliedService wash = new AppliedService(ServiceType.WASH, new BigDecimal("500"));

        assertThat(wash).isNotEqualTo(new AppliedService(ServiceType.IRON, new BigDecimal("500")));
        assertThat(wash).isNotEqualTo(new AppliedService(ServiceType.WASH, new BigDecimal("501")));
    }

    @Test
    @DisplayName("Accepts a zero price (a free service)")
    void shouldAcceptAZeroPrice() {
        AppliedService applied = new AppliedService(ServiceType.WASH, BigDecimal.ZERO);

        assertThat(applied.appliedPrice().toString()).isEqualTo("0.00");
    }

    @Test
    @DisplayName("Rejects a negative price")
    void shouldRejectANegativePrice() {
        assertThatThrownBy(() -> new AppliedService(ServiceType.WASH, new BigDecimal("-0.01")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");
    }

    @Test
    @DisplayName("Rejects a null price")
    void shouldRejectANullPrice() {
        assertThatThrownBy(() -> new AppliedService(ServiceType.WASH, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");
    }

    @Test
    @DisplayName("Rejects a null service")
    void shouldRejectANullService() {
        assertThatThrownBy(() -> new AppliedService(null, new BigDecimal("500")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service");
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(ServiceType.class)
    @DisplayName("Can be created for every service of the catalogue")
    void shouldAcceptEveryServiceType(ServiceType service) {
        assertThat(new AppliedService(service, BigDecimal.TEN).service()).isEqualTo(service);
    }
}
