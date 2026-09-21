package ken.vivid.application.port.input.order;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOrderCommand(Long clientId, LocalDate expectedDeliveryDate, String notes,
                                 BigDecimal totalAmount, BigDecimal discountAmount) {}
