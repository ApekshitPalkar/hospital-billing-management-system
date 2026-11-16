-- Educational project database. Use fictional/sample patient details only.
CREATE DATABASE IF NOT EXISTS hospital_billing_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hospital_billing_db;

CREATE TABLE IF NOT EXISTS patients (
    patient_id BIGINT NOT NULL AUTO_INCREMENT,
    patient_code VARCHAR(40) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    age TINYINT UNSIGNED NOT NULL,
    relative_name VARCHAR(120) NOT NULL,
    contact_number CHAR(10) NOT NULL,
    address VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (patient_id),
    UNIQUE KEY uq_patients_patient_code (patient_code),
    CONSTRAINT chk_patients_age CHECK (age BETWEEN 1 AND 120)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bills (
    bill_id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    admission_date DATE NOT NULL,
    discharge_date DATE NOT NULL,
    days_stayed INT UNSIGNED NOT NULL,
    ward_type VARCHAR(40) NOT NULL,
    ward_rate DECIMAL(12,2) NOT NULL,
    surgery_type VARCHAR(80) NOT NULL,
    surgery_charge DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    doctor_charge DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    medicine_charge DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    test_charge DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    other_charge DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(12,2) NOT NULL,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    demo_tax_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    payment_mode VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (bill_id),
    KEY idx_bills_patient_created (patient_id, created_at),
    KEY idx_bills_created (created_at),
    CONSTRAINT fk_bills_patient FOREIGN KEY (patient_id)
        REFERENCES patients (patient_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_bills_days CHECK (days_stayed > 0),
    CONSTRAINT chk_bills_dates CHECK (discharge_date >= admission_date),
    CONSTRAINT chk_bills_demo_tax CHECK (demo_tax_rate BETWEEN 0 AND 100)
) ENGINE=InnoDB;

-- Optional: create a dedicated local application account instead of using root.
-- Replace the password with a strong local password before running these lines.
-- CREATE USER 'hospital_app'@'localhost' IDENTIFIED BY 'REPLACE_WITH_A_STRONG_PASSWORD';
-- GRANT SELECT, INSERT, UPDATE ON hospital_billing_db.* TO 'hospital_app'@'localhost';
-- FLUSH PRIVILEGES;
