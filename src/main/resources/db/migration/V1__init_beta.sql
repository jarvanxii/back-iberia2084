CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  display_name VARCHAR(90) NOT NULL,
  password_hash VARCHAR(120) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE auth_tokens (
  token CHAR(64) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_auth_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE worlds (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(40) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255) NOT NULL,
  max_players INT NOT NULL,
  current_players INT NOT NULL DEFAULT 0,
  tick_seconds INT NOT NULL DEFAULT 10,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE factions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(40) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  short_name VARCHAR(40) NOT NULL,
  color VARCHAR(20) NOT NULL,
  motto VARCHAR(160) NOT NULL,
  satire VARCHAR(255) NOT NULL,
  starting_region VARCHAR(80) NOT NULL,
  corruption_affinity INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alliances (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  world_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(16) NOT NULL UNIQUE,
  description VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_alliances_world FOREIGN KEY (world_id) REFERENCES worlds(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE players (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  world_id BIGINT NOT NULL,
  faction_id BIGINT NOT NULL,
  alliance_id BIGINT NULL,
  leader_name VARCHAR(90) NOT NULL,
  votes INT NOT NULL DEFAULT 900,
  political_credit INT NOT NULL DEFAULT 40,
  reputation INT NOT NULL DEFAULT 65,
  media_heat INT NOT NULL DEFAULT 0,
  action_points INT NOT NULL DEFAULT 6,
  last_collected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_players_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_players_world FOREIGN KEY (world_id) REFERENCES worlds(id),
  CONSTRAINT fk_players_faction FOREIGN KEY (faction_id) REFERENCES factions(id),
  CONSTRAINT fk_players_alliance FOREIGN KEY (alliance_id) REFERENCES alliances(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alliance_members (
  alliance_id BIGINT NOT NULL,
  player_id BIGINT NOT NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'miembro',
  joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (alliance_id, player_id),
  CONSTRAINT fk_alliance_members_alliance FOREIGN KEY (alliance_id) REFERENCES alliances(id) ON DELETE CASCADE,
  CONSTRAINT fk_alliance_members_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alliance_messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  alliance_id BIGINT NOT NULL,
  player_id BIGINT NOT NULL,
  body VARCHAR(500) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_alliance_messages_alliance FOREIGN KEY (alliance_id) REFERENCES alliances(id) ON DELETE CASCADE,
  CONSTRAINT fk_alliance_messages_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE resource_definitions (
  code VARCHAR(30) PRIMARY KEY,
  name VARCHAR(60) NOT NULL,
  description VARCHAR(255) NOT NULL,
  icon VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE player_resources (
  player_id BIGINT NOT NULL,
  resource_code VARCHAR(30) NOT NULL,
  amount INT NOT NULL DEFAULT 0,
  production_per_minute INT NOT NULL DEFAULT 0,
  PRIMARY KEY (player_id, resource_code),
  CONSTRAINT fk_player_resources_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
  CONSTRAINT fk_player_resources_resource FOREIGN KEY (resource_code) REFERENCES resource_definitions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE territories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  world_id BIGINT NOT NULL,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(100) NOT NULL,
  region VARCHAR(80) NOT NULL,
  map_x INT NOT NULL,
  map_y INT NOT NULL,
  owner_player_id BIGINT NULL,
  flavor_faction_id BIGINT NOT NULL,
  defense INT NOT NULL,
  population INT NOT NULL,
  base_votes INT NOT NULL,
  resource_focus VARCHAR(30) NOT NULL,
  building_name VARCHAR(100) NOT NULL,
  satire VARCHAR(255) NOT NULL,
  UNIQUE KEY uk_territory_world_code (world_id, code),
  CONSTRAINT fk_territories_world FOREIGN KEY (world_id) REFERENCES worlds(id),
  CONSTRAINT fk_territories_owner FOREIGN KEY (owner_player_id) REFERENCES players(id) ON DELETE SET NULL,
  CONSTRAINT fk_territories_faction FOREIGN KEY (flavor_faction_id) REFERENCES factions(id),
  CONSTRAINT fk_territories_resource FOREIGN KEY (resource_focus) REFERENCES resource_definitions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE actions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  player_id BIGINT NOT NULL,
  action_type VARCHAR(40) NOT NULL,
  target_territory_id BIGINT NULL,
  scheme_code VARCHAR(60) NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'pending',
  risk_percent INT NOT NULL DEFAULT 0,
  success_percent INT NOT NULL DEFAULT 100,
  started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  resolves_at TIMESTAMP NOT NULL,
  resolved_at TIMESTAMP NULL,
  result_title VARCHAR(120) NULL,
  result_body VARCHAR(500) NULL,
  CONSTRAINT fk_actions_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
  CONSTRAINT fk_actions_target FOREIGN KEY (target_territory_id) REFERENCES territories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE research_definitions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(60) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255) NOT NULL,
  cost_memes INT NOT NULL DEFAULT 0,
  cost_funcionarios INT NOT NULL DEFAULT 0,
  cost_influencia INT NOT NULL DEFAULT 0,
  duration_seconds INT NOT NULL DEFAULT 60,
  effect_type VARCHAR(40) NOT NULL,
  effect_value INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE player_research (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  player_id BIGINT NOT NULL,
  research_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'pending',
  started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  finishes_at TIMESTAMP NOT NULL,
  finished_at TIMESTAMP NULL,
  UNIQUE KEY uk_player_research (player_id, research_id),
  CONSTRAINT fk_player_research_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
  CONSTRAINT fk_player_research_research FOREIGN KEY (research_id) REFERENCES research_definitions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE world_events (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  world_id BIGINT NOT NULL,
  territory_id BIGINT NOT NULL,
  event_type VARCHAR(40) NOT NULL,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(500) NOT NULL,
  severity INT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  spawned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  expires_at TIMESTAMP NOT NULL,
  resolved_by_player_id BIGINT NULL,
  result_summary VARCHAR(500) NULL,
  CONSTRAINT fk_world_events_world FOREIGN KEY (world_id) REFERENCES worlds(id),
  CONSTRAINT fk_world_events_territory FOREIGN KEY (territory_id) REFERENCES territories(id),
  CONSTRAINT fk_world_events_player FOREIGN KEY (resolved_by_player_id) REFERENCES players(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO worlds (code, name, description, max_players, tick_seconds) VALUES
('iberia-beta-1', 'Iberia Beta 1', 'Primer mapa persistente: provincias, chiringuitos, pactos y titulares imposibles.', 52, 10),
('iberia-beta-2', 'Iberia Beta 2', 'Mundo preparado para nuevas hordas de estrategas con exceso de rueda de prensa.', 52, 10);

INSERT INTO factions (code, name, short_name, color, motto, satire, starting_region, corruption_affinity) VALUES
('meseta_administrativa', 'La Meseta Administrativa', 'Meseta', '#d1a857', 'Sin expediente no hay épica.', 'Centralistas de sello, ventanilla y café recalentado: prometen orden mientras archivan el apocalipsis.', 'Madrid', 4),
('costa_ladrillo', 'La Costa del Ladrillo', 'Ladrillo', '#e8754f', 'Otra rotonda es posible.', 'Constructores playeros, recalificadores emocionales y amantes del hormigón con vistas al mar.', 'Mediterráneo', 12),
('senores_norte', 'Los Señores del Norte', 'Norte', '#5fa8a0', 'Menos hablar y más industria.', 'Orgullo frío, fábrica caliente y una superioridad moral calibrada con lluvia horizontal.', 'Norte', 2),
('confederacion_tapeo', 'La Confederación del Tapeo', 'Tapeo', '#f2b84b', 'Una caña, una enmienda.', 'Federalismo de barra, pactos en servilleta y diplomacia de croqueta.', 'Sur', 7),
('orden_boe', 'La Orden del BOE', 'BOE', '#8d7bd1', 'Está publicado, luego existe.', 'Monjes legislativos que convierten cualquier problema en 900 páginas y tres anexos.', 'Centro', 1),
('tecnocratas_m30', 'Los Tecnócratas de la M-30', 'M-30', '#4b8ee8', 'Si no tiene dashboard, no existe.', 'Gestores de KPI, smart cities y reuniones donde nadie sabe quién decidió nada.', 'Madrid', 5),
('reinos_levante', 'Los Reinos del Levante', 'Levante', '#f08ab3', 'Sol, datos y paella geopolítica.', 'Turismo, energía, agua, festivales y una habilidad innata para discutir por horarios.', 'Levante', 8),
('liga_islas', 'La Liga de las Islas', 'Islas', '#50c878', 'Lejos, caros y decisivos.', 'Coalición insular de puertos, volcanes, hoteles y presupuestos con suplemento de distancia.', 'Islas', 6);

INSERT INTO resource_definitions (code, name, description, icon) VALUES
('votos', 'Votos', 'La materia prima de todo imperio con urna y megáfono.', 'urn'),
('influencia', 'Influencia', 'Favores, tertulias, editoriales y llamadas que empiezan con "mira, te cuento".', 'spark'),
('hormigon', 'Hormigón', 'Sirve para construir, tapar promesas y levantar rotondas con alma.', 'block'),
('energia', 'Energía', 'Luz, enchufes y excusas para ruedas de prensa dramáticas.', 'bolt'),
('turismo', 'Turismo', 'Visitantes, apartamentos imposibles y fotos de sangría a precio imperial.', 'sun'),
('memes', 'Memes', 'Propaganda líquida para conquistar sobremesas y grupos familiares.', 'meme'),
('funcionarios', 'Funcionarios', 'Poder administrativo que avanza lento, pero no olvida jamás.', 'stamp'),
('aceite', 'Aceite', 'Oro líquido, arma diplomática y motivo de crisis nacional recurrente.', 'oil');

INSERT INTO research_definitions (code, name, description, cost_memes, cost_funcionarios, cost_influencia, duration_seconds, effect_type, effect_value) VALUES
('argumentario_24h', 'Argumentario 24h', 'Convierte cualquier metedura de pata en "contexto malinterpretado".', 80, 1, 45, 75, 'influence_production', 4),
('brigada_rotondas', 'Brigada de rotondas', 'Cuadrillas capaces de levantar obra pública antes de que llegue la oposición.', 35, 2, 30, 90, 'conquest_bonus', 8),
('boe_predictivo', 'BOE predictivo', 'Publica la realidad con tres días de antelación y cara muy seria.', 50, 4, 55, 110, 'corruption_risk_reduction', 6),
('memecracia_avanzada', 'Memecracia avanzada', 'Laboratorio de clips recortados, indignación premium y stickers de campaña.', 120, 1, 35, 95, 'meme_production', 6),
('siesta_logistica', 'Siesta logística', 'Reduce el desgaste porque nadie invade bien justo después de comer.', 45, 2, 50, 100, 'defense_bonus', 7);

INSERT INTO territories (world_id, code, name, region, map_x, map_y, flavor_faction_id, defense, population, base_votes, resource_focus, building_name, satire) VALUES
(1, 'madrid-centro', 'Madrid Centro', 'Madrid', 49, 43, 1, 58, 3200, 180, 'funcionarios', 'Ministerio de la Siesta Estratégica', 'Todo empieza aquí, especialmente lo que nadie pidió.'),
(1, 'm30-orbital', 'M-30 Orbital', 'Madrid', 53, 46, 6, 54, 2600, 150, 'influencia', 'Centro de Control de Promesas Pendientes', 'Si el tráfico no se mueve, al menos el relato sí.'),
(1, 'barcelona-algoritmica', 'Barcelona Algorítmica', 'Cataluña', 70, 34, 6, 52, 2900, 165, 'memes', 'Ateneo de Startups con Subvención Retroactiva', 'Diseña el futuro en beta y lo cobra en premium.'),
(1, 'valencia-solar', 'Valencia Solar', 'Levante', 64, 55, 7, 47, 2400, 140, 'energia', 'Conselleria del Sol Inagotable', 'Produce energía y debates sobre dónde empieza exactamente la paella.'),
(1, 'alicante-resort', 'Alicante Resort', 'Levante', 65, 63, 2, 42, 2100, 120, 'turismo', 'Palacio del Todo Incluido Electoral', 'Cada sombrilla cuenta como infraestructura crítica.'),
(1, 'murcia-regadio', 'Murcia Regadío', 'Sureste', 58, 66, 7, 45, 1900, 110, 'aceite', 'Confederación del Grifo Milagroso', 'Aquí el agua es recurso, mito y programa electoral.'),
(1, 'sevilla-tapeo', 'Sevilla Tapeo', 'Sur', 40, 70, 4, 46, 2300, 135, 'votos', 'Parlamento de la Croqueta Vinculante', 'La moción prospera si llega caliente.'),
(1, 'cadiz-resistencia', 'Cádiz Resistencia', 'Sur', 34, 76, 4, 50, 1300, 100, 'memes', 'Astillero de Chirigotas Geopolíticas', 'Una provincia pequeña con daño crítico en carnaval.'),
(1, 'malaga-nube', 'Málaga Nube', 'Costa', 43, 78, 2, 43, 2200, 125, 'turismo', 'Distrito Fiscal de la Hamaca Digital', 'Nómadas digitales, espetos y alquileres en modo jefe final.'),
(1, 'granada-alhambra', 'Granada Alhambra', 'Sur', 48, 73, 4, 49, 1500, 105, 'influencia', 'Archivo Nazarí de Pactos Imposibles', 'La belleza también se puede usar como arma electoral.'),
(1, 'bilbao-forja', 'Bilbao Forja', 'Norte', 43, 23, 3, 59, 1500, 125, 'hormigon', 'Alto Horno de Consensos Duros', 'No presume: simplemente asume que todo pesa más allí.'),
(1, 'santander-lluvia', 'Santander Lluvia', 'Norte', 46, 20, 3, 53, 900, 90, 'energia', 'Observatorio de Nubes Rentables', 'La lluvia cae, la moral sube y el paraguas cotiza.'),
(1, 'oviedo-ministerio', 'Oviedo Ministerio', 'Norte', 34, 25, 3, 55, 1000, 95, 'funcionarios', 'Consejo del Cachopo Regulador', 'Resistencia administrativa con sidra de precisión.'),
(1, 'galicia-nevoa', 'Galicia Névoa', 'Noroeste', 24, 31, 3, 57, 1700, 120, 'aceite', 'Consello de la Lluvia Horizontal', 'Nunca sabes si te han pactado o si era niebla.'),
(1, 'zaragoza-puente', 'Zaragoza Puente', 'Ebro', 57, 34, 1, 51, 1400, 105, 'hormigon', 'Nudo Logístico del "Ya Que Pasas"', 'Quien controla el paso controla la excusa.'),
(1, 'valladolid-sello', 'Valladolid Sello', 'Meseta', 42, 36, 1, 52, 1100, 100, 'funcionarios', 'Fábrica Nacional de Trámites con Eco', 'Todo expediente vuelve, como el frío.'),
(1, 'salamanca-doctrina', 'Salamanca Doctrina', 'Meseta', 35, 44, 5, 50, 850, 85, 'influencia', 'Universidad del Argumento Infinito', 'Aquí una coma puede tumbar un gobierno local.'),
(1, 'toledo-archivo', 'Toledo Archivo', 'Centro', 45, 50, 5, 56, 900, 90, 'funcionarios', 'Archivo de Pactos Visigodos Vigentes', 'Nadie entiende el precedente, pero todos le temen.'),
(1, 'extremadura-reserva', 'Extremadura Reserva', 'Oeste', 30, 56, 4, 48, 750, 80, 'energia', 'Dehesa Solar de Última Enmienda', 'Mucho espacio, poca prisa y sorpresas en el BOE.'),
(1, 'mallorca-premium', 'Mallorca Premium', 'Islas', 76, 60, 8, 44, 1300, 115, 'turismo', 'Consulado del Balcón Regulado', 'Diplomacia de temporada alta con suplemento de limpieza.'),
(1, 'tenerife-volcan', 'Tenerife Volcán', 'Islas', 16, 82, 8, 55, 1000, 100, 'energia', 'Cabildo Geotérmico del Drama Tropical', 'Lejos del mapa, cerca del presupuesto.'),
(1, 'ibiza-influencer', 'Ibiza Influencer', 'Islas', 73, 66, 8, 40, 700, 95, 'memes', 'Ministerio del After con Acreditación', 'Una story aquí vale tres mítines y medio.'),
(1, 'lisboa-atlantica', 'Lisboa Atlántica', 'Oeste Ibérico', 19, 61, 8, 49, 2100, 130, 'turismo', 'Torre del Fado Regulatorio', 'Añade saudade a cualquier negociación presupuestaria.'),
(1, 'porto-industrial', 'Porto Industrial', 'Oeste Ibérico', 21, 47, 3, 52, 1600, 110, 'hormigon', 'Bodega de Pactos con Denominación', 'Exporta calma, vino y maniobras discretas.');
