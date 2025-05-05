-- Provide SQL scripts here

CREATE TABLE IF NOT EXISTS shop
(
	id           BIGINT AUTO_INCREMENT PRIMARY KEY,
	name         VARCHAR(255) NOT NULL UNIQUE,
	address1     VARCHAR(255),
	address2     VARCHAR(255),
	city         VARCHAR(100),
	state        VARCHAR(100),
	zip          VARCHAR(20),
	phone_number VARCHAR(30),
	email        VARCHAR(255),
	website      VARCHAR(255),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_shop_name ON shop (name);


INSERT INTO shop (name, address1, address2, city, state, zip, phone_number, email, website)
values ( 'MarcusShop' , '121 Made Up Rd', '', 'MadeUp', 'VA', '22222', '7777777777', '123@123.com', '123.com')