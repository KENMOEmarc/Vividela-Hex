package ken.vivid.domain.dto;

/**
 * Services that can be applied to an article.
 * Shared by the Article and ServicePrice modules; values match the
 * {@code service} column of {@code article_services} and {@code service_prices}.
 * <p>
 * NOTE: {@code DEYING} keeps the spelling used by the existing SQL data
 * (queries.sql) so that persisted rows remain readable.
 */
public enum ServiceType {
    WASH,
    IRON,
    DRY_CLEAN,
    STAIN_REMOVAL,
    DEYING,
    ALTERATION
}
