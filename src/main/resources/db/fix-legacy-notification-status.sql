-- =============================================================================
-- fix-legacy-notification-status.sql
--
-- ONE-SHOT data migration to be executed manually once on the target database,
-- BEFORE deploying a backend version that no longer has the RequestStatusConverter
-- (i.e. one that reads `notifications.status` with a plain
-- @Enumerated(EnumType.STRING)).
--
-- Context: RequestStatus (PENDING / FAILED / SUCCESS) already matches exactly
-- the schema of `notifications.status` defined in laundry.sql. The crash
-- "No enum constant ...RequestStatus.SENT" therefore does not come from a
-- schema or form inconsistency, but from historical rows still present in the
-- real database with the old value 'SENT' (from an earlier version of the
-- enum predating PENDING/FAILED/SUCCESS, never migrated).
--
-- This migration fixes those rows once and for all, instead of indefinitely
-- tolerating the invalid value via an application converter.
-- =============================================================================

-- 'SENT' (notification sent successfully) -> SUCCESS
UPDATE notifications SET status = 'SUCCESS' WHERE status = 'SENT';

-- Verification: should return nothing after the migration.
-- SELECT DISTINCT status FROM notifications
-- WHERE status NOT IN ('PENDING', 'FAILED', 'SUCCESS');