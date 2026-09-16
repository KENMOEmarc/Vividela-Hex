package ken.vivid.adapter.input.web.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "The current password is required")
    private String currentPassword;

    @NotBlank(message = "The new password is required")
    @Size(min = 8, max = 100, message = "The password must be between 8 and 100 characters long")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "The password must contain at least one uppercase letter, one lowercase letter, one digit and one special character (@$!%*?&)"
    )
    private String newPassword;

    @NotBlank(message = "The password confirmation is required")
    private String confirmPassword;
}
