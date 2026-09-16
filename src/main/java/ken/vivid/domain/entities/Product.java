package ken.vivid.domain.entities;

import ken.vivid.domain.dto.MeasurementUnit;

import java.math.BigDecimal;
import java.time.Instant;

public class Product {

    private final Long id;
    private String name;
    private BigDecimal thresholdValue;
    private MeasurementUnit measurementUnit;
    private Instant createdAt;
    private Instant updatedAt;

    private Product(Long id, String name, BigDecimal thresholdValue, MeasurementUnit measurementUnit, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.thresholdValue = thresholdValue;
        this.measurementUnit = measurementUnit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Product createProduct(Long id, String name, BigDecimal thresholdValue, MeasurementUnit measurementUnit, Instant createdAt, Instant updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (thresholdValue == null || thresholdValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Threshold value cannot be null or negative");
        }
        if (measurementUnit == null) {
            throw new IllegalArgumentException("Measurement unit cannot be null");
        }
        if (createdAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Created at date cannot be in the future");
        }
        if (updatedAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Updated at date cannot be in the future");
        }
        return new Product(id, name, thresholdValue, measurementUnit, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(MeasurementUnit measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
