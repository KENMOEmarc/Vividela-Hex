package ken.vivid.application.service.product;

import ken.vivid.adapter.exception.product.InsufficientStockException;
import ken.vivid.domain.entities.Stock;
import ken.vivid.adapter.exception.InvalidRequestException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Decides how a requested quantity of a product should be drawn from its
 * available {@link Stock} lots.
 * <p>
 * Applies a FEFO (First-Expired, First-Out) strategy: lots that expire
 * soonest are consumed first, and lots without an expiration date are
 * treated as never-expiring and consumed last. Within that ordering, each
 * lot is drained as much as needed before moving on to the next one, so the
 * number of lots touched (and therefore the number of stock movements
 * generated) is kept to a minimum.
 */
public class StockAllocationPolicy {

    private static final Comparator<Stock> FEFO_ORDER = Comparator.comparing(
            Stock::getExpirationDate,
            Comparator.nullsLast(Comparator.naturalOrder())
    );

    /**
     * Allocates {@code quantityToConsume} across {@code availableStocks}.
     * <p>
     * Callers are expected to pass only lots belonging to a single product
     * that still hold a positive quantity; the list does not need to be
     * pre-sorted, as it is re-sorted defensively using FEFO order.
     *
     * @param availableStocks   candidate lots to draw from
     * @param quantityToConsume total quantity requested, must be strictly positive
     * @return the list of per-lot allocations, in the order the lots should be debited
     * @throws InvalidRequestException     if the requested quantity is null or not strictly positive
     * @throws InsufficientStockException  if the available lots cannot cover the requested quantity
     */
    public List<Allocation> allocate(List<Stock> availableStocks, BigDecimal quantityToConsume) {
        if (quantityToConsume == null || quantityToConsume.signum() <= 0) {
            throw new InvalidRequestException("Quantity to consume must be strictly positive");
        }

        List<Stock> orderedLots = new ArrayList<>(availableStocks == null ? List.of() : availableStocks);
        orderedLots.sort(FEFO_ORDER);

        List<Allocation> allocations = new ArrayList<>();
        BigDecimal remaining = quantityToConsume;

        for (Stock lot : orderedLots) {
            if (remaining.signum() <= 0) {
                break;
            }

            BigDecimal lotQuantity = lot.getQuantity() == null ? BigDecimal.ZERO : lot.getQuantity();
            if (lotQuantity.signum() <= 0) {
                continue;
            }

            BigDecimal quantityFromLot = lotQuantity.min(remaining);
            allocations.add(new Allocation(lot, quantityFromLot));
            remaining = remaining.subtract(quantityFromLot);
        }

        if (remaining.signum() > 0) {
            throw new InsufficientStockException(
                    "Unable to allocate the full requested quantity from available stock lots: missing "
                            + remaining);
        }

        return allocations;
    }

    /**
     * A single lot debit resulting from an allocation: the {@code stock} lot
     * to update and the {@code quantityToConsume} to subtract from it.
     */
    public record Allocation(Stock stock, BigDecimal quantityToConsume) {
    }
}
