package ken.vivid.application.service.auth;

import ken.vivid.adapter.exception.InvalidRequestException;
import ken.vivid.adapter.exception.ResourceNotFoundException;
import ken.vivid.adapter.exception.auth.InvalidCredentialsException;
import ken.vivid.adapter.exception.auth.PasswordMismatchException;
import ken.vivid.adapter.exception.auth.UserAlreadyExistsException;
import ken.vivid.application.port.input.auth.updateUser.ChangePasswordCommand;
import ken.vivid.application.port.input.auth.updateUser.UpdateCommand;
import ken.vivid.application.port.output.auth.DeleteUser;
import ken.vivid.application.port.output.auth.LoadUser;
import ken.vivid.application.port.output.auth.PasswordEncoder;
import ken.vivid.application.port.output.auth.SaveUser;
import ken.vivid.domain.dto.Role;
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
 * Unit tests for {@link UserService}: profile update, password change
 * and account deletion, with mocked output ports.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService (application)")
class UserServiceTest {

    @Mock
    private LoadUser loadUser;
    @Mock
    private SaveUser saveUser;
    @Mock
    private DeleteUser deleteUser;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("fails when the target user does not exist")
        void updateShouldThrowWhenTargetUserNotFound() {
            given(loadUser.loadById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.update(command(99L, "marc@vividela.cm", "mkemgang")))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(saveUser);
        }

        @Test
        @DisplayName("rejects a new email already used by another account")
        void updateShouldThrowWhenNewEmailIsAlreadyTaken() {
            User target = aUser().withId(1L).withEmail("old@vividela.cm").build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(target));
            given(loadUser.existsByEmail("new@vividela.cm")).willReturn(true);

            assertThatThrownBy(() -> userService.update(command(1L, "new@vividela.cm", "mkemgang")))
                    .isInstanceOf(UserAlreadyExistsException.class);

            verifyNoInteractions(saveUser);
        }

        @Test
        @DisplayName("rejects a new user name already taken")
        void updateShouldThrowWhenNewUserNameIsAlreadyTaken() {
            User target = aUser().withId(1L)
                    .withEmail("marc@vividela.cm").withUserName("mkemgang").build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(target));
            given(loadUser.existsByUserName("other")).willReturn(true);

            // email unchanged: only the username uniqueness check should fire
            assertThatThrownBy(() -> userService.update(command(1L, "marc@vividela.cm", "other")))
                    .isInstanceOf(UserAlreadyExistsException.class);

            verifyNoInteractions(saveUser);
        }

        @Test
        @DisplayName("skips uniqueness checks when email and user name are unchanged")
        void updateShouldSkipUniquenessChecksWhenIdentifiersAreUnchanged() {
            User target = aUser().withId(1L)
                    .withEmail("marc@vividela.cm").withUserName("mkemgang").build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(target));
            given(saveUser.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

            userService.update(command(1L, "marc@vividela.cm", "mkemgang"));

            verify(loadUser, never()).existsByEmail(any());
            verify(loadUser, never()).existsByUserName(any());
        }

        @Test
        @DisplayName("applies new profile fields and persists the user")
        void updateShouldApplyNewProfileFieldsAndSave() {
            User target = aUser().withId(1L)
                    .withEmail("marc@vividela.cm").withUserName("mkemgang")
                    .withFirstName("Marc").withLastName("Kemgang").withPhone("690000000").build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(target));
            given(saveUser.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

            UpdateCommand command = new UpdateCommand(1L, Role.ADMIN, "Bertrand", "Kenmoe",
                    "mkemgang", "marc@vividela.cm", "699111222");

            User updated = userService.update(command);

            verify(saveUser).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getFirstName()).isEqualTo("Bertrand");
            assertThat(userCaptor.getValue().getLastName()).isEqualTo("Kenmoe");
            assertThat(userCaptor.getValue().getPhone()).isEqualTo("699111222");
            assertThat(updated.getFirstName()).isEqualTo("Bertrand");
        }

        @Test
        @DisplayName("does not change either role or password")
        void updateShouldNotChangeRoleOrPassword() {
            User target = aUser().withId(1L).withRole(Role.EMPLOYEE)
                    .withEmail("marc@vividela.cm").withUserName("mkemgang")
                    .withPassword("existingHash").build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(target));
            given(saveUser.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

            User updated = userService.update(
                    new UpdateCommand(1L, Role.CUSTOMER, "Marc", "Kemgang",
                            "mkemgang", "marc@vividela.cm", "690000000"));

            assertThat(updated.getRole()).isEqualTo(Role.EMPLOYEE);
            assertThat(updated.getPassword()).isEqualTo("existingHash");
            verifyNoInteractions(passwordEncoder);
        }

        private UpdateCommand command(Long targetId, String email, String userName) {
            return new UpdateCommand(targetId, Role.ADMIN, "Marc", "Kemgang", userName, email, "690000000");
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        @Test
        @DisplayName("fails when the user does not exist")
        void changePasswordShouldThrowWhenUserNotFound() {
            given(loadUser.loadById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.changePassword(
                    new ChangePasswordCommand(99L, "old", "new", "new")))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(saveUser, passwordEncoder);
        }

        @Test
        @DisplayName("fails when the current password is wrong")
        void changePasswordShouldThrowWhenCurrentPasswordIsWrong() {
            User user = aUser().withId(1L).withPassword("existingHash").build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(user));
            given(passwordEncoder.matches("wrong", "existingHash")).willReturn(false);

            assertThatThrownBy(() -> userService.changePassword(
                    new ChangePasswordCommand(1L, "wrong", "new", "new")))
                    .isInstanceOf(InvalidCredentialsException.class);

            verifyNoInteractions(saveUser);
        }

        @Test
        @DisplayName("fails when the confirmation does not match the new password")
        void changePasswordShouldThrowWhenConfirmationDoesNotMatch() {
            User user = aUser().withId(1L).withPassword("existingHash").build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(user));
            given(passwordEncoder.matches("old", "existingHash")).willReturn(true);

            assertThatThrownBy(() -> userService.changePassword(
                    new ChangePasswordCommand(1L, "old", "new", "different")))
                    .isInstanceOf(PasswordMismatchException.class);

            verifyNoInteractions(saveUser);
        }

        @Test
        @DisplayName("hashes the new password and persists the user")
        void changePasswordShouldHashAndSaveTheNewPassword() {
            User user = aUser().withId(1L).withPassword("existingHash").build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(user));
            given(passwordEncoder.matches("old", "existingHash")).willReturn(true);
            given(passwordEncoder.hash("new")).willReturn("newHash");
            given(saveUser.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

            User result = userService.changePassword(
                    new ChangePasswordCommand(1L, "old", "new", "new"));

            assertThat(result.getPassword()).isEqualTo("newHash");
            verify(saveUser).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("newHash");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("fails when the user does not exist")
        void deleteShouldThrowWhenUserNotFound() {
            given(loadUser.loadById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(deleteUser);
        }

        @Test
        @DisplayName("rejects deletion of the last administrator")
        void deleteShouldThrowWhenDeletingTheLastAdministrator() {
            User admin = aUser().withId(1L).withRole(Role.ADMIN).build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(admin));
            given(loadUser.countByRole(Role.ADMIN)).willReturn(1L);

            assertThatThrownBy(() -> userService.delete(1L))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessageContaining("last administrator");

            verifyNoInteractions(deleteUser);
        }

        @Test
        @DisplayName("allows removing an administrator when another one remains")
        void deleteShouldAllowRemovingAnAdministratorWhenAnotherOneRemains() {
            User admin = aUser().withId(1L).withRole(Role.ADMIN).build();
            given(loadUser.loadById(1L)).willReturn(Optional.of(admin));
            given(loadUser.countByRole(Role.ADMIN)).willReturn(2L);

            userService.delete(1L);

            verify(deleteUser).delete(1L);
        }

        @Test
        @DisplayName("removes a non-administrator without counting administrators")
        void deleteShouldRemoveANonAdministratorWithoutCountingAdmins() {
            User employee = aUser().withId(5L).withRole(Role.EMPLOYEE).build();
            given(loadUser.loadById(5L)).willReturn(Optional.of(employee));

            userService.delete(5L);

            verify(deleteUser).delete(5L);
            verify(loadUser, never()).countByRole(any());
        }
    }
}
