package ken.vivid.domain.service;

import ken.vivid.domain.port.input.*;
import ken.vivid.domain.port.output.DeleteProduct;
import ken.vivid.domain.port.output.LoadProduct;
import ken.vivid.domain.port.output.SaveProduct;
import ken.vivid.domain.port.output.LoadStock;
import ken.vivid.domain.entities.Product;
import ken.vivid.domain.exception.DuplicateResourceException;
import ken.vivid.domain.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class ProductService implements CreateProductUseCase, UpdateProductUseCase, DeleteProductUseCase, GetProductUseCase, ListStockProductUseCase {
    private final LoadProduct loadProduct;
    private final SaveProduct saveProduct;
    private final DeleteProduct deleteProduct;
    private final LoadStock loadStock;

    public ProductService(LoadProduct loadProduct, SaveProduct saveProduct, DeleteProduct deleteProduct, LoadStock loadStock) {
        this.loadProduct = loadProduct;
        this.saveProduct = saveProduct;
        this.deleteProduct = deleteProduct;
        this.loadStock = loadStock;
    }


    @Override
    public Product create(CreateProductCommand command) {
        if (loadProduct.existsByName(command.name())) {
            throw new DuplicateResourceException("Product with name " + command.name() + " already exists.");
        }

        Instant now = Instant.now();
        Product product = Product.createProduct(
                null,
                command.name(),
                command.thresholdValue(),
                command.measurementUnit(),
                now,
                now
        );

        return saveProduct.save(product);
    }

    @Override
    public void delete(Long id) {
        loadProduct.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found : " + id));

        deleteProduct.delete(id);
    }

    @Override
    public Product getById(Long productId) {
        return loadProduct.loadById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found : " + productId));
    }

    @Override
    public List<Product> getAllProducts() {
        return loadProduct.loadAll();
    }

    @Override
    public Product update(UpdateProductCommand command) {
        Product target = loadProduct.loadById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found : " + command.productId()));

        if (!target.getName().equals(command.name()) && loadProduct.existsByName(command.name())) {
            throw new DuplicateResourceException("Product with name " + command.name() + " already exists.");
        }

        target.setName(command.name());
        target.setThresholdValue(command.thresholdValue());
        target.setMeasurementUnit(command.measurementUnit());
        target.setUpdatedAt(Instant.now());

        return saveProduct.save(target);
    }

    @Override
    public List<Product> listStockProducts() {
        return loadProduct.loadAll().stream()
                .filter(this::isBelowThreshold)
                .toList();
    }

    private boolean isBelowThreshold(Product product) {
        BigDecimal totalQuantity = loadStock.totalQuantityByProduct(product.getId());
        BigDecimal available = totalQuantity == null ? BigDecimal.ZERO : totalQuantity;
        BigDecimal threshold = product.getThresholdValue() == null ? BigDecimal.ZERO : product.getThresholdValue();
        return available.compareTo(threshold) <= 0;
    }
}
