-- =============================================================================
-- add-notification-retry-columns.sql
--
-- Schema migration to be executed manually on any environment where
-- `spring.jpa.hibernate.ddl-auto` is set to `validate` (typically the
-- `prod` profile — see application-prod.yml), BEFORE deploying the version
-- of the backend that introduces automatic retry of failed
-- EMAIL/SMS notifications (see NotificationServiceImpl —
-- retryFailedChannelNotifications / escalateExhaustedNotificationFailures).
--
-- In the `dev` profile (ddl-auto: update), Hibernate already adds these columns
-- automatically at startup — this script is not needed there.
-- =============================================================================

ALTER TABLE notifications
    ADD COLUMN retry_count INT NOT NULL DEFAULT 0,
    ADD COLUMN last_attempt_at TIMESTAMP NULL,
    ADD COLUMN escalated BOOLEAN NOT NULL DEFAULT FALSE;

-- Verification: notifications already failed before the migration must
-- remain eligible for retry (retry_count = 0 by default, escalated =
-- false by default) — no additional action is needed here.