package ken.vivid.domain.dto;

/**
 * Processing status of an article inside the laundry workflow.
 * <p>
 * Declaration order is the workflow order. A status can only move forward;
 * intermediate steps may be skipped because they do not apply to every
 * service (e.g. a dry-cleaned article never goes through WASHING).
 * COMPLETED is terminal.
 */
public enum ArticleStatus {
    PENDING,
    WASHING,
    IRONING,
    QUALITY_CHECK,
    PACKED,
    COMPLETED;

    public boolean canTransitionTo(ArticleStatus next) {
        return next != null && next.ordinal() > this.ordinal();
    }

    public boolean isTerminal() {
        return this == COMPLETED;
    }
}
