package ken.vivid.adapter.output.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the password hashing adapter.
 * <p>
 * No Spring context: the BCrypt encoder is instantiated directly and
 * injected through the constructor, exactly as {@code AppConfig} would do.
 */
@DisplayName("BCryptPasswordAdapter (output adapter)")
class BCryptPasswordAdapterTest {

    private final BCryptPasswordAdapter adapter = new BCryptPasswordAdapter(new BCryptPasswordEncoder());

    @Test
    @DisplayName("never returns the plain password")
    void hashShouldNeverReturnThePlainPassword() {
        String hash = adapter.hash("password123");

        assertThat(hash).isNotEqualTo("password123");
        assertThat(hash).doesNotContain("password123");
        assertThat(hash).startsWith("$2"); // BCrypt prefix
    }

    @Test
    @DisplayName("produces a different hash on each call (random salt)")
    void hashShouldBeNonDeterministicDueToSalt() {
        String first = adapter.hash("samePassword");
        String second = adapter.hash("samePassword");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("validates the correct password despite the random salt")
    void matchesShouldValidateTheCorrectPassword() {
        String hash = adapter.hash("password123");

        assertThat(adapter.matches("password123", hash)).isTrue();
    }

    @Test
    @DisplayName("rejects an incorrect password")
    void matchesShouldRejectAnIncorrectPassword() {
        String hash = adapter.hash("password123");

        assertThat(adapter.matches("wrongPassword", hash)).isFalse();
        assertThat(adapter.matches("Password123", hash)).isFalse(); // case sensitive
    }

    @Test
    @DisplayName("two identical passwords hashed separately both validate")
    void matchesShouldValidateBothHashesOfTheSamePassword() {
        String first = adapter.hash("samePassword");
        String second = adapter.hash("samePassword");

        assertThat(adapter.matches("samePassword", first)).isTrue();
        assertThat(adapter.matches("samePassword", second)).isTrue();
    }
}
