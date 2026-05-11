UPDATE troop_definitions
SET description = 'Yate fiscalmente creativo con fiesta montada a bordo: señoritas, copas, papeles y una brújula moral en modo vacaciones.'
WHERE code = 'yate_desgrabable';

INSERT INTO troop_definitions
  (code, name, role, description, image_key, faction_code, tier, attack, attack_type, defense,
   defense_bureaucratic, defense_incisive, defense_media, influence_power, speed, capacity, training_seconds,
   unlock_building_code, unlock_building_level, cost_pesetas, cost_votos, cost_favores)
VALUES
('pp_registrador_puerta_giratoria', 'Registrador de puerta giratoria', 'Especial PP', 'Abraza una puerta giratoria como si fuera un escudo medieval. Entra oposición, sale asesor.', 'pp-registrador-puerta-giratoria', 'pp', 4, 135, 'BUREAUCRATIC', 430, 430, 270, 240, 76, 8, 6, 116, 'archivo_boe', 4, 720, 240, 780),
('pp_notaria_sobremesa_infinita', 'Notaria de sobremesa infinita', 'Especial PP', 'Café, pluma y sello: firma tan lento que el rival envejece antes de recurrir.', 'pp-notaria-sobremesa-infinita', 'pp', 3, 290, 'INCISIVE', 285, 210, 285, 220, 105, 12, 5, 92, 'oficina_infinita', 4, 520, 270, 480),
('pp_senador_puro_presupuestario', 'Senador del puro presupuestario', 'Especial PP', 'Ataca con calculadora gigante y un puro de señalar partidas que nadie sabía que existían.', 'pp-senador-puro-presupuestario', 'pp', 4, 310, 'MEDIA', 330, 280, 230, 330, 180, 10, 6, 124, 'palacio_plenos', 4, 780, 560, 520),

('pisoe_doctor_argumentario_reversible', 'Doctor del argumentario reversible', 'Especial PISOE', 'Abre la carpeta por la izquierda o por la derecha y siempre encuentra una explicación convincente.', 'pisoe-doctor-argumentario-reversible', 'pisoe', 3, 330, 'MEDIA', 250, 150, 215, 250, 175, 18, 4, 86, 'plato_24h', 3, 460, 640, 220),
('pisoe_baronesa_comite_eterno', 'Baronesa de comité eterno', 'Especial PISOE', 'Aprieta el botón rojo del comité y aparecen tres subcomités, dos portavoces y una sonrisa institucional.', 'pisoe-baronesa-comite-eterno', 'pisoe', 4, 180, 'BUREAUCRATIC', 460, 460, 310, 260, 120, 7, 6, 126, 'palacio_plenos', 4, 760, 350, 720),
('pisoe_portavoz_plasma_4k', 'Portavoz de plasma 4K', 'Especial PISOE', 'Sale de la pantalla con mando a distancia y veinte micros. Niega, afirma y matiza en la misma frase.', 'pisoe-portavoz-plasma-4k', 'pisoe', 4, 380, 'MEDIA', 300, 170, 230, 300, 210, 16, 5, 110, 'plato_24h', 4, 680, 820, 260),

('gil_capataz_grua_dorada', 'Capataz de grúa dorada', 'Especial GIL', 'Con dos mandos y una tijera gigante convierte cualquier solar en promesa urbanística con música.', 'gil-capataz-grua-dorada', 'gil', 3, 395, 'BUREAUCRATIC', 250, 250, 160, 130, 100, 12, 5, 92, 'garaje_rotondas', 3, 680, 520, 260),
('gil_subastero_playa_infinita', 'Subastero de playa infinita', 'Especial GIL', 'Vende sombra, arena y vistas al futuro. Si no hay costa, la dibuja con rotulador dorado.', 'gil-subastero-playa-infinita', 'gil', 4, 350, 'INCISIVE', 285, 170, 285, 190, 150, 15, 5, 108, 'mercado_favores', 4, 740, 460, 560),
('gil_dj_inauguraciones', 'DJ de inauguraciones', 'Especial GIL', 'Pulsa el botón de confeti, corta la cinta y convierte una rotonda en festival municipal.', 'gil-dj-inauguraciones', 'gil', 4, 370, 'MEDIA', 275, 150, 170, 275, 190, 18, 4, 104, 'concejalia_festejos', 4, 620, 840, 220),

('puff_sindicalista_megasello', 'Sindicalista del megasello', 'Especial PUFF...', 'Estampa un sello del tamaño de una paellera y paraliza cualquier expediente que no haya pedido turno.', 'puff-sindicalista-megasello', 'puff', 3, 165, 'BUREAUCRATIC', 420, 420, 260, 270, 105, 8, 5, 94, 'oficina_infinita', 4, 500, 280, 640),
('puff_mediadora_sillas_infinitas', 'Mediadora de sillas infinitas', 'Especial PUFF...', 'Coloca sillas en círculo hasta que el enemigo olvida si venía a atacar o a consensuar el acta.', 'puff-mediadora-sillas-infinitas', 'puff', 4, 120, 'INCISIVE', 455, 260, 455, 310, 120, 7, 6, 120, 'palacio_plenos', 4, 620, 320, 780),
('puff_influencer_pancarta_morada', 'Influencer de pancarta morada', 'Especial PUFF...', 'Directo, aro de luz y megáfono glitter: convierte una queja local en tendencia con eco de plaza.', 'puff-influencer-pancarta-morada', 'puff', 3, 345, 'MEDIA', 230, 120, 190, 230, 190, 20, 4, 82, 'plato_24h', 3, 400, 660, 210),

('vox_notario_patria_sello_xxl', 'Notario de patria con sello XXL', 'Especial VOX', 'Lleva un sello tan grande que cada golpe suena a trámite, corneta y sobremesa indignada.', 'vox-notario-patria-sello-xxl', 'vox', 4, 190, 'BUREAUCRATIC', 440, 440, 260, 310, 120, 8, 6, 118, 'archivo_boe', 4, 620, 420, 720),
('vox_tambor_mayor_balcones', 'Tambor mayor de balcones', 'Especial VOX', 'Redoble, xilófono y bigote en alto: ataca por decibelios hasta que el mapa marca el paso.', 'vox-tambor-mayor-balcones', 'vox', 4, 405, 'MEDIA', 260, 140, 170, 260, 170, 14, 5, 108, 'plato_24h', 4, 560, 880, 200),
('vox_capitana_corneta_reglamentaria', 'Capitana de corneta reglamentaria', 'Especial VOX', 'Hace sonar la corneta y hasta los formularios se cuadran. Muy útil contra el caos y los flecos.', 'vox-capitana-corneta-reglamentaria', 'vox', 3, 235, 'INCISIVE', 360, 220, 360, 240, 130, 11, 5, 96, 'palacio_plenos', 3, 480, 420, 560),

('junts_negociador_peaje_portatil', 'Negociador de peaje portátil', 'Especial JUNTS', 'Despliega una barrera en cualquier mesa y cobra condiciones antes incluso de empezar la reunión.', 'junts-negociador-peaje-portatil', 'junts', 3, 335, 'INCISIVE', 310, 180, 310, 230, 145, 16, 4, 90, 'mercado_favores', 3, 500, 420, 560),
('junts_consellera_frontera_rotulador', 'Consellera de frontera con rotulador', 'Especial JUNTS', 'Traza una línea nueva en el mapa con rotulador XXL y todos fingen que estaba clarísima.', 'junts-consellera-frontera-rotulador', 'junts', 4, 285, 'BUREAUCRATIC', 420, 420, 330, 260, 135, 10, 6, 122, 'archivo_boe', 4, 760, 420, 760),
('junts_embajador_ventanilla_propia', 'Embajador de ventanilla propia', 'Especial JUNTS', 'Viaja con su propia ventanilla: donde la posa, nace una competencia exclusiva.', 'junts-embajador-ventanilla-propia', 'junts', 4, 260, 'MEDIA', 360, 240, 290, 360, 170, 12, 5, 112, 'palacio_plenos', 4, 690, 520, 620);
