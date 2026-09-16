-- =============================================================================
-- fix-legacy-stocks-schema.sql
--
-- ONE-SHOT schema migration to be executed manually once on any MySQL database
-- created BEFORE the stock model evolution (switching from "1 product = 1 stock row"
-- to "1 product = multiple batches").
--
-- Context: the Stock entity (see Stock.java) now expects the columns
-- entry_date, expiration_date and unit_price on the `stocks` table, as defined
-- in laundry.sql. On a database already created with the old schema version
-- (product_id UNIQUE, without these columns), Hibernate does not reliably add
-- them via ddl-auto=update (NOT NULL column without an explicit default for
-- existing rows), causing a runtime error:
--   "Unknown column 's1_0.entry_date' in 'field list'"
-- as soon as a stock batch is loaded (e.g. GET /api/stock/product/{id}).
--
-- This migration idempotently adds the missing columns (no effect if they already
-- exist, so it is safe to re-run) and removes the old UNIQUE constraint on
-- product_id if it is still present, since a product can now have multiple lots.
-- =============================================================================

-- 1) entry_date column (batch receipt date), always NOT NULL with a default
--    value to avoid breaking existing rows.
ALTER TABLE stocks
    ADD COLUMN IF NOT EXISTS entry_date DATE NOT NULL DEFAULT (CURRENT_DATE) AFTER unit_price;

-- 2) expiration_date column (optional: some products never expire).
ALTER TABLE stocks
    ADD COLUMN IF NOT EXISTS expiration_date DATE NULL AFTER entry_date;

-- 3) unit_price column (optional), in case it is also missing on an even older database.
ALTER TABLE stocks
    ADD COLUMN IF NOT EXISTS unit_price DECIMAL(12,2) NULL AFTER current_quantity;

-- 4) created_at column, in case a very old database does not have it either.
ALTER TABLE stocks
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 5) A product can now have multiple batches: drop the old UNIQUE constraint on
--    product_id if it still exists (usually named "product_id" by MySQL, but adapt
--    if your constraint has a different name — verify beforehand with:
--      SHOW INDEX FROM stocks WHERE Non_unique = 0 AND Key_name <> 'PRIMARY';
--    ). Silently ignore the error if the constraint does not exist anymore.
SET @constraint_exists := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'stocks'
      AND COLUMN_NAME = 'product_id'
      AND NON_UNIQUE = 0
      AND INDEX_NAME <> 'PRIMARY'
);
SET @idx_name := (
    SELECT INDEX_NAME FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'stocks'
      AND COLUMN_NAME = 'product_id'
      AND NON_UNIQUE = 0
      AND INDEX_NAME <> 'PRIMARY'
    LIMIT 1
);
SET @drop_sql := IF(@constraint_exists > 0,
    CONCAT('ALTER TABLE stocks DROP INDEX `', @idx_name, '`'),
    'SELECT 1');
PREPARE stmt FROM @drop_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verification: the query below should now execute without error.
-- SELECT id, product_id, current_quantity, unit_price, entry_date,
--        expiration_date, created_at
-- FROM stocks;