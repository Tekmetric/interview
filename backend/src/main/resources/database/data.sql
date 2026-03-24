-- Transactions table
CREATE TABLE transactions (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,

      amount DECIMAL(19, 4) NOT NULL,
      currency VARCHAR(3) NOT NULL,
      status VARCHAR(20) NOT NULL,

      created_at TIMESTAMP WITH TIME ZONE NOT NULL,
      updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);