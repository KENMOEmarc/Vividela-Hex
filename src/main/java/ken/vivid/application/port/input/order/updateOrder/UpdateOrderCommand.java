package ken.vivid.application.port.input.order.updateOrder;

import ken.vivid.domain.dto.MovementType;
import ken.vivid.domain.entities.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateOrderCommand(Long orderId, LocalDate expectedDeliveryDate, MovementType.OrderStatus status,
                                 PaymentStatus paymentStatus, String notes,
                                 BigDecimal totalAmount, BigDecimal discountAmount) {}
