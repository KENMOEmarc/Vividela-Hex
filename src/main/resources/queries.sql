-- ============================================================
-- STRUCTURAL ADJUSTMENTS & MISSING TABLES
-- ============================================================

-- Extended columns for orders

-- Service prices table
CREATE TABLE IF NOT EXISTS service_prices (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    clothing_type VARCHAR(50)    NOT NULL,
    service       VARCHAR(50)    NOT NULL,
    price         DECIMAL(10, 2) NOT NULL,
    active        BOOLEAN        NOT NULL DEFAULT TRUE
);

-- Articles table (AUTO_INCREMENT = 5 to align IDs 5 to 17)
CREATE TABLE IF NOT EXISTS articles (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT       NOT NULL,
    clothing_type VARCHAR(50)  NOT NULL,
    size          VARCHAR(20),
    fabric        VARCHAR(50),
    color         VARCHAR(50),
    distinction   VARCHAR(255),
    status        VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
) AUTO_INCREMENT = 5;

-- Article services table
CREATE TABLE IF NOT EXISTS article_services (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id    BIGINT         NOT NULL,
    service       VARCHAR(50)    NOT NULL,
    applied_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);

-- Treatments table
CREATE TABLE IF NOT EXISTS treatments (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id       BIGINT      NOT NULL,
    employee_user_id BIGINT      NOT NULL,
    service          VARCHAR(50) NOT NULL,
    started_at       TIMESTAMP   NOT NULL,
    completed_at     TIMESTAMP   NULL,
    notes            VARCHAR(255),
    FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE,
    FOREIGN KEY (employee_user_id) REFERENCES users (id)
);

-- Tickets table
CREATE TABLE IF NOT EXISTS tickets (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT       NOT NULL,
    barcode  VARCHAR(100) NOT NULL UNIQUE,
    status   VARCHAR(50)  NOT NULL DEFAULT 'GENERATED',
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    order_id          BIGINT       NULL,
    subject           VARCHAR(255) NOT NULL,
    message           TEXT         NOT NULL,
    notification_type VARCHAR(50)  NOT NULL,
    status            VARCHAR(50)  NOT NULL DEFAULT 'SUCCESS',
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE SET NULL
);

-- ============================================================
-- 1. SERVICE PRICES (108 requêtes)
-- ============================================================

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHIRT', 'WASH', 500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHIRT', 'IRON', 300, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHIRT', 'DRY_CLEAN', 1500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHIRT', 'STAIN_REMOVAL', 1000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHIRT', 'DEYING', 1800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHIRT', 'ALTERATION', 1200, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('T_SHIRT', 'WASH', 400, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('T_SHIRT', 'IRON', 200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('T_SHIRT', 'DRY_CLEAN', 1200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('T_SHIRT', 'STAIN_REMOVAL', 900, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('T_SHIRT', 'DEYING', 1500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('T_SHIRT', 'ALTERATION', 1000, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('PANTS', 'WASH', 800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('PANTS', 'IRON', 500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('PANTS', 'DRY_CLEAN', 2000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('PANTS', 'STAIN_REMOVAL', 1500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('PANTS', 'DEYING', 2500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('PANTS', 'ALTERATION', 2000, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JEANS', 'WASH', 900, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JEANS', 'IRON', 500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JEANS', 'DRY_CLEAN', 2200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JEANS', 'STAIN_REMOVAL', 1600, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JEANS', 'DEYING', 2700, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JEANS', 'ALTERATION', 2200, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SKIRT', 'WASH', 900, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SKIRT', 'IRON', 600, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SKIRT', 'DRY_CLEAN', 1800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SKIRT', 'STAIN_REMOVAL', 1200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SKIRT', 'DEYING', 2200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SKIRT', 'ALTERATION', 1800, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('DRESS', 'WASH', 1000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('DRESS', 'IRON', 700, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('DRESS', 'DRY_CLEAN', 2500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('DRESS', 'STAIN_REMOVAL', 1800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('DRESS', 'DEYING', 3000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('DRESS', 'ALTERATION', 2500, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JACKET', 'WASH', 1500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JACKET', 'IRON', 700, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JACKET', 'DRY_CLEAN', 3000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JACKET', 'STAIN_REMOVAL', 2000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JACKET', 'DEYING', 3500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('JACKET', 'ALTERATION', 3000, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('COAT', 'WASH', 2000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('COAT', 'IRON', 1000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('COAT', 'DRY_CLEAN', 4000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('COAT', 'STAIN_REMOVAL', 2500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('COAT', 'DEYING', 4500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('COAT', 'ALTERATION', 3500, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SWEATER', 'WASH', 800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SWEATER', 'IRON', 500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SWEATER', 'DRY_CLEAN', 1800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SWEATER', 'STAIN_REMOVAL', 1200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SWEATER', 'DEYING', 2200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SWEATER', 'ALTERATION', 1800, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BLOUSE', 'WASH', 600, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BLOUSE', 'IRON', 300, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BLOUSE', 'DRY_CLEAN', 1500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BLOUSE', 'STAIN_REMOVAL', 1000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BLOUSE', 'DEYING', 1800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BLOUSE', 'ALTERATION', 1200, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('UNDERWEAR', 'WASH', 200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('UNDERWEAR', 'IRON', 100, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('UNDERWEAR', 'DRY_CLEAN', 600, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('UNDERWEAR', 'STAIN_REMOVAL', 500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('UNDERWEAR', 'DEYING', 800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('UNDERWEAR', 'ALTERATION', 700, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SOCKS', 'WASH', 100, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SOCKS', 'IRON', 50, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SOCKS', 'DRY_CLEAN', 400, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SOCKS', 'STAIN_REMOVAL', 300, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SOCKS', 'DEYING', 500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SOCKS', 'ALTERATION', 500, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SUIT', 'WASH', 2500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SUIT', 'IRON', 1200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SUIT', 'DRY_CLEAN', 5000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SUIT', 'STAIN_REMOVAL', 3000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SUIT', 'DEYING', 6000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SUIT', 'ALTERATION', 4000, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('VEST', 'WASH', 600, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('VEST', 'IRON', 300, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('VEST', 'DRY_CLEAN', 1500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('VEST', 'STAIN_REMOVAL', 1000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('VEST', 'DEYING', 1800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('VEST', 'ALTERATION', 1200, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHORTS', 'WASH', 500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHORTS', 'IRON', 300, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHORTS', 'DRY_CLEAN', 1300, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHORTS', 'STAIN_REMOVAL', 900, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHORTS', 'DEYING', 1600, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SHORTS', 'ALTERATION', 1000, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SCARF', 'WASH', 400, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SCARF', 'IRON', 200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SCARF', 'DRY_CLEAN', 1200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SCARF', 'STAIN_REMOVAL', 800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SCARF', 'DEYING', 1500, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('SCARF', 'ALTERATION', 900, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('GLOVES', 'WASH', 400, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('GLOVES', 'IRON', 150, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('GLOVES', 'DRY_CLEAN', 1000, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('GLOVES', 'STAIN_REMOVAL', 800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('GLOVES', 'DEYING', 1400, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('GLOVES', 'ALTERATION', 900, TRUE);

INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BELT', 'WASH', 300, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BELT', 'IRON', 100, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BELT', 'DRY_CLEAN', 800, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BELT', 'STAIN_REMOVAL', 700, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BELT', 'DEYING', 1200, TRUE);
INSERT INTO service_prices (clothing_type, service, price, active) VALUES ('BELT', 'ALTERATION', 1000, TRUE);

-- ============================================================
-- 2. COMMANDES (5 requêtes)
-- ============================================================

INSERT INTO orders (client_user_id, deposit_date, expected_delivery_date, status, payment_status, total_amount, discount_amount)
VALUES (5, '2026-06-25', '2026-06-28', 'IN_PROGRESS', 'PENDING', 8600.00, 0.00);

INSERT INTO orders (client_user_id, deposit_date, expected_delivery_date, status, payment_status, total_amount, discount_amount)
VALUES (6, '2026-06-26', '2026-06-29', 'RECEIVED', 'PENDING', 7300.00, 0.00);

INSERT INTO orders (client_user_id, deposit_date, expected_delivery_date, status, payment_status, total_amount, discount_amount)
VALUES (5, '2026-06-27', '2026-06-30', 'PENDING', 'PENDING', 12400.00, 500.00);

INSERT INTO orders (client_user_id, deposit_date, expected_delivery_date, status, payment_status, total_amount, discount_amount)
VALUES (6, '2026-06-28', '2026-07-01', 'READY', 'COMPLETED', 9800.00, 0.00);

INSERT INTO orders (client_user_id, deposit_date, expected_delivery_date, status, payment_status, total_amount, discount_amount)
VALUES (5, '2026-06-29', '2026-07-02', 'IN_PROGRESS', 'COMPLETED', 5400.00, 0.00);

-- ============================================================
-- 3. ARTICLES DES COMMANDES (13 requêtes)
-- ============================================================

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (3, 'SHIRT', 'L', 'COTTON', 'Bleu ciel', 'Chemise manches longues', 'IRONING');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (3, 'PANTS', 'L', 'COTTON', 'Noir', 'Jean Levis', 'WASHING');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (3, 'JACKET', 'XL', 'WOOL', 'Gris', 'Veste costume', 'QUALITY_CHECK');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (4, 'DRESS', 'M', 'SILK', 'Rouge', 'Robe de soirée', 'PENDING');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (4, 'SKIRT', 'S', 'COTTON', 'Jaune', 'Jupe Wax', 'PENDING');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (4, 'BLOUSE', 'M', 'POLYESTER', 'Blanc', 'Chemisier bureau', 'PENDING');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (5, 'SUIT', 'XL', 'WOOL', 'Noir', 'Costume mariage', 'PACKED');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (5, 'SHIRT', 'XL', 'COTTON', 'Blanc', 'Chemise cérémonie', 'PACKED');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (5, 'BELT', 'UNIQUE', 'LEATHER', 'Noir', 'Ceinture cuir', 'PACKED');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (6, 'COAT', 'XL', 'WOOL', 'Marron', 'Manteau hiver', 'COMPLETED');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (6, 'SCARF', 'UNIQUE', 'WOOL', 'Rouge', 'Echarpe laine', 'COMPLETED');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (7, 'JEANS', 'L', 'COTTON', 'Bleu', 'Jean Slim', 'WASHING');

INSERT INTO articles (order_id, clothing_type, size, fabric, color, distinction, status)
VALUES (7, 'T_SHIRT', 'M', 'COTTON', 'Blanc', 'T-shirt Nike', 'PENDING');

-- ============================================================
-- 4. SERVICES APPLIQUES AUX ARTICLES (21 requêtes)
-- ============================================================

INSERT INTO article_services (article_id, service, applied_price) VALUES (5, 'WASH', 500);
INSERT INTO article_services (article_id, service, applied_price) VALUES (5, 'IRON', 300);
INSERT INTO article_services (article_id, service, applied_price) VALUES (6, 'WASH', 800);
INSERT INTO article_services (article_id, service, applied_price) VALUES (6, 'STAIN_REMOVAL', 1500);
INSERT INTO article_services (article_id, service, applied_price) VALUES (7, 'DRY_CLEAN', 3000);
INSERT INTO article_services (article_id, service, applied_price) VALUES (8, 'DRY_CLEAN', 2500);
INSERT INTO article_services (article_id, service, applied_price) VALUES (9, 'WASH', 900);
INSERT INTO article_services (article_id, service, applied_price) VALUES (9, 'IRON', 600);
INSERT INTO article_services (article_id, service, applied_price) VALUES (10, 'WASH', 600);
INSERT INTO article_services (article_id, service, applied_price) VALUES (10, 'IRON', 300);
INSERT INTO article_services (article_id, service, applied_price) VALUES (11, 'DRY_CLEAN', 4500);
INSERT INTO article_services (article_id, service, applied_price) VALUES (11, 'IRON', 1000);
INSERT INTO article_services (article_id, service, applied_price) VALUES (12, 'DRY_CLEAN', 1500);
INSERT INTO article_services (article_id, service, applied_price) VALUES (12, 'IRON', 300);
INSERT INTO article_services (article_id, service, applied_price) VALUES (13, 'ALTERATION', 1500);
INSERT INTO article_services (article_id, service, applied_price) VALUES (14, 'DRY_CLEAN', 4000);
INSERT INTO article_services (article_id, service, applied_price) VALUES (15, 'WASH', 600);
INSERT INTO article_services (article_id, service, applied_price) VALUES (16, 'WASH', 800);
INSERT INTO article_services (article_id, service, applied_price) VALUES (16, 'IRON', 500);
INSERT INTO article_services (article_id, service, applied_price) VALUES (17, 'WASH', 400);
INSERT INTO article_services (article_id, service, applied_price) VALUES (17, 'IRON', 300);

-- ============================================================
-- 5. TRAITEMENTS EFFECTUES PAR LES EMPLOYES (7 requêtes)
-- ============================================================

INSERT INTO treatments (article_id, employee_user_id, service, started_at, completed_at, notes)
VALUES (5, 3, 'WASH', '2026-06-25 08:00:00', '2026-06-25 09:30:00', 'Lavage standard');

INSERT INTO treatments (article_id, employee_user_id, service, started_at, completed_at, notes)
VALUES (5, 4, 'IRON', '2026-06-25 10:00:00', '2026-06-25 10:30:00', 'Repassage vapeur');

INSERT INTO treatments (article_id, employee_user_id, service, started_at, completed_at, notes)
VALUES (6, 3, 'WASH', '2026-06-25 08:30:00', NULL, 'Cycle délicat');

INSERT INTO treatments (article_id, employee_user_id, service, started_at, completed_at, notes)
VALUES (7, 4, 'DRY_CLEAN', '2026-06-25 09:00:00', NULL, 'Nettoyage à sec');

INSERT INTO treatments (article_id, employee_user_id, service, started_at, completed_at, notes)
VALUES (8, 3, 'DRY_CLEAN', '2026-06-26 08:00:00', NULL, 'Soie');

INSERT INTO treatments (article_id, employee_user_id, service, started_at, completed_at, notes)
VALUES (11, 4, 'DRY_CLEAN', '2026-06-27 09:00:00', '2026-06-27 11:30:00', 'Costume');

INSERT INTO treatments (article_id, employee_user_id, service, started_at, completed_at, notes)
VALUES (14, 3, 'DRY_CLEAN', '2026-06-28 10:00:00', '2026-06-28 13:00:00', 'Manteau laine');

-- ============================================================
-- 6. PAIEMENTS (5 requêtes - payment_method_type aligné)
-- ============================================================

INSERT INTO payments (order_id, payment_method_type, amount, payer_phone, transaction_reference, status, created_by)
VALUES (3, 'MOBILE_PAYMENT', 8600.00, '237698765432', 'OM-260625-001', 'PENDING', 2);

INSERT INTO payments (order_id, payment_method_type, amount, payer_phone, transaction_reference, status, created_by)
VALUES (4, 'CASH', 7300.00, NULL, NULL, 'PENDING', 2);

INSERT INTO payments (order_id, payment_method_type, amount, payer_phone, transaction_reference, status, created_by)
VALUES (5, 'CHECK', 12400.00, NULL, 'CHQ-00125', 'PENDING', 2);

INSERT INTO payments (order_id, payment_method_type, amount, payer_phone, transaction_reference, status, created_by)
VALUES (6, 'MOBILE_PAYMENT', 9800.00, '237670998877', 'MOMO-260628-001', 'COMPLETED', 2);

INSERT INTO payments (order_id, payment_method_type, amount, payer_phone, transaction_reference, status, created_by)
VALUES (7, 'CASH', 5400.00, NULL, NULL, 'COMPLETED', 2);

-- ============================================================
-- 7. TICKETS (5 requêtes)
-- ============================================================

INSERT INTO tickets (order_id, barcode, status) VALUES (3, 'PRS-003-CMR', 'GENERATED');
INSERT INTO tickets (order_id, barcode, status) VALUES (4, 'PRS-004-CMR', 'GENERATED');
INSERT INTO tickets (order_id, barcode, status) VALUES (5, 'PRS-005-CMR', 'GENERATED');
INSERT INTO tickets (order_id, barcode, status) VALUES (6, 'PRS-006-CMR', 'GENERATED');
INSERT INTO tickets (order_id, barcode, status) VALUES (7, 'PRS-007-CMR', 'GENERATED');

-- ============================================================
-- 8. NOTIFICATIONS (2 requêtes)
-- ============================================================

INSERT INTO notifications (user_id, order_id, subject, message, notification_type, status)
VALUES (5, 3, 'Commande en cours', 'Votre commande est actuellement en cours de traitement.', 'IN_APP', 'SUCCESS');

INSERT INTO notifications (user_id, order_id, subject, message, notification_type, status)
VALUES (6, 6, 'Commande prête', 'Vos vêtements sont prêts pour le retrait.', 'SMS', 'SUCCESS');