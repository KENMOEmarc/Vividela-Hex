package ken.vivid.adapter.input.web;

import jakarta.validation.Valid;
import ken.vivid.adapter.input.web.dto.OrderDto;
import ken.vivid.adapter.input.web.payloads.ApiResponse;
import ken.vivid.adapter.input.web.payloads.order.*;
import ken.vivid.application.port.input.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final UpdateOrderUseCase updateOrderUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = OrderDto.from(createOrderUseCase.create(new CreateOrderCommand(
                request.getClientId(), request.getExpectedDeliveryDate(), request.getNotes(),
                request.getTotalAmount(), request.getDiscountAmount())));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Order created", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Orders", getOrderUseCase.getAll().stream()
                .map(OrderDto::from).toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order", OrderDto.from(getOrderUseCase.getById(id))));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(ApiResponse.success("Client orders", getOrderUseCase.getByClientId(clientId)
                .stream().map(OrderDto::from).toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> update(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateOrderRequest request) {
        OrderDto order = OrderDto.from(updateOrderUseCase.update(new UpdateOrderCommand(
                id, request.getExpectedDeliveryDate(), request.getStatus(), request.getPaymentStatus(),
                request.getNotes(), request.getTotalAmount(), request.getDiscountAmount())));
        return ResponseEntity.ok(ApiResponse.success("Order updated", order));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteOrderUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Order deleted"));
    }
}
