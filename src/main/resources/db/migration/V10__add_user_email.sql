ALTER TABLE users
  ADD COLUMN email VARCHAR(190) NULL AFTER display_name,
  ADD UNIQUE KEY uk_users_email (email);
