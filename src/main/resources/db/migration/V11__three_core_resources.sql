ALTER TABLE research_definitions
  ADD COLUMN cost_votos INT NOT NULL DEFAULT 0 AFTER cost_pesetas;

ALTER TABLE research_definitions
  ADD COLUMN cost_favores INT NOT NULL DEFAULT 0 AFTER cost_votos;

UPDATE research_definitions
SET cost_votos = cost_influencia,
    cost_favores = cost_funcionarios * 40,
    cost_influencia = 0,
    cost_funcionarios = 0;

UPDATE city_building_definitions
SET cost_votos = cost_votos + cost_influencia,
    cost_favores = cost_favores + cost_funcionarios * 35,
    cost_influencia = 0,
    cost_funcionarios = 0;

UPDATE troop_definitions
SET cost_votos = cost_votos + cost_influencia,
    cost_favores = cost_favores + cost_funcionarios * 25,
    cost_influencia = 0,
    cost_funcionarios = 0;

UPDATE player_resources votos
LEFT JOIN player_resources influencia
  ON influencia.player_id = votos.player_id AND influencia.resource_code = 'influencia'
LEFT JOIN player_resources escanos
  ON escanos.player_id = votos.player_id AND escanos.resource_code = 'escanos'
SET votos.amount = LEAST(999999, votos.amount + COALESCE(influencia.amount, 0) + COALESCE(escanos.amount, 0) * 100),
    votos.production_per_minute = votos.production_per_minute + COALESCE(influencia.production_per_minute, 0)
WHERE votos.resource_code = 'votos';

UPDATE player_resources favores
LEFT JOIN player_resources funcionarios
  ON funcionarios.player_id = favores.player_id AND funcionarios.resource_code = 'funcionarios'
SET favores.amount = LEAST(999999, favores.amount + COALESCE(funcionarios.amount, 0) * 25),
    favores.production_per_minute = favores.production_per_minute + COALESCE(funcionarios.production_per_minute, 0) * 10
WHERE favores.resource_code = 'favores';

UPDATE territories
SET resource_focus = 'votos'
WHERE resource_focus IN ('influencia', 'escanos');

UPDATE territories
SET resource_focus = 'favores'
WHERE resource_focus = 'funcionarios';

DELETE FROM player_resources
WHERE resource_code IN ('influencia', 'funcionarios', 'escanos');

DELETE FROM resource_definitions
WHERE code IN ('influencia', 'funcionarios', 'escanos');

UPDATE resource_definitions
SET name = 'Pesetas',
    description = 'Sirven para comprar casi todo: edificios, tropas, investigaciones y cualquier ocurrencia con factura.',
    icon = 'coin'
WHERE code = 'pesetas';

UPDATE resource_definitions
SET name = 'Votos',
    description = 'Sirven para comprar unidades y edificios ofensivos. Sin votos no hay invasión que sobreviva al escrutinio.',
    icon = 'urn'
WHERE code = 'votos';

UPDATE resource_definitions
SET name = 'Favores',
    description = 'Sirven para comprar unidades y edificios defensivos. Son la red de llamadas, deudas y puertas que no se cierran.',
    icon = 'handshake'
WHERE code = 'favores';
