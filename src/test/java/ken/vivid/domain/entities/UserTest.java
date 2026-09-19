package ken.vivid.domain.entities;

import ken.vivid.domain.dto.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static ken.vivid.support.UserTestBuilder.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the domain entity {@link User}.
 */
@DisplayName("User (domain)")
class UserTest {

    @Test
    @DisplayName("Creates a valid user, active by default")
    void createUserShouldBuildAnActiveUser() {
        User user = User.createUser(1L, Role.EMPLOYEE, "Marc", "KENMOE",
                "ken47", "690000000", "marc@vividela.cm", "hash");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getRole()).isEqualTo(Role.EMPLOYEE);
        assertThat(user.getUserName()).isEqualTo("ken47");
        assertThat(user.getEmail()).isEqualTo("marc@vividela.cm");
        assertThat(user.getPassword()).isEqualTo("hash");
        assertThat(user.getActive()).isTrue(); // invariant: a new user is active
    }

    @Test
    @DisplayName("Allows a null identifier (user not yet persisted)")
    void createUserShouldAcceptNullId() {
        User user = aUser().withId(null).build();

        assertThat(user.getId()).isNull();
    }

    @ParameterizedTest(name = "invalid first name: \"{0}\"")
    @ValueSource(strings = {"", "   "})
    @DisplayName("Rejects a blank first name")
    void createUserShouldRejectBlankFirstName(String invalidFirstName) {
        assertThatThrownBy(() -> User.createUser(1L, Role.CUSTOMER, invalidFirstName, "KENMOE",
                "ken47", "690000000", "marc@vividela.cm", "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejects a blank last name")
    void createUserShouldRejectBlankLastName() {
        assertThatThrownBy(() -> User.createUser(1L, Role.CUSTOMER, "Marc", "  ",
                "ken47", "690000000", "marc@vividela.cm", "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejects a blank username")
    void createUserShouldRejectBlankUserName() {
        assertThatThrownBy(() -> User.createUser(1L, Role.CUSTOMER, "Marc", "KENMOE",
                "", "690000000", "marc@vividela.cm", "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejects a blank phone number")
    void createUserShouldRejectBlankPhone() {
        assertThatThrownBy(() -> User.createUser(1L, Role.CUSTOMER, "Marc", "KENMOE",
                "ken47", "", "marc@vividela.cm", "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejects a blank email")
    void createUserShouldRejectBlankEmail() {
        assertThatThrownBy(() -> User.createUser(1L, Role.CUSTOMER, "Marc", "KENMOE",
                "ken47", "690000000", "   ", "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rejects a blank password")
    void createUserShouldRejectBlankPassword() {
        assertThatThrownBy(() -> User.createUser(1L, Role.CUSTOMER, "Marc", "KENMOE",
                "ken47", "690000000", "marc@vividela.cm", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName(" A null field throws a IllegalArgumentException instead of an NullPointerException")
    void createUserShouldCurrentlyThrowIllegalArgumentExceptionOnNullFieldISnteadOfNullPointerException() {
        assertThatThrownBy(() -> User.createUser(1L, Role.CUSTOMER, null, "KENMOE",
                "ken47", "690000000", "marc@vividela.cm", "hash"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> User.createUser(1L, null, "Marc", "KENMOE",
                "ken47", "690000000", "marc@vividela.cm", "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Loyalty points are not initialized at creation")
    void loyaltyPointsShouldBeNullUntilExplicitlySet() {
        User user = aUser().build();

        assertThat(user.getLoyaltyPoints()).isNull();

        user.setLoyaltyPoints(120);

        assertThat(user.getLoyaltyPoints()).isEqualTo(120);
    }
}