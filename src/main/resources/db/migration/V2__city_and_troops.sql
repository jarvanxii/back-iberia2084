CREATE TABLE city_building_definitions (
  code VARCHAR(60) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  category VARCHAR(60) NOT NULL,
  description VARCHAR(255) NOT NULL,
  image_key VARCHAR(80) NOT NULL,
  map_x INT NOT NULL,
  map_y INT NOT NULL,
  width INT NOT NULL,
  height INT NOT NULL,
  max_level INT NOT NULL DEFAULT 20,
  cost_votos INT NOT NULL DEFAULT 0,
  cost_influencia INT NOT NULL DEFAULT 0,
  cost_hormigon INT NOT NULL DEFAULT 0,
  cost_energia INT NOT NULL DEFAULT 0,
  cost_turismo INT NOT NULL DEFAULT 0,
  cost_memes INT NOT NULL DEFAULT 0,
  cost_funcionarios INT NOT NULL DEFAULT 0,
  cost_aceite INT NOT NULL DEFAULT 0,
  duration_seconds INT NOT NULL DEFAULT 60,
  effect_label VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE player_city_buildings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  player_id BIGINT NOT NULL,
  building_code VARCHAR(60) NOT NULL,
  level INT NOT NULL DEFAULT 0,
  upgrading BOOLEAN NOT NULL DEFAULT FALSE,
  upgrade_started_at TIMESTAMP NULL,
  upgrade_finishes_at TIMESTAMP NULL,
  UNIQUE KEY uk_player_city_building (player_id, building_code),
  CONSTRAINT fk_player_city_buildings_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
  CONSTRAINT fk_player_city_buildings_definition FOREIGN KEY (building_code) REFERENCES city_building_definitions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE troop_definitions (
  code VARCHAR(60) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  role VARCHAR(60) NOT NULL,
  description VARCHAR(255) NOT NULL,
  image_key VARCHAR(80) NOT NULL,
  tier INT NOT NULL DEFAULT 1,
  attack INT NOT NULL DEFAULT 0,
  defense INT NOT NULL DEFAULT 0,
  influence_power INT NOT NULL DEFAULT 0,
  speed INT NOT NULL DEFAULT 0,
  capacity INT NOT NULL DEFAULT 0,
  training_seconds INT NOT NULL DEFAULT 30,
  unlock_building_code VARCHAR(60) NOT NULL,
  unlock_building_level INT NOT NULL DEFAULT 1,
  cost_votos INT NOT NULL DEFAULT 0,
  cost_influencia INT NOT NULL DEFAULT 0,
  cost_hormigon INT NOT NULL DEFAULT 0,
  cost_energia INT NOT NULL DEFAULT 0,
  cost_turismo INT NOT NULL DEFAULT 0,
  cost_memes INT NOT NULL DEFAULT 0,
  cost_funcionarios INT NOT NULL DEFAULT 0,
  cost_aceite INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_troop_definitions_building FOREIGN KEY (unlock_building_code) REFERENCES city_building_definitions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE player_troops (
  player_id BIGINT NOT NULL,
  unit_code VARCHAR(60) NOT NULL,
  amount INT NOT NULL DEFAULT 0,
  PRIMARY KEY (player_id, unit_code),
  CONSTRAINT fk_player_troops_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
  CONSTRAINT fk_player_troops_definition FOREIGN KEY (unit_code) REFERENCES troop_definitions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE troop_training (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  player_id BIGINT NOT NULL,
  unit_code VARCHAR(60) NOT NULL,
  amount INT NOT NULL,
  started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  finishes_at TIMESTAMP NOT NULL,
  CONSTRAINT fk_troop_training_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
  CONSTRAINT fk_troop_training_definition FOREIGN KEY (unit_code) REFERENCES troop_definitions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO city_building_definitions
  (code, name, category, description, image_key, map_x, map_y, width, height, max_level,
   cost_votos, cost_influencia, cost_hormigon, cost_energia, cost_turismo, cost_memes, cost_funcionarios, cost_aceite,
   duration_seconds, effect_label)
VALUES
('palacio_plenos', 'Palacio de Plenos', 'Gobierno', 'Centro neurálgico de la provincia: discursos, pactos, desbloqueos y café malo.', 'palacio-plenos', 50, 24, 18, 12, 20, 180, 90, 120, 60, 0, 60, 2, 20, 75, '+capacidad política y desbloqueos urbanos'),
('oficina_infinita', 'Oficina de Atención Infinita', 'Burocracia', 'Fábrica de sellos, colas y funcionarios con poder real.', 'oficina-infinita', 18, 33, 14, 11, 20, 120, 70, 80, 40, 0, 30, 1, 12, 55, '+funcionarios y defensa administrativa'),
('redaccion_subvencionada', 'Redacción Subvencionada', 'Medios', 'Periodistas, filtraciones y titulares que llegan antes que los hechos.', 'redaccion-subvencionada', 33, 48, 15, 11, 20, 140, 95, 60, 45, 20, 100, 1, 18, 70, '+periodistas e influencia mediática'),
('plato_24h', 'Plató Nacional 24h', 'Medios', 'Donde el drama se empaqueta con luz cálida y rótulos urgentes.', 'plato-24h', 61, 46, 15, 11, 20, 210, 150, 80, 90, 60, 180, 2, 24, 95, '+presentadores y presión de relato'),
('archivo_boe', 'Archivo del BOE Profundo', 'Legalidad', 'Un laberinto de anexos capaz de convertir la realidad en trámite.', 'archivo-boe', 18, 65, 13, 10, 20, 120, 140, 100, 55, 0, 70, 3, 16, 85, '+reducción de riesgo y poder normativo'),
('mercado_favores', 'Mercado de Favores', 'Influencia', 'Puestos discretos donde nadie compra nada y todo el mundo sale debiendo algo.', 'mercado-favores', 74, 65, 14, 10, 20, 160, 160, 80, 55, 80, 110, 1, 30, 80, '+influencia, corrupción y trueques'),
('plaza_promesas', 'Plaza de Promesas', 'Votos', 'Lugar ceremonial para inaugurar lo mismo tres veces con distinta pancarta.', 'plaza-promesas', 50, 56, 16, 10, 20, 200, 80, 70, 40, 30, 120, 0, 20, 65, '+votos y moral de campaña'),
('centro_crisis', 'Centro de Crisis y Chalecos', 'Emergencias', 'Sala de pantallas, mapas mojados y chalecos reflectantes sin estrenar.', 'centro-crisis', 62, 75, 13, 10, 20, 140, 130, 130, 100, 0, 60, 2, 28, 90, '+planes de crisis y reputación'),
('antena_relato', 'Torre de Antenas del Relato', 'Memes', 'Amplifica memes, tertulias, directos y explicaciones que nadie pidió.', 'antena-relato', 86, 24, 12, 16, 20, 140, 110, 90, 150, 0, 170, 1, 14, 80, '+memes y velocidad informativa'),
('garaje_rotondas', 'Garaje de Rotondas Oficiales', 'Obra pública', 'Depósito de vallas, conos y proyectos que sobreviven a cualquier gobierno.', 'garaje-rotondas', 82, 50, 14, 10, 20, 110, 60, 190, 70, 0, 30, 1, 12, 70, '+hormigón y operaciones territoriales'),
('concejalia_festejos', 'Concejalía de Festejos y Contratos', 'Poder local', 'Donde una charanga puede ser infraestructura estratégica.', 'concejalia-festejos', 36, 72, 14, 10, 20, 230, 160, 120, 60, 120, 130, 2, 35, 100, '+concejales, turismo y votos locales'),
('alcaldia_perpetua', 'Despacho de Alcaldía Perpetua', 'Élite', 'Balcón, llave gigante y la sensación de que el municipio cabe en un bolsillo.', 'alcaldia-perpetua', 50, 82, 14, 10, 20, 340, 240, 180, 90, 160, 180, 4, 55, 130, '+alcaldes y control territorial');

INSERT INTO troop_definitions
  (code, name, role, description, image_key, tier, attack, defense, influence_power, speed, capacity, training_seconds,
   unlock_building_code, unlock_building_level,
   cost_votos, cost_influencia, cost_hormigon, cost_energia, cost_turismo, cost_memes, cost_funcionarios, cost_aceite)
VALUES
('funcionario_raso', 'Funcionario raso', 'Burocracia', 'Avanza lento, sella fuerte y convierte cualquier invasión en expediente.', 'funcionario-raso', 1, 10, 34, 8, 8, 12, 18, 'oficina_infinita', 1, 24, 12, 0, 0, 0, 8, 1, 4),
('administrativo', 'Administrativo de ventanilla', 'Burocracia', 'Especialista en mirar por encima de las gafas y exigir el anexo correcto.', 'administrativo', 1, 16, 48, 18, 7, 16, 28, 'oficina_infinita', 2, 42, 24, 0, 0, 0, 18, 1, 7),
('periodista', 'Periodista de guardia', 'Medios', 'Llega con micro, libreta y una pregunta que destroza desayunos oficiales.', 'periodista', 1, 28, 18, 42, 18, 10, 24, 'redaccion_subvencionada', 1, 36, 32, 0, 10, 0, 38, 0, 6),
('presentador_tv', 'Presentador de televisión', 'Medios', 'Sonríe a cámara mientras convierte una anécdota local en crisis nacional.', 'presentador-tv', 2, 48, 24, 76, 15, 14, 46, 'plato_24h', 1, 86, 72, 0, 36, 35, 96, 1, 14),
('inspector_hacienda', 'Inspector de Hacienda', 'Control', 'No corre mucho, pero cuando llega todo el mundo recuerda recibos olvidados.', 'inspector-hacienda', 2, 38, 62, 46, 10, 18, 42, 'archivo_boe', 2, 72, 58, 0, 12, 0, 48, 2, 10),
('asesor_sombra', 'Asesor en la sombra', 'Intriga', 'Nunca sale en la foto, pero todos los pinganillos obedecen su tos.', 'asesor-sombra', 2, 58, 30, 68, 20, 8, 54, 'mercado_favores', 3, 95, 90, 0, 28, 20, 110, 1, 22),
('concejal', 'Concejal de área estratégica', 'Poder local', 'Domina fiestas, contratos menores y la frase "eso lo lleva urbanismo".', 'concejal', 3, 82, 72, 94, 13, 24, 76, 'concejalia_festejos', 2, 150, 125, 80, 40, 90, 130, 3, 34),
('alcalde', 'Alcalde de balcón', 'Élite', 'Unidad pesada: inaugura, promete, manda y sonríe como si el mapa fuera suyo.', 'alcalde', 4, 128, 118, 150, 11, 32, 118, 'alcaldia_perpetua', 3, 260, 220, 150, 70, 140, 210, 5, 55);
