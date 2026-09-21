package ken.vivid.application.port.input.order;

import ken.vivid.domain.entities.order.OrderStatus;
import ken.vivid.domain.entities.order.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateOrderCommand(Long orderId, LocalDate expectedDeliveryDate, OrderStatus status,
                                 PaymentStatus paymentStatus, String notes,
                                 BigDecimal totalAmount, BigDecimal discountAmount) {}
