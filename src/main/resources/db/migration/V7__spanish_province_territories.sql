CREATE TEMPORARY TABLE province_seed (
  legacy_code VARCHAR(60) NULL,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(100) NOT NULL,
  region VARCHAR(80) NOT NULL,
  view_x INT NOT NULL,
  view_y INT NOT NULL,
  faction_id BIGINT NOT NULL,
  defense INT NOT NULL,
  population INT NOT NULL,
  base_votes INT NOT NULL,
  resource_focus VARCHAR(30) NOT NULL,
  building_name VARCHAR(100) NOT NULL,
  satire VARCHAR(255) NOT NULL,
  PRIMARY KEY (code)
) ENGINE=Memory;

INSERT INTO province_seed
  (legacy_code, code, name, region, view_x, view_y, faction_id, defense, population, base_votes, resource_focus, building_name, satire)
VALUES
('galicia-nevoa', 'a-coruna', 'A Coruña', 'Galicia', 105, 105, 3, 57, 1120, 112, 'favores', 'Consello de la Lluvia Horizontal', 'Nunca sabes si te han pactado o si era niebla.'),
(NULL, 'lugo', 'Lugo', 'Galicia', 175, 95, 3, 54, 330, 72, 'funcionarios', 'Muralla de Expedientes Circulares', 'Aquí cada trámite da la vuelta completa antes de decir que no.'),
('porto-industrial', 'pontevedra', 'Pontevedra', 'Galicia', 105, 175, 3, 53, 950, 104, 'pesetas', 'Ría de Pactos con Denominación', 'Exporta calma, marisco y maniobras discretas.'),
(NULL, 'ourense', 'Ourense', 'Galicia', 180, 170, 3, 51, 305, 68, 'influencia', 'Balneario de Consensos Tibios', 'La política entra caliente y sale diciendo depende.'),
('oviedo-ministerio', 'asturias', 'Asturias', 'Principado de Asturias', 275, 82, 3, 55, 1000, 95, 'funcionarios', 'Consejo del Cachopo Regulador', 'Resistencia administrativa con sidra de precisión.'),
('santander-lluvia', 'cantabria', 'Cantabria', 'Cantabria', 360, 88, 4, 53, 590, 84, 'pesetas', 'Observatorio de Nubes Rentables', 'La lluvia cae, la moral sube y el paraguas cotiza.'),
('bilbao-forja', 'bizkaia', 'Bizkaia', 'País Vasco', 435, 82, 3, 59, 1150, 124, 'pesetas', 'Alto Horno de Consensos Duros', 'No presume: simplemente asume que todo pesa más allí.'),
(NULL, 'gipuzkoa', 'Gipuzkoa', 'País Vasco', 505, 86, 3, 58, 725, 96, 'influencia', 'Diputación del Pintxo Estratégico', 'Pequeña, intensa y capaz de negociar con precisión quirúrgica.'),
(NULL, 'alava', 'Álava', 'País Vasco', 470, 145, 3, 56, 335, 76, 'funcionarios', 'Archivo Foral de Pactos Blindados', 'Todo acuerdo sale con denominación administrativa.'),
(NULL, 'navarra', 'Navarra', 'Navarra', 565, 150, 5, 57, 665, 88, 'favores', 'Palacio del Fuero Reversible', 'Si no encaja en el mapa, se negocia una excepción elegante.'),
(NULL, 'la-rioja', 'La Rioja', 'La Rioja', 500, 205, 4, 50, 320, 70, 'votos', 'Consejo Regulador del Brindis Electoral', 'Un voto entra mejor si respira antes de contarse.'),
(NULL, 'leon', 'León', 'Castilla y León', 270, 160, 1, 55, 450, 82, 'funcionarios', 'Reino del Sello con Historia', 'Cualquier debate mejora si empieza en el siglo correcto.'),
(NULL, 'palencia', 'Palencia', 'Castilla y León', 350, 160, 1, 49, 160, 56, 'pesetas', 'Nave de Logística Sin Prisa', 'Parece vacía hasta que necesitas mover medio país.'),
(NULL, 'burgos', 'Burgos', 'Castilla y León', 425, 205, 1, 54, 355, 76, 'pesetas', 'Nudo del Frío Industrial', 'El consenso se conserva mejor a baja temperatura.'),
(NULL, 'zamora', 'Zamora', 'Castilla y León', 260, 245, 1, 50, 165, 58, 'favores', 'Puente de Pactos Silenciosos', 'La discreción aquí no es virtud: es infraestructura.'),
('valladolid-sello', 'valladolid', 'Valladolid', 'Castilla y León', 350, 235, 1, 52, 520, 88, 'funcionarios', 'Fábrica Nacional de Trámites con Eco', 'Todo expediente vuelve, como el frío.'),
(NULL, 'soria', 'Soria', 'Castilla y León', 520, 260, 1, 48, 90, 52, 'influencia', 'Despoblado de Alta Estrategia', 'Hay pocos votantes, pero todos caben en una rueda de prensa.'),
('salamanca-doctrina', 'salamanca', 'Salamanca', 'Castilla y León', 280, 325, 5, 50, 330, 76, 'influencia', 'Universidad del Argumento Infinito', 'Aquí una coma puede tumbar un gobierno local.'),
(NULL, 'avila', 'Ávila', 'Castilla y León', 375, 320, 5, 51, 160, 58, 'funcionarios', 'Muralla de Competencias Transferibles', 'Todo queda protegido, incluso lo que nadie pidió proteger.'),
(NULL, 'segovia', 'Segovia', 'Castilla y León', 440, 305, 1, 50, 155, 58, 'votos', 'Acueducto de Promesas en Cascada', 'El agua no siempre llega, la foto sí.'),
(NULL, 'huesca', 'Huesca', 'Aragón', 655, 205, 1, 51, 225, 64, 'pesetas', 'Pirineo de Presupuestos Verticales', 'La cuesta arriba también cuenta como programa electoral.'),
('zaragoza-puente', 'zaragoza', 'Zaragoza', 'Aragón', 625, 290, 1, 51, 970, 105, 'pesetas', 'Nudo Logístico del Ya Que Pasas', 'Quien controla el paso controla la excusa.'),
(NULL, 'teruel', 'Teruel', 'Aragón', 625, 380, 1, 49, 135, 54, 'influencia', 'Observatorio de Existencia Recurrente', 'Aparece en campaña con la puntualidad de una promesa olvidada.'),
(NULL, 'lleida', 'Lleida', 'Cataluña', 745, 225, 6, 52, 440, 82, 'pesetas', 'Granero de Datos y Fruta Regulada', 'Cada campaña empieza con fibra, cobertura y una cooperativa enfadada.'),
(NULL, 'girona', 'Girona', 'Cataluña', 835, 215, 6, 54, 805, 98, 'influencia', 'Costa del Relato Premium', 'La frontera mira, la temporada cobra y el titular madruga.'),
('barcelona-algoritmica', 'barcelona', 'Barcelona', 'Cataluña', 805, 300, 6, 52, 2900, 165, 'influencia', 'Ateneo de Startups con Subvención Retroactiva', 'Diseña el futuro en beta y lo cobra en premium.'),
(NULL, 'tarragona', 'Tarragona', 'Cataluña', 740, 365, 6, 51, 830, 96, 'pesetas', 'Puerto de Pactos Petroquímicos', 'Huele a industria, verano y negociación larga.'),
('madrid-centro', 'madrid', 'Madrid', 'Comunidad de Madrid', 455, 380, 6, 60, 6900, 190, 'funcionarios', 'Kilómetro Cero del Argumentario', 'Todo empieza aquí, especialmente lo que nadie pidió.'),
('m30-orbital', 'guadalajara', 'Guadalajara', 'Castilla-La Mancha', 540, 355, 5, 49, 270, 64, 'funcionarios', 'Corredor del Despacho Expandido', 'Madrid estornuda y aquí aparece una urbanización filosófica.'),
('toledo-archivo', 'toledo', 'Toledo', 'Castilla-La Mancha', 455, 455, 5, 56, 710, 90, 'funcionarios', 'Archivo de Pactos Visigodos Vigentes', 'Nadie entiende el precedente, pero todos le temen.'),
(NULL, 'cuenca', 'Cuenca', 'Castilla-La Mancha', 555, 455, 5, 48, 195, 58, 'influencia', 'Hoces del Argumento Suspendido', 'La promesa cuelga, el rival mira abajo.'),
(NULL, 'ciudad-real', 'Ciudad Real', 'Castilla-La Mancha', 455, 540, 5, 50, 495, 78, 'votos', 'Llanura de Mayorías Pacientes', 'Parece quieta hasta que decide una investidura.'),
(NULL, 'albacete', 'Albacete', 'Castilla-La Mancha', 590, 550, 5, 50, 390, 74, 'pesetas', 'Navaja Presupuestaria Multiusos', 'Corta cinta, corta déficit y a veces corta la conversación.'),
('extremadura-reserva', 'caceres', 'Cáceres', 'Extremadura', 305, 425, 4, 49, 385, 70, 'favores', 'Dehesa de Pactos a Media Voz', 'Mucho espacio, poca prisa y sorpresas en el boletín.'),
('lisboa-atlantica', 'badajoz', 'Badajoz', 'Extremadura', 305, 525, 4, 48, 670, 82, 'votos', 'Frontera de Promesas Reversibles', 'Añade paciencia a cualquier negociación presupuestaria.'),
(NULL, 'castellon', 'Castellón', 'Comunidad Valenciana', 705, 445, 7, 50, 590, 82, 'pesetas', 'Cerámica de Campaña Permanente', 'El azulejo aguanta mejor que muchos programas.'),
('valencia-solar', 'valencia', 'Valencia', 'Comunidad Valenciana', 690, 515, 7, 48, 2600, 150, 'votos', 'Conselleria de la Mascletà Presupuestaria', 'Produce debates sobre dónde empieza exactamente la paella.'),
('alicante-resort', 'alicante', 'Alicante', 'Comunidad Valenciana', 680, 590, 2, 44, 1950, 125, 'pesetas', 'Palacio del Todo Incluido Electoral', 'Cada sombrilla cuenta como infraestructura crítica.'),
('murcia-regadio', 'murcia', 'Murcia', 'Región de Murcia', 600, 615, 7, 45, 1550, 112, 'favores', 'Confederación del Grifo Milagroso', 'Aquí el agua es recurso, mito y programa electoral.'),
(NULL, 'huelva', 'Huelva', 'Andalucía', 235, 590, 4, 47, 530, 76, 'pesetas', 'Muelle de Promesas Fosforescentes', 'Brilla de noche y en campaña, que no siempre es lo mismo.'),
('sevilla-tapeo', 'sevilla', 'Sevilla', 'Andalucía', 335, 600, 4, 48, 1950, 145, 'votos', 'Parlamento de la Croqueta Vinculante', 'La moción prospera si llega caliente.'),
(NULL, 'cordoba', 'Córdoba', 'Andalucía', 430, 595, 4, 49, 775, 92, 'influencia', 'Patio de Consensos con Sombra', 'Se discute mejor cuando alguien riega la estrategia.'),
(NULL, 'jaen', 'Jaén', 'Andalucía', 520, 600, 4, 50, 620, 86, 'favores', 'Consejo del Olivar Parlamentario', 'Todo pacto necesita raíz, paciencia y alguien que pague el desayuno.'),
('cadiz-resistencia', 'cadiz', 'Cádiz', 'Andalucía', 325, 665, 4, 50, 1250, 104, 'influencia', 'Astillero de Chirigotas Geopolíticas', 'Una provincia pequeña con daño crítico en carnaval.'),
('malaga-nube', 'malaga', 'Málaga', 'Andalucía', 440, 650, 2, 45, 1750, 130, 'pesetas', 'Distrito Fiscal de la Hamaca Digital', 'Nómadas digitales, espetos y alquileres en modo jefe final.'),
('granada-alhambra', 'granada', 'Granada', 'Andalucía', 550, 665, 4, 49, 930, 104, 'influencia', 'Archivo Nazarí de Pactos Imposibles', 'La belleza también se puede usar como arma electoral.'),
(NULL, 'almeria', 'Almería', 'Andalucía', 645, 650, 2, 46, 740, 88, 'pesetas', 'Invernadero de Mayorías Aceleradas', 'Todo crece rápido, incluso las explicaciones.'),
('mallorca-premium', 'illes-balears', 'Illes Balears', 'Islas Baleares', 875, 505, 8, 46, 1250, 118, 'pesetas', 'Consulado del Balcón Regulado', 'Diplomacia de temporada alta con suplemento de limpieza.'),
('ibiza-influencer', 'las-palmas', 'Las Palmas', 'Islas Canarias', 150, 675, 8, 44, 1130, 104, 'votos', 'Cabildo del Voto con Brisa', 'Lejos del centro, cerca de cualquier pacto que necesite sol.'),
('tenerife-volcan', 'santa-cruz-de-tenerife', 'Santa Cruz de Tenerife', 'Islas Canarias', 235, 675, 8, 55, 1050, 102, 'influencia', 'Cabildo Geotérmico del Drama Tropical', 'Lejos del mapa, cerca del presupuesto.'),
(NULL, 'ceuta', 'Ceuta', 'Ceuta', 500, 695, 1, 52, 85, 42, 'funcionarios', 'Aduana de Pactos Relámpago', 'Pequeña en superficie, enorme cuando falta un voto.'),
(NULL, 'melilla', 'Melilla', 'Melilla', 575, 695, 5, 54, 86, 42, 'favores', 'Oficina de Equilibrios Imposibles', 'Aquí la aritmética política viene con vistas al puerto.');

UPDATE territories t
JOIN province_seed p ON p.legacy_code = t.code
SET t.code = p.code,
    t.name = p.name,
    t.region = p.region,
    t.map_x = ROUND(p.view_x / 10),
    t.map_y = ROUND(p.view_y / 7.2),
    t.flavor_faction_id = p.faction_id,
    t.defense = p.defense,
    t.population = p.population,
    t.base_votes = p.base_votes,
    t.resource_focus = p.resource_focus,
    t.building_name = p.building_name,
    t.satire = p.satire
WHERE t.world_id = 1;

INSERT INTO territories
  (world_id, code, name, region, map_x, map_y, flavor_faction_id, defense, population, base_votes, resource_focus, building_name, satire)
SELECT w.id,
       p.code,
       p.name,
       p.region,
       ROUND(p.view_x / 10),
       ROUND(p.view_y / 7.2),
       p.faction_id,
       p.defense,
       p.population,
       p.base_votes,
       p.resource_focus,
       p.building_name,
       p.satire
FROM worlds w
JOIN province_seed p
WHERE NOT EXISTS (
  SELECT 1
  FROM territories t
  WHERE t.world_id = w.id
    AND t.code = p.code
);

DROP TEMPORARY TABLE province_seed;
