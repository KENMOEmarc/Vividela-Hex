package ken.vivid.adapter.input.web.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "The username is required")
    @Size(min = 3, max = 50, message = "The username must contains at least 3 characters and less than 50 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "The username can only contain letters, numbers, and underscores"
    )
    private String userName;


    @NotBlank(message = "The email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "The email cannot exceed 150 characters")
    private String email;

    @NotBlank(message = "The phone number is required")
    @Size(min = 9, max = 20, message = "The phone number cannot exceed 20 characters")
    private String phone;

    @NotBlank(message = "The password is required")
    @Size(min = 8, max = 100, message = "The password must contain between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "The password must contain at least one uppercase letter, one lowercase letter, one digit and one special character (@$!%*?&)"
    )
    private String password;

    @NotBlank(message = "The password confirmation is required")
    private String confirmPassword;

    @NotBlank(message = "The first name is required")
    @Size(max = 100, message = "The first name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "The last name is required")
    @Size(max = 100, message = "The last name cannot exceed 100 characters")
    private String lastName;

    private String role;

}
