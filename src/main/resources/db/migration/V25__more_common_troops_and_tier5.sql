-- Nuevas unidades comunes y ajuste de plazas: el tier marca complejidad/desbloqueo, no tamaño físico.

UPDATE troop_definitions
SET capacity = 2
WHERE code = 'presentador_tv';

UPDATE troop_definitions
SET capacity = 3
WHERE code = 'asesor_sombra';

UPDATE troop_definitions
SET capacity = 4
WHERE code = 'baron_territorial';

UPDATE troop_definitions
SET capacity = 6
WHERE code = 'alcalde';

INSERT INTO troop_definitions
  (code, name, role, description, image_key, faction_code, tier, attack, attack_type, defense,
   defense_bureaucratic, defense_incisive, defense_media, influence_power, speed, capacity, training_seconds,
   unlock_building_code, unlock_building_level, cost_pesetas, cost_votos, cost_favores)
VALUES
('tecnica_licencias_imposibles', 'Técnica de licencias imposibles', 'Legalidad',
 'Sostiene un tomo que parece pesar más que la provincia y convierte cualquier avance rival en permiso pendiente.',
 'pp-registrador-blindado', NULL, 2, 86, 'BUREAUCRATIC', 156, 156, 72, 52, 42, 10, 2, 56,
 'archivo_boe', 2, 190, 92, 170),
('fontanera_expedientes_urgentes', 'Fontanera de expedientes urgentes', 'Burocracia',
 'Aprieta una tubería administrativa y de pronto aparecen informes, anexos y tres excusas con sello de entrada.',
 'pisoe-fontanero-comite', NULL, 3, 132, 'INCISIVE', 150, 118, 150, 84, 88, 13, 2, 74,
 'oficina_infinita', 3, 260, 190, 210),
('sindicalista_expediente_eterno', 'Sindicalista de expediente eterno', 'Presión social',
 'Carga carpetas como barricadas y sabe convertir una cola de funcionarios en muralla negociadora.',
 'puff-sindicalista-expediente-eterno', NULL, 3, 108, 'BUREAUCRATIC', 238, 238, 150, 118, 122, 9, 3, 86,
 'mercado_favores', 3, 330, 280, 360),
('registradora_blindada_tomo_unico', 'Registradora blindada del tomo único', 'Legalidad superior',
 'No ocupa mucho sitio, pero cuando abre el libro el rival descubre que su ofensiva necesitaba compulsa previa.',
 'pp-registrador-blindado', NULL, 5, 310, 'BUREAUCRATIC', 540, 540, 310, 220, 180, 8, 4, 148,
 'archivo_boe', 5, 980, 420, 760),
('senor_palco_prismaticos', 'Señor del palco con prismáticos', 'Influencia superior',
 'Mira desde arriba, saluda poco y detecta grietas políticas antes de que el acta llegue a la mesa.',
 'pp-senor-del-palco', NULL, 5, 430, 'MEDIA', 390, 250, 210, 390, 240, 11, 3, 156,
 'alcaldia_perpetua', 5, 1180, 840, 640);
