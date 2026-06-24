ALTER TABLE troop_definitions
  ADD COLUMN transport_type VARCHAR(20) NULL AFTER attack_type;

UPDATE troop_definitions
SET attack = 0,
    transport_type = 'terrestre'
WHERE code IN ('peugeot_campana', 'autobus_partido', 'furgon_pancartas');

UPDATE troop_definitions
SET attack = 0,
    transport_type = 'maritimo'
WHERE code IN ('yate_desgrabable', 'lancha_publicitaria');

UPDATE troop_definitions
SET attack = 0,
    transport_type = 'aereo'
WHERE code IN ('avioneta_contrabando', 'charter_agenda_oficial');
