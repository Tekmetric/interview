DROP TABLE IF EXISTS repairjob;

CREATE TABLE repairjob (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_name VARCHAR(50),
  user_id VARCHAR(36),
  repair_description VARCHAR(255),
  license_plate VARCHAR(7),
  make VARCHAR(50),
  model VARCHAR(50),
  status VARCHAR(50),
  created TIMESTAMP,
  last_modified TIMESTAMP
);

CREATE INDEX idx_user_id ON repairjob(user_id);
CREATE INDEX idx_created ON repairjob(created);
CREATE INDEX idx_license_plate ON repairjob(license_plate);