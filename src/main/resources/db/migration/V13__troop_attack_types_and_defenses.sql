ALTER TABLE troop_definitions
  ADD COLUMN attack_type VARCHAR(30) NOT NULL DEFAULT 'BUREAUCRATIC' AFTER attack,
  ADD COLUMN defense_bureaucratic INT NOT NULL DEFAULT 0 AFTER defense,
  ADD COLUMN defense_incisive INT NOT NULL DEFAULT 0 AFTER defense_bureaucratic,
  ADD COLUMN defense_media INT NOT NULL DEFAULT 0 AFTER defense_incisive;

UPDATE troop_definitions
SET attack_type = 'BUREAUCRATIC',
    defense_bureaucratic = 42,
    defense_incisive = 24,
    defense_media = 12,
    defense = 42
WHERE code = 'funcionario_raso';

UPDATE troop_definitions
SET attack_type = 'MEDIA',
    defense_bureaucratic = 12,
    defense_incisive = 18,
    defense_media = 46,
    defense = 46
WHERE code = 'periodista';

UPDATE troop_definitions
SET attack_type = 'BUREAUCRATIC',
    defense_bureaucratic = 88,
    defense_incisive = 48,
    defense_media = 24,
    defense = 88
WHERE code = 'administrativo';

UPDATE troop_definitions
SET attack_type = 'INCISIVE',
    defense_bureaucratic = 76,
    defense_incisive = 104,
    defense_media = 36,
    defense = 104
WHERE code = 'inspector_hacienda';

UPDATE troop_definitions
SET attack_type = 'MEDIA',
    defense_bureaucratic = 38,
    defense_incisive = 54,
    defense_media = 120,
    defense = 120
WHERE code = 'presentador_tv';

UPDATE troop_definitions
SET attack_type = 'INCISIVE',
    defense_bureaucratic = 62,
    defense_incisive = 116,
    defense_media = 74,
    defense = 116
WHERE code = 'asesor_sombra';

UPDATE troop_definitions
SET attack_type = 'BUREAUCRATIC',
    defense_bureaucratic = 255,
    defense_incisive = 190,
    defense_media = 160,
    defense = 255
WHERE code = 'concejal';

UPDATE troop_definitions
SET attack_type = 'BUREAUCRATIC',
    defense_bureaucratic = 420,
    defense_incisive = 310,
    defense_media = 260,
    defense = 420
WHERE code = 'alcalde';

INSERT INTO troop_definitions
  (code, name, role, description, image_key, tier, attack, attack_type, defense,
   defense_bureaucratic, defense_incisive, defense_media, influence_power, speed, capacity, training_seconds,
   unlock_building_code, unlock_building_level, cost_pesetas, cost_votos, cost_favores)
VALUES
('ujier_cordon', 'Ujier de cordón rojo', 'Burocracia', 'Controla pasillos, puertas y silencios con una cinta aterciopelada y mirada de reglamento.', 'ujier-cordon', 1, 18, 'BUREAUCRATIC', 74, 74, 36, 20, 8, 9, 2, 34, 'palacio_plenos', 1, 62, 24, 76),
('fiscalizador_contratos', 'Fiscalizador de contratos', 'Control', 'Aparece con una carpeta roja y convierte cualquier adjudicación en deporte de riesgo.', 'fiscalizador-contratos', 2, 98, 'INCISIVE', 138, 82, 138, 48, 34, 13, 3, 58, 'archivo_boe', 2, 170, 118, 190),
('tertuliano_prime_time', 'Tertuliano de prime time', 'Medios', 'No sabe bajar la voz, pero sube la presión mediática hasta que el mapa pide descanso.', 'tertuliano-prime-time', 3, 205, 'MEDIA', 170, 64, 88, 170, 110, 14, 4, 82, 'plato_24h', 2, 320, 390, 85),
('baron_territorial', 'Barón territorial', 'Poder local', 'Lleva décadas inaugurando bancos, pactando silencios y oliendo traiciones antes del café.', 'baron-territorial', 4, 360, 'INCISIVE', 330, 260, 330, 220, 140, 12, 7, 124, 'concejalia_festejos', 3, 760, 620, 520);
