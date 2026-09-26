package ken.vivid.adapter.input.web.output.persistence.adapter.order;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.order.OrderJpaEntity;
import ken.vivid.adapter.input.web.output.persistence.jpaRepositories.order.OrderJpaRepository;
import ken.vivid.application.port.output.order.*;
import ken.vivid.domain.entities.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements LoadOrder, SaveOrder, DeleteOrder {
    private final OrderJpaRepository repository;

    @Override
    public Optional<Order> loadOrderById(Long id) { return repository.findById(id).map(OrderPersistenceAdapter::toDomain); }

    @Override
    public List<Order> loadOrderAll() {
        return repository.findAll().stream().map(OrderPersistenceAdapter::toDomain).toList();
    }

    @Override
    public List<Order> loadOrderByClientId(Long clientId) {
        return repository.findByClientIdOrderByOrderDateDesc(clientId)
                .stream().map(OrderPersistenceAdapter::toDomain).toList();
    }

    @Override
    public Order save(Order order) { return toDomain(repository.save(toEntity(order))); }

    @Override
    public void delete(Long id) { repository.deleteById(id); }

    public static Order toDomain(OrderJpaEntity entity) {
        return Order.create(entity.getId(), entity.getClientId(), entity.getOrderDate(),
                entity.getExpectedDeliveryDate(), entity.getDeliveredAt(), entity.getStatus(),
                entity.getPaymentStatus(), entity.getNotes(), entity.getTotalAmount(),
                entity.getDiscountAmount(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public static OrderJpaEntity toEntity(Order order) {
        return OrderJpaEntity.builder().id(order.getId()).clientId(order.getClientId())
                .orderDate(order.getOrderDate()).expectedDeliveryDate(order.getExpectedDeliveryDate())
                .deliveredAt(order.getDeliveredAt()).status(order.getStatus())
                .paymentStatus(order.getPaymentStatus()).notes(order.getNotes())
                .totalAmount(order.getTotalAmount()).discountAmount(order.getDiscountAmount())
                .createdAt(order.getCreatedAt()).updatedAt(order.getUpdatedAt()).build();
    }
}
