ALTER TABLE players
  ADD COLUMN is_bot BOOLEAN NOT NULL DEFAULT FALSE AFTER user_id,
  ADD COLUMN bot_level INT NOT NULL DEFAULT 0 AFTER is_bot;

CREATE INDEX idx_players_world_bot ON players(world_id, is_bot, bot_level);

UPDATE players p
JOIN worlds w ON w.id = p.world_id
SET p.bot_level = w.difficulty_level
WHERE p.is_bot = TRUE;
