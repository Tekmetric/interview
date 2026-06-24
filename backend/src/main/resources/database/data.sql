
INSERT INTO customer_invoices (invoice_number, version, status, customer_id, notes, payment_instructions, issue_date, due_date, paid_date, created_at, created_by, updated_at, updated_by)
VALUES ('SAMPLE_001', 1, 'ISSUED', 'CUST_1001', 'Full brake system replacement', 'Bank transfer within 15 days.', '2025-10-10', '2025-10-25', NULL, '2025-10-10 09:00:00', 'auth0|u-2395702', '2025-10-10 10:00:00', 'auth0|u-2395702'),
       ('SAMPLE_002', 1, 'PAID', 'CUST_1002', 'Regular maintenance and oil change', 'Paid in cash.', '2025-09-20', '2025-09-30', '2025-09-28', '2025-09-20 09:00:00', 'auth0|u-2395702', '2025-09-28 18:00:00','auth0|u-2395702'),
       ('SAMPLE_003', 1, 'DRAFT', 'CUST_1003', 'Pending approval for engine diagnostics', NULL, '2025-10-28', NULL, NULL, '2025-10-28 09:00:00', 'auth0|u-2395702', '2025-10-28 09:30:00', 'auth0|u-2395702'),
       ('SAMPLE_004', 1, 'ISSUED', 'CUST_1004', 'Wheel alignment and tire rotation', 'Card payment accepted on site.', '2025-09-25', '2025-10-10', NULL, '2025-09-25 09:00:00', 'auth0|u-2395702', '2025-09-25 09:15:00', 'auth0|u-2395702'),
       ('SAMPLE_005', 1, 'PAID', 'CUST_1005', 'Clutch replacement', 'Payment received via PayPal.', '2025-08-14', '2025-08-29', '2025-08-25', '2025-08-14 09:00:00', 'auth0|u-2395702', '2025-08-25 17:45:00', 'auth0|u-2395702'),
       ('SAMPLE_006', 1, 'CANCELLED', 'CUST_1001', 'Customer canceled before repair start.', NULL, '2025-10-05', NULL, NULL, '2025-10-05 09:00:00', 'auth0|u-2395702', '2025-10-05 11:00:00', 'auth0|u-2395702'),
       ('SAMPLE_007', 1, 'ISSUED', 'CUST_1006', 'Transmission service', 'Payment on delivery.', '2025-10-18', '2025-11-02', NULL, '2025-10-18 09:00:00', 'auth0|u-2395702', '2025-10-18 09:20:00', 'auth0|u-2395702'),
       ('SAMPLE_008', 1, 'PAID', 'CUST_1007', 'Battery and alternator check', 'Online payment.', '2025-09-05', '2025-09-20', '2025-09-15', '2025-09-05 09:00:00', 'auth0|u-2395702', '2025-09-15 17:30:00', 'auth0|u-2395702'),
       ('SAMPLE_009', 1, 'ISSUED', 'CUST_1002', 'Exhaust system replacement', 'Bank wire transfer.', '2025-10-21', '2025-11-05', NULL, '2025-10-21 09:00:00', 'auth0|u-2395702', '2025-10-21 09:30:00', 'auth0|u-2395702'),
       ('SAMPLE_010', 1, 'DRAFT', 'CUST_1001', 'Diagnostic estimate', NULL, '2025-10-30', NULL, NULL, '2025-10-30 09:00:00', 'auth0|u-2395702', '2025-10-30 09:05:00', 'auth0|u-2395702');

INSERT INTO customer_invoice_items
    (invoice_number, item_code, item_name, quantity, unit_price, tax_rate)
VALUES ('SAMPLE_001', 'BRK001', 'Front Brake Pads', 1, 180.00, 0.07),
       ('SAMPLE_001', 'BRK002', 'Brake Fluid Replacement', 1, 45.00, 0.07),
       ('SAMPLE_002', 'OIL001', 'Engine Oil Change', 1, 70.00, 0.07),
       ('SAMPLE_002', 'FLT001', 'Oil Filter', 1, 25.00, 0.07),
       ('SAMPLE_004', 'TIR001', 'Tire Rotation', 1, 40.00, 0.07),
       ('SAMPLE_004', 'ALI001', 'Wheel Alignment', 1, 80.00, 0.07),
       ('SAMPLE_005', 'CLT001', 'Clutch Kit', 1, 450.00, 5.00),
       ('SAMPLE_005', 'LAB001', 'Labor - Clutch Replacement (hr)', 5, 60.00, 0.07),
       ('SAMPLE_007', 'TRN001', 'Transmission Fluid (qt)', 3, 25.00, 0.07),
       ('SAMPLE_007', 'LAB002', 'Labor - Transmission Service (hr)', 4, 85.00, 0.07),
       ('SAMPLE_008', 'BAT001', 'Battery Replacement', 1, 120.00, 0.07),
       ('SAMPLE_008', 'ALT001', 'Alternator Test', 1, 35.00, 0.07),
       ('SAMPLE_009', 'EXH001', 'Muffler Assembly', 1, 210.00, 0.07),
       ('SAMPLE_009', 'EXH002', 'Labor - Exhaust Replacement (hr)', 3, 80.00, 0.07);
