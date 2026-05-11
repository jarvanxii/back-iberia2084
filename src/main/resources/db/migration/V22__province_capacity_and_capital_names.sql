INSERT INTO territories
  (world_id, code, name, region, map_x, map_y, flavor_faction_id, defense, population, base_votes, resource_focus, building_name, satire)
SELECT w.id,
       template.code,
       template.name,
       template.region,
       template.map_x,
       template.map_y,
       template.flavor_faction_id,
       template.defense,
       template.population,
       template.base_votes,
       template.resource_focus,
       template.building_name,
       template.satire
FROM worlds w
JOIN worlds template_world ON template_world.code = 'iberia-beta-1'
JOIN territories template ON template.world_id = template_world.id
WHERE NOT EXISTS (
  SELECT 1
  FROM territories existing
  WHERE existing.world_id = w.id
    AND existing.code = template.code
);

UPDATE worlds w
LEFT JOIN (
  SELECT world_id, COUNT(*) total
  FROM territories
  GROUP BY world_id
) totals ON totals.world_id = w.id
SET w.max_players = COALESCE(totals.total, 0);

UPDATE worlds w
LEFT JOIN (
  SELECT world_id, COUNT(*) current_players
  FROM players
  GROUP BY world_id
) player_counts ON player_counts.world_id = w.id
SET w.current_players = COALESCE(player_counts.current_players, 0);

UPDATE players p
JOIN (
  SELECT owner_player_id, MIN(id) territory_id
  FROM territories
  WHERE owner_player_id IS NOT NULL
  GROUP BY owner_player_id
) first_owned ON first_owned.owner_player_id = p.id
JOIN territories first_territory ON first_territory.id = first_owned.territory_id
LEFT JOIN territories current_capital
  ON current_capital.owner_player_id = p.id
 AND current_capital.name = p.capital_city_name
SET p.capital_city_name = COALESCE(current_capital.name, first_territory.name),
    p.onboarding_done = TRUE
WHERE current_capital.id IS NULL
   OR p.onboarding_done = FALSE;
