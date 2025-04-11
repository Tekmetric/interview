-- Schema definition for repair_services table
-- Uses IF NOT EXISTS to avoid errors if table already exists

CREATE TABLE IF NOT EXISTS repair_services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    vehicle_make VARCHAR(50) NOT NULL,
    vehicle_model VARCHAR(50) NOT NULL,
    vehicle_year INT NOT NULL,
    license_plate VARCHAR(20) NOT NULL,
    service_description TEXT,
    odometer_reading INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index on commonly searched fields
CREATE INDEX IF NOT EXISTS idx_repair_services_customer_name ON repair_services(customer_name);
CREATE INDEX IF NOT EXISTS idx_repair_services_license_plate ON repair_services(license_plate);
CREATE INDEX IF NOT EXISTS idx_repair_services_status ON repair_services(status);
