package ken.vivid.adapter.input.web;

import jakarta.validation.Valid;
import ken.vivid.adapter.input.web.dto.UserDto;
import ken.vivid.adapter.input.web.payloads.ApiResponse;
import ken.vivid.adapter.input.web.payloads.auth.ChangePasswordRequest;
import ken.vivid.adapter.input.web.payloads.auth.UpdateUserRequest;
import ken.vivid.application.port.input.auth.updateUser.ChangePasswordCommand;
import ken.vivid.application.port.input.auth.updateUser.ChangePasswordUseCase;
import ken.vivid.application.port.input.auth.deleteUser.DeleteUserUseCase;
import ken.vivid.application.port.input.auth.GetCurrentUserUseCase;
import ken.vivid.application.port.input.auth.updateUser.UpdateCommand;
import ken.vivid.application.port.input.auth.updateUser.UpdateUserUseCase;
import ken.vivid.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(Authentication authentication) {
        var user = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("User profile", UserDto.from(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> update(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateUserRequest request,
                                                       Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        User updated = updateUserUseCase.update(new UpdateCommand(
                id,
                actingUser.getRole(),
                request.getFirstName(),
                request.getLastName(),
                request.getUserName(),
                request.getEmail(),
                request.getPhone()
        ));
        return ResponseEntity.ok(ApiResponse.success("Profile updated", UserDto.from(updated)));
    }

    /** Deliberately self-service only — no admin override to change someone else's password. */
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long id,
                                                            @Valid @RequestBody ChangePasswordRequest request,
                                                            Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        if (!actingUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only modify your own password");
        }
        changePasswordUseCase.changePassword(new ChangePasswordCommand(
                id,
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        ));
        return ResponseEntity.ok(ApiResponse.success("Password updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        deleteUserUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé"));
    }

}
