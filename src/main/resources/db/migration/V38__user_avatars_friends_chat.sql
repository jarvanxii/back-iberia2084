ALTER TABLE users
  ADD COLUMN avatar_key VARCHAR(120) NOT NULL DEFAULT 'abalos' AFTER email_verified;

CREATE TABLE user_relations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  requester_user_id BIGINT NOT NULL,
  addressee_user_id BIGINT NOT NULL,
  lower_user_id BIGINT NOT NULL,
  higher_user_id BIGINT NOT NULL,
  status VARCHAR(24) NOT NULL DEFAULT 'pendiente',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_relations_pair (lower_user_id, higher_user_id),
  INDEX idx_user_relations_requester (requester_user_id),
  INDEX idx_user_relations_addressee (addressee_user_id),
  INDEX idx_user_relations_status (status),
  CONSTRAINT fk_user_relations_requester FOREIGN KEY (requester_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_relations_addressee FOREIGN KEY (addressee_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_relations_lower FOREIGN KEY (lower_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_relations_higher FOREIGN KEY (higher_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chat_messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sender_user_id BIGINT NOT NULL,
  recipient_user_id BIGINT NOT NULL,
  message VARCHAR(1024) NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  read_at TIMESTAMP NULL,
  INDEX idx_chat_messages_sender_recipient (sender_user_id, recipient_user_id, created_at),
  INDEX idx_chat_messages_recipient_unread (recipient_user_id, sender_user_id, is_read),
  CONSTRAINT fk_chat_messages_sender FOREIGN KEY (sender_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_chat_messages_recipient FOREIGN KEY (recipient_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
