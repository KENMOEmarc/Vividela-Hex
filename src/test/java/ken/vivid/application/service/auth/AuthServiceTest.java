package ken.vivid.application.service.auth;

import ken.vivid.adapter.exception.auth.InvalidCredentialsException;
import ken.vivid.adapter.exception.auth.UserAlreadyExistsException;
import ken.vivid.application.port.input.auth.saveUser.LoginCommand;
import ken.vivid.application.port.input.auth.saveUser.StoreCommand;
import ken.vivid.application.port.output.auth.LoadUser;
import ken.vivid.application.port.output.auth.PasswordEncoder;
import ken.vivid.application.port.output.auth.SaveUser;
import ken.vivid.application.port.output.auth.TokenBlacklist;
import ken.vivid.application.port.output.auth.TokenGenerator;
import ken.vivid.domain.dto.Role;
import ken.vivid.domain.entities.AuthResult;
import ken.vivid.domain.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static ken.vivid.support.UserTestBuilder.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link AuthService}: output ports are mocked,
 * no Spring context, no real JWT or BCrypt implementation.
 * <p>
 * This is the layer where the most expensive security bugs hide:
 * every authentication failure path is explicitly covered.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService (application)")
class AuthServiceTest {

    @Mock
    private LoadUser loadUser;
    @Mock
    private SaveUser saveUser;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenGenerator tokenGenerator;
    @Mock
    private TokenBlacklist tokenBlacklist;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("fails when the user is not found, without generating a token")
        void loginShouldThrowInvalidCredentialsWhenUserNotFound() {
            given(loadUser.loadByEmailOrUserName("unknown@vividela.cm")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(new LoginCommand("unknown@vividela.cm", "secret")))
                    .isInstanceOf(InvalidCredentialsException.class);

            verifyNoInteractions(tokenGenerator, passwordEncoder);
        }

        @Test
        @DisplayName("fails when the password does not match")
        void loginShouldThrowInvalidCredentialsWhenPasswordDoesNotMatch() {
            User user = aUser().withPassword("storedHash").build();
            given(loadUser.loadByEmailOrUserName("marc@vividela.cm")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("wrong", "storedHash")).willReturn(false);

            assertThatThrownBy(() -> authService.login(new LoginCommand("marc@vividela.cm", "wrong")))
                    .isInstanceOf(InvalidCredentialsException.class);

            verifyNoInteractions(tokenGenerator);
        }

        @Test
        @DisplayName("fails when the account is disabled, even with the correct password")
        void loginShouldThrowInvalidCredentialsWhenAccountIsInactive() {
            User user = aUser().withPassword("storedHash").inactive().build();
            given(loadUser.loadByEmailOrUserName("marc@vividela.cm")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("goodPassword", "storedHash")).willReturn(true);

            assertThatThrownBy(() -> authService.login(new LoginCommand("marc@vividela.cm", "goodPassword")))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessageContaining("not active");

            verifyNoInteractions(tokenGenerator);
        }

        @Test
        @DisplayName("returns a token and its validity duration when credentials are valid")
        void loginShouldReturnAuthResultWhenCredentialsAreValid() {
            User user = aUser().withPassword("storedHash").build();
            given(loadUser.loadByEmailOrUserName("marc@vividela.cm")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("goodPassword", "storedHash")).willReturn(true);
            given(tokenGenerator.generateToken(user)).willReturn("jwt-token");
            given(tokenGenerator.getExpirationMillis()).willReturn(3_600_000L);

            AuthResult result = authService.login(new LoginCommand("marc@vividela.cm", "goodPassword"));

            assertThat(result.token()).isEqualTo("jwt-token");
            assertThat(result.expiresInMillis()).isEqualTo(3_600_000L);
            assertThat(result.user()).isSameAs(user);
        }

        @Test
        @DisplayName("also accepts the user name as a login identifier")
        void loginShouldAcceptUserNameAsIdentifier() {
            User user = aUser().withPassword("storedHash").build();
            given(loadUser.loadByEmailOrUserName("mkemgang")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("goodPassword", "storedHash")).willReturn(true);
            given(tokenGenerator.generateToken(user)).willReturn("jwt-token");
            given(tokenGenerator.getExpirationMillis()).willReturn(1_000L);

            AuthResult result = authService.login(new LoginCommand("mkemgang", "goodPassword"));

            assertThat(result.token()).isEqualTo("jwt-token");
        }
    }

    @Nested
    @DisplayName("logout")
    class Logout {

        @Test
        @DisplayName("revokes the provided token")
        void logoutShouldRevokeTheToken() {
            authService.logout("jwt-token");

            verify(tokenBlacklist).revoke("jwt-token");
        }
    }

    @Nested
    @DisplayName("getCurrentUser")
    class GetCurrentUser {

        @Test
        @DisplayName("returns the user matching the identifier")
        void getCurrentUserShouldReturnTheMatchingUser() {
            User user = aUser().build();
            given(loadUser.loadByEmailOrUserName("marc@vividela.cm")).willReturn(Optional.of(user));

            assertThat(authService.getCurrentUser("marc@vividela.cm")).isSameAs(user);
        }

        @Test
        @DisplayName("fails when the authenticated user no longer exists")
        void getCurrentUserShouldThrowWhenUserNoLongerExists() {
            given(loadUser.loadByEmailOrUserName("deleted@vividela.cm")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.getCurrentUser("deleted@vividela.cm"))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("register / store")
    class Register {

        @Test
        @DisplayName("rejects a registration with an email already in use")
        void registerShouldThrowWhenEmailAlreadyExists() {
            given(loadUser.existsByEmail("marc@vividela.cm")).willReturn(true);

            assertThatThrownBy(() -> authService.register(registrationCommand(Role.CUSTOMER)))
                    .isInstanceOf(UserAlreadyExistsException.class);

            verifyNoInteractions(saveUser, tokenGenerator, passwordEncoder);
        }

        @Test
        @DisplayName("hashes the password before saving the user")
        void registerShouldHashThePasswordBeforeSaving() {
            given(loadUser.existsByEmail("marc@vividela.cm")).willReturn(false);
            given(passwordEncoder.hash("plainPassword")).willReturn("bcryptHash");
            given(saveUser.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
            given(tokenGenerator.generateToken(any(User.class))).willReturn("jwt-token");
            given(tokenGenerator.getExpirationMillis()).willReturn(3_600_000L);

            authService.register(registrationCommand(Role.CUSTOMER));

            verify(saveUser).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("bcryptHash");
            assertThat(userCaptor.getValue().getPassword()).isNotEqualTo("plainPassword");
        }

        @Test
        @DisplayName("forces the CUSTOMER role regardless of the requested role (public registration)")
        void registerShouldAlwaysForceCustomerRole() {
            given(loadUser.existsByEmail("marc@vividela.cm")).willReturn(false);
            given(passwordEncoder.hash("plainPassword")).willReturn("bcryptHash");
            given(saveUser.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
            given(tokenGenerator.generateToken(any(User.class))).willReturn("jwt-token");
            given(tokenGenerator.getExpirationMillis()).willReturn(3_600_000L);

            // The caller tries to self-assign the ADMIN role
            AuthResult result = authService.register(registrationCommand(Role.ADMIN));

            verify(saveUser).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(result.user().getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(result.token()).isEqualTo("jwt-token");
            assertThat(result.expiresInMillis()).isEqualTo(3_600_000L);
        }

        @Test
        @DisplayName("store respects the requested role (creation by an administrator) and generates no token")
        void storeShouldKeepTheRequestedRoleAndNotGenerateAToken() {
            given(loadUser.existsByEmail("marc@vividela.cm")).willReturn(false);
            given(passwordEncoder.hash("plainPassword")).willReturn("bcryptHash");
            given(saveUser.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

            User created = authService.store(registrationCommand(Role.EMPLOYEE));

            assertThat(created.getRole()).isEqualTo(Role.EMPLOYEE);
            assertThat(created.getPassword()).isEqualTo("bcryptHash");
            verify(tokenGenerator, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("store rejects an email already in use")
        void storeShouldThrowWhenEmailAlreadyExists() {
            given(loadUser.existsByEmail("marc@vividela.cm")).willReturn(true);

            assertThatThrownBy(() -> authService.store(registrationCommand(Role.EMPLOYEE)))
                    .isInstanceOf(UserAlreadyExistsException.class);

            verifyNoInteractions(saveUser);
        }

        private StoreCommand registrationCommand(Role role) {
            return new StoreCommand(1L, "Marc", "Kemgang", "mkemgang",
                    "marc@vividela.cm", "690000000", "plainPassword", role);
        }
    }
}