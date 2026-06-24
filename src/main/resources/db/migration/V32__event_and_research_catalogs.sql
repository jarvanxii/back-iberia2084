CREATE TABLE event_definitions (
  code VARCHAR(40) PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  category VARCHAR(60) NOT NULL,
  description VARCHAR(500) NOT NULL,
  image_key VARCHAR(80) NOT NULL,
  base_severity INT NOT NULL DEFAULT 3,
  duration_seconds INT NOT NULL DEFAULT 360,
  scope_label VARCHAR(120) NOT NULL,
  impact_label VARCHAR(180) NOT NULL,
  response_label VARCHAR(180) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO event_definitions
  (code, name, category, description, image_key, base_severity, duration_seconds, scope_label, impact_label, response_label)
VALUES
('dana', 'DANA con rueda de prensa incluida', 'Clima', 'Una DANA golpea {territory}. El agua sube, los alcaldes corren y todos descubren la palabra resiliencia.', 'evento-dana', 7, 520, 'Afecta a todos los jugadores del mundo activo', 'Sube la presión pública y castiga provincias mal defendidas.', 'Conviene responder con brigadas, planes serios y gasto visible.'),
('terremoto', 'Terremoto administrativo', 'Territorio', 'Tiembla {territory}: se caen cornisas, promesas y tres planes urbanísticos sospechosamente recientes.', 'evento-terremoto', 6, 470, 'Impacto territorial compartido', 'Reduce estabilidad local y expone edificios descuidados.', 'Funciona mejor una respuesta técnica que una foto con casco.'),
('apagon', 'Apagón de soberanía energética', 'Servicios', '{territory} se queda a oscuras y de pronto todo el mundo entiende la factura de la luz peor que antes.', 'evento-apagon', 6, 430, 'Afecta a recursos y relato de todos', 'Frena producción y dispara quejas en cadena.', 'Requiere gestión rápida, favores y comunicación sobria.'),
('pandemia', 'Brote de gripazo institucional', 'Salud pública', 'Un virus deja {territory} con mascarillas, bulos y expertos que nadie escuchaba ayer.', 'evento-pandemia', 8, 620, 'Riesgo global de partida', 'Dificulta acciones, baja confianza y multiplica contradicciones.', 'Sirven planes largos, datos claros y paciencia política.'),
('incendios', 'Incendios de agosto eterno', 'Clima', '{territory} arde entre viento, negligencias y comparecencias donde nadie sabe quién limpió el monte.', 'evento-incendios', 7, 500, 'Presión simultánea sobre varias estrategias', 'Castiga provincias con poca defensa y mucha improvisación.', 'Ayudan brigadas, presupuesto y no discutir por competencias.'),
('sequia', 'Sequía con comité de fuentes secas', 'Recursos', '{territory} mira al cielo y solo caen ruedas de prensa sobre ahorro, regadíos y duchas cortas.', 'evento-sequia', 5, 560, 'Evento económico de largo alcance', 'Reduce margen operativo y encarece decisiones territoriales.', 'Exige priorizar recursos y pactar medidas impopulares.'),
('ciberataque', 'Ciberataque de ventanilla digital', 'Tecnología', 'Los trámites de {territory} empiezan a hablar en errores 500 y la sede electrónica pide vacaciones.', 'evento-ciberataque', 6, 390, 'Bloquea servicios comunes', 'Aumenta riesgo administrativo y retrasa reacciones.', 'El BOE salvavidas y la gestión técnica bajan el daño.'),
('suministros', 'Crisis de suministros y paciencia', 'Logística', 'Los camiones paran en {territory}. Los lineales tiemblan y los tertulianos descubren la logística.', 'evento-suministros', 5, 410, 'Afecta al ritmo logístico de todos', 'Tensiona transportes, costes y opinión pública.', 'Conviene coordinar rutas, favores y mensajes sin dramatismo.'),
('inflacion', 'Crisis de precios nivel tostada seca', 'Economía', 'Los precios suben en {territory}. La población mira la compra semanal como si fuera un producto de lujo.', 'evento-inflacion', 5, 450, 'Presión económica compartida', 'Encarece el juego y erosiona votos si se gestiona tarde.', 'Reducir daño exige pesetas, relato y medidas visibles.'),
('boe', 'Plaga de trámites autoconscientes', 'Administración', 'Los formularios de {territory} han desarrollado voluntad propia y exigen anexos para respirar.', 'evento-boe', 4, 360, 'Caos burocrático para todos', 'Ralentiza mejoras y abre huecos a rivales atentos.', 'El control administrativo y las investigaciones legales ayudan.');

ALTER TABLE research_definitions
  ADD COLUMN category VARCHAR(60) NOT NULL DEFAULT 'Común' AFTER name,
  ADD COLUMN image_key VARCHAR(80) NOT NULL DEFAULT 'investigacion-generica' AFTER description,
  ADD COLUMN faction_code VARCHAR(40) NULL AFTER image_key,
  ADD INDEX idx_research_definitions_faction_code (faction_code),
  ADD CONSTRAINT fk_research_definitions_faction_code FOREIGN KEY (faction_code) REFERENCES factions(code);

UPDATE research_definitions
SET category = 'Relato',
    image_key = 'investigacion-argumentario-24h'
WHERE code = 'argumentario_24h';

UPDATE research_definitions
SET category = 'Territorio',
    image_key = 'investigacion-brigada-rotondas'
WHERE code = 'brigada_rotondas';

UPDATE research_definitions
SET category = 'Legalidad',
    image_key = 'investigacion-boe-predictivo'
WHERE code = 'boe_predictivo';

UPDATE research_definitions
SET category = 'Relato',
    image_key = 'investigacion-relato-avanzado'
WHERE code = 'memecracia_avanzada';

UPDATE research_definitions
SET category = 'Defensa',
    image_key = 'investigacion-siesta-logistica'
WHERE code = 'siesta_logistica';

INSERT INTO research_definitions
  (code, name, category, description, image_key, faction_code, cost_pesetas, cost_votos, cost_favores, duration_seconds, effect_type, effect_value)
VALUES
('pp_auditoria_pulcra', 'Auditoría pulcra con sobre cerrado', 'Especial PP', 'Ordena expedientes, limpia titulares y convierte la calma institucional en defensa provincial.', 'investigacion-pp-auditoria-pulcra', 'pp', 420, 260, 180, 145, 'defense_bonus', 9),
('pisoe_subvencion_circular', 'Subvención circular explicada tres veces', 'Especial PISOE', 'Transforma retrasos, ayudas y ruedas de prensa en una producción constante de apoyo público.', 'investigacion-pisoe-subvencion-circular', 'pisoe', 360, 310, 160, 138, 'influence_production', 7),
('gil_grua_prioritaria', 'Grúa prioritaria de obra urgente', 'Especial GIL', 'Acelera ocupaciones con casco brillante, contrato rápido y una maqueta que nadie se atreve a discutir.', 'investigacion-gil-grua-prioritaria', 'gil', 520, 360, 140, 150, 'conquest_bonus', 10),
('puff_asamblea_resistencia', 'Asamblea de resistencia infinita', 'Especial PUFF...', 'Convierte debate, pancarta y comisión permanente en una defensa social difícil de desalojar.', 'investigacion-puff-asamblea-resistencia', 'puff', 300, 340, 210, 142, 'defense_bonus', 8),
('vox_corneta_movilizacion', 'Corneta de movilización de balcón', 'Especial VOX', 'Sincroniza himnos, pancartas y redoble emocional para empujar operaciones territoriales.', 'investigacion-vox-corneta-movilizacion', 'vox', 390, 420, 110, 132, 'conquest_bonus', 8),
('junts_peaje_negociador', 'Peaje negociador de alta precisión', 'Especial JUNTS', 'Reduce daños colaterales negociando salidas, entradas y condiciones con letra pequeña premium.', 'investigacion-junts-peaje-negociador', 'junts', 380, 280, 240, 150, 'corruption_risk_reduction', 8);
