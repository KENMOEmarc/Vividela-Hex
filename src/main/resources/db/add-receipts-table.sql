-- =============================================================================
-- add-receipts-table.sql
--
-- ONE-SHOT migration to be executed manually once on an already deployed
-- database, BEFORE deploying a backend version that now persists the receipt
-- reference for each order (see Receipt / ReceiptRepository /
-- PaymentServiceImpl#reconcileOrderPaymentStatus).
--
-- Context: until now, the receipt number (invoice reference) was randomly
-- regenerated at EACH PDF download (see
-- DocumentReferenceGenerator#generateReceiptReference), without ever being
-- persisted anywhere — two downloads of the same receipt thus produced two
-- different invoice numbers, and no receipt was ever actually "generated"
-- until a user clicked the download button. This table fixes the problem by
-- establishing, once and for all, the receipt number for each order as soon
-- as it reaches paymentStatus=COMPLETED.
--
-- On a fresh installation, this table is already created by laundry.sql:
-- this script only concerns databases already in production before this fix.
-- =============================================================================

CREATE TABLE IF NOT EXISTS receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    reference VARCHAR(50) NOT NULL UNIQUE,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    issued_by BIGINT,
    FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY(issued_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ADDITION (catch‑up): generates a receipt reference for all orders ALREADY
-- fully paid (paymentStatus=COMPLETED) before this fix, so that they also
-- have a stable invoice number from the update onwards, without waiting for
-- a new payment event.
INSERT INTO receipts (order_id, reference, issued_at)
SELECT o.id,
       CONCAT('REC-', UPPER(SUBSTRING(MD5(CONCAT(o.id, '-', RAND())), 1, 8))),
       COALESCE(o.updated_at, CURRENT_TIMESTAMP)
FROM orders o
WHERE o.payment_status = 'COMPLETED'
  AND NOT EXISTS (SELECT 1 FROM receipts r WHERE r.order_id = o.id);