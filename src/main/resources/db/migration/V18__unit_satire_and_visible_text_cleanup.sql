UPDATE resource_definitions
SET description = 'Sirven para comprar casi todo: edificios, unidades, investigaciones y cualquier ocurrencia con factura.'
WHERE code = 'pesetas';

UPDATE resource_definitions
SET description = 'Sirven para comprar unidades y edificios ofensivos. Sin votos no hay invasión que sobreviva al escrutinio.'
WHERE code = 'votos';

UPDATE resource_definitions
SET description = 'Sirven para comprar unidades y edificios defensivos. Son llamadas, deudas y puertas que no se cierran.'
WHERE code = 'favores';

UPDATE city_building_definitions
SET name = 'Palacio de Plenos',
    description = 'Centro neurálgico de la provincia: discursos, pactos, desbloqueos y café malo.',
    effect_label = '+capacidad política y desbloqueos urbanos'
WHERE code = 'palacio_plenos';

UPDATE city_building_definitions
SET name = 'Oficina de Atención Infinita',
    description = 'Fábrica de sellos, colas y funcionarios con poder real.'
WHERE code = 'oficina_infinita';

UPDATE city_building_definitions
SET name = 'Redacción Subvencionada',
    description = 'Periodistas, filtraciones y titulares que llegan antes que los hechos.',
    effect_label = '+periodistas e influencia mediática'
WHERE code = 'redaccion_subvencionada';

UPDATE city_building_definitions
SET name = 'Plató Nacional 24h',
    description = 'Donde el drama se empaqueta con luz cálida y rótulos urgentes.'
WHERE code = 'plato_24h';

UPDATE city_building_definitions
SET description = 'Un laberinto de anexos capaz de convertir la realidad en trámite.'
WHERE code = 'archivo_boe';

UPDATE city_building_definitions
SET effect_label = '+influencia, corrupción y trueques'
WHERE code = 'mercado_favores';

UPDATE city_building_definitions
SET effect_label = '+votos y moral de campaña'
WHERE code = 'plaza_promesas';

UPDATE city_building_definitions
SET description = 'Sala de pantallas, mapas mojados y chalecos reflectantes sin estrenar.',
    effect_label = '+planes de crisis y reputación'
WHERE code = 'centro_crisis';

UPDATE city_building_definitions
SET name = 'Torre de Antenas del Relato',
    description = 'Amplifica directos, tertulias y explicaciones que nadie pidió.'
WHERE code = 'antena_relato';

UPDATE city_building_definitions
SET name = 'Garaje de Rotondas Oficiales',
    description = 'Depósito de vallas, conos y proyectos que sobreviven a cualquier gobierno.',
    effect_label = '+operaciones territoriales'
WHERE code = 'garaje_rotondas';

UPDATE city_building_definitions
SET name = 'Concejalía de Festejos y Contratos',
    description = 'Donde una charanga puede ser infraestructura estratégica.'
WHERE code = 'concejalia_festejos';

UPDATE city_building_definitions
SET name = 'Despacho de Alcaldía Perpetua',
    description = 'Balcón, llave gigante y la sensación de que el municipio cabe en un bolsillo.'
WHERE code = 'alcaldia_perpetua';

UPDATE troop_definitions
SET name = 'Funcionario raso de sello vengativo',
    description = 'Camina como si el BOE pesara en los tobillos, pero cada sello convierte una invasión en cola de lunes a las ocho.'
WHERE code = 'funcionario_raso';

UPDATE troop_definitions
SET name = 'Administrativo de ventanilla blindada',
    description = 'Levanta la ceja, pide el anexo que nadie sabía que existía y gana defensas por puro agotamiento rival.'
WHERE code = 'administrativo';

UPDATE troop_definitions
SET name = 'Periodista de guardia con micro afilado',
    description = 'Aparece donde hay café gratis, mete el micro entre dos croquetas y convierte cualquier tropiezo en exclusiva.'
WHERE code = 'periodista';

UPDATE troop_definitions
SET name = 'Presentador de televisión incendiaria',
    description = 'Sonríe como anuncio de colonia mientras prende tres tertulias y culpa al mapa de la tensión.'
WHERE code = 'presentador_tv';

UPDATE troop_definitions
SET name = 'Inspector de Hacienda con lupa de castigo',
    description = 'No corre, pero encuentra una factura de 2037 en el bolsillo equivocado y la enseña con cariño fiscal.'
WHERE code = 'inspector_hacienda';

UPDATE troop_definitions
SET name = 'Asesor en la sombra con pinganillo ajeno',
    description = 'Nadie lo ve, todos lo obedecen: tose una vez y media bancada cambia de opinión sin mirar.'
WHERE code = 'asesor_sombra';

UPDATE troop_definitions
SET name = 'Concejal de festejos y asfaltos',
    description = 'Domina fiestas, contratas menores y la magia de inaugurar una papelera como infraestructura crítica.'
WHERE code = 'concejal';

UPDATE troop_definitions
SET name = 'Alcalde de balcón blindado',
    description = 'Balcón, bastón y sonrisa panorámica: promete tres puentes sin río y aun así sube la moral.'
WHERE code = 'alcalde';

UPDATE troop_definitions
SET name = 'Ujier del cordón rojo',
    description = 'Mueve una cinta aterciopelada dos centímetros y detiene un ejército entero por falta de acreditación.'
WHERE code = 'ujier_cordon';

UPDATE troop_definitions
SET name = 'Fiscalizador de contratos menores',
    description = 'Saca carpeta roja, huele una adjudicación rara y convierte el pliego en deporte extremo televisado.'
WHERE code = 'fiscalizador_contratos';

UPDATE troop_definitions
SET name = 'Tertuliano de prime time y vena hinchada',
    description = 'No trae datos, trae volumen. Gana a distancia hasta que el rival pide subtítulos y tila.'
WHERE code = 'tertuliano_prime_time';

UPDATE troop_definitions
SET name = 'Barón territorial de café torcido',
    description = 'Lleva décadas oliendo traiciones, nombrando primos y sobreviviendo a congresos con una servilleta.'
WHERE code = 'baron_territorial';

UPDATE troop_definitions
SET description = 'Veterano de rotondas, megáfono en el techo y olor a octavilla húmeda: no acelera, hace promesas en tercera.'
WHERE code = 'peugeot_campana';

UPDATE troop_definitions
SET description = 'Sede móvil con atril desplegable, bocadillos tibios y una megafonía que convierte baches en épica.'
WHERE code = 'autobus_partido';

UPDATE troop_definitions
SET description = 'Fiesta flotante con señoritas, copas, asesores en lino y una factura que siempre aparece a nombre de Cultura Náutica.'
WHERE code = 'yate_desgrabable';

UPDATE troop_definitions
SET description = 'Entra por la costa con megáfono XXL, salpica propaganda y se marcha antes de que llegue la normativa portuaria.'
WHERE code = 'lancha_publicitaria';

UPDATE troop_definitions
SET name = 'Tesorera del candado legendario',
    description = 'Abraza la caja fuerte como si fuera patrimonio nacional: cada pregunta rebota y vuelve convertida en informe interno.'
WHERE code = 'pp_tesorera_caja_fuerte';

UPDATE troop_definitions
SET name = 'Marqués del palco y la calculadora',
    description = 'Observa la batalla con prismáticos de terciopelo y defiende con sonrisas de palco, puro y Excel heredado.'
WHERE code = 'pp_marques_palco_vip';

UPDATE troop_definitions
SET name = 'Registrador de puerta giratoria',
    description = 'Gira tan deprisa entre cargo y consejo asesor que el rival se marea antes de presentar alegaciones.'
WHERE code = 'pp_registrador_puerta_giratoria';

UPDATE troop_definitions
SET name = 'Notaria de sobremesa infinita',
    description = 'Café, pluma y sello: firma tan lento que el enemigo envejece, hereda y vuelve a pedir cita previa.'
WHERE code = 'pp_notaria_sobremesa_infinita';

UPDATE troop_definitions
SET name = 'Senador del puro presupuestario',
    description = 'Señala partidas ocultas con un puro ceremonial y encuentra margen fiscal debajo de cualquier alfombra.'
WHERE code = 'pp_senador_puro_presupuestario';

UPDATE troop_definitions
SET name = 'Fontanera de comité con llave inglesa',
    description = 'Aprieta una tubería interna y de pronto media ejecutiva vota lo correcto sin recordar quién abrió el grifo.'
WHERE code = 'pisoe_fontanera_comite';

UPDATE troop_definitions
SET name = 'Ministro de rueda perpetua',
    description = 'No responde preguntas: las convierte en marco televisivo con sonrisa, focos y quince micrófonos indignados.'
WHERE code = 'pisoe_ministro_rueda_perpetua';

UPDATE troop_definitions
SET name = 'Doctor del argumentario reversible',
    description = 'Abre la carpeta por la izquierda o por la derecha y siempre encuentra una explicación solemne para lo contrario.'
WHERE code = 'pisoe_doctor_argumentario_reversible';

UPDATE troop_definitions
SET name = 'Baronesa del comité eterno',
    description = 'Pulsa el botón rojo y aparecen tres subcomités, dos portavoces y una sonrisa institucional que nadie pidió.'
WHERE code = 'pisoe_baronesa_comite_eterno';

UPDATE troop_definitions
SET name = 'Portavoz de plasma 4K',
    description = 'Sale de la pantalla con mando a distancia: niega, afirma y matiza antes de que termine la entradilla.'
WHERE code = 'pisoe_portavoz_plasma_4k';

UPDATE troop_definitions
SET name = 'Promotor de rotondas con fe',
    description = 'Si hay suelo, hay rotonda; si hay rotonda, hay foto; si hay foto, hay otra rotonda con fuente.'
WHERE code = 'gil_promotor_rotondas';

UPDATE troop_definitions
SET name = 'Concejala de festejos premium',
    description = 'Tijera gigante, confeti y escenario: inaugura cualquier cosa y encima parece que estaba en el plan general.'
WHERE code = 'gil_concejala_festejos_premium';

UPDATE troop_definitions
SET name = 'Capataz de grúa dorada',
    description = 'Con dos mandos y una sonrisa de obra nueva convierte cualquier solar en promesa urbanística con música.'
WHERE code = 'gil_capataz_grua_dorada';

UPDATE troop_definitions
SET name = 'Subastero de playa infinita',
    description = 'Vende sombra, arena y vistas al futuro. Si no hay costa, la dibuja con rotulador dorado y aplausos.'
WHERE code = 'gil_subastero_playa_infinita';

UPDATE troop_definitions
SET name = 'DJ de inauguraciones municipales',
    description = 'Pulsa el botón de confeti, corta la cinta y convierte una rotonda recién pintada en festival fundacional.'
WHERE code = 'gil_dj_inauguraciones';

UPDATE troop_definitions
SET name = 'Asamblearia del megáfono morado',
    description = 'Grita una consigna, levanta el puño de espuma y convierte una plaza pequeña en tormenta de directo.'
WHERE code = 'puff_asamblearia_megafono_morado';

UPDATE troop_definitions
SET name = 'Guardiana del círculo infinito',
    description = 'Silla plegable en ristre: nadie rompe una asamblea que todavía está votando si votar el orden del día.'
WHERE code = 'puff_guardiana_circulo_infinito';

UPDATE troop_definitions
SET name = 'Sindicalista del megasello',
    description = 'Estampa un sello del tamaño de una paellera y paraliza cualquier expediente que no haya pedido turno.'
WHERE code = 'puff_sindicalista_megasello';

UPDATE troop_definitions
SET name = 'Mediadora de sillas infinitas',
    description = 'Coloca sillas en círculo hasta que el enemigo olvida si venía a atacar o a consensuar el acta.'
WHERE code = 'puff_mediadora_sillas_infinitas';

UPDATE troop_definitions
SET name = 'Influencer de pancarta morada',
    description = 'Directo, aro de luz y megáfono glitter: convierte una queja local en tendencia con eco de plaza.'
WHERE code = 'puff_influencer_pancarta_morada';

UPDATE troop_definitions
SET name = 'Tamborilera de balcón',
    description = 'Redoble, xilófono y balcón: ataca por saturación épica hasta que el mapa pide tapones.'
WHERE code = 'vox_tamborilera_balcon';

UPDATE troop_definitions
SET name = 'Heraldo del atril verde',
    description = 'Campana, batuta y atril: convierte cada defensa mediática en una ceremonia imposible de interrumpir.'
WHERE code = 'vox_heraldo_atril_verde';

UPDATE troop_definitions
SET name = 'Notario de patria con sello XXL',
    description = 'Lleva un sello tan grande que cada golpe suena a trámite, corneta y sobremesa indignada.'
WHERE code = 'vox_notario_patria_sello_xxl';

UPDATE troop_definitions
SET name = 'Tambor mayor de balcones',
    description = 'Redoble, xilófono y bigote en alto: ataca por decibelios hasta que el mapa marca el paso.'
WHERE code = 'vox_tambor_mayor_balcones';

UPDATE troop_definitions
SET name = 'Capitana de corneta reglamentaria',
    description = 'Hace sonar la corneta y hasta los formularios se cuadran. Muy útil contra el caos y los flecos.'
WHERE code = 'vox_capitana_corneta_reglamentaria';

UPDATE troop_definitions
SET name = 'Negociadora de peaje',
    description = 'Sube la barrera, baja la barrera y cobra una condición nueva por cada metro de territorio.'
WHERE code = 'junts_negociadora_peaje';

UPDATE troop_definitions
SET name = 'Conseller de frontera simbólica',
    description = 'Traza una línea en el mapa y de repente todo el mundo necesita una comisión bilateral con catering.'
WHERE code = 'junts_conseller_frontera_simbolica';

UPDATE troop_definitions
SET name = 'Negociador de peaje portátil',
    description = 'Despliega una barrera en cualquier mesa y cobra condiciones antes incluso de empezar la reunión.'
WHERE code = 'junts_negociador_peaje_portatil';

UPDATE troop_definitions
SET name = 'Consellera de frontera con rotulador',
    description = 'Traza una línea nueva con rotulador XXL y todos fingen que estaba clarísima desde 1714.'
WHERE code = 'junts_consellera_frontera_rotulador';

UPDATE troop_definitions
SET name = 'Embajador de ventanilla propia',
    description = 'Viaja con su propia ventanilla: donde la posa, nace una competencia exclusiva y una tasa nueva.'
WHERE code = 'junts_embajador_ventanilla_propia';
