package ken.vivid.adapter.input.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ken.vivid.domain.entities.Stock;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StockDto(
        Long id,
        Long productId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        Instant entryDate,
        Instant expirationDate,
        Instant updatedAt
) {
    public static StockDto from(Stock stock) {
        return StockDto.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .quantity(stock.getQuantity())
                .unitPrice(stock.getUnitPrice())
                .entryDate(stock.getEntryDate())
                .expirationDate(stock.getExpirationDate())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }

}
