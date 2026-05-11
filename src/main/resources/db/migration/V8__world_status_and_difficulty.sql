ALTER TABLE worlds
  ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  ADD COLUMN difficulty_code VARCHAR(30) NOT NULL DEFAULT 'normal',
  ADD COLUMN difficulty_name VARCHAR(60) NOT NULL DEFAULT 'Normal',
  ADD COLUMN difficulty_level INT NOT NULL DEFAULT 2,
  ADD COLUMN opens_at TIMESTAMP NULL,
  ADD COLUMN closed_at TIMESTAMP NULL,
  ADD COLUMN winning_alliance_id BIGINT NULL,
  ADD COLUMN closure_reason VARCHAR(255) NULL,
  ADD CONSTRAINT fk_worlds_winning_alliance FOREIGN KEY (winning_alliance_id) REFERENCES alliances(id) ON DELETE SET NULL;

CREATE INDEX idx_worlds_status_opens ON worlds(status, opens_at);

UPDATE worlds
SET status = 'OPEN',
    difficulty_code = 'normal',
    difficulty_name = 'Normal',
    difficulty_level = 2,
    description = 'Primer mapa persistente: provincias, pactos, bots moderados y titulares imposibles.'
WHERE code = 'iberia-beta-1';

UPDATE worlds
SET status = 'OPEN',
    difficulty_code = 'dificil',
    difficulty_name = 'Difícil',
    difficulty_level = 3,
    description = 'Segundo mapa abierto: bots más agresivos, menos siesta estratégica y alianzas con colmillo.'
WHERE code = 'iberia-beta-2';

INSERT INTO worlds
  (code, name, description, max_players, tick_seconds, status, difficulty_code, difficulty_name, difficulty_level, opens_at)
VALUES
  (
    'iberia-velocidad-1',
    'Iberia Velocidad 1',
    'Mundo rápido para partidas más tensas: colas cortas, bots nerviosos y campañas que envejecen en minutos.',
    52,
    6,
    'UPCOMING',
    'rapida',
    'Rápida',
    3,
    CURRENT_TIMESTAMP + INTERVAL 7 DAY
  ),
  (
    'iberia-pesadilla-1',
    'Iberia Pesadilla 1',
    'Mapa para estrategas con gusto por el sufrimiento: bots duros, crisis frecuentes y pactos que muerden.',
    52,
    8,
    'UPCOMING',
    'pesadilla',
    'Pesadilla',
    5,
    CURRENT_TIMESTAMP + INTERVAL 21 DAY
  );
