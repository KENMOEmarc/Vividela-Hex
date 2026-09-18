package ken.vivid.application.service.auth;

import ken.vivid.application.port.input.auth.updateUser.ChangePasswordCommand;
import ken.vivid.application.port.input.auth.updateUser.ChangePasswordUseCase;
import ken.vivid.application.port.input.auth.deleteUser.DeleteUserUseCase;
import ken.vivid.application.port.input.auth.updateUser.UpdateCommand;
import ken.vivid.application.port.input.auth.updateUser.UpdateUserUseCase;
import ken.vivid.adapter.exception.auth.InvalidCredentialsException;
import ken.vivid.adapter.exception.auth.PasswordMismatchException;
import ken.vivid.adapter.exception.auth.UserAlreadyExistsException;
import ken.vivid.application.port.output.auth.DeleteUser;
import ken.vivid.application.port.output.auth.LoadUser;
import ken.vivid.application.port.output.auth.PasswordEncoder;
import ken.vivid.application.port.output.auth.SaveUser;
import ken.vivid.domain.entities.User;
import ken.vivid.domain.dto.Role;
import ken.vivid.adapter.exception.InvalidRequestException;
import ken.vivid.adapter.exception.ResourceNotFoundException;

public class UserService implements UpdateUserUseCase,
        DeleteUserUseCase, ChangePasswordUseCase {

    private final LoadUser loadUser;
    private final SaveUser saveUser;
    private final DeleteUser deleteUser;
    private final PasswordEncoder passwordEncoder;

    public UserService(LoadUser loadUser, SaveUser saveUser, DeleteUser deleteUser, PasswordEncoder passwordEncoder) {
        this.loadUser = loadUser;
        this.saveUser = saveUser;
        this.deleteUser = deleteUser;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User update(UpdateCommand command) {
        User target = loadUser.loadById(command.targetUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found : " + command.targetUserId()));

        if (!target.getEmail().equals(command.email()) && loadUser.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException("This email is already in use : " + command.email());
        }
        if (!target.getUserName().equals(command.userName()) && loadUser.existsByUserName(command.userName())) {
            throw new UserAlreadyExistsException("This username is already taken : " + command.userName());
        }

        target.setFirstName(command.firstName());
        target.setLastName(command.lastName());
        target.setUserName(command.userName());
        target.setEmail(command.email());
        target.setPhone(command.phone());

        return saveUser.save(target);
    }

    @Override
    public User changePassword(ChangePasswordCommand command) {
        User user = loadUser.loadById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found : " + command.userId()));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.hash(command.newPassword()));
        return saveUser.save(user);
    }

    @Override
    public void delete(Long userId) {
        User target = loadUser.loadById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found : " + userId));

        if (target.getRole() == Role.ADMIN && loadUser.countByRole(Role.ADMIN) <= 1) {
            throw new InvalidRequestException("Cannot delete the last administrator account");
        }

        deleteUser.delete(userId);
    }
}
