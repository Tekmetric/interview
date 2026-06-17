-- V7__Insert_service_packages_sample_data.sql
-- Insert sample service packages and customer subscriptions

-- Insert sample service packages
INSERT INTO service_packages (name, description, monthly_price, active, created_date, updated_date, created_by,
                              updated_by)
VALUES ('Basic Maintenance', 'Essential vehicle maintenance including oil changes and basic inspections', 29.99, TRUE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
       ('Premium Care', 'Comprehensive maintenance package with priority scheduling and discounts', 59.99, TRUE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
       ('Performance Package', 'Specialized services for high-performance vehicles and sports cars', 89.99, TRUE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
       ('Fleet Management', 'Bulk maintenance services for commercial vehicle fleets', 199.99, TRUE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
       ('Legacy Basic', 'Discontinued basic package - replaced by Basic Maintenance', 24.99, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
       ('Seasonal Special', 'Limited time winter maintenance package - temporarily inactive', 39.99, FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM');

-- Insert sample customer subscriptions
INSERT INTO customer_service_packages (customer_id, service_package_id)
VALUES
    -- John Doe subscribed to Basic Maintenance and Premium Care
    (1, 1),
    (1, 2),

    -- Jane Smith subscribed to Premium Care
    (2, 2),

    -- Bob Johnson subscribed to Basic Maintenance and Fleet Management
    (3, 1),
    (3, 4);