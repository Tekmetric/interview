CREATE TABLE IF NOT EXISTS car (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vin VARCHAR(17) NOT NULL UNIQUE,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    manufactured_year INT NOT NULL,
    color VARCHAR(50),
    fuel_type VARCHAR(20) NOT NULL,
    transmission VARCHAR(50),
    base_price DECIMAL(12,2) NOT NULL,
    selling_price DECIMAL(12,2),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_fuel_type CHECK (fuel_type IN ('GASOLINE', 'DIESEL', 'ELECTRIC', 'HYBRID')),
    CONSTRAINT chk_status CHECK (status IN ('AVAILABLE', 'RESERVED', 'SOLD')),
    CONSTRAINT chk_selling_price_status CHECK (
        (status IN ('RESERVED', 'SOLD') AND selling_price IS NOT NULL)
        OR
        (status = 'AVAILABLE' AND selling_price IS NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_car_status_brand_price ON car(status, brand, base_price);
