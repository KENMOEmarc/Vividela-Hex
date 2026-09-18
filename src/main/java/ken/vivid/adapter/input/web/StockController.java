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
        List<ProductDto> lowStockProducts = listStockProductUseCase.listStockProducts().stream()
                .map(ProductDto::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Products below threshold", lowStockProducts));
    }

    @GetMapping("/product/{productId}/batches")
    public ResponseEntity<ApiResponse<List<StockDto>>> getBatchesByProduct(@PathVariable Long productId) {
        List<StockDto> batches = loadStock.loadAvailableByProductOrderedByExpiration(productId).stream()
                .map(StockDto::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Stock batches", batches));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<StockDto>> getStockByProduct(@PathVariable Long productId) {
        List<StockDto> batches = loadStock.loadAvailableByProductOrderedByExpiration(productId).stream()
                .map(StockDto::from)
                .toList();
        StockDto first = batches.stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No available stock for product : " + productId));
        return ResponseEntity.ok(ApiResponse.success("Stock", first));
    }

    @PostMapping("/batches")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<StockDto>> createBatch(@Valid @RequestBody RegisterStockRequest request,
                                                             Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
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
        return ResponseEntity.ok(ApiResponse.success("Stock batch registered", stock));
    }

    @PutMapping("/batches/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<StockDto>> updateBatch(@PathVariable Long batchId,
                                                             @Valid @RequestBody AdjustStockRequest request,
                                                             Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        adjustStockUseCase.adjust(new AdjustStockCommand(
                batchId, actingUser.getId(), request.getNewStockLevel(), request.getNotes()
        ));
        StockDto updated = loadStock.loadById(batchId)
                .map(StockDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Stock lot not found : " + batchId));
        return ResponseEntity.ok(ApiResponse.success("Stock batch updated", updated));
    }

    @PostMapping("/consume")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> consume(@Valid @RequestBody ConsumeStockRequest request,
                                                     Authentication authentication) {
        User actingUser = getCurrentUserUseCase.getCurrentUser(authentication.getName());
        consumeStockUseCase.consume(new ConsumeStockCommand(
                request.getProductId(),
                actingUser.getId(),
                request.getQuantity(),
                request.getNotes()
        ));
        return ResponseEntity.ok(ApiResponse.success("Stock consumed"));
    }
}
