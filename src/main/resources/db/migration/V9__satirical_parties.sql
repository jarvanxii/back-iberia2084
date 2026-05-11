UPDATE factions
SET code = 'pp',
    name = 'Pantomima Popular',
    short_name = 'PP',
    color = '#1f78d1',
    motto = 'Gestión, populismo y demagogia.',
    satire = 'Lema secundario: "Orden, puro y palco." Conservadores de gestión solemne, calculadora rápida y promesa de estabilidad con reserva VIP.',
    starting_region = 'Castilla y León',
    corruption_affinity = 6
WHERE code = 'meseta_administrativa';

UPDATE factions
SET code = 'gil',
    name = 'Grupo Independiente Liberal',
    short_name = 'GIL',
    color = '#f2a900',
    motto = 'Obras, orden y espectáculo.',
    satire = 'Lema secundario: "Menos papeles y más grúas." Populismo de ladrillo, mando fuerte y urbanismo con aplauso municipal.',
    starting_region = 'Costa del Ladrillo',
    corruption_affinity = 12
WHERE code = 'costa_ladrillo';

UPDATE factions
SET code = 'junts',
    name = 'Junts Units Nacionalment per la Terra Sobirana',
    short_name = 'JUNTS',
    color = '#00a3ad',
    motto = 'Una provincia, una ilusión.',
    satire = 'Alternativa: "Lo nuestro primero, el resto va después." Bisagra territorial de factura propia, peaje emocional y negociación perpetua.',
    starting_region = 'Cataluña',
    corruption_affinity = 7
WHERE code = 'senores_norte';

UPDATE factions
SET code = 'pisoe',
    name = 'Unión Progresista Nacional',
    short_name = 'PISOE',
    color = '#e30613',
    motto = 'Todo para el pueblo, pero sin el pueblo.',
    satire = 'Lema secundario: "Libertad, mafia e igualdad." Progreso televisado, comité permanente y relato ajustable según barómetro.',
    starting_region = 'Andalucía',
    corruption_affinity = 8
WHERE code = 'confederacion_tapeo';

UPDATE factions
SET code = 'puff',
    name = 'Partido Unido Feminista Federal',
    short_name = 'PUFF...',
    color = '#7a3db8',
    motto = 'Igualdad, sindicatos y mucho mucho enfado.',
    satire = 'Lema secundario: "¡Levantemos el puñito!" Asamblea permanente, manifiesto urgente y bronca con perspectiva federal.',
    starting_region = 'Barcelona',
    corruption_affinity = 5
WHERE code = 'orden_boe';

UPDATE factions
SET code = 'vox',
    name = 'Votantes obreros con Xilófono',
    short_name = 'VOX',
    color = '#63be21',
    motto = 'Patria, bombo y xilófono.',
    satire = 'Alternativa: "Mucho himno, poco bemol." Orden de balcón, épica de sobremesa y percusión patriótica a contratiempo.',
    starting_region = 'Ceuta',
    corruption_affinity = 6
WHERE code = 'tecnocratas_m30';

UPDATE territories
SET flavor_faction_id = (SELECT id FROM factions WHERE code = 'junts')
WHERE flavor_faction_id IN (SELECT id FROM factions WHERE code IN ('reinos_levante', 'liga_islas'));

UPDATE players
SET faction_id = (SELECT id FROM factions WHERE code = 'junts')
WHERE faction_id IN (SELECT id FROM factions WHERE code IN ('reinos_levante', 'liga_islas'));

UPDATE alliances
SET faction_id = (SELECT id FROM factions WHERE code = 'junts')
WHERE faction_id IN (SELECT id FROM factions WHERE code IN ('reinos_levante', 'liga_islas'));

UPDATE territories
SET flavor_faction_id = (SELECT id FROM factions WHERE code = 'pp')
WHERE region IN ('Castilla y León', 'Comunidad de Madrid', 'Aragón', 'Cantabria', 'La Rioja');

UPDATE territories
SET flavor_faction_id = (SELECT id FROM factions WHERE code = 'pisoe')
WHERE region IN ('Andalucía', 'Extremadura', 'Castilla-La Mancha', 'Principado de Asturias');

UPDATE territories
SET flavor_faction_id = (SELECT id FROM factions WHERE code = 'gil')
WHERE region IN ('Comunidad Valenciana', 'Región de Murcia', 'Islas Baleares');

UPDATE territories
SET flavor_faction_id = (SELECT id FROM factions WHERE code = 'junts')
WHERE region IN ('Cataluña', 'País Vasco', 'Galicia', 'Islas Canarias');

UPDATE territories
SET flavor_faction_id = (SELECT id FROM factions WHERE code = 'vox')
WHERE region IN ('Ceuta', 'Melilla')
   OR code IN ('almeria', 'guadalajara');

UPDATE territories
SET flavor_faction_id = (SELECT id FROM factions WHERE code = 'puff')
WHERE code IN ('barcelona', 'toledo', 'navarra');

DELETE FROM factions
WHERE code IN ('reinos_levante', 'liga_islas');
