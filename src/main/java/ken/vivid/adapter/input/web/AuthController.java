package ken.vivid.adapter.input.web;

import jakarta.validation.Valid;
import ken.vivid.adapter.input.web.dto.UserDto;
import ken.vivid.adapter.input.web.payloads.ApiResponse;
import ken.vivid.adapter.input.web.payloads.auth.LoginRequest;
import ken.vivid.adapter.input.web.payloads.auth.LoginResponse;
import ken.vivid.adapter.input.web.payloads.auth.RegisterRequest;
import ken.vivid.application.port.input.auth.saveUser.LoginCommand;
import ken.vivid.application.port.input.auth.saveUser.LoginUseCase;
import ken.vivid.application.port.input.auth.LogoutUseCase;
import ken.vivid.application.port.input.auth.saveUser.RegisterUseCase;
import ken.vivid.application.port.input.auth.saveUser.StoreCommand;
import ken.vivid.domain.entities.AuthResult;
import ken.vivid.domain.entities.User;
import ken.vivid.domain.dto.Role;
import ken.vivid.adapter.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = loginUseCase.login(
                new LoginCommand(request.getIdentifier(), request.getPassword())
        );
        return ResponseEntity.ok(ApiResponse.success("Connection successful", LoginResponse.from(result)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = registerUseCase.register(new StoreCommand(
                null,
                request.getFirstName(),
                request.getLastName(),
                request.getUserName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                Role.CUSTOMER
        ));
        return ResponseEntity.ok(ApiResponse.success("Account created successfully", LoginResponse.from(result)));
    }

    @PostMapping("/store")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<UserDto>> store(@Valid @RequestBody RegisterRequest request,
                                                      Authentication authentication) {
        User user = registerUseCase.store(new StoreCommand(
                null,
                request.getFirstName(),
                request.getLastName(),
                request.getUserName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                parseRole(request.getRole())
        ));
        return ResponseEntity.ok(ApiResponse.success("Account created successfully", UserDto.from(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        logoutUseCase.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            throw new InvalidRequestException("The role is required for creating an account via this endpoint");
        }
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid role : " + role);
        }
    }
}
