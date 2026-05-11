UPDATE city_building_definitions
SET description = 'Fábrica de sellos, colas y trámites con poder real.',
    effect_label = '+favores y defensa administrativa'
WHERE code = 'oficina_infinita';

UPDATE city_building_definitions
SET effect_label = '+votos y velocidad informativa'
WHERE code = 'redaccion_subvencionada';

UPDATE city_building_definitions
SET effect_label = '+favores, corrupción y trueques'
WHERE code = 'mercado_favores';

UPDATE city_building_definitions
SET effect_label = '+planes de crisis y favores'
WHERE code = 'centro_crisis';
