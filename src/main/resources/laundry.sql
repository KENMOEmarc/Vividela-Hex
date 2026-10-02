-- ============================================================
-- DATABASE FOR A PRESSING SHOP IN CAMEROON
-- ============================================================
-- Schema aligned with the DOMAIN layer (ken.vivid.domain.*):
-- one table per domain entity, and only the attributes carried by
-- the domain classes. Anything absent from the domain was removed.
--
--   users                  <-> User            (Role)
--   orders                 <-> Order           (OrderStatus, PaymentStatus)
--   payments               <-> Payment         (PaymentMethodType, PaymentStatus)
--   products               <-> Product         (MeasurementUnit)
--   stocks                 <-> Stock
--   product_registrations  <-> ProductRegistration (RegistrationType)
--   stock_movements        <-> StockMovement   (MovementType)
--   revoked_tokens         <-> RevokedTokenJpaEntity (JWT blacklist, no domain class)
--
-- ENUM values match the Java enums value by value and order by order.
-- Any change to a Java enum must be reflected HERE first.
-- ============================================================

-- ============================================================
-- 1. TABLES
-- ============================================================

DROP DATABASE IF EXISTS defaultdb;
CREATE DATABASE defaultdb;
USE defaultdb;

-- Users (role <-> Role)
-- Removed (not in User): created_by, created_at, updated_at.
-- phone is NOT NULL: User.createUser rejects a blank phone.
CREATE TABLE users
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    role           ENUM('ADMIN','MANAGER','EMPLOYEE','CUSTOMER') NOT NULL,
    first_name     VARCHAR(255) NOT NULL,
    last_name      VARCHAR(255) NOT NULL,
    user_name      VARCHAR(50)  NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL UNIQUE,
    phone          VARCHAR(255) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    loyalty_points INT          NOT NULL DEFAULT 0,
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Orders (status <-> OrderStatus, payment_status <-> PaymentStatus)
-- Removed (not in Order): shipping_address, loyalty_points_used,
-- loyalty_processed, created_by, updated_by.
-- deposit_date stores Order.orderDate (column name kept as mapped by OrderJpaEntity).
CREATE TABLE orders
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_user_id         BIGINT         NOT NULL,
    deposit_date           DATE           NOT NULL,
    expected_delivery_date DATE,
    delivered_at           DATE,
    status                 ENUM('RECEIVED','PENDING','IN_PROGRESS','READY','DELIVERED','CANCELLED') NOT NULL DEFAULT 'RECEIVED',
    payment_status         ENUM('PENDING','COMPLETED','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    notes                  VARCHAR(1000),
    total_amount           DECIMAL(10, 2) NOT NULL DEFAULT 0,
    discount_amount        DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at             TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_user_id) REFERENCES users (id),
    CHECK (total_amount >= 0),
    CHECK (discount_amount >= 0),
    CHECK (discount_amount <= total_amount)
);

-- Payments (payment_method_type <-> PaymentMethodType, status <-> PaymentStatus)
-- Removed (not in Payment): payment_date.
-- Added (in Payment): created_at, updated_at.
CREATE TABLE payments
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id              BIGINT         NOT NULL,
    payment_method_type   ENUM('CASH','CHECK','MOBILE_PAYMENT') NOT NULL,
    amount                DECIMAL(10, 2) NOT NULL,
    payer_phone           VARCHAR(255),
    transaction_reference VARCHAR(255),
    status                ENUM('PENDING','COMPLETED','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    paid_at               TIMESTAMP      NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            BIGINT,
    created_at            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CHECK (amount > 0)
);

-- Products (measurement_unit <-> MeasurementUnit)
CREATE TABLE products
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100)   NOT NULL UNIQUE,
    threshold_value  DECIMAL(12, 2) NOT NULL DEFAULT 0,
    measurement_unit ENUM('LITER','UNIT','KG','ML','PACKET') NOT NULL,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (threshold_value >= 0)
);

-- Stocks: a product can have MULTIPLE batches (product_id is not UNIQUE).
-- Removed (not in Stock): created_at. Added (in Stock): updated_at.
-- unit_price and expiration_date are NOT NULL: Stock.createStock rejects null values.
CREATE TABLE stocks
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id       BIGINT         NOT NULL,
    current_quantity DECIMAL(12, 2) NOT NULL DEFAULT 0,
    unit_price       DECIMAL(12, 2) NOT NULL,
    entry_date       DATE           NOT NULL DEFAULT (CURRENT_DATE),
    expiration_date  DATE           NOT NULL,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CHECK (current_quantity >= 0),
    CHECK (unit_price >= 0),
    CHECK (expiration_date >= entry_date)
);

-- Product registrations (registration_type <-> RegistrationType: IN, ADJUSTMENT)
-- Removed (not in ProductRegistration): employee_user_id, registration type 'SACHET'.
-- notes is NOT NULL: ProductRegistration rejects blank notes.
CREATE TABLE product_registrations
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id        BIGINT         NOT NULL,
    quantity          DECIMAL(12, 2) NOT NULL,
    registration_type ENUM('IN','ADJUSTMENT') NOT NULL,
    notes             VARCHAR(255)   NOT NULL,
    registered_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products (id),
    CHECK (quantity >= 0)
);

-- Stock movements (movement_type <-> MovementType)
-- Removed (not in StockMovement): treatment_id.
-- notes is NOT NULL: StockMovement rejects blank notes.
CREATE TABLE stock_movements
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_id      BIGINT         NOT NULL,
    user_id       BIGINT         NOT NULL,
    quantity      DECIMAL(12, 2) NOT NULL,
    movement_type ENUM('CONSUMPTION','RESTOCK','ADJUSTMENT') NOT NULL,
    notes         VARCHAR(255)   NOT NULL,
    movement_date TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (stock_id) REFERENCES stocks (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id),
    CHECK (quantity >= 0)
);

-- Revoked JWT tokens (RevokedTokenJpaEntity)
CREATE TABLE revoked_tokens
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    token      VARCHAR(512) NOT NULL UNIQUE,
    revoked_at DATETIME(6)  NOT NULL
);

-- ============================================================
-- 2. HISTORICAL DATA REPAIR (idempotent)
-- ------------------------------------------------------------
-- No effect on a fresh database. Protects a re-execution on a
-- database populated by an old version whose payment statuses
-- were not aligned with the current PaymentStatus enum.
-- ============================================================
UPDATE payments
SET status = 'COMPLETED'
WHERE status IN ('PAID', 'PAYE', 'VALIDE');
UPDATE payments
SET status = 'REFUNDED'
WHERE status = 'REFUND';

-- ============================================================
-- 3. DATA INSERTS (CAMEROON REALITIES)
-- ============================================================

-- Users (ids 1..10)
INSERT INTO users (role, first_name, last_name, user_name, email, phone, password, loyalty_points, is_active)
VALUES ('ADMIN', 'Jean-Pierre', 'Mbarga', 'jpmbarga', 'jp.mbarga@pressingplus.cm', '237699887766',
        SHA2('Admin@2025', 256), 0, TRUE),
       ('MANAGER', 'Sophie', 'Ngo Mbock', 'sngombock', 's.ngombock@pressingplus.cm', '237677112233',
        SHA2('Manager@2025', 256), 0, TRUE),
       ('EMPLOYEE', 'Alain', 'Etoundi', 'aetoundi', 'alain.etoundi@pressingplus.cm', '237655443322',
        SHA2('Employe@2025', 256), 0, TRUE),
       ('EMPLOYEE', 'Christelle', 'Bikanda', 'cbikanda', 'christelle.bikanda@pressingplus.cm', '237690123456',
        SHA2('Employe@2025', 256), 0, TRUE),
       ('CUSTOMER', 'Luc', 'Owona', 'lowona', 'luc.owona@gmail.com', '237698765432', SHA2('Client@2025', 256), 150,
        TRUE),
       ('CUSTOMER', 'Marie', 'Essimi', 'messimi', 'marie.essimi@yahoo.fr', '237670998877', SHA2('Client@2025', 256), 80,
        TRUE),
       ('ADMIN', 'Kenmoe', 'KEMGANG', '_ken_', 'ken@gmail.com', '237699000001',
        '$2a$12$xdKVfWN4X0ujnQEZu2Sgo.ZNCkcu6bfqlDRuOj3VdNztW0IlPxcNa', 0, TRUE),
       ('MANAGER', 'Aline', 'Nguemo', 'anguemo', 'aline.nguemo@vividela.cm', '237699000002',
        '$2a$12$ER8ZFIR1dSCCz6zW44gjn.RJsEzfQnEuUmJZAceEcnHLwfsODucje', 0, TRUE),
       ('EMPLOYEE', 'Boris', 'Tchoumi', 'btchoumi', 'boris.tchoumi@vividela.cm', '237699000003',
        '$2a$12$ER8ZFIR1dSCCz6zW44gjn.RJsEzfQnEuUmJZAceEcnHLwfsODucje', 0, TRUE),
       ('CUSTOMER', 'Estelle', 'Njoya', 'enjoya', 'estelle.njoya@vividela.cm', '237699000004',
        '$2a$12$ER8ZFIR1dSCCz6zW44gjn.RJsEzfQnEuUmJZAceEcnHLwfsODucje', 0, TRUE);

-- Products (consumables)
INSERT INTO products (name, threshold_value, measurement_unit)
VALUES ('Lessive Omo', 5.00, 'KG'),
       ('Détachant local', 2.00, 'LITER'),
       ('Assouplissant', 3.00, 'LITER'),
       ('Carton de protection', 50.00, 'UNIT'),
       ('Cintre', 100.00, 'UNIT'),
       ('Sachet plastique', 200.00, 'PACKET');

-- Initial stock (one batch per product; unit_price in CFA francs)
INSERT INTO stocks (product_id, current_quantity, unit_price, expiration_date)
VALUES (1, 20.50, 1500.00, '2027-12-31'),
       (2, 8.00, 2500.00, '2027-12-31'),
       (3, 12.00, 2000.00, '2027-12-31'),
       (4, 200.00, 150.00, '2030-12-31'),
       (5, 500.00, 100.00, '2030-12-31'),
       (6, 300.00, 50.00, '2030-12-31');

-- Order #1 from Luc Owona (id=5): ready, paid
INSERT INTO orders (client_user_id, deposit_date, expected_delivery_date, status, payment_status, total_amount)
VALUES (5, '2026-06-15', '2026-06-18', 'READY', 'COMPLETED', 3800);

-- Mobile Money payment (fully settles order #1)
INSERT INTO payments (order_id, payment_method_type, amount, payer_phone, transaction_reference, status, created_by)
VALUES (1, 'MOBILE_PAYMENT', 3800, '237698765432', 'OM-20260615-001', 'COMPLETED', 1);

-- Order #2 from Marie Essimi (id=6): in progress, payment pending
INSERT INTO orders (client_user_id, deposit_date, expected_delivery_date, status, payment_status, total_amount)
VALUES (6, '2026-06-20', '2026-06-23', 'IN_PROGRESS', 'PENDING', 4000);

-- Stock movement (consumption by employee Christelle, id=4)
INSERT INTO stock_movements (stock_id, user_id, quantity, movement_type, notes)
VALUES (1, 4, 0.5, 'CONSUMPTION', 'Utilisé pour lavage commande #2');

-- Restocking
INSERT INTO product_registrations (product_id, quantity, registration_type, notes)
VALUES (1, 5.00, 'IN', 'Approvisionnement hebdomadaire');

-- Manual stock update (no trigger in this script)
UPDATE stocks
SET current_quantity = current_quantity - 0.5
WHERE product_id = 1;
UPDATE stocks
SET current_quantity = current_quantity + 5
WHERE product_id = 1;

-- Order #3 from Luc Owona: cancelled after deposit, payment refunded
-- (covers the CANCELLED / REFUNDED statuses)
INSERT INTO orders (client_user_id, deposit_date, expected_delivery_date, status, payment_status, total_amount)
VALUES (5, '2026-06-25', '2026-06-28', 'CANCELLED', 'REFUNDED', 1500);

INSERT INTO payments (order_id, payment_method_type, amount, payer_phone, transaction_reference, status, created_by)
VALUES (3, 'MOBILE_PAYMENT', 1500, '237698765432', 'OM-20260625-002', 'REFUNDED', 1);

-- ============================================================
-- END OF SCRIPT
-- ============================================================