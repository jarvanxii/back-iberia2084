ALTER TABLE users
  ADD COLUMN is_system BOOLEAN NOT NULL DEFAULT FALSE AFTER email;

UPDATE users u
SET u.is_system = TRUE
WHERE EXISTS (
  SELECT 1
  FROM players p
  WHERE p.user_id = u.id
    AND p.is_bot = TRUE
);
