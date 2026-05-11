INSERT INTO resource_definitions (code, name, description, icon) VALUES
('pesetas', 'Pesetas', 'La eurozona terminó harta de Iberia y el país volvió a imprimir nostalgia con inflación incluida.', 'coin'),
('favores', 'Favores', 'Moneda blanda del poder: no figura en presupuestos, pero abre más puertas que un BOE urgente.', 'handshake'),
('escanos', 'Escaños', 'Asientos parlamentarios, cromos de gobernabilidad y materia prima de ministerios imposibles.', 'seat');

INSERT INTO player_resources (player_id, resource_code, amount, production_per_minute)
SELECT p.id, 'pesetas', 1200, 24
FROM players p
WHERE NOT EXISTS (
  SELECT 1 FROM player_resources pr WHERE pr.player_id = p.id AND pr.resource_code = 'pesetas'
);

INSERT INTO player_resources (player_id, resource_code, amount, production_per_minute)
SELECT p.id, 'favores', 90, 3
FROM players p
WHERE NOT EXISTS (
  SELECT 1 FROM player_resources pr WHERE pr.player_id = p.id AND pr.resource_code = 'favores'
);

INSERT INTO player_resources (player_id, resource_code, amount, production_per_minute)
SELECT p.id, 'escanos', 0, 0
FROM players p
WHERE NOT EXISTS (
  SELECT 1 FROM player_resources pr WHERE pr.player_id = p.id AND pr.resource_code = 'escanos'
);
