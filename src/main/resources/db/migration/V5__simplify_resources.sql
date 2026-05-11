ALTER TABLE research_definitions
  CHANGE cost_memes cost_pesetas INT NOT NULL DEFAULT 0;

ALTER TABLE city_building_definitions
  ADD COLUMN cost_pesetas INT NOT NULL DEFAULT 0 AFTER max_level,
  ADD COLUMN cost_favores INT NOT NULL DEFAULT 0 AFTER cost_influencia;

ALTER TABLE troop_definitions
  ADD COLUMN cost_pesetas INT NOT NULL DEFAULT 0 AFTER unlock_building_level,
  ADD COLUMN cost_favores INT NOT NULL DEFAULT 0 AFTER cost_influencia;

UPDATE city_building_definitions
SET cost_pesetas = cost_hormigon * 3 + cost_energia * 2 + cost_turismo * 2 + cost_aceite * 4,
    cost_favores = GREATEST(0, FLOOR(cost_aceite / 10)),
    cost_influencia = cost_influencia + cost_memes,
    cost_hormigon = 0,
    cost_energia = 0,
    cost_turismo = 0,
    cost_memes = 0,
    cost_aceite = 0;

UPDATE troop_definitions
SET cost_pesetas = cost_hormigon * 3 + cost_energia * 2 + cost_turismo * 2 + cost_aceite * 4,
    cost_favores = GREATEST(0, FLOOR(cost_aceite / 10)),
    cost_influencia = cost_influencia + cost_memes,
    cost_hormigon = 0,
    cost_energia = 0,
    cost_turismo = 0,
    cost_memes = 0,
    cost_aceite = 0;

UPDATE city_building_definitions SET category = 'Relato', effect_label = '+influencia y velocidad de campaña'
WHERE code = 'antena_relato';

UPDATE city_building_definitions SET category = 'Obra pública', effect_label = '+pesetas y operaciones territoriales'
WHERE code = 'garaje_rotondas';

UPDATE city_building_definitions SET effect_label = '+concejales, votos y favores locales'
WHERE code = 'concejalia_festejos';

UPDATE research_definitions
SET name = 'Relato avanzado',
    description = 'Laboratorio de clips, argumentarios y titulares para ganar la sobremesa sin crear otra divisa absurda.',
    effect_type = 'influence_production',
    effect_value = 6
WHERE code = 'memecracia_avanzada';

UPDATE research_definitions
SET cost_pesetas = GREATEST(cost_pesetas, 40)
WHERE cost_pesetas = 0;

UPDATE player_resources pr
JOIN player_resources removed ON removed.player_id = pr.player_id AND removed.resource_code = 'hormigon'
SET pr.amount = LEAST(999999, pr.amount + removed.amount * 3),
    pr.production_per_minute = pr.production_per_minute + removed.production_per_minute * 3
WHERE pr.resource_code = 'pesetas';

UPDATE player_resources pr
JOIN player_resources removed ON removed.player_id = pr.player_id AND removed.resource_code = 'energia'
SET pr.amount = LEAST(999999, pr.amount + removed.amount * 2),
    pr.production_per_minute = pr.production_per_minute + removed.production_per_minute * 2
WHERE pr.resource_code = 'pesetas';

UPDATE player_resources pr
JOIN player_resources removed ON removed.player_id = pr.player_id AND removed.resource_code = 'turismo'
SET pr.amount = LEAST(999999, pr.amount + removed.amount * 2),
    pr.production_per_minute = pr.production_per_minute + removed.production_per_minute * 2
WHERE pr.resource_code = 'votos';

UPDATE player_resources pr
JOIN player_resources removed ON removed.player_id = pr.player_id AND removed.resource_code = 'memes'
SET pr.amount = LEAST(999999, pr.amount + removed.amount),
    pr.production_per_minute = pr.production_per_minute + removed.production_per_minute
WHERE pr.resource_code = 'influencia';

UPDATE player_resources pr
JOIN player_resources removed ON removed.player_id = pr.player_id AND removed.resource_code = 'aceite'
SET pr.amount = LEAST(999999, pr.amount + removed.amount),
    pr.production_per_minute = pr.production_per_minute + removed.production_per_minute
WHERE pr.resource_code = 'favores';

UPDATE territories SET resource_focus = 'pesetas' WHERE resource_focus IN ('hormigon', 'energia');
UPDATE territories SET resource_focus = 'votos' WHERE resource_focus = 'turismo';
UPDATE territories SET resource_focus = 'influencia' WHERE resource_focus = 'memes';
UPDATE territories SET resource_focus = 'favores' WHERE resource_focus = 'aceite';

DELETE FROM player_resources
WHERE resource_code IN ('hormigon', 'energia', 'turismo', 'memes', 'aceite');

DELETE FROM resource_definitions
WHERE code IN ('hormigon', 'energia', 'turismo', 'memes', 'aceite');

UPDATE resource_definitions
SET description = 'Dinero operativo. Sirve para construir edificios, financiar unidades y pagar campañas sin inventar subrecursos.'
WHERE code = 'pesetas';

UPDATE resource_definitions
SET description = 'Apoyo electoral. Sirve para disputar elecciones, sostener campañas y medir músculo político.'
WHERE code = 'votos';

UPDATE resource_definitions
SET description = 'Capacidad de persuasión. Sirve para conquistar relato, bajar defensas y reforzar operaciones políticas.'
WHERE code = 'influencia';

UPDATE resource_definitions
SET description = 'Capital interno del partido. Sirve para corrupción, alianzas, atajos políticos y jugadas especiales.'
WHERE code = 'favores';

UPDATE resource_definitions
SET description = 'Burocracia disponible. Sirve para edificios administrativos, crisis y unidades de control.'
WHERE code = 'funcionarios';

UPDATE resource_definitions
SET description = 'Poder institucional ganado en elecciones. Desbloquea ministerios y peso parlamentario.'
WHERE code = 'escanos';
