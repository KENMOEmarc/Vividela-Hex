package ken.vivid.adapter.input.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ken.vivid.domain.entities.order.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDto(Long id, Long clientId, LocalDate orderDate, LocalDate expectedDeliveryDate,
                       LocalDate deliveredAt, OrderStatus status, PaymentStatus paymentStatus,
                       String notes, BigDecimal totalAmount, BigDecimal discountAmount,
                       Instant createdAt, Instant updatedAt) {
    public static OrderDto from(Order order) {
        return new OrderDto(order.getId(), order.getClientId(), order.getOrderDate(),
                order.getExpectedDeliveryDate(), order.getDeliveredAt(), order.getStatus(),
                order.getPaymentStatus(), order.getNotes(), order.getTotalAmount(),
                order.getDiscountAmount(), order.getCreatedAt(), order.getUpdatedAt());
    }
}
