CREATE TABLE auth_email_changes (
  id VARCHAR(36) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  email VARCHAR(190) NOT NULL,
  code_hash VARCHAR(120) NOT NULL,
  attempts INT NOT NULL DEFAULT 0,
  expires_at TIMESTAMP NOT NULL,
  consumed_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_auth_email_changes_user (user_id, consumed_at, expires_at),
  INDEX idx_auth_email_changes_email (email, consumed_at, expires_at),
  CONSTRAINT fk_auth_email_changes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
