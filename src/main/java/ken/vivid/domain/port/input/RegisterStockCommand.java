package ken.vivid.domain.port.input;

import ken.vivid.domain.dto.RegistrationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RegisterStockCommand(
        Long productId,
        Long employeeUserId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        Instant entryDate,
        LocalDate expirationDate,
        RegistrationType registrationType,
        String notes
) {
}
