CREATE TABLE auth_oauth_handoffs (
  id CHAR(36) PRIMARY KEY,
  token CHAR(64) NOT NULL,
  user_id BIGINT NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  consumed_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_auth_oauth_handoffs_expires (expires_at),
  CONSTRAINT fk_auth_oauth_handoffs_token FOREIGN KEY (token) REFERENCES auth_tokens(token) ON DELETE CASCADE,
  CONSTRAINT fk_auth_oauth_handoffs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
