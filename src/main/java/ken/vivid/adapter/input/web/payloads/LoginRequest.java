package ken.vivid.adapter.input.web.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "An identifier (email or username) is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}
