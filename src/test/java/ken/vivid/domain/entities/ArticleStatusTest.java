package ken.vivid.domain.entities;

import ken.vivid.domain.dto.ArticleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit tests for the workflow rules carried by {@link ArticleStatus}.
 */
@DisplayName("ArticleStatus (domain)")
class ArticleStatusTest {

    @Test
    @DisplayName("Declaration order is the workflow order")
    void declarationOrderShouldBeTheWorkflowOrder() {
        assertThat(ArticleStatus.values()).containsExactly(
                ArticleStatus.PENDING, ArticleStatus.WASHING, ArticleStatus.IRONING,
                ArticleStatus.QUALITY_CHECK, ArticleStatus.PACKED, ArticleStatus.COMPLETED);
    }

    @ParameterizedTest(name = "{0} -> {1} is allowed")
    @CsvSource({
            "PENDING, WASHING",
            "PENDING, IRONING",
            "PENDING, COMPLETED",
            "WASHING, IRONING",
            "IRONING, QUALITY_CHECK",
            "QUALITY_CHECK, PACKED",
            "PACKED, COMPLETED"
    })
    @DisplayName("Allows moving forward, including skipping intermediate steps")
    void shouldAllowForwardTransitions(ArticleStatus from, ArticleStatus to) {
        assertThat(from.canTransitionTo(to)).isTrue();
    }

    @ParameterizedTest(name = "{0} -> {1} is refused")
    @CsvSource({
            "WASHING, PENDING",
            "IRONING, WASHING",
            "PACKED, QUALITY_CHECK",
            "COMPLETED, PACKED",
            "COMPLETED, PENDING"
    })
    @DisplayName("Refuses moving backward")
    void shouldRefuseBackwardTransitions(ArticleStatus from, ArticleStatus to) {
        assertThat(from.canTransitionTo(to)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(ArticleStatus.class)
    @DisplayName("Refuses staying on the same status")
    void shouldRefuseTransitionToItself(ArticleStatus status) {
        assertThat(status.canTransitionTo(status)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(ArticleStatus.class)
    @DisplayName("Refuses a null target status")
    void shouldRefuseNullTarget(ArticleStatus status) {
        assertThat(status.canTransitionTo(null)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(ArticleStatus.class)
    @DisplayName("COMPLETED is terminal and can go nowhere")
    void completedShouldBeTerminal(ArticleStatus target) {
        assertThat(ArticleStatus.COMPLETED.canTransitionTo(target)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ArticleStatus.class, names = "COMPLETED", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Only COMPLETED is terminal")
    void otherStatusesShouldNotBeTerminal(ArticleStatus status) {
        assertThat(status.isTerminal()).isFalse();
    }

    @Test
    @DisplayName("COMPLETED reports itself as terminal")
    void completedShouldReportTerminal() {
        assertThat(ArticleStatus.COMPLETED.isTerminal()).isTrue();
    }
}