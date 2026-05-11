ALTER TABLE players
  ADD COLUMN capital_city_name VARCHAR(90) NULL,
  ADD COLUMN onboarding_done BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE alliances
  ADD COLUMN faction_id BIGINT NULL,
  ADD CONSTRAINT fk_alliances_faction FOREIGN KEY (faction_id) REFERENCES factions(id);

UPDATE alliances a
JOIN (
  SELECT am.alliance_id, MIN(p.faction_id) faction_id
  FROM alliance_members am
  JOIN players p ON p.id = am.player_id
  GROUP BY am.alliance_id
) owner_faction ON owner_faction.alliance_id = a.id
SET a.faction_id = owner_faction.faction_id
WHERE a.faction_id IS NULL;

CREATE TABLE city_garrisons (
  player_id BIGINT NOT NULL,
  territory_id BIGINT NOT NULL,
  unit_code VARCHAR(60) NOT NULL,
  amount INT NOT NULL DEFAULT 0,
  PRIMARY KEY (player_id, territory_id, unit_code),
  CONSTRAINT fk_city_garrisons_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
  CONSTRAINT fk_city_garrisons_territory FOREIGN KEY (territory_id) REFERENCES territories(id) ON DELETE CASCADE,
  CONSTRAINT fk_city_garrisons_unit FOREIGN KEY (unit_code) REFERENCES troop_definitions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE election_deployments (
  player_id BIGINT NOT NULL,
  election_code VARCHAR(80) NOT NULL,
  territory_id BIGINT NOT NULL,
  unit_code VARCHAR(60) NOT NULL,
  amount INT NOT NULL DEFAULT 0,
  deployed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (player_id, election_code, territory_id, unit_code),
  CONSTRAINT fk_election_deployments_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
  CONSTRAINT fk_election_deployments_territory FOREIGN KEY (territory_id) REFERENCES territories(id) ON DELETE CASCADE,
  CONSTRAINT fk_election_deployments_unit FOREIGN KEY (unit_code) REFERENCES troop_definitions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
