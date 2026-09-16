-- ============================================================
-- Migration: Drop email_templates table
-- ============================================================
-- The email_templates table is no longer used since the refactoring
-- of the notification system which now generates messages dynamically
-- instead of using predefined templates.
--
-- This migration:
-- 1. Drops the foreign key constraint template_id in notifications
-- 2. Drops the template_id column from the notifications table
-- 3. Drops the email_templates table itself
-- ============================================================

-- Drop the foreign key constraint on notifications.template_id
ALTER TABLE notifications DROP FOREIGN KEY notifications_ibfk_3;

-- Drop the template_id column from the notifications table
ALTER TABLE notifications DROP COLUMN template_id;

-- Drop the email_templates table
DROP TABLE IF EXISTS email_templates;

-- ============================================================
-- Migration applied successfully
-- ============================================================