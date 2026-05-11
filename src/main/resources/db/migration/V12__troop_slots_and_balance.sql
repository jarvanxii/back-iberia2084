-- El campo capacity se expone en la API como slots: plazas ocupadas por unidad.
-- Las unidades caras ocupan mas plazas, pero tienen mejor poder por plaza.

UPDATE troop_definitions
SET attack = 8,
    defense = 34,
    influence_power = 4,
    speed = 8,
    capacity = 1,
    training_seconds = 18,
    cost_pesetas = 18,
    cost_votos = 12,
    cost_favores = 28
WHERE code = 'funcionario_raso';

UPDATE troop_definitions
SET attack = 30,
    defense = 16,
    influence_power = 18,
    speed = 18,
    capacity = 1,
    training_seconds = 24,
    cost_pesetas = 24,
    cost_votos = 40,
    cost_favores = 8
WHERE code = 'periodista';

UPDATE troop_definitions
SET attack = 22,
    defense = 72,
    influence_power = 10,
    speed = 7,
    capacity = 2,
    training_seconds = 32,
    cost_pesetas = 56,
    cost_votos = 28,
    cost_favores = 82
WHERE code = 'administrativo';

UPDATE troop_definitions
SET attack = 56,
    defense = 130,
    influence_power = 22,
    speed = 10,
    capacity = 3,
    training_seconds = 48,
    cost_pesetas = 130,
    cost_votos = 70,
    cost_favores = 150
WHERE code = 'inspector_hacienda';

UPDATE troop_definitions
SET attack = 118,
    defense = 48,
    influence_power = 58,
    speed = 15,
    capacity = 3,
    training_seconds = 52,
    cost_pesetas = 140,
    cost_votos = 165,
    cost_favores = 42
WHERE code = 'presentador_tv';

UPDATE troop_definitions
SET attack = 150,
    defense = 62,
    influence_power = 76,
    speed = 20,
    capacity = 4,
    training_seconds = 68,
    cost_pesetas = 230,
    cost_votos = 250,
    cost_favores = 88
WHERE code = 'asesor_sombra';

UPDATE troop_definitions
SET attack = 255,
    defense = 225,
    influence_power = 105,
    speed = 13,
    capacity = 6,
    training_seconds = 92,
    cost_pesetas = 480,
    cost_votos = 430,
    cost_favores = 310
WHERE code = 'concejal';

UPDATE troop_definitions
SET attack = 470,
    defense = 390,
    influence_power = 160,
    speed = 11,
    capacity = 9,
    training_seconds = 138,
    cost_pesetas = 900,
    cost_votos = 760,
    cost_favores = 560
WHERE code = 'alcalde';
