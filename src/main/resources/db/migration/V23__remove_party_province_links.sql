ALTER TABLE territories
  DROP FOREIGN KEY fk_territories_faction;

ALTER TABLE territories
  DROP COLUMN flavor_faction_id;

ALTER TABLE factions
  DROP COLUMN starting_region;
