INSERT INTO troop_definitions
  (code, name, role, description, image_key, faction_code, tier, attack, attack_type, defense,
   defense_bureaucratic, defense_incisive, defense_media, influence_power, speed, capacity, training_seconds,
   unlock_building_code, unlock_building_level, cost_pesetas, cost_votos, cost_favores)
VALUES
('peugeot_campana', 'Peugeot de campaña', 'Transporte terrestre', 'Coche veterano con megáfono en el techo: rápido, barato y con olor a octavilla mojada.', 'peugeot-campana', NULL, 1, 18, 'MEDIA', 28, 22, 20, 28, 42, 34, 2, 22, 'garaje_rotondas', 1, 80, 70, 18),
('autobus_partido', 'Autobús de partido', 'Transporte terrestre', 'Sede móvil con atril desplegable, altavoces y maletero para pancartas, bocadillos y contradicciones.', 'autobus-partido', NULL, 2, 38, 'BUREAUCRATIC', 56, 50, 42, 56, 78, 18, 9, 54, 'garaje_rotondas', 2, 240, 180, 75),
('yate_desgrabable', 'Yate desgrabable', 'Transporte marítimo', 'Lujo fiscal con brújula moral flexible: mueve élites por la costa y deja facturas que nadie entiende.', 'yate-desgrabable', NULL, 3, 70, 'INCISIVE', 110, 45, 110, 65, 92, 24, 5, 84, 'mercado_favores', 3, 540, 160, 520),
('lancha_publicitaria', 'Lancha publicitaria', 'Transporte marítimo', 'Bólido costero con megáfono XXL: desembarca relato antes de que el rival encuentre el chaleco salvavidas.', 'lancha-publicitaria', NULL, 2, 64, 'MEDIA', 80, 38, 42, 80, 98, 38, 3, 60, 'plato_24h', 2, 260, 280, 80);
