package ken.vivid.adapter.input.web.payloads.auth;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import ken.vivid.domain.dto.RegistrationType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class RegisterStockRequest {

    @NotNull(message = "The product id is required")
    private Long productId;

    @NotNull(message = "The quantity is required")
    @DecimalMin(value = "0.01", message = "The quantity must be strictly positive")
    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private Instant entryDate;

    private LocalDate expirationDate;

    @NotNull(message = "The registration type is required")
    private RegistrationType registrationType;

    private String notes;
}
