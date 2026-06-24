INSERT INTO troop_definitions
  (code, name, role, description, image_key, faction_code, tier, attack, attack_type, transport_type, defense,
   defense_bureaucratic, defense_incisive, defense_media, influence_power, speed, capacity, training_seconds,
   unlock_building_code, unlock_building_level, cost_pesetas, cost_votos, cost_favores)
VALUES
('agente_dossier_fantasma', 'Agente del dossier fantasma', 'Espionaje',
 'No toma plazas por la puerta principal: escucha ascensores, cruza notas de prensa y deja al rival defendiendo un rumor que todavía no existe.',
 'agente-dossier-fantasma', NULL, 3, 142, 'INCISIVE', NULL, 132, 82, 132, 92, 175, 18, 1, 78,
 'observatorio_rumor', 2, 210, 160, 260);
