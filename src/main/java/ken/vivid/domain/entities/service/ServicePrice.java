package ken.vivid.domain.entities.service;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An entry of the price list: the price of one {@link ServiceType} for one
 * {@link ClothingType} (maps to {@code service_prices}).
 * <p>
 * Invariants enforced here:
 * <ul>
 *     <li>clothing type and service are mandatory and never change once created
 *     (they identify the entry);</li>
 *     <li>the price is strictly positive, rounded to 2 decimals and fits the
 *     {@code DECIMAL(10,2)} column (a free service is modelled by deactivating
 *     the entry, not by a zero price).</li>
 * </ul>
 * Inactive entries are kept in the list but are ignored when pricing articles.
 */
public class ServicePrice {

    public static final BigDecimal MAX_PRICE = new BigDecimal("99999999.99");

    private final Long id;
    private final ClothingType clothingType;
    private final ServiceType service;
    private BigDecimal price;
    private boolean active;

    private ServicePrice(Long id, ClothingType clothingType, ServiceType service, BigDecimal price, boolean active) {
        this.id = id;
        this.clothingType = clothingType;
        this.service = service;
        this.price = price;
        this.active = active;
    }

    public static ServicePrice create(Long id, ClothingType clothingType, ServiceType service,
                                      BigDecimal price, boolean active) {
        if (clothingType == null) {
            throw new IllegalArgumentException("Clothing type cannot be null");
        }
        if (service == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        return new ServicePrice(id, clothingType, service, validatedPrice(price), active);
    }

    public Long getId() { return id; }
    public ClothingType getClothingType() { return clothingType; }
    public ServiceType getService() { return service; }
    public BigDecimal getPrice() { return price; }
    public boolean isActive() { return active; }

    public void changePrice(BigDecimal newPrice) {
        this.price = validatedPrice(newPrice);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private static BigDecimal validatedPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        BigDecimal scaled = price.setScale(2, RoundingMode.HALF_UP);
        if (scaled.signum() <= 0) {
            throw new IllegalArgumentException("Price must be strictly positive");
        }
        if (scaled.compareTo(MAX_PRICE) > 0) {
            throw new IllegalArgumentException("Price cannot exceed " + MAX_PRICE.toPlainString());
        }
        return scaled;
    }
}
