package ken.vivid.domain.entities.article;

import ken.vivid.domain.dto.ArticleStatus;
import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An item of clothing belonging to an order, together with the services to
 * apply to it.
 * <p>
 * Invariants enforced here:
 * <ul>
 *     <li>it belongs to an order, has a clothing type and a status;</li>
 *     <li>it carries at least one service, never the same service twice;</li>
 *     <li>text attributes respect the column sizes of the {@code articles} table;</li>
 *     <li>its status only moves forward (see {@link ArticleStatus});</li>
 *     <li>a COMPLETED article can no longer be modified;</li>
 *     <li>services can only be added or removed while the article is PENDING.</li>
 * </ul>
 */
public class Article {

    public static final int SIZE_MAX_LENGTH = 20;
    public static final int FABRIC_MAX_LENGTH = 50;
    public static final int COLOR_MAX_LENGTH = 50;
    public static final int DISTINCTION_MAX_LENGTH = 255;

    private final Long id;
    private final Long orderId;
    private final ClothingType clothingType;
    private String size;
    private String fabric;
    private String color;
    private String distinction;
    private ArticleStatus status;
    private final List<AppliedService> services;

    private Article(Long id, Long orderId, ClothingType clothingType, String size, String fabric,
                    String color, String distinction, ArticleStatus status, List<AppliedService> services) {
        this.id = id;
        this.orderId = orderId;
        this.clothingType = clothingType;
        this.size = size;
        this.fabric = fabric;
        this.color = color;
        this.distinction = distinction;
        this.status = status;
        this.services = services;
    }

    public static Article create(Long id, Long orderId, ClothingType clothingType, String size,
                                 String fabric, String color, String distinction,
                                 ArticleStatus status, List<AppliedService> services) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (clothingType == null) {
            throw new IllegalArgumentException("Clothing type cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (services == null || services.isEmpty()) {
            throw new IllegalArgumentException("An article must have at least one service");
        }
        Set<ServiceType> seen = new HashSet<>();
        for (AppliedService applied : services) {
            if (applied == null) {
                throw new IllegalArgumentException("Services cannot contain null elements");
            }
            if (!seen.add(applied.service())) {
                throw new IllegalArgumentException("Duplicate service: " + applied.service());
            }
        }
        return new Article(id, orderId, clothingType,
                normalize(size, SIZE_MAX_LENGTH, "Size"),
                normalize(fabric, FABRIC_MAX_LENGTH, "Fabric"),
                normalize(color, COLOR_MAX_LENGTH, "Color"),
                normalize(distinction, DISTINCTION_MAX_LENGTH, "Distinction"),
                status, new ArrayList<>(services));
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public ClothingType getClothingType() { return clothingType; }
    public String getSize() { return size; }
    public String getFabric() { return fabric; }
    public String getColor() { return color; }
    public String getDistinction() { return distinction; }
    public ArticleStatus getStatus() { return status; }

    public List<AppliedService> getServices() {
        return Collections.unmodifiableList(services);
    }

    /** Sum of the prices applied to this article. */
    public BigDecimal getTotalPrice() {
        return services.stream()
                .map(AppliedService::appliedPrice)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }

    public boolean hasService(ServiceType service) {
        return services.stream().anyMatch(s -> s.service() == service);
    }

    public void updateDetails(String size, String fabric, String color, String distinction) {
        ensureNotCompleted();
        String newSize = normalize(size, SIZE_MAX_LENGTH, "Size");
        String newFabric = normalize(fabric, FABRIC_MAX_LENGTH, "Fabric");
        String newColor = normalize(color, COLOR_MAX_LENGTH, "Color");
        String newDistinction = normalize(distinction, DISTINCTION_MAX_LENGTH, "Distinction");
        this.size = newSize;
        this.fabric = newFabric;
        this.color = newColor;
        this.distinction = newDistinction;
    }

    public void changeStatus(ArticleStatus next) {
        if (next == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (!status.canTransitionTo(next)) {
            throw new IllegalStateException("Invalid article status transition: " + status + " -> " + next);
        }
        this.status = next;
    }

    public void addService(AppliedService applied) {
        if (applied == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        ensureServicesEditable();
        if (hasService(applied.service())) {
            throw new IllegalArgumentException("Duplicate service: " + applied.service());
        }
        services.add(applied);
    }

    public void removeService(ServiceType service) {
        if (service == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        ensureServicesEditable();
        if (!hasService(service)) {
            throw new IllegalArgumentException("Service not applied to this article: " + service);
        }
        if (services.size() == 1) {
            throw new IllegalStateException("An article must keep at least one service");
        }
        services.removeIf(s -> s.service() == service);
    }

    private void ensureNotCompleted() {
        if (status.isTerminal()) {
            throw new IllegalStateException("A completed article cannot be modified");
        }
    }

    private void ensureServicesEditable() {
        if (status != ArticleStatus.PENDING) {
            throw new IllegalStateException("Services can only be changed while the article is PENDING");
        }
    }

    private static String normalize(String value, int maxLength, String fieldName) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
        return trimmed;
    }
}
