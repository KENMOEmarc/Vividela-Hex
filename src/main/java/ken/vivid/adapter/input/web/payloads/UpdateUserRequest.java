package ken.vivid.adapter.input.web.payloads;

import jakarta.validation.constraints.Email;
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
public class UpdateUserRequest {

    @NotBlank(message = "The username is required")
    @Size(min = 3, max = 50, message = "The username must contain between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "The username can only contain letters, numbers and underscores")
    private String userName;

    @NotBlank(message = "The email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "The email cannot exceed 150 characters")
    private String email;

    @NotBlank(message = "The phone number is required")
    @Size(min = 9, max = 20, message = "The phone number must contain between 9 and 20 characters")
    private String phone;

    @NotBlank(message = "The first name is required")
    @Size(max = 100, message = "The first name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "The last name is required")
    @Size(max = 100, message = "The last name cannot exceed 100 characters")
    private String lastName;
}
