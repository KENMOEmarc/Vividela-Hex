package ken.vivid.application.service.product;

import ken.vivid.application.port.input.product.stock.adjustStock.AdjustStockCommand;
import ken.vivid.application.port.input.product.stock.adjustStock.AdjustStockUseCase;
import ken.vivid.application.port.input.product.stock.consumeStock.ConsumeStockCommand;
import ken.vivid.application.port.input.product.stock.consumeStock.ConsumeStockUseCase;
import ken.vivid.application.port.input.product.stock.registerStock.RegisterStockCommand;
import ken.vivid.application.port.input.product.stock.registerStock.RegisterStockEntryUseCase;
import ken.vivid.application.port.output.product.LoadProduct;
import ken.vivid.application.port.output.product.SaveProductRegistration;
import ken.vivid.application.port.output.product.stock.LoadStock;
import ken.vivid.application.port.output.product.stock.movement.SaveStockMovement;
import ken.vivid.application.port.output.product.stock.SaveStock;
import ken.vivid.adapter.exception.product.InsufficientStockException;
import ken.vivid.domain.entities.product.Product;
import ken.vivid.domain.entities.product.ProductRegistration;
import ken.vivid.domain.entities.product.Stock;
import ken.vivid.domain.entities.product.StockMovement;
import ken.vivid.domain.dto.MovementType;
import ken.vivid.adapter.exception.InvalidRequestException;
import ken.vivid.adapter.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

public class StockService implements RegisterStockEntryUseCase, ConsumeStockUseCase, AdjustStockUseCase {

    private final LoadProduct loadProduct;
    private final LoadStock loadStock;
    private final SaveStock saveStock;
    private final SaveProductRegistration saveProductRegistration;
    private final SaveStockMovement saveStockMovement;
    private final StockAllocationPolicy stockAllocationPolicy;

    public StockService(LoadProduct loadProduct, LoadStock loadStock, SaveStock saveStock, SaveProductRegistration saveProductRegistration, SaveStockMovement saveStockMovement, StockAllocationPolicy stockAllocationPolicy) {
        this.loadProduct = loadProduct;
        this.loadStock = loadStock;
        this.saveStock = saveStock;
        this.saveProductRegistration = saveProductRegistration;
        this.saveStockMovement = saveStockMovement;
        this.stockAllocationPolicy = stockAllocationPolicy;
    }

    @Override
    public Stock register(RegisterStockCommand command) {
        if (command.quantity() == null || command.quantity().signum() <= 0) {
            throw new InvalidRequestException("Quantity must be strictly positive");
        }

        Product product = loadProduct.loadById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found : " + command.productId()));

        Instant now = Instant.now();

        Stock newLot = Stock.createStock(
                null,
                product.getId(),
                command.quantity(),
                now,
                command.entryDate() == null ? now : command.entryDate(),
                command.unitPrice(),
                command.expirationDate() == null ? null : command.expirationDate().atStartOfDay(ZoneOffset.UTC).toInstant()
        );

        Stock savedLot = saveStock.save(newLot);

        ProductRegistration registration = ProductRegistration.createProductRegistration(
                null,
                product.getId(),
                command.quantity(),
                command.registrationType(),
                command.notes() == null || command.notes().isBlank() ? "Stock entry" : command.notes(),
                command.entryDate()== null ? now : command.entryDate()
        );
        saveProductRegistration.save(registration);

        StockMovement movement = StockMovement.createStockMovement(
                null,
                savedLot.getId(),
                command.employeeUserId(),
                command.quantity(),
                MovementType.RESTOCK,
                command.notes(),
                now
        );
        saveStockMovement.save(movement);

        return savedLot;
    }

    @Override
    public void consume(ConsumeStockCommand command) {
        if (command.quantity() == null || command.quantity().signum() <= 0) {
            throw new InvalidRequestException("Quantity must be strictly positive");
        }

        loadProduct.loadById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found : " + command.productId()));

        BigDecimal totalAvailable = loadStock.totalQuantityByProduct(command.productId());
        if (totalAvailable == null || totalAvailable.compareTo(command.quantity()) < 0) {
            throw new InsufficientStockException(
                    "Insufficient stock for product " + command.productId() + ": requested " + command.quantity()
                            + ", available " + (totalAvailable == null ? BigDecimal.ZERO : totalAvailable));
        }

        List<Stock> availableStocks = loadStock.loadAvailableByProductOrderedByExpiration(command.productId());
        List<StockAllocationPolicy.Allocation> allocations = stockAllocationPolicy.allocate(availableStocks, command.quantity());

        Instant now = Instant.now();
        String notes = appendReference(command.notes(), "Consumption for product " + command.productId());

        for (StockAllocationPolicy.Allocation allocation : allocations) {
            Stock lot = allocation.stock();
            lot.setQuantity(lot.getQuantity().subtract(allocation.quantityToConsume()));
            lot.setUpdatedAt(now);
            saveStock.save(lot);

            StockMovement movement = StockMovement.createStockMovement(
                    null,
                    lot.getId(),
                    command.actingUserId(),
                    allocation.quantityToConsume(),
                    MovementType.CONSUMPTION,
                    notes,
                    now
            );
            saveStockMovement.save(movement);
        }
    }

    @Override
    public void adjust(AdjustStockCommand command) {
        if (command.newStockLevel() == null || command.newStockLevel().signum() < 0) {
            throw new InvalidRequestException("New stock level cannot be negative");
        }

        Stock stock = loadStock.loadById(command.stockId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock lot not found : " + command.stockId()));

        BigDecimal previousQuantity = stock.getQuantity() == null ? BigDecimal.ZERO : stock.getQuantity();
        BigDecimal delta = command.newStockLevel().subtract(previousQuantity);

        if (delta.signum() == 0) {
            return;
        }

        Instant now = Instant.now();
        stock.setQuantity(command.newStockLevel());
        stock.setUpdatedAt(now);
        saveStock.save(stock);

        StockMovement movement = StockMovement.createStockMovement(
                null,
                stock.getId(),
                command.actingUserId(),
                delta.abs(),
                MovementType.ADJUSTMENT,
                command.notes(),
                now
        );
        saveStockMovement.save(movement);
    }

    private String appendReference(String notes, String reference) {
        if (notes == null || notes.isBlank()) {
            return reference;
        }
        return notes + " (" + reference + ")";
    }
}
