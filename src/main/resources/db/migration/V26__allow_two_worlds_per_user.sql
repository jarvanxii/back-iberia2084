-- Permite que una cuenta participe hasta en dos partidas distintas.
-- La lógica de máximo 2 queda en servicio; en base de datos solo evitamos duplicar usuario en la misma partida.

ALTER TABLE players
  ADD UNIQUE KEY uk_players_user_world (user_id, world_id);

ALTER TABLE players
  DROP INDEX user_id;
