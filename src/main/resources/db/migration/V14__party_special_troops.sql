ALTER TABLE troop_definitions
  ADD COLUMN faction_code VARCHAR(40) NULL AFTER image_key,
  ADD INDEX idx_troop_definitions_faction_code (faction_code),
  ADD CONSTRAINT fk_troop_definitions_faction_code FOREIGN KEY (faction_code) REFERENCES factions(code);

INSERT INTO troop_definitions
  (code, name, role, description, image_key, faction_code, tier, attack, attack_type, defense,
   defense_bureaucratic, defense_incisive, defense_media, influence_power, speed, capacity, training_seconds,
   unlock_building_code, unlock_building_level, cost_pesetas, cost_votos, cost_favores)
VALUES
('pp_tesorera_caja_fuerte', 'Tesorera de caja fuerte', 'Especial PP', 'Blindaje contable con llave dorada: aguanta auditorías, titulares y preguntas con una ceja inmóvil.', 'pp-tesorera-caja-fuerte', 'pp', 3, 90, 'BUREAUCRATIC', 360, 360, 260, 210, 80, 8, 5, 96, 'archivo_boe', 3, 520, 180, 620),
('pp_marques_palco_vip', 'Marqués del palco VIP', 'Especial PP', 'Convierte un palco, unos prismáticos y una sonrisa de palco en defensa mediática de alta alcurnia.', 'pp-marques-palco-vip', 'pp', 4, 260, 'MEDIA', 290, 220, 210, 290, 130, 12, 6, 118, 'alcaldia_perpetua', 3, 740, 560, 420),

('pisoe_fontanera_comite', 'Fontanera de comité', 'Especial PISOE', 'Aprieta una tubería interna y, de pronto, media ejecutiva vuelve a votar lo correcto.', 'pisoe-fontanera-comite', 'pisoe', 3, 315, 'INCISIVE', 230, 190, 230, 170, 120, 17, 5, 92, 'mercado_favores', 3, 430, 520, 360),
('pisoe_ministro_rueda_perpetua', 'Ministro de rueda perpetua', 'Especial PISOE', 'No responde preguntas: las convierte en marco televisivo con sonrisa, focos y quince micrófonos.', 'pisoe-ministro-rueda-perpetua', 'pisoe', 4, 330, 'MEDIA', 340, 190, 220, 340, 170, 14, 6, 112, 'plato_24h', 3, 680, 660, 300),

('gil_promotor_rotondas', 'Promotor de rotondas', 'Especial GIL', 'Si hay suelo, hay rotonda; si hay rotonda, hay votos; si hay votos, hay otra rotonda.', 'gil-promotor-rotondas', 'gil', 3, 360, 'BUREAUCRATIC', 240, 240, 160, 130, 90, 10, 5, 88, 'garaje_rotondas', 3, 620, 480, 240),
('gil_concejala_festejos_premium', 'Concejala de festejos premium', 'Especial GIL', 'Tijera gigante, confeti y escenario: inaugura cualquier cosa y encima parece planificada.', 'gil-concejala-festejos-premium', 'gil', 4, 340, 'MEDIA', 260, 150, 170, 260, 150, 16, 5, 102, 'concejalia_festejos', 3, 560, 720, 180),

('puff_asamblearia_megafono_morado', 'Asamblearia de megáfono morado', 'Especial PUFF...', 'Grita una consigna, levanta el puño de espuma y convierte una plaza pequeña en tormenta mediática.', 'puff-asamblearia-megafono-morado', 'puff', 3, 300, 'MEDIA', 270, 140, 210, 270, 150, 18, 4, 84, 'plaza_promesas', 3, 380, 560, 260),
('puff_guardiana_circulo_infinito', 'Guardiana del círculo infinito', 'Especial PUFF...', 'Silla plegable en ristre: nadie rompe una asamblea que todavía no ha votado el orden del día.', 'puff-guardiana-circulo-infinito', 'puff', 4, 140, 'BUREAUCRATIC', 380, 380, 240, 260, 95, 8, 5, 104, 'palacio_plenos', 4, 520, 260, 620),

('vox_tamborilera_balcon', 'Tamborilera de balcón', 'Especial VOX', 'Redoble, xilófono y balcón: ataca a distancia por saturación épica y decibelios patrióticos.', 'vox-tamborilera-balcon', 'vox', 3, 350, 'MEDIA', 210, 120, 150, 210, 125, 13, 4, 86, 'plato_24h', 3, 420, 690, 180),
('vox_heraldo_atril_verde', 'Heraldo del atril verde', 'Especial VOX', 'Campana, batuta y atril: convierte cada defensa mediática en una ceremonia imposible de interrumpir.', 'vox-heraldo-atril-verde', 'vox', 4, 210, 'BUREAUCRATIC', 350, 260, 220, 350, 110, 9, 5, 106, 'palacio_plenos', 4, 560, 420, 520),

('junts_negociadora_peaje', 'Negociadora de peaje', 'Especial JUNTS', 'Sube la barrera, baja la barrera y cobra una condición nueva por cada metro de territorio.', 'junts-negociadora-peaje', 'junts', 3, 310, 'INCISIVE', 280, 180, 280, 210, 120, 15, 4, 90, 'mercado_favores', 3, 460, 380, 520),
('junts_conseller_frontera_simbolica', 'Conseller de frontera simbólica', 'Especial JUNTS', 'Traza una línea en el mapa y de repente todo el mundo necesita una comisión bilateral.', 'junts-conseller-frontera-simbolica', 'junts', 4, 245, 'INCISIVE', 390, 390, 300, 250, 135, 11, 6, 120, 'archivo_boe', 4, 720, 460, 680);
