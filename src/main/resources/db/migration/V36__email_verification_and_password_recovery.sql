ALTER TABLE users
  ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT TRUE AFTER email;

CREATE TABLE auth_email_verifications (
  id CHAR(36) PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  display_name VARCHAR(90) NOT NULL,
  email VARCHAR(190) NOT NULL,
  password_hash VARCHAR(120) NOT NULL,
  code_hash VARCHAR(120) NOT NULL,
  attempts INT NOT NULL DEFAULT 0,
  expires_at TIMESTAMP NOT NULL,
  consumed_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_auth_email_verifications_email (email, consumed_at, expires_at),
  INDEX idx_auth_email_verifications_username (username, consumed_at, expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE auth_password_resets (
  id CHAR(36) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  email VARCHAR(190) NOT NULL,
  token_hash VARCHAR(120) NOT NULL,
  attempts INT NOT NULL DEFAULT 0,
  expires_at TIMESTAMP NOT NULL,
  consumed_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_auth_password_resets_email (email, consumed_at, expires_at),
  CONSTRAINT fk_auth_password_resets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
