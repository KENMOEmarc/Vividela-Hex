package ken.vivid.domain.entities.service;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.serviceprice.ServicePrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static ken.vivid.support.ServicePriceTestBuilder.aServicePrice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the domain entity {@link ServicePrice}: no mocks, no
 * framework, only JUnit 5 + AssertJ.
 * <p>
 * Goal: ensure that a price list entry can never hold an unusable price
 * (null, zero, negative, too large for the DECIMAL(10,2) column).
 */
@DisplayName("ServicePrice (domain)")
class ServicePriceTest {

    @Test
    @DisplayName("The maximum price matches the DECIMAL(10,2) column of 'service_prices'")
    void maxPriceShouldMatchTheDatabaseColumn() {
        assertThat(ServicePrice.MAX_PRICE.toString()).isEqualTo("99999999.99");
    }

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("Creates a valid entry and exposes its attributes")
        void createShouldBuildAValidEntry() {
            ServicePrice servicePrice = aServicePrice().withId(12L).withClothingType(ClothingType.COAT)
                    .withService(ServiceType.DRY_CLEAN).withPrice("4500").build();

            assertThat(servicePrice.getId()).isEqualTo(12L);
            assertThat(servicePrice.getClothingType()).isEqualTo(ClothingType.COAT);
            assertThat(servicePrice.getService()).isEqualTo(ServiceType.DRY_CLEAN);
            assertThat(servicePrice.getPrice().toString()).isEqualTo("4500.00");
            assertThat(servicePrice.isActive()).isTrue();
        }

        @Test
        @DisplayName("Accepts a null identifier (entry not persisted yet)")
        void createShouldAcceptANullId() {
            assertThat(aServicePrice().withId(null).build().getId()).isNull();
        }

        @Test
        @DisplayName("Can be created inactive")
        void createShouldAcceptAnInactiveEntry() {
            assertThat(aServicePrice().inactive().build().isActive()).isFalse();
        }

        @ParameterizedTest(name = "{0} is stored as {1}")
        @CsvSource({
                "500,500.00", "500.5,500.50", "0.01,0.01", "10.004,10.00", "10.005,10.01", "0.005,0.01",
                "99999999.99,99999999.99", "99999999.994,99999999.99"
        })
        @DisplayName("Rounds the price to 2 decimals (HALF_UP)")
        void createShouldRoundThePriceToTwoDecimals(String given, String expected) {
            // toString (not isEqualByComparingTo): the scale itself is part of the contract
            assertThat(aServicePrice().withPrice(given).build().getPrice().toString()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "price {0} is rejected")
        @ValueSource(strings = {"0", "0.00", "0.004", "-1", "-0.01"})
        @DisplayName("Rejects a zero or negative price (a free service is modelled by deactivating the entry)")
        void createShouldRejectANonPositivePrice(String invalidPrice) {
            assertThatThrownBy(() -> aServicePrice().withPrice(invalidPrice).build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("strictly positive");
        }

        @ParameterizedTest(name = "price {0} is rejected")
        @ValueSource(strings = {"100000000", "99999999.995", "1000000000.00"})
        @DisplayName("Rejects a price that does not fit the database column")
        void createShouldRejectAPriceAboveTheMaximum(String tooLarge) {
            assertThatThrownBy(() -> aServicePrice().withPrice(tooLarge).build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("99999999.99");
        }

        @Test
        @DisplayName("Rejects a null price")
        void createShouldRejectANullPrice() {
            assertThatThrownBy(() -> aServicePrice().withPrice((BigDecimal) null).build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Price");
        }

        @Test
        @DisplayName("Rejects a null clothing type")
        void createShouldRejectANullClothingType() {
            assertThatThrownBy(() -> aServicePrice().withClothingType(null).build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Clothing type");
        }

        @Test
        @DisplayName("Rejects a null service")
        void createShouldRejectANullService() {
            assertThatThrownBy(() -> aServicePrice().withService(null).build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Service");
        }
    }

    @Nested
    @DisplayName("ChangePrice")
    class ChangePrice {

        @Test
        @DisplayName("Replaces the price, rounded to 2 decimals")
        void changePriceShouldReplaceThePrice() {
            ServicePrice servicePrice = aServicePrice().withPrice("500").build();

            servicePrice.changePrice(new BigDecimal("650.505"));

            assertThat(servicePrice.getPrice().toString()).isEqualTo("650.51");
        }

        @Test
        @DisplayName("Leaves the entry's identity and activity untouched")
        void changePriceShouldNotTouchTheOtherFields() {
            ServicePrice servicePrice = aServicePrice().withId(3L).withClothingType(ClothingType.PANTS)
                    .withService(ServiceType.IRON).inactive().build();

            servicePrice.changePrice(new BigDecimal("700"));

            assertThat(servicePrice.getId()).isEqualTo(3L);
            assertThat(servicePrice.getClothingType()).isEqualTo(ClothingType.PANTS);
            assertThat(servicePrice.getService()).isEqualTo(ServiceType.IRON);
            assertThat(servicePrice.isActive()).isFalse();
        }

        @ParameterizedTest(name = "price {0} is rejected")
        @ValueSource(strings = {"0", "-5", "100000000"})
        @DisplayName("Rejects an invalid new price and keeps the current one")
        void changePriceShouldRejectInvalidPricesAndKeepTheOldOne(String invalidPrice) {
            ServicePrice servicePrice = aServicePrice().withPrice("500").build();

            assertThatThrownBy(() -> servicePrice.changePrice(new BigDecimal(invalidPrice)))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(servicePrice.getPrice().toString()).isEqualTo("500.00");
        }

        @Test
        @DisplayName("Rejects a null price and keeps the current one")
        void changePriceShouldRejectNull() {
            ServicePrice servicePrice = aServicePrice().withPrice("500").build();

            assertThatThrownBy(() -> servicePrice.changePrice(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Price");

            assertThat(servicePrice.getPrice().toString()).isEqualTo("500.00");
        }
    }

    @Nested
    @DisplayName("Activation")
    class Activation {

        @Test
        @DisplayName("Deactivates an active entry")
        void deactivateShouldTurnTheEntryOff() {
            ServicePrice servicePrice = aServicePrice().build();

            servicePrice.deactivate();

            assertThat(servicePrice.isActive()).isFalse();
        }

        @Test
        @DisplayName("Activates an inactive entry")
        void activateShouldTurnTheEntryOn() {
            ServicePrice servicePrice = aServicePrice().inactive().build();

            servicePrice.activate();

            assertThat(servicePrice.isActive()).isTrue();
        }

        @Test
        @DisplayName("Is idempotent: repeating the same operation changes nothing")
        void activateAndDeactivateShouldBeIdempotent() {
            ServicePrice servicePrice = aServicePrice().build();

            servicePrice.activate();
            assertThat(servicePrice.isActive()).isTrue();

            servicePrice.deactivate();
            servicePrice.deactivate();
            assertThat(servicePrice.isActive()).isFalse();
        }

        @Test
        @DisplayName("Keeps the price when toggled")
        void togglingShouldKeepThePrice() {
            ServicePrice servicePrice = aServicePrice().withPrice("1200").build();

            servicePrice.deactivate();
            servicePrice.activate();

            assertThat(servicePrice.getPrice().toString()).isEqualTo("1200.00");
        }
    }
}
