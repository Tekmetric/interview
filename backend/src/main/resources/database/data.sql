-- Provide SQL scripts here

CREATE TABLE IF NOT EXISTS vehicle_recall (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR (255) NOT NULL,
    model VARCHAR (255) NOT NULL,
    model_year INT NOT NULL,
    recall_description TEXT NOT NULL,
    recall_date DATE NOT NULL
);