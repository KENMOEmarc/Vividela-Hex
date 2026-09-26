package ken.vivid.adapter.output.token;

import ken.vivid.adapter.input.web.output.token.JwtTokenAdapter;
import ken.vivid.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static ken.vivid.support.UserTestBuilder.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the JWT adapter. No database, no Spring context:
 * the properties normally injected by {@code @Value} are set directly,
 * then the key initialization is triggered manually.
 * <p>
 * The adapter is critical for security: signature, expiration and
 * token tampering are explicitly covered.
 */
@DisplayName("JwtTokenAdapter (output adapter)")
class JwtTokenAdapterTest {

    private static final String TEST_SECRET = "test-key-vividela-long-enough-for-hs256";
    private static final long ONE_HOUR_IN_MILLIS = 3_600_000L;

    private JwtTokenAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = adapterWith(TEST_SECRET, ONE_HOUR_IN_MILLIS);
    }

    @Test
    @DisplayName("rejects a secret that is too short for HS256 (less than 32 bytes)")
    void validateAndInitSecretShouldRejectTooShortSecret() {
        JwtTokenAdapter invalidAdapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(invalidAdapter, "secret", "too-short");
        ReflectionTestUtils.setField(invalidAdapter, "expirationMillis", ONE_HOUR_IN_MILLIS);

        assertThatThrownBy(invalidAdapter::validateAndInitSecret)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("jwt.secret");
    }

    @Test
    @DisplayName("generates a non-empty token made of three segments")
    void generateTokenShouldProduceAWellFormedJwt() {
        String token = adapter.generateToken(aUser().build());

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    /*
     * Watch out for the port naming: validateAndExtractEmail actually
     * returns the token "subject", which is populated with the user
     * name (generateToken uses user.getUserName()), and not with
     * the email. This test documents the actual contract.
     */
    @Test
    @DisplayName("a valid token returns the subject, which is the user name")
    void generateTokenThenValidateShouldRoundTripTheUserName() {
        User user = aUser().withUserName("mkemgang").withEmail("marc@vividela.cm").build();

        String token = adapter.generateToken(user);

        assertThat(adapter.validateAndExtractEmail(token)).contains("mkemgang");
    }

    @Test
    @DisplayName("a tampered token is rejected without exception")
    void validateAndExtractEmailShouldReturnEmptyForATamperedToken() {
        String token = adapter.generateToken(aUser().build());

        Optional<String> result = adapter.validateAndExtractEmail(token + "x");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("a token signed with another key is rejected")
    void validateAndExtractEmailShouldReturnEmptyForATokenSignedWithAnotherKey() {
        JwtTokenAdapter otherAdapter = adapterWith(
                "another-test-key-vividela-just-as-long", ONE_HOUR_IN_MILLIS);
        String foreignToken = otherAdapter.generateToken(aUser().build());

        assertThat(adapter.validateAndExtractEmail(foreignToken)).isEmpty();
    }

    @Test
    @DisplayName("an expired token is rejected")
    void validateAndExtractEmailShouldReturnEmptyForAnExpiredToken() {
        JwtTokenAdapter expiredAdapter = adapterWith(TEST_SECRET, -1_000L);
        String expiredToken = expiredAdapter.generateToken(aUser().build());

        assertThat(expiredAdapter.validateAndExtractEmail(expiredToken)).isEmpty();
    }

    @Test
    @DisplayName("a string that is not a token is rejected")
    void validateAndExtractEmailShouldReturnEmptyForGarbageInput() {
        assertThat(adapter.validateAndExtractEmail("not-a-token-at-all")).isEmpty();
        assertThat(adapter.validateAndExtractEmail("")).isEmpty();
    }

    @Test
    @DisplayName("exposes the configured validity duration")
    void getExpirationMillisShouldReturnTheConfiguredValue() {
        assertThat(adapter.getExpirationMillis()).isEqualTo(ONE_HOUR_IN_MILLIS);
    }

    private static JwtTokenAdapter adapterWith(String secret, long expirationMillis) {
        JwtTokenAdapter adapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(adapter, "secret", secret);
        ReflectionTestUtils.setField(adapter, "expirationMillis", expirationMillis);
        adapter.validateAndInitSecret();
        return adapter;
    }
}