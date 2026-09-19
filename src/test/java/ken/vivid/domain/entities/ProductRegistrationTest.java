package ken.vivid.domain.entities;

import ken.vivid.domain.dto.RegistrationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the domain entity {@link ProductRegistration}.
 */
@DisplayName("ProductRegistration (domain)")
class ProductRegistrationTest {

    private static final Instant NOW = Instant.now().minusSeconds(5);

    @Test
    @DisplayName("Creates a valid registration")
    void createProductRegistrationShouldBuildAValidRegistration() {
        ProductRegistration registration = ProductRegistration.createProductRegistration(
                1L, 42L, new BigDecimal("20"), RegistrationType.IN, "Supplier delivery", NOW);

        assertThat(registration.getId()).isEqualTo(1L);
        assertThat(registration.getProductId()).isEqualTo(42L);
        assertThat(registration.getQuantity()).isEqualByComparingTo("20");
        assertThat(registration.getRegistrationType()).isEqualTo(RegistrationType.IN);
        assertThat(registration.getNotes()).isEqualTo("Supplier delivery");
        assertThat(registration.getRegisteredAt()).isEqualTo(NOW);
    }

    @Test
    @DisplayName("Rejects a null identifier")
    void createProductRegistrationShouldRejectNullId() {
        assertThatThrownBy(() -> ProductRegistration.createProductRegistration(null, 42L,
                BigDecimal.ONE, RegistrationType.IN, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    @DisplayName("Rejects a null product identifier")
    void createProductRegistrationShouldRejectNullProductId() {
        assertThatThrownBy(() -> ProductRegistration.createProductRegistration(1L, null,
                BigDecimal.ONE, RegistrationType.IN, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product ID");
    }

    @Test
    @DisplayName("Rejects a null or negative quantity as reference value")
    void createProductRegistrationShouldRejectNullOrNegativeQuantity() {
        assertThatThrownBy(() -> ProductRegistration.createProductRegistration(1L, 42L,
                null, RegistrationType.IN, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");

        assertThatThrownBy(() -> ProductRegistration.createProductRegistration(1L, 42L,
                new BigDecimal("-5"), RegistrationType.IN, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");
    }

    @Test
    @DisplayName("Rejects a null registration type")
    void createProductRegistrationShouldRejectNullRegistrationType() {
        assertThatThrownBy(() -> ProductRegistration.createProductRegistration(1L, 42L,
                BigDecimal.ONE, null, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registration type");
    }

    @ParameterizedTest(name = "invalid note: \"{0}\"")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Rejects a null or blank note")
    void createProductRegistrationShouldRejectBlankNotes(String invalidNote) {
        assertThatThrownBy(() -> ProductRegistration.createProductRegistration(1L, 42L,
                BigDecimal.ONE, RegistrationType.ADJUSTMENT, invalidNote, NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notes");
    }

    @Test
    @DisplayName("Rejects a null or future registration date")
    void createProductRegistrationShouldRejectNullOrFutureRegisteredAt() {
        assertThatThrownBy(() -> ProductRegistration.createProductRegistration(1L, 42L,
                BigDecimal.ONE, RegistrationType.IN, "note", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registered at");

        assertThatThrownBy(() -> ProductRegistration.createProductRegistration(1L, 42L,
                BigDecimal.ONE, RegistrationType.IN, "note", Instant.now().plusSeconds(3600)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registered at");
    }
}