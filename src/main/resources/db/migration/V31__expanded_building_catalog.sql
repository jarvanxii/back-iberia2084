INSERT INTO city_building_definitions
  (code, name, category, description, image_key, map_x, map_y, width, height, max_level,
   cost_pesetas, cost_votos, cost_favores, duration_seconds, effect_label)
VALUES
('imprenta_argumentarios', 'Imprenta de Argumentarios', 'Impresión', 'Máquinas calientes, papel nervioso y consignas listas antes de que alguien pregunte por los datos.', 'imprenta-argumentarios', 25, 20, 12, 10, 20, 210, 170, 42, 72, '+votos y reducción de coste de investigaciones'),
('escuela_cuadros', 'Escuela de Cuadros Locales', 'Formación', 'Aula de atriles, libretas y promesas medidas con cronómetro para convertir vecinos en mandos obedientes.', 'escuela-cuadros', 39, 20, 13, 10, 20, 260, 210, 58, 82, '+velocidad de generación de unidades comunes'),
('centro_logistica', 'Centro Logístico de Comitiva', 'Logística', 'Nave de rutas, furgones y cafés de termo para mover tropa sin pedir milagros al mapa.', 'centro-logistica', 72, 35, 14, 11, 20, 330, 190, 72, 88, '+plazas operativas y apoyo a transportes terrestres'),
('observatorio_rumor', 'Observatorio del Rumor Temprano', 'Inteligencia', 'Sala discreta donde una captura borrosa se convierte en alarma antes de que llegue el comunicado.', 'observatorio-rumor', 12, 48, 12, 10, 20, 240, 260, 90, 92, '+detección de riesgos y lectura de provincias rivales'),
('tribunal_cuentas_flexible', 'Tribunal de Cuentas Flexible', 'Fiscalización', 'Despacho severo por fuera y elástico por dentro: pregunta mucho, sanciona cuando conviene.', 'tribunal-cuentas-flexible', 27, 83, 13, 10, 20, 390, 220, 120, 105, '+defensa incisiva y control de corrupción'),
('banco_obras_promesas', 'Banco de Obras y Promesas', 'Tesorería', 'Ventanilla para convertir futuribles, maquetas y fotos con casco en liquidez inmediata.', 'banco-obras-promesas', 66, 88, 14, 10, 20, 440, 180, 140, 112, '+pesetas y financiación de mejoras largas'),
('puerto_comitivas', 'Puerto de Comitivas Discretas', 'Logística marítima', 'Muelle de yates, lanchas y agendas mojadas para llevar apoyos donde la carretera se rinde.', 'puerto-comitivas', 88, 74, 13, 10, 20, 360, 240, 160, 116, '+transportes marítimos y alcance costero'),
('aerodromo_ruedas_prensa', 'Aeródromo de Ruedas de Prensa', 'Logística aérea', 'Pista corta, foco largo y una escalera perfecta para aterrizar en cualquier crisis con cara seria.', 'aerodromo-ruedas-prensa', 88, 14, 13, 11, 20, 520, 360, 220, 130, '+transportes aéreos y respuesta rápida');

INSERT IGNORE INTO player_city_buildings (player_id, building_code, level)
SELECT p.id, b.code, 0
FROM players p
JOIN city_building_definitions b
WHERE b.code IN (
  'imprenta_argumentarios',
  'escuela_cuadros',
  'centro_logistica',
  'observatorio_rumor',
  'tribunal_cuentas_flexible',
  'banco_obras_promesas',
  'puerto_comitivas',
  'aerodromo_ruedas_prensa'
);
