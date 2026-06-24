INSERT INTO troop_definitions
  (code, name, role, description, image_key, faction_code, tier, attack, attack_type, defense,
   defense_bureaucratic, defense_incisive, defense_media, influence_power, speed, capacity, training_seconds,
   unlock_building_code, unlock_building_level, cost_pesetas, cost_votos, cost_favores)
VALUES
('furgon_pancartas', 'Furgón de pancartas', 'Transporte terrestre', 'Caja con ruedas, bridas, megáfono y carteles de sobra. No gana carreras, pero mueve cuadrillas sin pedir demasiadas explicaciones.', 'furgon-pancartas', NULL, 2, 30, 'BUREAUCRATIC', 48, 46, 34, 42, 64, 27, 4, 40, 'garaje_rotondas', 2, 170, 130, 38),
('avioneta_contrabando', 'Avioneta de contrabando administrativo', 'Transporte aéreo', 'Sobrevuela atascos, fronteras emocionales y controles de agenda con una carpeta sellada que nadie quiere abrir en altura.', 'avioneta-contrabando', NULL, 3, 72, 'INCISIVE', 76, 38, 76, 48, 96, 64, 2, 72, 'garaje_rotondas', 3, 420, 220, 220),
('charter_agenda_oficial', 'Chárter de agenda oficial', 'Transporte aéreo', 'Vuelo de comitiva con asiento reservado para promesas urgentes. Caro, rápido y perfecto para aparecer donde el mapa empieza a arder.', 'charter-agenda-oficial', NULL, 4, 94, 'MEDIA', 118, 64, 72, 118, 135, 56, 4, 104, 'plato_24h', 4, 760, 620, 360);
