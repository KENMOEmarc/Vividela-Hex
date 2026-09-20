package ken.vivid.adapter.input.web;

import jakarta.validation.Valid;
import ken.vivid.adapter.input.web.dto.ProductDto;
import ken.vivid.adapter.input.web.dto.StockDto;
import ken.vivid.adapter.input.web.payloads.product.AdjustStockRequest;
import ken.vivid.adapter.input.web.payloads.ApiResponse;
import ken.vivid.adapter.input.web.payloads.product.ConsumeStockRequest;
import ken.vivid.adapter.input.web.payloads.auth.RegisterStockRequest;
import ken.vivid.application.port.input.auth.GetCurrentUserUseCase;
import ken.vivid.application.port.input.product.stock.*;
import ken.vivid.application.port.input.product.stock.adjustStock.AdjustStockCommand;
import ken.vivid.application.port.input.product.stock.adjustStock.AdjustStockUseCase;
import ken.vivid.application.port.input.product.stock.consumeStock.ConsumeStockCommand;
import ken.vivid.application.port.input.product.stock.consumeStock.ConsumeStockUseCase;
import ken.vivid.application.port.input.product.stock.registerStock.RegisterStockCommand;
import ken.vivid.application.port.input.product.stock.registerStock.RegisterStockEntryUseCase;
import ken.vivid.application.port.output.product.stock.LoadStock;
import ken.vivid.domain.entities.User;
import ken.vivid.adapter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final RegisterStockEntryUseCase registerStockEntryUseCase;
    private final ConsumeStockUseCase consumeStockUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final ListStockProductUseCase listStockProductUseCase;
    private final LoadStock loadStock;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @GetMapping("/low")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getLowStock() {
        log.debug("Request to list low-stock products");
        List<ProductDto> lowStockProducts = listStockProductUseCase.listStockProducts().stream()
                .map(ProductDto::from)
                .toList();
        log.debug("Found {} low-stock product(s)", lowStockProducts.size());
        return ResponseEntity.ok(ApiResponse.success("Products below threshold", lowStockProducts));
    }

    @GetMapping("/product/{productId}/batches")
    public ResponseEntity<ApiResponse<List<StockDto>>> getBatchesByProduct(@PathVariable Long productId) {
        log.debug("Request to fetch stock batches for productId={}", productId);
        List<StockDto> batches = loadStock.loadAvailableByProductOrderedByExpiration(productId).stream()
                .map(StockDto::from)
                .toList();
        log.debug("Returned {} batch(es) for productId={}", batches.size(), productId);
        return ResponseEntity.ok(ApiResponse.success("Stock batches", batches));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<StockDto>> getStockByProduct(@PathVariable Long productId) {
        log.debug("Request to fetch current stock for productId={}", productId);
        List<StockDto> batches = loadStock.loadAvailableByProductOrderedByExpiration(productId).stream()
                .map(StockDto::from)
                .toList();
        StockDto first = batches.stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No available stock for product : " + productId));
        log.debug("Current stock for productId={} resolved to batchId={}", productId, first.id());
        return ResponseEntity.ok(ApiResponse.success("Stock", first));
    }

    @PostMapping("/batches")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<StockDto>> createBatch(@Valid @RequestBody RegisterStockRequest request,
                                                             Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        log.info("Stock batch registration request for productId={} by userId={}", request.getProductId(), actingUser.getId());
        StockDto stock = StockDto.from(registerStockEntryUseCase.register(new RegisterStockCommand(
                request.getProductId(),
                actingUser.getId(),
                request.getQuantity(),
                request.getUnitPrice(),
                request.getEntryDate(),
                request.getExpirationDate(),
                request.getRegistrationType(),
                request.getNotes()
        )));
        log.info("Stock batch {} registered successfully for productId={}", stock.id(), request.getProductId());
        return ResponseEntity.ok(ApiResponse.success("Stock batch registered", stock));
    }

    @PutMapping("/batches/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<StockDto>> updateBatch(@PathVariable Long batchId,
                                                             @Valid @RequestBody AdjustStockRequest request,
                                                             Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        log.info("Stock batch update request for batchId={} by userId={} to newLevel={}",
                batchId, actingUser.getId(), request.getNewStockLevel());
        adjustStockUseCase.adjust(new AdjustStockCommand(
                batchId, actingUser.getId(), request.getNewStockLevel(), request.getNotes()
        ));
        StockDto updated = loadStock.loadById(batchId)
                .map(StockDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Stock lot not found : " + batchId));
        log.info("Stock batch {} updated successfully", batchId);
        return ResponseEntity.ok(ApiResponse.success("Stock batch updated", updated));
    }

    @PostMapping("/consume")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> consume(@Valid @RequestBody ConsumeStockRequest request,
                                                     Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        log.info("Stock consumption request for productId={} quantity={} by userId={}",
                request.getProductId(), request.getQuantity(), actingUser.getId());
        consumeStockUseCase.consume(new ConsumeStockCommand(
                request.getProductId(),
                actingUser.getId(),
                request.getQuantity(),
                request.getNotes()
        ));
        log.info("Stock consumption completed for productId={} by userId={}", request.getProductId(), actingUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Stock consumed"));
    }
}
