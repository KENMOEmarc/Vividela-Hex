package ken.vivid.adapter.input.web;

import jakarta.validation.Valid;
import ken.vivid.adapter.input.web.dto.PaymentDto;
import ken.vivid.adapter.input.web.payloads.ApiResponse;
import ken.vivid.adapter.input.web.payloads.payment.RecordPaymentRequest;
import ken.vivid.application.port.input.auth.GetCurrentUserUseCase;
import ken.vivid.application.port.input.payment.ConfirmPaymentUseCase;
import ken.vivid.application.port.input.payment.FailPaymentUseCase;
import ken.vivid.application.port.input.payment.GetPaymentUseCase;
import ken.vivid.application.port.input.payment.recordPayment.RecordPaymentCommand;
import ken.vivid.application.port.input.payment.recordPayment.RecordPaymentUseCase;
import ken.vivid.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
public class PaymentController {
    private final RecordPaymentUseCase recordPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;
    private final ConfirmPaymentUseCase confirmPaymentUseCase;
    private final FailPaymentUseCase failPaymentUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDto>> create(@Valid @RequestBody RecordPaymentRequest request,
                                                          Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        PaymentDto payment = PaymentDto.from(recordPaymentUseCase.record(new RecordPaymentCommand(
                request.getOrderId(),
                request.getPaymentMethod(),
                request.getAmount(),
                request.getPayerPhone(),
                request.getTransactionReference(),
                actingUser.getId()
        )));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded", payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Payment", PaymentDto.from(getPaymentUseCase.getById(id))));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Order payments", getPaymentUseCase.getByOrderId(orderId)
                .stream().map(PaymentDto::from).toList()));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<PaymentDto>> confirm(@PathVariable Long id, Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        PaymentDto payment = PaymentDto.from(confirmPaymentUseCase.confirm(id, actingUser.getId()));
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed", payment));
    }

    @PutMapping("/{id}/fail")
    public ResponseEntity<ApiResponse<PaymentDto>> fail(@PathVariable Long id, Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        PaymentDto payment = PaymentDto.from(failPaymentUseCase.fail(id, actingUser.getId()));
        return ResponseEntity.ok(ApiResponse.success("Payment failed", payment));
    }
}
