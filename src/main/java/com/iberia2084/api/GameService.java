package com.iberia2084.api;

import com.iberia2084.api.GameDtos.ActionDto;
import com.iberia2084.api.GameDtos.AllianceDto;
import com.iberia2084.api.GameDtos.AllianceMessageDto;
import com.iberia2084.api.GameDtos.AllianceScoreDto;
import com.iberia2084.api.GameDtos.AuthMessageResponse;
import com.iberia2084.api.GameDtos.AuthProviderDto;
import com.iberia2084.api.GameDtos.AuthResponse;
import com.iberia2084.api.GameDtos.BuildingDefinitionDto;
import com.iberia2084.api.GameDtos.CityDto;
import com.iberia2084.api.GameDtos.CityGarrisonDto;
import com.iberia2084.api.GameDtos.CityBuildingDto;
import com.iberia2084.api.GameDtos.CorruptionSchemeDto;
import com.iberia2084.api.GameDtos.DisasterPlanDto;
import com.iberia2084.api.GameDtos.EventDefinitionDto;
import com.iberia2084.api.GameDtos.FactionDto;
import com.iberia2084.api.GameDtos.GameStateDto;
import com.iberia2084.api.GameDtos.JoinWorldRequest;
import com.iberia2084.api.GameDtos.LoginRequest;
import com.iberia2084.api.GameDtos.MinistryDto;
import com.iberia2084.api.GameDtos.OnboardingRequest;
import com.iberia2084.api.GameDtos.PasswordRecoveryConfirmRequest;
import com.iberia2084.api.GameDtos.PasswordRecoveryStartRequest;
import com.iberia2084.api.GameDtos.PlayerDto;
import com.iberia2084.api.GameDtos.PlayerTroopDto;
import com.iberia2084.api.GameDtos.RegionalGovernmentDto;
import com.iberia2084.api.GameDtos.ResearchDto;
import com.iberia2084.api.GameDtos.ResearchDefinitionDto;
import com.iberia2084.api.GameDtos.ResourceCostDto;
import com.iberia2084.api.GameDtos.ResourceDto;
import com.iberia2084.api.GameDtos.SignupConfirmRequest;
import com.iberia2084.api.GameDtos.SignupRequest;
import com.iberia2084.api.GameDtos.TerritoryDto;
import com.iberia2084.api.GameDtos.TrainingQueueDto;
import com.iberia2084.api.GameDtos.TroopDefinitionDto;
import com.iberia2084.api.GameDtos.UserDto;
import com.iberia2084.api.GameDtos.WorldDto;
import com.iberia2084.api.GameDtos.WorldEventDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {
    private static final int MAX_ACTION_POINTS = 12;
    private static final int MAX_WORLDS_PER_USER = 2;
    private static final String BOT_PASSWORD_HASH = "{noop}bot-account-disabled";
    private static final List<String> BOT_NAMES = List.of(
            "Comisario de Ventanilla",
            "Mesa Técnica Permanente",
            "Delegación de Orden Local",
            "Gabinete de Crisis Tibia",
            "Oficina del Relato Provincial",
            "Comité de Obras Menores",
            "Junta de Sellos y Cafés",
            "Patronato de Promesas",
            "Unidad de Rotondas Discretas",
            "Subdirección de Pactos Raros",
            "Archivo de Excusas Preventivas",
            "Brigada de Notas de Prensa");

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final PasswordEncoder passwordEncoder;
    private final IberiaAuthMailService authMailService;
    private final SecureRandom random = new SecureRandom();
    @Value("${iberia2084.auth.signup.code-ttl-minutes:15}")
    private long signupCodeTtlMinutes;
    @Value("${iberia2084.auth.signup.max-attempts:5}")
    private int signupMaxAttempts;
    @Value("${iberia2084.auth.password-recovery.token-ttl-minutes:30}")
    private long recoveryTokenTtlMinutes;
    @Value("${iberia2084.auth.password-recovery.max-attempts:5}")
    private int recoveryMaxAttempts;
    @Value("${iberia2084.frontend-base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    public GameService(
            JdbcTemplate jdbc,
            NamedParameterJdbcTemplate namedJdbc,
            PasswordEncoder passwordEncoder,
            IberiaAuthMailService authMailService) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
        this.passwordEncoder = passwordEncoder;
        this.authMailService = authMailService;
    }

    @Transactional
    public AuthMessageResponse requestSignup(SignupRequest request) {
        var username = normalize(request.username());
        if (username.length() < 3) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El usuario necesita al menos 3 caracteres.");
        }

        var email = normalizeEmail(request.email());
        var displayName = request.displayName().trim();
        if (queryMap("SELECT id FROM users WHERE username = ? OR email = ? LIMIT 1", username, email).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Ese usuario o correo ya existe. Otro caudillo de sofá llegó antes.");
        }

        jdbc.update(
                """
                UPDATE auth_email_verifications
                SET consumed_at = CURRENT_TIMESTAMP
                WHERE consumed_at IS NULL AND (email = ? OR username = ?)
                """,
                email,
                username);

        var code = verificationCode();
        var expiresAt = Instant.now().plus(Duration.ofMinutes(Math.max(1, signupCodeTtlMinutes)));
        jdbc.update(
                """
                INSERT INTO auth_email_verifications
                  (id, username, display_name, email, password_hash, code_hash, expires_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                UUID.randomUUID().toString(),
                username,
                displayName,
                email,
                passwordEncoder.encode(request.password()),
                passwordEncoder.encode(code),
                Timestamp.from(expiresAt));
        authMailService.sendSignupCode(email, displayName, code);

        return new AuthMessageResponse(true, "Código enviado por email.", email, expiresAt);
    }

    @Transactional
    public AuthResponse confirmSignup(SignupConfirmRequest request) {
        var email = normalizeEmail(request.email());
        var code = request.code().trim();
        var row = queryMap(
                        """
                        SELECT *
                        FROM auth_email_verifications
                        WHERE email = ? AND consumed_at IS NULL AND expires_at > CURRENT_TIMESTAMP
                        ORDER BY created_at DESC
                        LIMIT 1
                        """,
                        email)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "El código ha caducado o no existe."));
        var verificationId = string(row, "id");
        var attempts = intValue(row, "attempts");
        if (attempts >= Math.max(1, signupMaxAttempts)) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "Código bloqueado por demasiados intentos.");
        }

        jdbc.update("UPDATE auth_email_verifications SET attempts = attempts + 1 WHERE id = ?", verificationId);
        if (!passwordEncoder.matches(code, string(row, "code_hash"))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Código incorrecto.");
        }

        long userId;
        try {
            var keyHolder = new GeneratedKeyHolder();
            namedJdbc.update(
                    """
                    INSERT INTO users (username, display_name, email, email_verified, password_hash)
                    VALUES (:username, :displayName, :email, TRUE, :passwordHash)
                    """,
                    new MapSqlParameterSource()
                            .addValue("username", string(row, "username"))
                            .addValue("displayName", string(row, "display_name"))
                            .addValue("email", email)
                            .addValue("passwordHash", string(row, "password_hash")),
                    keyHolder,
                    new String[] {"id"});
            userId = keyHolder.getKey().longValue();
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Ese usuario o correo ya existe. Otro caudillo de sofá llegó antes.");
        }

        jdbc.update("UPDATE auth_email_verifications SET consumed_at = CURRENT_TIMESTAMP WHERE id = ?", verificationId);
        var token = createToken(userId);
        return new AuthResponse(token, user(userId), null);
    }

    @Transactional
    public AuthMessageResponse requestPasswordRecovery(PasswordRecoveryStartRequest request) {
        var email = normalizeEmail(request.email());
        var expiresAt = Instant.now().plus(Duration.ofMinutes(Math.max(1, recoveryTokenTtlMinutes)));
        var user = queryMap("SELECT id, display_name, email_verified FROM users WHERE email = ? LIMIT 1", email);
        if (user.isPresent() && booleanValue(user.get(), "email_verified")) {
            jdbc.update(
                    "UPDATE auth_password_resets SET consumed_at = CURRENT_TIMESTAMP WHERE email = ? AND consumed_at IS NULL",
                    email);
            var token = recoveryToken();
            var resetId = UUID.randomUUID().toString();
            jdbc.update(
                    """
                    INSERT INTO auth_password_resets (id, user_id, email, token_hash, expires_at)
                    VALUES (?, ?, ?, ?, ?)
                    """,
                    resetId,
                    longValue(user.get(), "id"),
                    email,
                    passwordEncoder.encode(token),
                    Timestamp.from(expiresAt));
            authMailService.sendPasswordResetLink(
                    email,
                    string(user.get(), "display_name"),
                    passwordResetUrl(resetId, email, token));
        }
        return new AuthMessageResponse(true, "Si existe una cuenta con ese correo, te enviaremos un enlace de recuperación.", email, expiresAt);
    }

    @Transactional
    public AuthResponse confirmPasswordRecovery(PasswordRecoveryConfirmRequest request) {
        var email = normalizeEmail(request.email());
        var row = queryMap(
                        "SELECT * FROM auth_password_resets WHERE id = ? AND email = ? LIMIT 1",
                        request.resetId().trim(),
                        email)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Recuperación no encontrada."));
        if (row.get("consumed_at") != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El enlace de recuperación ya se ha utilizado.");
        }
        if (instantObject(row.get("expires_at")).isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El enlace de recuperación ha caducado.");
        }
        var attempts = intValue(row, "attempts");
        if (attempts >= Math.max(1, recoveryMaxAttempts)) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "Recuperación bloqueada por demasiados intentos.");
        }

        jdbc.update("UPDATE auth_password_resets SET attempts = attempts + 1 WHERE id = ?", string(row, "id"));
        if (!passwordEncoder.matches(request.token().trim(), string(row, "token_hash"))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Token de recuperación incorrecto.");
        }

        var userId = longValue(row, "user_id");
        jdbc.update(
                "UPDATE users SET password_hash = ?, email_verified = TRUE WHERE id = ?",
                passwordEncoder.encode(request.password()),
                userId);
        jdbc.update("UPDATE auth_password_resets SET consumed_at = CURRENT_TIMESTAMP WHERE id = ?", string(row, "id"));
        jdbc.update("DELETE FROM auth_tokens WHERE user_id = ?", userId);
        var token = createToken(userId);
        var player = playerIdForUser(userId).map(this::player).orElse(null);
        return new AuthResponse(token, user(userId), player);
    }

    public List<AuthProviderDto> authProviders() {
        return List.of(
                new AuthProviderDto("google", "Google", "Acceso OAuth con Google", false),
                new AuthProviderDto("microsoft", "Microsoft", "Acceso OAuth con Microsoft", false),
                new AuthProviderDto("github", "GitHub", "Acceso OAuth con GitHub", false),
                new AuthProviderDto("apple", "Apple", "Acceso OAuth con Apple", false));
    }

    @Transactional
    private AuthResponse signup(SignupRequest request) {
        var username = normalize(request.username());
        if (username.length() < 3) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El usuario necesita al menos 3 caracteres.");
        }

        var email = request.email().trim().toLowerCase(Locale.ROOT);

        long userId;
        try {
            var keyHolder = new GeneratedKeyHolder();
            namedJdbc.update(
                    """
                    INSERT INTO users (username, display_name, email, password_hash)
                    VALUES (:username, :displayName, :email, :passwordHash)
                    """,
                    new MapSqlParameterSource()
                            .addValue("username", username)
                            .addValue("displayName", request.displayName().trim())
                            .addValue("email", email)
                            .addValue("passwordHash", passwordEncoder.encode(request.password())),
                    keyHolder,
                    new String[] {"id"});
            userId = keyHolder.getKey().longValue();
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Ese usuario o correo ya existe. Otro caudillo de sofá llegó antes.");
        }

        var token = createToken(userId);
        return new AuthResponse(token, user(userId), null);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var login = request.username().trim();
        var username = normalize(login);
        var email = login.toLowerCase(Locale.ROOT);
        var row = queryMap("SELECT id, password_hash, email_verified FROM users WHERE username = ? OR email = ?", username, email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos."));
        if (!booleanValue(row, "email_verified")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Confirma tu correo antes de iniciar sesión.");
        }
        if (!passwordEncoder.matches(request.password(), string(row, "password_hash"))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos.");
        }
        var userId = longValue(row, "id");
        var token = createToken(userId);
        var player = playerIdForUser(userId).map(this::player).orElse(null);
        return new AuthResponse(token, user(userId), player);
    }

    public long requireUser(String token) {
        if (token == null || token.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Falta token de sesión.");
        }
        jdbc.update("DELETE FROM auth_tokens WHERE expires_at < CURRENT_TIMESTAMP");
        var userId = queryObject(
                        "SELECT user_id FROM auth_tokens WHERE token = ? AND expires_at > CURRENT_TIMESTAMP",
                        Long.class,
                        token)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Sesión caducada."));
        return userId;
    }

    public long requirePlayer(String token) {
        return requirePlayer(token, null);
    }

    public long requirePlayer(String token, String worldCode) {
        var userId = requireUser(token);
        return playerIdForUser(userId, worldCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Elige una partida abierta antes de entrar al mapa."));
    }

    @Transactional
    public GameStateDto state(String token, String worldCode) {
        var userId = requireUser(token);
        var playerIdOptional = playerIdForUser(userId, worldCode);
        if (playerIdOptional.isEmpty()) {
            return buildLobbyState();
        }
        var playerId = playerIdOptional.get();
        ensureCityAndTroops(playerId);
        collectResources(playerId);
        completeResearch(playerId);
        completeBuildingUpgrades(playerId);
        completeTroopTraining(playerId);
        resolveDueActions(playerId);
        var player = player(playerId);
        updateWorldClosure(player.worldId());
        if (isWorldOpen(player.worldId())) {
            spawnWorldEvent(player.worldId());
        }
        return buildState(playerId);
    }

    @Transactional
    public GameStateDto joinWorld(String token, JoinWorldRequest request) {
        var userId = requireUser(token);
        var existingPlayer = playerIdForUser(userId, request.worldCode());
        if (existingPlayer.isPresent()) {
            return buildState(existingPlayer.get());
        }
        if (playerCountForUser(userId) >= MAX_WORLDS_PER_USER) {
            throw new ApiException(HttpStatus.CONFLICT, "Cada cuenta puede estar en un máximo de 2 partidas.");
        }

        var factionCode = normalizeCode(request.factionCode());
        var factionId = idByCode("factions", "code", factionCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Partido no encontrado."));
        var worldId = worldIdForSignup(request.worldCode());
        var leaderName = request.leaderName().trim();

        long playerId;
        try {
            playerId = insertPlayer(userId, worldId, factionId, leaderName);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Ese usuario ya está dentro de esa partida.");
        }

        initializeResources(playerId, factionCode);
        initializeCityAndTroops(playerId);
        ensureCapitalProvince(playerId, request.provinceCode());
        syncWorldPlayerCount(worldId);
        return buildState(playerId);
    }

    @Transactional
    public GameStateDto collect(String token, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        ensureCityAndTroops(playerId);
        collectResources(playerId);
        completeBuildingUpgrades(playerId);
        completeTroopTraining(playerId);
        return buildState(playerId);
    }

    @Transactional
    public GameStateDto completeOnboarding(String token, OnboardingRequest request, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        ensureCityAndTroops(playerId);
        var provinceName = ensureCapitalProvince(playerId);

        var joinCode = request.joinAllianceCode() == null ? "" : request.joinAllianceCode().trim();
        if (!joinCode.isBlank()) {
            joinAllianceByCode(token, joinCode, worldCode);
        } else if (request.allianceName() != null && !request.allianceName().isBlank()) {
            var code = request.allianceCode() == null || request.allianceCode().isBlank()
                    ? provinceName.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT)
                    : request.allianceCode();
            if (code.isBlank()) {
                code = "ALIANZA" + playerId;
            }
            var description = request.allianceDescription() == null || request.allianceDescription().isBlank()
                    ? "Comité estratégico con café recalentado y ambición territorial."
                    : request.allianceDescription();
            createAlliance(token, request.allianceName(), code, description, worldCode);
        }
        return buildState(playerId);
    }

    @Transactional
    public ActionDto startConquest(String token, long territoryId, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        collectResources(playerId);
        ensureTerritoryExists(territoryId);
        if (ownsTerritory(playerId, territoryId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ya controlas esa zona. Invadirte a ti mismo queda raro hasta para 2084.");
        }
        spendActionPoint(playerId);
        charge(playerId, List.of(cost("pesetas", 180), cost("votos", 90)));

        var success = clamp(38 + politicalCredit(playerId) / 4 + researchBonus(playerId, "conquest_bonus") - defense(territoryId) / 3, 18, 88);
        var actionId = insertAction(playerId, "CONQUEST", territoryId, null, 0, success, 35);
        return action(actionId);
    }

    @Transactional
    public ActionDto startInfluence(String token, long territoryId, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        collectResources(playerId);
        ensureTerritoryExists(territoryId);
        spendActionPoint(playerId);
        charge(playerId, List.of(cost("pesetas", 90), cost("votos", 80)));

        var success = clamp(52 + reputation(playerId) / 5 + researchBonus(playerId, "influence_production") - defense(territoryId) / 5, 25, 93);
        var actionId = insertAction(playerId, "INFLUENCE", territoryId, null, 0, success, 24);
        return action(actionId);
    }

    @Transactional
    public ActionDto startCorruption(String token, String schemeCode, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        collectResources(playerId);
        var scheme = corruptionScheme(schemeCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Trama no encontrada."));
        spendActionPoint(playerId);
        charge(playerId, scheme.costs());

        var affinity = factionCorruptionAffinity(playerId);
        var risk = clamp(
                scheme.baseRiskPercent() - researchBonus(playerId, "corruption_risk_reduction") - affinity / 3 + mediaHeat(playerId) / 4,
                5,
                82);
        var actionId = insertAction(playerId, "CORRUPTION", null, scheme.code(), risk, 100, scheme.durationSeconds());
        return action(actionId);
    }

    @Transactional
    public ActionDto startDisasterPlan(String token, long eventId, String planCode, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        collectResources(playerId);
        var event = queryMap("SELECT * FROM world_events WHERE id = ? AND status = 'active'", eventId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "La crisis no está activa."));
        var playerWorld = worldId(playerId);
        if (longValue(event, "world_id") != playerWorld) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Esa crisis pertenece a otro mundo.");
        }
        var plan = disasterPlan(planCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Plan de gestión no encontrado."));
        spendActionPoint(playerId);
        charge(playerId, plan.costs());

        var success = clamp(plan.baseSuccessPercent() + reputation(playerId) / 8 - intValue(event, "severity") * 5, 18, 92);
        var actionId = insertAction(playerId, "DISASTER", longValue(event, "territory_id"), plan.code() + ":" + eventId, 0, success, plan.durationSeconds());
        return action(actionId);
    }

    @Transactional
    public ResearchDto startResearch(String token, String researchCode, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        collectResources(playerId);
        var row = queryMap("SELECT * FROM research_definitions WHERE code = ?", researchCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Investigación no encontrada."));
        var requiredFaction = string(row, "faction_code");
        if (!requiredFaction.isBlank() && !requiredFaction.equals(playerFactionCode(playerId))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Esa investigación es especial de otro partido.");
        }
        charge(playerId, List.of(
                cost("pesetas", intValue(row, "cost_pesetas")),
                cost("votos", intValue(row, "cost_votos")),
                cost("favores", intValue(row, "cost_favores"))));
        try {
            jdbc.update(
                    """
                    INSERT INTO player_research (player_id, research_id, finishes_at)
                    VALUES (?, ?, ?)
                    """,
                    playerId,
                    longValue(row, "id"),
                    Timestamp.from(Instant.now().plusSeconds(intValue(row, "duration_seconds"))));
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Esa investigación ya está en marcha o terminada.");
        }
        return researchRow(playerId, row);
    }

    @Transactional
    public GameStateDto startTroopTraining(String token, String unitCode, int requestedAmount, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        ensureCityAndTroops(playerId);
        collectResources(playerId);
        completeBuildingUpgrades(playerId);
        completeTroopTraining(playerId);

        var amount = Math.max(1, Math.min(250, requestedAmount));
        var unit = troopDefinitionRow(unitCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unidad no disponible."));
        var buildingLevel = buildingLevel(playerId, string(unit, "unlock_building_code"));
        if (buildingLevel < intValue(unit, "unlock_building_level")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Necesitas mejorar " + unlockBuildingName(unit) + " para entrenar esa unidad.");
        }
        var unitFactionCode = string(unit, "faction_code");
        if (unitFactionCode != null && !unitFactionCode.equals(playerFactionCode(playerId))) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Esa unidad especial pertenece a otro partido.");
        }

        charge(playerId, multiplyCosts(resourceCosts(unit), amount));
        var seconds = troopTrainingSeconds(playerId, unit, amount);
        namedJdbc.update(
                """
                INSERT INTO troop_training (player_id, unit_code, amount, finishes_at)
                VALUES (:playerId, :unitCode, :amount, :finishesAt)
                """,
                new MapSqlParameterSource()
                        .addValue("playerId", playerId)
                        .addValue("unitCode", string(unit, "code"))
                        .addValue("amount", amount)
                        .addValue("finishesAt", Timestamp.from(Instant.now().plusSeconds(seconds))));
        return buildState(playerId);
    }

    @Transactional
    public GameStateDto deployTroopsToCity(String token, long territoryId, String unitCode, int requestedAmount, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        ensureCityAndTroops(playerId);
        completeTroopTraining(playerId);
        ensureOwnedTerritory(playerId, territoryId);
        ensureUnitExists(unitCode);
        var amount = Math.max(1, Math.min(250, requestedAmount));
        var reserve = troopReserve(playerId, unitCode);
        if (reserve < amount) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No tienes tantas unidades libres. El argumentario no se clona solo.");
        }
        jdbc.update(
                "UPDATE player_troops SET amount = amount - ? WHERE player_id = ? AND unit_code = ?",
                amount,
                playerId,
                unitCode);
        jdbc.update(
                """
                INSERT INTO city_garrisons (player_id, territory_id, unit_code, amount)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE amount = amount + VALUES(amount)
                """,
                playerId,
                territoryId,
                unitCode,
                amount);
        jdbc.update("UPDATE territories SET defense = defense + ? WHERE id = ?", Math.max(1, amount / 4), territoryId);
        return buildState(playerId);
    }

    @Transactional
    public GameStateDto startBuildingUpgrade(String token, String buildingCode, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        ensureCityAndTroops(playerId);
        collectResources(playerId);
        completeBuildingUpgrades(playerId);
        completeTroopTraining(playerId);

        var building = queryMap(
                        """
                        SELECT pcb.*, cbd.*
                        FROM player_city_buildings pcb
                        JOIN city_building_definitions cbd ON cbd.code = pcb.building_code
                        WHERE pcb.player_id = ? AND pcb.building_code = ?
                        """,
                        playerId,
                        buildingCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Edificio no encontrado."));
        if (booleanValue(building, "upgrading")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ese edificio ya está en obras. La cinta inaugural tendrá que esperar.");
        }
        var level = intValue(building, "level");
        if (level >= intValue(building, "max_level")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Edificio al máximo.");
        }
        if (activeBuildingUpgrades(playerId) >= constructionQueueLimit(playerId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cola de construcción completa.");
        }

        var nextLevel = level + 1;
        charge(playerId, scaledBuildingCosts(building, nextLevel));
        var seconds = buildingUpgradeSeconds(playerId, building, nextLevel);
        jdbc.update(
                """
                UPDATE player_city_buildings
                SET upgrading = TRUE, upgrade_started_at = CURRENT_TIMESTAMP, upgrade_finishes_at = ?
                WHERE player_id = ? AND building_code = ?
                """,
                Timestamp.from(Instant.now().plusSeconds(seconds)),
                playerId,
                buildingCode);
        return buildState(playerId);
    }

    @Transactional
    public GameStateDto exchangeResources(String token, String fromCode, String toCode, int requestedAmount, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        collectResources(playerId);
        completeBuildingUpgrades(playerId);
        completeTroopTraining(playerId);

        var from = normalizeCode(fromCode);
        var to = normalizeCode(toCode);
        if (from.equals(to)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Intercambiar lo mismo por lo mismo ya lo hace demasiada gente.");
        }
        ensureResourceExists(from);
        ensureResourceExists(to);
        var amount = Math.max(1, Math.min(50000, requestedAmount));
        charge(playerId, List.of(cost(from, amount)));
        var received = Math.max(1, (int) Math.round(amount * exchangeRate(from, to)));
        addResource(playerId, to, received);
        jdbc.update(
                "UPDATE players SET media_heat = LEAST(100, media_heat + 1), political_credit = political_credit + 1 WHERE id = ?",
                playerId);
        return buildState(playerId);
    }

    @Transactional
    public AllianceDto createAlliance(String token, String name, String code, String description, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        var normalizedCode = normalizeCode(code);
        var worldId = worldId(playerId);
        var factionId = factionId(playerId);
        long allianceId;
        try {
            var keyHolder = new GeneratedKeyHolder();
            namedJdbc.update(
                    """
                    INSERT INTO alliances (world_id, faction_id, name, code, description)
                    VALUES (:worldId, :factionId, :name, :code, :description)
                    """,
                    new MapSqlParameterSource()
                            .addValue("worldId", worldId)
                            .addValue("factionId", factionId)
                            .addValue("name", name.trim())
                            .addValue("code", normalizedCode)
                            .addValue("description", description.trim()),
                    keyHolder,
                    new String[] {"id"});
            allianceId = keyHolder.getKey().longValue();
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Ese código de coalición ya está ocupado.");
        }
        joinAlliance(playerId, allianceId, "fundador");
        return alliance(allianceId);
    }

    @Transactional
    public AllianceDto joinAllianceByCode(String token, String code, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        var allianceId = queryObject(
                        "SELECT id FROM alliances WHERE code = ? AND world_id = ?",
                        Long.class,
                        normalizeCode(code),
                        worldId(playerId))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Coalición no encontrada en este mundo."));
        ensureAllianceFaction(playerId, allianceId);
        joinAlliance(playerId, allianceId, "miembro");
        return alliance(allianceId);
    }

    @Transactional
    public AllianceMessageDto sendAllianceMessage(String token, String body, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        var allianceId = allianceId(playerId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Necesitas coalición para escribir ahí."));
        var keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(
                """
                INSERT INTO alliance_messages (alliance_id, player_id, body)
                VALUES (:allianceId, :playerId, :body)
                """,
                new MapSqlParameterSource()
                        .addValue("allianceId", allianceId)
                        .addValue("playerId", playerId)
                        .addValue("body", body.trim()),
                keyHolder,
                new String[] {"id"});
        return allianceMessage(keyHolder.getKey().longValue());
    }

    public List<AllianceDto> alliances(String token, String worldCode) {
        var playerId = requirePlayer(token, worldCode);
        return jdbc.query(
                """
                SELECT a.id, a.name, a.code, a.description,
                       f.code faction_code, f.name faction_name, f.color faction_color
                FROM alliances a
                JOIN factions f ON f.id = a.faction_id
                WHERE a.world_id = ?
                ORDER BY a.created_at DESC
                """,
                allianceMapper(),
                worldId(playerId));
    }

    public List<FactionDto> factions() {
        return jdbc.query(
                """
                SELECT *
                FROM factions
                ORDER BY CASE code
                    WHEN 'pp' THEN 1
                    WHEN 'pisoe' THEN 2
                    WHEN 'vox' THEN 3
                    WHEN 'puff' THEN 4
                    WHEN 'gil' THEN 5
                    WHEN 'junts' THEN 6
                    ELSE 99
                END, id
                """,
                (rs, rowNum) -> new FactionDto(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("short_name"),
                        rs.getString("color"),
                        rs.getString("motto"),
                        rs.getString("satire")));
    }

    public List<WorldDto> worlds() {
        ensureBotsForOpenWorlds();
        return jdbc.query(
                """
                SELECT w.*,
                       wa.name winning_alliance_name,
                       COALESCE(player_counts.current_players, 0) actual_current_players,
                       COALESCE(total_territories.total, 0) total_territories,
                       COALESCE(controlled_territories.controlled, 0) controlled_territories,
                       COALESCE(available_territories.available, 0) available_territories
                FROM worlds w
                LEFT JOIN alliances wa ON wa.id = w.winning_alliance_id
                LEFT JOIN (
                    SELECT world_id, COUNT(*) current_players
                    FROM players
                    WHERE is_bot = FALSE
                    GROUP BY world_id
                ) player_counts ON player_counts.world_id = w.id
                LEFT JOIN (
                    SELECT world_id, COUNT(*) total
                    FROM territories
                    GROUP BY world_id
                ) total_territories ON total_territories.world_id = w.id
                LEFT JOIN (
                    SELECT world_id, COUNT(*) controlled
                    FROM territories
                    WHERE owner_player_id IS NOT NULL
                    GROUP BY world_id
                ) controlled_territories ON controlled_territories.world_id = w.id
                LEFT JOIN (
                    SELECT world_id, COUNT(*) available
                    FROM territories
                    WHERE owner_player_id IS NULL
                    GROUP BY world_id
                ) available_territories ON available_territories.world_id = w.id
                ORDER BY
                    CASE w.status
                        WHEN 'OPEN' THEN 0
                        WHEN 'UPCOMING' THEN 1
                        ELSE 2
                    END,
                    w.opens_at IS NULL,
                    w.opens_at,
                    w.id
                """,
                (rs, rowNum) -> {
                    var totalTerritories = rs.getInt("total_territories");
                    var currentPlayers = rs.getInt("actual_current_players");
                    return new WorldDto(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"),
                            totalTerritories,
                            currentPlayers,
                            rs.getInt("tick_seconds"),
                            rs.getString("status"),
                            rs.getString("difficulty_code"),
                            rs.getString("difficulty_name"),
                            rs.getInt("difficulty_level"),
                            instantOrNull(rs, "opens_at"),
                            instantOrNull(rs, "closed_at"),
                            rs.getString("winning_alliance_name"),
                            rs.getInt("controlled_territories"),
                            totalTerritories,
                            "OPEN".equals(rs.getString("status")) && rs.getInt("available_territories") > 0);
                });
    }

    private long worldIdForSignup(String worldCode) {
        var code = normalizeCode(worldCode);
        return queryObject(
                        """
                        SELECT id
                        FROM worlds
                        WHERE code = ?
                          AND status = 'OPEN'
                          AND EXISTS (
                              SELECT 1
                              FROM territories
                              WHERE territories.world_id = worlds.id
                                AND territories.owner_player_id IS NULL
                          )
                        """,
                        Long.class,
                        code)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Ese mundo no está abierto o ya está lleno. Elige otro tablero antes de repartir sobres."));
    }

    private void syncWorldPlayerCount(long worldId) {
        jdbc.update(
                """
                UPDATE worlds
                SET current_players = (
                SELECT COUNT(*)
                FROM players
                    WHERE players.world_id = ?
                      AND players.is_bot = FALSE
                )
                WHERE id = ?
                """,
                worldId,
                worldId);
    }

    private void ensureBotsForOpenWorlds() {
        var openWorlds = jdbc.queryForList(
                """
                SELECT id, code, difficulty_level
                FROM worlds
                WHERE status = 'OPEN'
                ORDER BY id
                """);
        for (var world : openWorlds) {
            ensureWorldBots(
                    longValue(world, "id"),
                    string(world, "code"),
                    intValue(world, "difficulty_level"));
        }
    }

    private void ensureWorldBots(long worldId, String worldCode, int difficultyLevel) {
        var botLevel = clamp(difficultyLevel, 1, 5);
        jdbc.update(
                "UPDATE players SET bot_level = ? WHERE world_id = ? AND is_bot = TRUE AND bot_level <> ?",
                botLevel,
                worldId,
                botLevel);

        var totalTerritories = queryObject("SELECT COUNT(*) FROM territories WHERE world_id = ?", Integer.class, worldId)
                .orElse(0);
        var targetBots = Math.min(Math.max(0, totalTerritories - 1), Math.max(0, botLevel * 2));
        var existingBots = queryObject(
                        "SELECT COUNT(*) FROM players WHERE world_id = ? AND is_bot = TRUE",
                        Integer.class,
                        worldId)
                .orElse(0);
        if (existingBots >= targetBots) {
            return;
        }

        var factions = jdbc.queryForList(
                """
                SELECT id, code
                FROM factions
                ORDER BY CASE code
                    WHEN 'pp' THEN 1
                    WHEN 'pisoe' THEN 2
                    WHEN 'vox' THEN 3
                    WHEN 'puff' THEN 4
                    WHEN 'gil' THEN 5
                    WHEN 'junts' THEN 6
                    ELSE 99
                END, id
                """);
        if (factions.isEmpty()) {
            return;
        }

        for (var botIndex = existingBots + 1; botIndex <= targetBots; botIndex++) {
            var freeTerritories = queryObject(
                            "SELECT COUNT(*) FROM territories WHERE world_id = ? AND owner_player_id IS NULL",
                            Integer.class,
                            worldId)
                    .orElse(0);
            if (freeTerritories <= 0) {
                return;
            }

            var faction = factions.get((botIndex - 1) % factions.size());
            var factionId = longValue(faction, "id");
            var factionCode = string(faction, "code");
            var leaderName = BOT_NAMES.get((botIndex - 1) % BOT_NAMES.size());
            var botUserId = ensureBotUser(worldCode, botIndex, leaderName);
            var botPlayerId = insertBotPlayer(botUserId, worldId, factionId, leaderName, botLevel);

            initializeResources(botPlayerId, factionCode);
            initializeCityAndTroops(botPlayerId);
            ensureCapitalProvince(botPlayerId);
            scaleBotByDifficulty(botPlayerId, factionCode, botLevel);
        }
    }

    private long ensureBotUser(String worldCode, int botIndex, String displayName) {
        var baseUsername = normalizeCode("bot-" + worldCode + "-" + botIndex);
        for (var attempt = 0; attempt < 10; attempt++) {
            var username = attempt == 0 ? baseUsername : baseUsername + "-" + attempt;
            jdbc.update(
                    """
                    INSERT IGNORE INTO users (username, display_name, email, password_hash, is_system)
                    VALUES (?, ?, NULL, ?, TRUE)
                    """,
                    username,
                    displayName,
                    BOT_PASSWORD_HASH);
            var row = queryMap("SELECT id, is_system FROM users WHERE username = ?", username);
            if (row.isPresent() && booleanValue(row.get(), "is_system")) {
                return longValue(row.get(), "id");
            }
        }
        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo preparar el bot de partida.");
    }

    private void scaleBotByDifficulty(long playerId, String factionCode, int botLevel) {
        var level = clamp(botLevel, 1, 5);
        addBotResourceBonus(playerId, "pesetas", level * 420, level * 8);
        addBotResourceBonus(playerId, "votos", level * 180, level * 5);
        addBotResourceBonus(playerId, "favores", level * 35, level);

        jdbc.update(
                """
                UPDATE player_city_buildings pcb
                JOIN city_building_definitions cbd ON cbd.code = pcb.building_code
                SET pcb.level = LEAST(cbd.max_level, GREATEST(pcb.level, ?))
                WHERE pcb.player_id = ?
                """,
                level,
                playerId);

        jdbc.update(
                """
                UPDATE player_troops pt
                JOIN troop_definitions td ON td.code = pt.unit_code
                SET pt.amount = GREATEST(pt.amount, GREATEST(1, (? - td.tier + 3) * 2))
                WHERE pt.player_id = ?
                  AND td.tier <= LEAST(5, ? + 1)
                  AND (td.faction_code IS NULL OR td.faction_code = ?)
                """,
                level,
                playerId,
                level,
                factionCode);

        jdbc.update("UPDATE territories SET defense = defense + ? WHERE owner_player_id = ?", level * 7, playerId);
        jdbc.update("UPDATE players SET onboarding_done = TRUE WHERE id = ?", playerId);
    }

    private void addBotResourceBonus(long playerId, String code, int amount, int production) {
        jdbc.update(
                """
                UPDATE player_resources
                SET amount = amount + ?,
                    production_per_minute = production_per_minute + ?
                WHERE player_id = ? AND resource_code = ?
                """,
                amount,
                production,
                playerId,
                code);
    }

    private boolean isWorldOpen(long worldId) {
        return queryObject("SELECT status FROM worlds WHERE id = ?", String.class, worldId)
                .map("OPEN"::equals)
                .orElse(false);
    }

    private void updateWorldClosure(long worldId) {
        if (!isWorldOpen(worldId)) {
            return;
        }

        var totalTerritories = queryObject("SELECT COUNT(*) FROM territories WHERE world_id = ?", Integer.class, worldId)
                .orElse(0);
        if (totalTerritories == 0) {
            return;
        }

        var winner = queryMap(
                """
                SELECT a.id, a.name, COUNT(*) controlled
                FROM territories t
                JOIN players p ON p.id = t.owner_player_id
                JOIN alliances a ON a.id = p.alliance_id
                WHERE t.world_id = ?
                GROUP BY a.id, a.name
                HAVING COUNT(*) = ?
                LIMIT 1
                """,
                worldId,
                totalTerritories);
        if (winner.isEmpty()) {
            return;
        }

        jdbc.update(
                """
                UPDATE worlds
                SET status = 'CLOSED',
                    closed_at = CURRENT_TIMESTAMP,
                    winning_alliance_id = ?,
                    closure_reason = ?
                WHERE id = ? AND status = 'OPEN'
                """,
                longValue(winner.get(), "id"),
                "La coalición " + string(winner.get(), "name") + " controla todas las provincias de España.",
                worldId);
    }

    private GameStateDto buildState(long playerId) {
        ensureCityAndTroops(playerId);
        ensureCapitalProvince(playerId);
        completeResearch(playerId);
        completeBuildingUpgrades(playerId);
        completeTroopTraining(playerId);
        var player = player(playerId);
        updateWorldClosure(player.worldId());
        return new GameStateDto(
                player,
                playersForUser(userIdForPlayer(playerId)),
                worlds(),
                factions(),
                resources(playerId),
                territories(player.worldId()),
                actions(playerId),
                corruptionSchemes(),
                disasterPlans(),
                eventDefinitions(),
                worldEvents(player.worldId()),
                researchDefinitions(),
                research(playerId),
                cities(playerId),
                buildingDefinitions(),
                cityBuildings(playerId),
                troopDefinitions(),
                troops(playerId),
                garrisons(playerId),
                trainingQueue(playerId),
                ministries(playerId),
                regionalGovernments(),
                allianceScores(playerId),
                allianceMessages(playerId));
    }

    private GameStateDto buildLobbyState() {
        return new GameStateDto(
                null,
                List.of(),
                worlds(),
                factions(),
                List.of(),
                List.of(),
                List.of(),
                corruptionSchemes(),
                disasterPlans(),
                eventDefinitions(),
                List.of(),
                researchDefinitions(),
                List.of(),
                List.of(),
                buildingDefinitions(),
                List.of(),
                troopDefinitions(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                regionalGovernments(),
                List.of(),
                List.of());
    }

    private long insertPlayer(long userId, long worldId, long factionId, String leaderName) {
        var keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(
                """
                INSERT INTO players (user_id, world_id, faction_id, leader_name)
                VALUES (:userId, :worldId, :factionId, :leaderName)
                """,
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("worldId", worldId)
                        .addValue("factionId", factionId)
                        .addValue("leaderName", leaderName),
                keyHolder,
                new String[] {"id"});
        return keyHolder.getKey().longValue();
    }

    private long insertBotPlayer(long userId, long worldId, long factionId, String leaderName, int botLevel) {
        var keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(
                """
                INSERT INTO players (user_id, world_id, faction_id, leader_name, is_bot, bot_level)
                VALUES (:userId, :worldId, :factionId, :leaderName, TRUE, :botLevel)
                """,
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("worldId", worldId)
                        .addValue("factionId", factionId)
                        .addValue("leaderName", leaderName)
                        .addValue("botLevel", botLevel),
                keyHolder,
                new String[] {"id"});
        return keyHolder.getKey().longValue();
    }

    private void initializeResources(long playerId, String factionCode) {
        var production = Map.of(
                "pesetas", 30,
                "votos", 18,
                "favores", 3);
        var amounts = Map.ofEntries(
                Map.entry("pesetas", 1800),
                Map.entry("votos", 700),
                Map.entry("favores", 90));
        for (var entry : amounts.entrySet()) {
            var bonus = factionProductionBonus(factionCode, entry.getKey());
            jdbc.update(
                    """
                    INSERT INTO player_resources (player_id, resource_code, amount, production_per_minute)
                    VALUES (?, ?, ?, ?)
                    """,
                    playerId,
                    entry.getKey(),
                    entry.getValue(),
                    production.getOrDefault(entry.getKey(), 0) + bonus);
        }
    }

    private void initializeCityAndTroops(long playerId) {
        ensureCityAndTroops(playerId);
        jdbc.update("UPDATE player_city_buildings SET level = 1 WHERE player_id = ? AND building_code IN ('palacio_plenos','oficina_infinita','redaccion_subvencionada','plaza_promesas')", playerId);
        jdbc.update("UPDATE player_troops SET amount = 10 WHERE player_id = ? AND unit_code = 'funcionario_raso' AND amount = 0", playerId);
        jdbc.update("UPDATE player_troops SET amount = 2 WHERE player_id = ? AND unit_code = 'periodista' AND amount = 0", playerId);
    }

    private void ensureCityAndTroops(long playerId) {
        jdbc.update(
                """
                INSERT IGNORE INTO player_city_buildings (player_id, building_code, level)
                SELECT ?, code, 0 FROM city_building_definitions
                """,
                playerId);
        jdbc.update(
                """
                INSERT IGNORE INTO player_troops (player_id, unit_code, amount)
                SELECT ?, code, 0 FROM troop_definitions
                """,
                playerId);
        jdbc.update(
                """
                UPDATE player_city_buildings
                SET level = 1
                WHERE player_id = ?
                  AND building_code IN ('palacio_plenos','oficina_infinita','redaccion_subvencionada','plaza_promesas')
                  AND level = 0
                """,
                playerId);
        var totalTroops = jdbc.queryForObject("SELECT COALESCE(SUM(amount), 0) FROM player_troops WHERE player_id = ?", Integer.class, playerId);
        if (totalTroops == 0) {
            jdbc.update("UPDATE player_troops SET amount = 10 WHERE player_id = ? AND unit_code = 'funcionario_raso'", playerId);
            jdbc.update("UPDATE player_troops SET amount = 2 WHERE player_id = ? AND unit_code = 'periodista'", playerId);
        }
    }

    private String ensureCapitalProvince(long playerId) {
        return ensureCapitalProvince(playerId, null);
    }

    private String ensureCapitalProvince(long playerId, String preferredProvinceCode) {
        var currentCapital = queryObject(
                """
                SELECT t.name
                FROM players p
                JOIN territories t ON t.owner_player_id = p.id
                                  AND t.name = p.capital_city_name
                WHERE p.id = ?
                LIMIT 1
                """,
                String.class,
                playerId);
        if (currentCapital.isPresent()) {
            jdbc.update("UPDATE players SET onboarding_done = TRUE WHERE id = ?", playerId);
            return currentCapital.get();
        }

        var ownedProvince = queryObject(
                """
                SELECT name
                FROM territories
                WHERE owner_player_id = ?
                ORDER BY id
                LIMIT 1
                """,
                String.class,
                playerId);
        if (ownedProvince.isPresent()) {
            updateCapitalProvince(playerId, ownedProvince.get());
            return ownedProvince.get();
        }

        return claimStartingProvince(playerId, preferredProvinceCode);
    }

    private String claimStartingProvince(long playerId) {
        return claimStartingProvince(playerId, null);
    }

    private String claimStartingProvince(long playerId, String preferredProvinceCode) {
        var playerWorldId = worldId(playerId);
        var provinceCode = normalizeCode(preferredProvinceCode);
        if (!provinceCode.isBlank()) {
            return claimPreferredStartingProvince(playerId, playerWorldId, provinceCode);
        }

        for (var attempt = 0; attempt < 2; attempt++) {
            var province = availableStartingProvince(playerWorldId)
                    .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "No queda ninguna provincia libre en este mundo."));
            var provinceId = longValue(province, "id");
            var updated = jdbc.update(
                    "UPDATE territories SET owner_player_id = ? WHERE id = ? AND owner_player_id IS NULL",
                    playerId,
                    provinceId);
            if (updated == 1) {
                var provinceName = string(province, "name");
                updateCapitalProvince(playerId, provinceName);
                return provinceName;
            }
        }
        throw new ApiException(HttpStatus.CONFLICT, "Esa provincia acaba de ser ocupada. Inténtalo otra vez.");
    }

    private String claimPreferredStartingProvince(long playerId, long worldId, String provinceCode) {
        var province = queryMap(
                        """
                        SELECT id, name, owner_player_id
                        FROM territories
                        WHERE world_id = ?
                          AND code = ?
                        FOR UPDATE
                        """,
                        worldId,
                        provinceCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "La provincia invitada no existe en esta partida."));

        if (province.get("owner_player_id") != null) {
            throw new ApiException(HttpStatus.CONFLICT, "Ese hueco ya está ocupado. Pide otra invitación.");
        }

        var provinceId = longValue(province, "id");
        var updated = jdbc.update(
                "UPDATE territories SET owner_player_id = ? WHERE id = ? AND owner_player_id IS NULL",
                playerId,
                provinceId);
        if (updated != 1) {
            throw new ApiException(HttpStatus.CONFLICT, "Esa provincia acaba de ser ocupada. Inténtalo otra vez.");
        }

        var provinceName = string(province, "name");
        updateCapitalProvince(playerId, provinceName);
        return provinceName;
    }

    private Optional<Map<String, Object>> availableStartingProvince(long worldId) {
        return queryMap(
                        """
                        SELECT id, name
                        FROM territories
                        WHERE world_id = ?
                          AND owner_player_id IS NULL
                        ORDER BY id
                        LIMIT 1
                        FOR UPDATE
                        """,
                        worldId);
    }

    private void updateCapitalProvince(long playerId, String provinceName) {
        jdbc.update(
                "UPDATE players SET capital_city_name = ?, onboarding_done = TRUE WHERE id = ?",
                provinceName,
                playerId);
    }

    private int factionProductionBonus(String factionCode, String resourceCode) {
        return switch (factionCode) {
            case "pp" -> resourceCode.equals("pesetas") || resourceCode.equals("favores") ? 5 : 0;
            case "pisoe" -> resourceCode.equals("votos") ? 6 : resourceCode.equals("pesetas") ? 3 : 0;
            case "gil" -> resourceCode.equals("pesetas") ? 8 : resourceCode.equals("favores") ? 4 : 0;
            case "puff" -> resourceCode.equals("votos") || resourceCode.equals("favores") ? 5 : 0;
            case "vox" -> resourceCode.equals("votos") ? 7 : resourceCode.equals("pesetas") ? 2 : 0;
            case "junts" -> resourceCode.equals("favores") ? 7 : resourceCode.equals("pesetas") ? 2 : 0;
            default -> 0;
        };
    }

    private String createToken(long userId) {
        var bytes = new byte[32];
        random.nextBytes(bytes);
        var token = HexFormat.of().formatHex(bytes);
        jdbc.update(
                "INSERT INTO auth_tokens (token, user_id, expires_at) VALUES (?, ?, ?)",
                token,
                userId,
                Timestamp.from(Instant.now().plus(Duration.ofDays(14))));
        return token;
    }

    private void collectResources(long playerId) {
        var lastCollected = queryObject("SELECT last_collected_at FROM players WHERE id = ?", Timestamp.class, playerId)
                .orElseThrow()
                .toInstant();
        var minutes = Math.min(180, Math.max(0, Duration.between(lastCollected, Instant.now()).toMinutes()));
        if (minutes <= 0) {
            return;
        }
        jdbc.update(
                """
                UPDATE player_resources
                SET amount = LEAST(999999, amount + production_per_minute * ?)
                WHERE player_id = ?
                """,
                minutes,
                playerId);
        for (var ministry : ministries(playerId)) {
            if (ministry.activeForPlayer() && ministry.bonusAmount() > 0) {
                addResource(playerId, ministry.bonusResource(), (int) (ministry.bonusAmount() * minutes));
            }
        }
        jdbc.update(
                """
                UPDATE players
                SET action_points = LEAST(?, action_points + ?), last_collected_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """,
                MAX_ACTION_POINTS,
                Math.max(1, minutes / 6),
                playerId);
    }

    private void completeResearch(long playerId) {
        var rows = jdbc.queryForList(
                """
                SELECT pr.id, rd.effect_type, rd.effect_value
                FROM player_research pr
                JOIN research_definitions rd ON rd.id = pr.research_id
                WHERE pr.player_id = ? AND pr.status = 'pending' AND pr.finishes_at <= CURRENT_TIMESTAMP
                """,
                playerId);
        for (var row : rows) {
            var effect = string(row, "effect_type");
            var value = intValue(row, "effect_value");
            if (effect.equals("influence_production")) {
                jdbc.update(
                        "UPDATE player_resources SET production_per_minute = production_per_minute + ? WHERE player_id = ? AND resource_code = 'votos'",
                        value,
                        playerId);
            } else if (effect.equals("defense_bonus")) {
                jdbc.update(
                        "UPDATE territories SET defense = defense + ? WHERE owner_player_id = ?",
                        value,
                        playerId);
            }
            jdbc.update("UPDATE player_research SET status = 'done', finished_at = CURRENT_TIMESTAMP WHERE id = ?", longValue(row, "id"));
        }
    }

    private void completeBuildingUpgrades(long playerId) {
        var rows = jdbc.queryForList(
                """
                SELECT id
                FROM player_city_buildings
                WHERE player_id = ? AND upgrading = TRUE AND upgrade_finishes_at <= CURRENT_TIMESTAMP
                """,
                playerId);
        for (var row : rows) {
            jdbc.update(
                    """
                    UPDATE player_city_buildings
                    SET level = level + 1,
                        upgrading = FALSE,
                        upgrade_started_at = NULL,
                        upgrade_finishes_at = NULL
                    WHERE id = ?
                    """,
                    longValue(row, "id"));
        }
    }

    private void completeTroopTraining(long playerId) {
        var rows = jdbc.queryForList(
                """
                SELECT id, unit_code, amount
                FROM troop_training
                WHERE player_id = ? AND finishes_at <= CURRENT_TIMESTAMP
                ORDER BY finishes_at
                """,
                playerId);
        for (var row : rows) {
            jdbc.update(
                    """
                    INSERT INTO player_troops (player_id, unit_code, amount)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE amount = amount + VALUES(amount)
                    """,
                    playerId,
                    string(row, "unit_code"),
                    intValue(row, "amount"));
            jdbc.update("DELETE FROM troop_training WHERE id = ?", longValue(row, "id"));
        }
    }

    private void resolveDueActions(long playerId) {
        var pending = jdbc.queryForList(
                "SELECT * FROM actions WHERE player_id = ? AND status = 'pending' AND resolves_at <= CURRENT_TIMESTAMP ORDER BY resolves_at",
                playerId);
        for (var row : pending) {
            var actionType = string(row, "action_type");
            if (actionType.equals("CONQUEST")) {
                resolveConquest(row);
            } else if (actionType.equals("INFLUENCE")) {
                resolveInfluence(row);
            } else if (actionType.equals("CORRUPTION")) {
                resolveCorruption(row);
            } else if (actionType.equals("DISASTER")) {
                resolveDisaster(row);
            }
        }
    }

    private void resolveConquest(Map<String, Object> action) {
        var playerId = longValue(action, "player_id");
        var territoryId = longValue(action, "target_territory_id");
        var success = roll(intValue(action, "success_percent"));
        if (success) {
            jdbc.update("UPDATE territories SET owner_player_id = ?, defense = defense + 2 WHERE id = ?", playerId, territoryId);
            var votes = jdbc.queryForObject("SELECT base_votes FROM territories WHERE id = ?", Integer.class, territoryId);
            jdbc.update(
                    "UPDATE players SET votes = votes + ?, political_credit = political_credit + 4, reputation = LEAST(100, reputation + 2) WHERE id = ?",
                    votes,
                    playerId);
            finishAction(action, "Provincia capturada", "Tus asesores han entrado con mapas, argumentarios y una promesa por barrio. El territorio cambia de color.");
        } else {
            jdbc.update(
                    "UPDATE players SET votes = GREATEST(0, votes - 90), media_heat = LEAST(100, media_heat + 8) WHERE id = ?",
                    playerId);
            finishAction(action, "Invasión convertida en tertulia", "La operación se atascó en una rueda de prensa. Pierdes votos y ganas explicaciones incómodas.");
        }
    }

    private void resolveInfluence(Map<String, Object> action) {
        var playerId = longValue(action, "player_id");
        var territoryId = longValue(action, "target_territory_id");
        var success = roll(intValue(action, "success_percent"));
        if (success) {
            jdbc.update("UPDATE territories SET owner_player_id = ?, defense = GREATEST(20, defense - 3) WHERE id = ?", playerId, territoryId);
            jdbc.update(
                    "UPDATE players SET votes = votes + 110, political_credit = political_credit + 7, reputation = LEAST(100, reputation + 1) WHERE id = ?",
                    playerId);
            finishAction(action, "Relato conquistado", "Tres clips, dos encuestas y un primo con podcast bastaron: el territorio compra tu marco mental.");
        } else {
            jdbc.update(
                    "UPDATE players SET political_credit = GREATEST(0, political_credit - 4), media_heat = LEAST(100, media_heat + 5) WHERE id = ?",
                    playerId);
            finishAction(action, "Campaña en visto", "La población ha reaccionado con un bostezo transversal. Duele más que una derrota limpia.");
        }
    }

    private void resolveCorruption(Map<String, Object> action) {
        var playerId = longValue(action, "player_id");
        var scheme = corruptionScheme(string(action, "scheme_code")).orElseThrow();
        var caught = roll(intValue(action, "risk_percent"));
        if (caught) {
            jdbc.update(
                    """
                    UPDATE players
                    SET votes = GREATEST(0, votes - ?),
                        political_credit = GREATEST(0, political_credit - ?),
                        reputation = GREATEST(0, reputation - ?),
                        media_heat = LEAST(100, media_heat + ?)
                    WHERE id = ?
                    """,
                    180 + scheme.baseRiskPercent() * 4,
                    10,
                    12,
                    22,
                    playerId);
            finishAction(action, "Te han pillado con el carrito del BOE", scheme.caughtLabel());
        } else {
            applyCorruptionReward(playerId, scheme.code());
            jdbc.update(
                    "UPDATE players SET political_credit = political_credit + 8, media_heat = LEAST(100, media_heat + 4) WHERE id = ?",
                    playerId);
            finishAction(action, "Todo legalísimo, aparentemente", scheme.rewardLabel());
        }
    }

    private void resolveDisaster(Map<String, Object> action) {
        var playerId = longValue(action, "player_id");
        var planCode = string(action, "scheme_code").split(":")[0];
        var eventId = Long.parseLong(string(action, "scheme_code").split(":")[1]);
        var plan = disasterPlan(planCode).orElseThrow();
        var success = roll(intValue(action, "success_percent"));
        if (success) {
            jdbc.update(
                    "UPDATE world_events SET status = 'resolved', resolved_by_player_id = ?, result_summary = ? WHERE id = ?",
                    playerId,
                    plan.upside(),
                    eventId);
            jdbc.update(
                    "UPDATE players SET votes = votes + 160, reputation = LEAST(100, reputation + 6), political_credit = political_credit + 8 WHERE id = ?",
                    playerId);
            addResource(playerId, "favores", 45);
            finishAction(action, "Crisis convertida en foto útil", plan.upside());
        } else {
            jdbc.update(
                    "UPDATE players SET votes = GREATEST(0, votes - 140), reputation = GREATEST(0, reputation - 7), media_heat = LEAST(100, media_heat + 16) WHERE id = ?",
                    playerId);
            finishAction(action, "La crisis te ha comido el argumentario", plan.downside());
        }
    }

    private void applyCorruptionReward(long playerId, String schemeCode) {
        switch (schemeCode) {
            case "rotonda_gourmet" -> {
                addResource(playerId, "pesetas", 520);
                addResource(playerId, "votos", 90);
            }
            case "asesor_primo" -> {
                addResource(playerId, "favores", 90);
                addResource(playerId, "votos", 80);
            }
            case "master_diferido" -> {
                addResource(playerId, "votos", 150);
                addResource(playerId, "pesetas", 120);
            }
            case "contrato_chiringuito" -> {
                addResource(playerId, "pesetas", 420);
                addResource(playerId, "favores", 80);
            }
            case "puerta_giratoria" -> {
                addResource(playerId, "pesetas", 520);
                addResource(playerId, "favores", 70);
            }
            default -> {
                addResource(playerId, "votos", 120);
                addResource(playerId, "favores", 45);
            }
        }
    }

    private void spawnWorldEvent(long worldId) {
        jdbc.update("UPDATE world_events SET status = 'expired' WHERE world_id = ? AND status = 'active' AND expires_at < CURRENT_TIMESTAMP", worldId);
        var active = jdbc.queryForObject("SELECT COUNT(*) FROM world_events WHERE world_id = ? AND status = 'active'", Integer.class, worldId);
        if (active >= 4 || random.nextInt(100) > 18) {
            return;
        }
        var territory = jdbc.queryForMap(
                "SELECT id, name FROM territories WHERE world_id = ? ORDER BY RAND() LIMIT 1",
                worldId);
        var type = disasterTypes().get(random.nextInt(disasterTypes().size()));
        var severity = 2 + random.nextInt(6);
        jdbc.update(
                """
                INSERT INTO world_events (world_id, territory_id, event_type, name, description, severity, expires_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                worldId,
                longValue(territory, "id"),
                type.code(),
                type.name(),
                type.description().replace("{territory}", string(territory, "name")),
                severity,
                Timestamp.from(Instant.now().plusSeconds(240 + severity * 40L)));
    }

    private List<DisasterType> disasterTypes() {
        return jdbc.query(
                """
                SELECT code, name, description
                FROM event_definitions
                ORDER BY code
                """,
                (rs, rowNum) -> new DisasterType(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description")));
    }

    private List<CorruptionSchemeDto> corruptionSchemes() {
        return List.of(
                new CorruptionSchemeDto("rotonda_gourmet", "Rotonda gourmet", "Adjudica una rotonda con olivo de autor y presupuesto con levadura.", 22, 22, "+Pesetas, +votos locales y una inauguración con tijera gigante.", "Un interventor despierto ha olido el presupuesto trufado. Pierdes votos y favores.", costs(cost("votos", 55), cost("favores", 12))),
                new CorruptionSchemeDto("asesor_primo", "Asesor primo segundo", "Coloca a alguien que no sabe qué hace, pero saluda fuerte en campaña.", 18, 18, "+Votos y favores. Nadie pregunta por el organigrama.", "Un periodista ha encontrado el árbol genealógico. El despacho huele a dimisión.", costs(cost("votos", 70), cost("favores", 25))),
                new CorruptionSchemeDto("master_diferido", "Máster en diferido", "Mejora credenciales con trabajos invisibles y PDFs de procedencia filosófica.", 28, 16, "+Votos y pesetas. Tu biografía parece un DLC académico.", "Aparece un acta con más tachones que tu argumentario público.", costs(cost("pesetas", 90), cost("favores", 35))),
                new CorruptionSchemeDto("contrato_chiringuito", "Contrato chiringuito", "Subvenciona un observatorio del observatorio que observa cosas observables.", 30, 24, "+Pesetas, +favores y sonrisas en photocall.", "La oposición aprende Excel y te arruina la mañana.", costs(cost("votos", 70), cost("pesetas", 120))),
                new CorruptionSchemeDto("puerta_giratoria", "Puerta giratoria turbo", "Promete regular un sector justo antes de aterrizar suavemente en él.", 34, 30, "+Pesetas y favores. Todo muy técnico, todo muy casual.", "La hemeroteca te hace un placaje limpio en directo.", costs(cost("pesetas", 140), cost("favores", 55))),
                new CorruptionSchemeDto("mariscada_estrategica", "Mariscada estratégica", "Reunión discreta con factura indiscreta y servilleta constituyente.", 16, 14, "+Votos y favores. La coalición sabe mejor con limón.", "Alguien subió la foto con ubicación. Democracia geolocalizada.", costs(cost("favores", 18), cost("pesetas", 90))));
    }

    private Optional<CorruptionSchemeDto> corruptionScheme(String code) {
        return corruptionSchemes().stream().filter(scheme -> scheme.code().equals(code)).findFirst();
    }

    private List<DisasterPlanDto> disasterPlans() {
        return List.of(
                new DisasterPlanDto("gabinete_crisis", "Gabinete de crisis serio", "Activa técnicos, datos y cara de no haber dormido. Aburrido, pero funciona.", 76, 26, "La gestión parece adulta: suben votos y favores.", "Demasiadas siglas y poca calle. La crisis te pasa por encima.", costs(cost("favores", 70), cost("pesetas", 80))),
                new DisasterPlanDto("chaleco_foto", "Chaleco reflectante y selfie", "Te plantas allí con botas limpias, gesto grave y fotógrafo en ángulo heroico.", 48, 16, "El relato entra por los ojos: ganas votos rápidos.", "La gente nota que el chaleco tiene etiqueta. Suben el cabreo y los vídeos.", costs(cost("pesetas", 70), cost("votos", 45))),
                new DisasterPlanDto("culpar_anterior", "Culpar al anterior", "Clásico ibérico: todo empezó antes, incluso lo que pasó esta mañana.", 42, 14, "Tu base aplaude y ganas votos baratos.", "La cronología existe y te muerde. Pierdes votos.", costs(cost("votos", 45))),
                new DisasterPlanDto("brigada_emergencia", "Brigada de emergencia", "Envía presupuesto, técnicos y gente que sabe diferenciar una pala de un argumentario.", 68, 24, "El territorio se estabiliza y ganas favores.", "Faltan medios y sobran excusas. La crisis sigue oliendo a improvisación.", costs(cost("pesetas", 140), cost("favores", 80))),
                new DisasterPlanDto("boe_salvavidas", "BOE salvavidas", "Publica una orden tan larga que la catástrofe se cansa de leer.", 58, 20, "Ganas favores, control administrativo y una sensación rara de legalidad.", "El anexo III llega tarde y la población exige menos pdf y más ayuda.", costs(cost("favores", 90), cost("votos", 55))));
    }

    private Optional<DisasterPlanDto> disasterPlan(String code) {
        return disasterPlans().stream().filter(plan -> plan.code().equals(code)).findFirst();
    }

    private List<EventDefinitionDto> eventDefinitions() {
        return jdbc.query(
                """
                SELECT *
                FROM event_definitions
                ORDER BY base_severity DESC, code
                """,
                (rs, rowNum) -> new EventDefinitionDto(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("image_key"),
                        rs.getInt("base_severity"),
                        rs.getInt("duration_seconds"),
                        rs.getString("scope_label"),
                        rs.getString("impact_label"),
                        rs.getString("response_label")));
    }

    private void finishAction(Map<String, Object> action, String title, String body) {
        jdbc.update(
                """
                UPDATE actions
                SET status = 'resolved', resolved_at = CURRENT_TIMESTAMP, result_title = ?, result_body = ?
                WHERE id = ?
                """,
                title,
                body,
                longValue(action, "id"));
    }

    private int insertAction(long playerId, String type, Long territoryId, String schemeCode, int risk, int success, int durationSeconds) {
        var keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(
                """
                INSERT INTO actions
                    (player_id, action_type, target_territory_id, scheme_code, risk_percent, success_percent, resolves_at)
                VALUES
                    (:playerId, :type, :territoryId, :schemeCode, :risk, :success, :resolvesAt)
                """,
                new MapSqlParameterSource()
                        .addValue("playerId", playerId)
                        .addValue("type", type)
                        .addValue("territoryId", territoryId)
                        .addValue("schemeCode", schemeCode)
                        .addValue("risk", risk)
                        .addValue("success", success)
                        .addValue("resolvesAt", Timestamp.from(Instant.now().plusSeconds(durationSeconds))),
                keyHolder,
                new String[] {"id"});
        return keyHolder.getKey().intValue();
    }

    private PlayerDto player(long playerId) {
        var row = jdbc.queryForMap(
                """
                SELECT p.*, f.id faction_id, f.code faction_code, f.name faction_name, f.short_name, f.color,
                       f.motto, f.satire,
                       a.id alliance_id, a.name alliance_name, a.code alliance_code, a.description alliance_description,
                       af.code alliance_faction_code, af.name alliance_faction_name, af.color alliance_faction_color
                FROM players p
                JOIN factions f ON f.id = p.faction_id
                LEFT JOIN alliances a ON a.id = p.alliance_id
                LEFT JOIN factions af ON af.id = a.faction_id
                WHERE p.id = ?
                """,
                playerId);
        var faction = new FactionDto(
                longValue(row, "faction_id"),
                string(row, "faction_code"),
                string(row, "faction_name"),
                string(row, "short_name"),
                string(row, "color"),
                string(row, "motto"),
                string(row, "satire"));
        var alliance = row.get("alliance_id") == null
                ? null
                : new AllianceDto(
                        longValue(row, "alliance_id"),
                        string(row, "alliance_name"),
                        string(row, "alliance_code"),
                        string(row, "alliance_description"),
                        string(row, "alliance_faction_code"),
                        string(row, "alliance_faction_name"),
                        string(row, "alliance_faction_color"));
        return new PlayerDto(
                playerId,
                longValue(row, "world_id"),
                string(row, "leader_name"),
                faction,
                alliance,
                intValue(row, "votes"),
                intValue(row, "political_credit"),
                intValue(row, "reputation"),
                intValue(row, "media_heat"),
                intValue(row, "action_points"),
                row.get("capital_city_name") == null ? null : string(row, "capital_city_name"),
                booleanValue(row, "onboarding_done"));
    }

    private Optional<Long> playerIdForUser(long userId) {
        return playerIdForUser(userId, null);
    }

    private Optional<Long> playerIdForUser(long userId, String worldCode) {
        if (worldCode != null && !worldCode.isBlank()) {
            return queryObject(
                    """
                    SELECT p.id
                    FROM players p
                    JOIN worlds w ON w.id = p.world_id
                    WHERE p.user_id = ? AND w.code = ?
                      AND p.is_bot = FALSE
                    LIMIT 1
                    """,
                    Long.class,
                    userId,
                    normalizeCode(worldCode));
        }
        return queryObject("SELECT id FROM players WHERE user_id = ? AND is_bot = FALSE ORDER BY id DESC LIMIT 1", Long.class, userId);
    }

    private int playerCountForUser(long userId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM players WHERE user_id = ? AND is_bot = FALSE", Integer.class, userId);
    }

    private long userIdForPlayer(long playerId) {
        return jdbc.queryForObject("SELECT user_id FROM players WHERE id = ?", Long.class, playerId);
    }

    private List<PlayerDto> playersForUser(long userId) {
        return jdbc.query(
                "SELECT id FROM players WHERE user_id = ? AND is_bot = FALSE ORDER BY id DESC",
                (rs, rowNum) -> player(rs.getLong("id")),
                userId);
    }

    private UserDto user(long userId) {
        var row = jdbc.queryForMap("SELECT id, username, display_name, email FROM users WHERE id = ?", userId);
        return new UserDto(
                longValue(row, "id"),
                string(row, "username"),
                string(row, "display_name"),
                Optional.ofNullable(string(row, "email")).orElse(""));
    }

    private List<ResourceDto> resources(long playerId) {
        return jdbc.query(
                """
                SELECT rd.code, rd.name, rd.description, rd.icon, pr.amount, pr.production_per_minute
                FROM resource_definitions rd
                JOIN player_resources pr ON pr.resource_code = rd.code
                WHERE pr.player_id = ?
                ORDER BY FIELD(rd.code, 'pesetas','votos','favores')
                """,
                (rs, rowNum) -> new ResourceDto(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("icon"),
                        rs.getInt("amount"),
                        rs.getInt("production_per_minute")),
                playerId);
    }

    private List<TerritoryDto> territories(long worldId) {
        return jdbc.query(
                """
                SELECT t.*, p.leader_name owner_name, f.code owner_faction_code,
                       f.short_name owner_faction_short_name, f.color owner_faction_color,
                       rd.name resource_name
                FROM territories t
                LEFT JOIN players p ON p.id = t.owner_player_id
                LEFT JOIN factions f ON f.id = p.faction_id
                JOIN resource_definitions rd ON rd.code = t.resource_focus
                WHERE t.world_id = ?
                ORDER BY t.id
                """,
                (rs, rowNum) -> new TerritoryDto(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("region"),
                        rs.getInt("map_x"),
                        rs.getInt("map_y"),
                        rs.getObject("owner_player_id") == null ? null : rs.getLong("owner_player_id"),
                        rs.getString("owner_name"),
                        rs.getString("owner_faction_code"),
                        rs.getString("owner_faction_short_name"),
                        rs.getString("owner_faction_color"),
                        rs.getInt("defense"),
                        rs.getInt("population"),
                        rs.getInt("base_votes"),
                        rs.getString("resource_focus"),
                        rs.getString("resource_name"),
                        rs.getString("building_name"),
                        rs.getString("satire")),
                worldId);
    }

    private List<CityDto> cities(long playerId) {
        var capitalName = queryObject("SELECT capital_city_name FROM players WHERE id = ?", String.class, playerId).orElse("");
        return jdbc.query(
                """
                SELECT t.*, rd.name resource_name
                FROM territories t
                JOIN players p ON p.id = t.owner_player_id
                JOIN resource_definitions rd ON rd.code = t.resource_focus
                WHERE t.owner_player_id = ?
                ORDER BY CASE WHEN t.name = ? THEN 0 ELSE 1 END, t.id
                """,
                (rs, rowNum) -> new CityDto(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("region"),
                        rs.getInt("map_x"),
                        rs.getInt("map_y"),
                        rs.getInt("defense"),
                        rs.getInt("population"),
                        rs.getInt("base_votes"),
                        rs.getString("resource_name"),
                        rs.getString("building_name"),
                        rs.getString("satire"),
                        rs.getString("name").equals(capitalName)),
                playerId,
                capitalName);
    }

    private List<ActionDto> actions(long playerId) {
        return jdbc.query(
                """
                SELECT * FROM actions
                WHERE player_id = ?
                ORDER BY CASE WHEN status = 'pending' THEN 0 ELSE 1 END, resolves_at DESC
                LIMIT 20
                """,
                actionMapper(),
                playerId);
    }

    private ActionDto action(long actionId) {
        return jdbc.queryForObject("SELECT * FROM actions WHERE id = ?", actionMapper(), actionId);
    }

    private RowMapper<ActionDto> actionMapper() {
        return (rs, rowNum) -> new ActionDto(
                rs.getLong("id"),
                rs.getString("action_type"),
                rs.getObject("target_territory_id") == null ? null : rs.getLong("target_territory_id"),
                rs.getString("scheme_code"),
                rs.getString("status"),
                rs.getInt("risk_percent"),
                rs.getInt("success_percent"),
                instant(rs, "started_at"),
                instant(rs, "resolves_at"),
                instantOrNull(rs, "resolved_at"),
                rs.getString("result_title"),
                rs.getString("result_body"));
    }

    private List<WorldEventDto> worldEvents(long worldId) {
        return jdbc.query(
                """
                SELECT we.*, t.name territory_name
                FROM world_events we
                JOIN territories t ON t.id = we.territory_id
                WHERE we.world_id = ? AND we.status = 'active'
                ORDER BY we.severity DESC, we.expires_at
                """,
                (rs, rowNum) -> new WorldEventDto(
                        rs.getLong("id"),
                        rs.getLong("territory_id"),
                        rs.getString("territory_name"),
                        rs.getString("event_type"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("severity"),
                        rs.getString("status"),
                        instant(rs, "spawned_at"),
                        instant(rs, "expires_at"),
                        rs.getString("result_summary")),
                worldId);
    }

    private List<ResearchDto> research(long playerId) {
        var factionCode = playerFactionCode(playerId);
        return jdbc.query(
                """
                SELECT rd.*, pr.status, pr.finishes_at
                FROM research_definitions rd
                LEFT JOIN player_research pr ON pr.research_id = rd.id AND pr.player_id = ?
                WHERE rd.faction_code IS NULL OR rd.faction_code = ?
                ORDER BY rd.id
                """,
                (rs, rowNum) -> new ResearchDto(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("cost_pesetas"),
                        rs.getInt("cost_votos"),
                        rs.getInt("cost_favores"),
                        rs.getInt("duration_seconds"),
                        rs.getString("effect_type"),
                        rs.getInt("effect_value"),
                        rs.getString("status"),
                        instantOrNull(rs, "finishes_at")),
                playerId,
                factionCode);
    }

    private List<ResearchDefinitionDto> researchDefinitions() {
        return jdbc.query(
                """
                SELECT rd.*, f.name faction_name, f.short_name faction_short_name, f.color faction_color
                FROM research_definitions rd
                LEFT JOIN factions f ON f.code = rd.faction_code
                ORDER BY CASE WHEN rd.faction_code IS NULL THEN 0 ELSE 1 END,
                         CASE rd.faction_code
                            WHEN 'pp' THEN 1
                            WHEN 'pisoe' THEN 2
                            WHEN 'vox' THEN 3
                            WHEN 'puff' THEN 4
                            WHEN 'gil' THEN 5
                            WHEN 'junts' THEN 6
                            ELSE 99
                         END,
                         rd.category, rd.duration_seconds, rd.code
                """,
                (rs, rowNum) -> new ResearchDefinitionDto(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("image_key"),
                        rs.getString("faction_code"),
                        rs.getString("faction_name"),
                        rs.getString("faction_short_name"),
                        rs.getString("faction_color"),
                        rs.getInt("cost_pesetas"),
                        rs.getInt("cost_votos"),
                        rs.getInt("cost_favores"),
                        rs.getInt("duration_seconds"),
                        rs.getString("effect_type"),
                        rs.getInt("effect_value"),
                        researchEffectLabel(rs.getString("effect_type"), rs.getInt("effect_value"))));
    }

    private ResearchDto researchRow(long playerId, Map<String, Object> row) {
        var joined = jdbc.queryForMap(
                """
                SELECT rd.*, pr.status, pr.finishes_at
                FROM research_definitions rd
                JOIN player_research pr ON pr.research_id = rd.id
                WHERE pr.player_id = ? AND rd.id = ?
                """,
                playerId,
                longValue(row, "id"));
        return new ResearchDto(
                longValue(joined, "id"),
                string(joined, "code"),
                string(joined, "name"),
                string(joined, "description"),
                intValue(joined, "cost_pesetas"),
                intValue(joined, "cost_votos"),
                intValue(joined, "cost_favores"),
                intValue(joined, "duration_seconds"),
                string(joined, "effect_type"),
                intValue(joined, "effect_value"),
                string(joined, "status"),
                instantObject(joined.get("finishes_at")));
    }

    private List<CityBuildingDto> cityBuildings(long playerId) {
        return jdbc.query(
                """
                SELECT pcb.id, pcb.building_code, pcb.level, pcb.upgrading, pcb.upgrade_started_at, pcb.upgrade_finishes_at,
                       cbd.*
                FROM player_city_buildings pcb
                JOIN city_building_definitions cbd ON cbd.code = pcb.building_code
                WHERE pcb.player_id = ?
                ORDER BY cbd.map_y, cbd.map_x
                """,
                (rs, rowNum) -> {
                    var row = rowFrom(rs);
                    var level = rs.getInt("level");
                    var nextLevel = Math.min(level + 1, rs.getInt("max_level"));
                    return new CityBuildingDto(
                            rs.getLong("id"),
                            rs.getString("building_code"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getString("image_key"),
                            rs.getInt("map_x"),
                            rs.getInt("map_y"),
                            rs.getInt("width"),
                            rs.getInt("height"),
                            level,
                            rs.getInt("max_level"),
                            rs.getBoolean("upgrading"),
                            instantOrNull(rs, "upgrade_started_at"),
                            instantOrNull(rs, "upgrade_finishes_at"),
                            level >= rs.getInt("max_level") ? List.of() : scaledBuildingCosts(row, nextLevel),
                            level >= rs.getInt("max_level") ? 0 : buildingUpgradeSeconds(playerId, row, nextLevel),
                            List.of(rs.getString("effect_label")));
                },
                playerId);
    }

    private List<BuildingDefinitionDto> buildingDefinitions() {
        return jdbc.query(
                """
                SELECT *
                FROM city_building_definitions
                ORDER BY category, map_y, map_x, code
                """,
                (rs, rowNum) -> new BuildingDefinitionDto(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("image_key"),
                        rs.getInt("map_x"),
                        rs.getInt("map_y"),
                        rs.getInt("width"),
                        rs.getInt("height"),
                        rs.getInt("max_level"),
                        resourceCosts(rowFrom(rs)),
                        rs.getInt("duration_seconds"),
                        List.of(rs.getString("effect_label"))));
    }

    private List<TroopDefinitionDto> troopDefinitions() {
        return jdbc.query(
                """
                SELECT td.*, f.name faction_name, f.short_name faction_short_name, f.color faction_color
                FROM troop_definitions td
                LEFT JOIN factions f ON f.code = td.faction_code
                ORDER BY CASE WHEN td.faction_code IS NULL THEN 0 ELSE 1 END,
                         CASE td.faction_code
                            WHEN 'pp' THEN 1
                            WHEN 'pisoe' THEN 2
                            WHEN 'vox' THEN 3
                            WHEN 'puff' THEN 4
                            WHEN 'gil' THEN 5
                            WHEN 'junts' THEN 6
                            ELSE 99
                         END,
                         td.tier, td.training_seconds, td.code
                """,
                (rs, rowNum) -> new TroopDefinitionDto(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("description"),
                        rs.getString("image_key"),
                        rs.getString("faction_code"),
                        rs.getString("faction_name"),
                        rs.getString("faction_short_name"),
                        rs.getString("faction_color"),
                        rs.getInt("tier"),
                        rs.getInt("attack"),
                        rs.getString("attack_type"),
                        attackTypeLabel(rs.getString("attack_type")),
                        rs.getString("transport_type"),
                        transportTypeLabel(rs.getString("transport_type")),
                        rs.getInt("defense_bureaucratic"),
                        rs.getInt("defense_incisive"),
                        rs.getInt("defense_media"),
                        rs.getInt("influence_power"),
                        rs.getInt("speed"),
                        rs.getInt("capacity"),
                        rs.getInt("training_seconds"),
                        rs.getString("unlock_building_code"),
                        rs.getInt("unlock_building_level"),
                        resourceCosts(rowFrom(rs))));
    }

    private List<PlayerTroopDto> troops(long playerId) {
        return jdbc.query(
                """
                SELECT td.*, pt.amount, f.name faction_name, f.short_name faction_short_name, f.color faction_color
                FROM troop_definitions td
                JOIN player_troops pt ON pt.unit_code = td.code
                LEFT JOIN factions f ON f.code = td.faction_code
                WHERE pt.player_id = ?
                ORDER BY CASE WHEN td.faction_code IS NULL THEN 0 ELSE 1 END,
                         CASE td.faction_code
                            WHEN 'pp' THEN 1
                            WHEN 'pisoe' THEN 2
                            WHEN 'vox' THEN 3
                            WHEN 'puff' THEN 4
                            WHEN 'gil' THEN 5
                            WHEN 'junts' THEN 6
                            ELSE 99
                         END,
                         td.tier, td.training_seconds, td.code
                """,
                (rs, rowNum) -> new PlayerTroopDto(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("image_key"),
                        rs.getString("faction_code"),
                        rs.getString("faction_name"),
                        rs.getString("faction_short_name"),
                        rs.getString("faction_color"),
                        rs.getInt("amount"),
                        rs.getInt("attack"),
                        rs.getString("attack_type"),
                        attackTypeLabel(rs.getString("attack_type")),
                        rs.getString("transport_type"),
                        transportTypeLabel(rs.getString("transport_type")),
                        rs.getInt("defense_bureaucratic"),
                        rs.getInt("defense_incisive"),
                        rs.getInt("defense_media"),
                        rs.getInt("influence_power"),
                        rs.getInt("speed"),
                        rs.getInt("capacity")),
                playerId);
    }

    private List<CityGarrisonDto> garrisons(long playerId) {
        return jdbc.query(
                """
                SELECT cg.territory_id, t.name territory_name, td.code, td.name unit_name, td.image_key,
                       td.faction_code, f.name faction_name, f.short_name faction_short_name, f.color faction_color,
                       cg.amount, td.attack, td.attack_type, td.transport_type, td.defense_bureaucratic,
                       td.defense_incisive, td.defense_media, td.influence_power, td.capacity
                FROM city_garrisons cg
                JOIN territories t ON t.id = cg.territory_id
                JOIN troop_definitions td ON td.code = cg.unit_code
                LEFT JOIN factions f ON f.code = td.faction_code
                WHERE cg.player_id = ? AND cg.amount > 0
                ORDER BY t.name,
                         CASE WHEN td.faction_code IS NULL THEN 0 ELSE 1 END,
                         CASE td.faction_code
                            WHEN 'pp' THEN 1
                            WHEN 'pisoe' THEN 2
                            WHEN 'vox' THEN 3
                            WHEN 'puff' THEN 4
                            WHEN 'gil' THEN 5
                            WHEN 'junts' THEN 6
                            ELSE 99
                         END,
                         td.tier, td.training_seconds
                """,
                (rs, rowNum) -> new CityGarrisonDto(
                        rs.getLong("territory_id"),
                        rs.getString("territory_name"),
                        rs.getString("code"),
                        rs.getString("unit_name"),
                        rs.getString("image_key"),
                        rs.getString("faction_code"),
                        rs.getString("faction_name"),
                        rs.getString("faction_short_name"),
                        rs.getString("faction_color"),
                        rs.getInt("amount"),
                        rs.getInt("attack"),
                        rs.getString("attack_type"),
                        attackTypeLabel(rs.getString("attack_type")),
                        rs.getString("transport_type"),
                        transportTypeLabel(rs.getString("transport_type")),
                        rs.getInt("defense_bureaucratic"),
                        rs.getInt("defense_incisive"),
                        rs.getInt("defense_media"),
                        rs.getInt("influence_power"),
                        rs.getInt("capacity")),
                playerId);
    }

    private String attackTypeLabel(String attackType) {
        return switch (attackType) {
            case "INCISIVE" -> "Incisivo";
            case "MEDIA" -> "Mediático";
            default -> "Burocrático";
        };
    }

    private String transportTypeLabel(String transportType) {
        if (transportType == null || transportType.isBlank()) {
            return null;
        }
        return switch (transportType) {
            case "terrestre" -> "Terrestre";
            case "maritimo", "marítimo" -> "Marítimo";
            case "aereo", "aéreo" -> "Aéreo";
            default -> transportType;
        };
    }

    private String researchEffectLabel(String effectType, int effectValue) {
        return switch (effectType) {
            case "conquest_bonus" -> "+" + effectValue + " a operaciones territoriales";
            case "corruption_risk_reduction" -> "-" + effectValue + " riesgo en corrupción";
            case "defense_bonus" -> "+" + effectValue + " defensa provincial";
            default -> "+" + effectValue + " producción de votos";
        };
    }

    private String playerFactionCode(long playerId) {
        return queryObject(
                        """
                        SELECT f.code
                        FROM players p
                        JOIN factions f ON f.id = p.faction_id
                        WHERE p.id = ?
                        """,
                        String.class,
                        playerId)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Partido del jugador no configurado."));
    }

    private List<TrainingQueueDto> trainingQueue(long playerId) {
        return jdbc.query(
                """
                SELECT tt.*, td.name, td.image_key
                FROM troop_training tt
                JOIN troop_definitions td ON td.code = tt.unit_code
                WHERE tt.player_id = ?
                ORDER BY tt.finishes_at
                """,
                (rs, rowNum) -> {
                    var startedAt = instant(rs, "started_at");
                    var finishesAt = instant(rs, "finishes_at");
                    return new TrainingQueueDto(
                            rs.getLong("id"),
                            rs.getString("unit_code"),
                            rs.getString("name"),
                            rs.getString("image_key"),
                            rs.getInt("amount"),
                            startedAt,
                            finishesAt,
                            Math.max(1, (int) Duration.between(startedAt, finishesAt).toSeconds()));
                },
                playerId);
    }

    private List<MinistryDto> ministries(long playerId) {
        var playerFaction = jdbc.queryForObject("SELECT f.code FROM players p JOIN factions f ON f.id = p.faction_id WHERE p.id = ?", String.class, playerId);
        return ministryRows().stream()
                .map(row -> {
                    var faction = factionByCode(row.controlledByFactionCode());
                    var support = institutionalSupport(row.controlledByFactionCode());
                    var active = row.controlledByFactionCode().equals(playerFaction)
                            || (isGoverningFaction(playerFaction) && support >= row.requiredSupport());
                    return new MinistryDto(
                            row.code(),
                            row.name(),
                            row.description(),
                            row.controlledByFactionCode(),
                            string(faction, "name"),
                            string(faction, "color"),
                            row.bonusResource(),
                            row.bonusAmount(),
                            row.requiredSupport(),
                            active,
                            row.effectLabel());
                })
                .toList();
    }

    private List<RegionalGovernmentDto> regionalGovernments() {
        return regionalRows().stream()
                .map(row -> new RegionalGovernmentDto(
                        row.code(),
                        row.name(),
                        row.provinces(),
                        row.stability(),
                        row.seats()))
                .toList();
    }

    private List<AllianceScoreDto> allianceScores(long playerId) {
        return jdbc.query(
                """
                SELECT a.id, a.name, a.code, f.name faction_name, f.color faction_color,
                       (SELECT COUNT(*) FROM players p WHERE p.alliance_id = a.id) members,
                       (SELECT COALESCE(SUM(p.votes), 0) FROM players p WHERE p.alliance_id = a.id) total_votes,
                       (
                         SELECT COUNT(*)
                         FROM territories t
                         JOIN players p ON p.id = t.owner_player_id
                         WHERE p.alliance_id = a.id
                       ) territories,
                       (
                         SELECT COALESCE(SUM(pt.amount * (td.attack + td.defense + td.influence_power)), 0)
                         FROM player_troops pt
                         JOIN troop_definitions td ON td.code = pt.unit_code
                         JOIN players p ON p.id = pt.player_id
                         WHERE p.alliance_id = a.id
                       ) troop_power
                FROM alliances a
                JOIN factions f ON f.id = a.faction_id
                WHERE a.world_id = ?
                ORDER BY total_votes + territories * 500 + troop_power / 5 DESC
                LIMIT 20
                """,
                (rs, rowNum) -> {
                    var totalVotes = rs.getInt("total_votes");
                    var territories = rs.getInt("territories");
                    var troopPower = rs.getInt("troop_power");
                    return new AllianceScoreDto(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("code"),
                            rs.getString("faction_name"),
                            rs.getString("faction_color"),
                            rs.getInt("members"),
                            totalVotes,
                            territories,
                            troopPower,
                            totalVotes + territories * 500 + troopPower / 5);
                },
                worldId(playerId));
    }

    private List<AllianceMessageDto> allianceMessages(long playerId) {
        var allianceId = allianceId(playerId);
        if (allianceId.isEmpty()) {
            return List.of();
        }
        return jdbc.query(
                """
                SELECT am.id, p.leader_name, am.body, am.created_at
                FROM alliance_messages am
                JOIN players p ON p.id = am.player_id
                WHERE am.alliance_id = ?
                ORDER BY am.created_at DESC
                LIMIT 30
                """,
                (rs, rowNum) -> new AllianceMessageDto(
                        rs.getLong("id"),
                        rs.getString("leader_name"),
                        rs.getString("body"),
                        instant(rs, "created_at")),
                allianceId.get());
    }

    private AllianceMessageDto allianceMessage(long id) {
        return jdbc.queryForObject(
                """
                SELECT am.id, p.leader_name, am.body, am.created_at
                FROM alliance_messages am
                JOIN players p ON p.id = am.player_id
                WHERE am.id = ?
                """,
                (rs, rowNum) -> new AllianceMessageDto(rs.getLong("id"), rs.getString("leader_name"), rs.getString("body"), instant(rs, "created_at")),
                id);
    }

    private void joinAlliance(long playerId, long allianceId, String role) {
        jdbc.update("UPDATE players SET alliance_id = ? WHERE id = ?", allianceId, playerId);
        jdbc.update(
                """
                INSERT INTO alliance_members (alliance_id, player_id, role)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE role = VALUES(role)
                """,
                allianceId,
                playerId,
                role);
    }

    private AllianceDto alliance(long allianceId) {
        return jdbc.queryForObject(
                """
                SELECT a.id, a.name, a.code, a.description,
                       f.code faction_code, f.name faction_name, f.color faction_color
                FROM alliances a
                JOIN factions f ON f.id = a.faction_id
                WHERE a.id = ?
                """,
                allianceMapper(),
                allianceId);
    }

    private RowMapper<AllianceDto> allianceMapper() {
        return (rs, rowNum) -> new AllianceDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("code"),
                rs.getString("description"),
                rs.getString("faction_code"),
                rs.getString("faction_name"),
                rs.getString("faction_color"));
    }

    private void ensureTerritoryExists(long territoryId) {
        queryObject("SELECT id FROM territories WHERE id = ?", Long.class, territoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Territorio no encontrado."));
    }

    private void ensureOwnedTerritory(long playerId, long territoryId) {
        var owner = queryObject("SELECT owner_player_id FROM territories WHERE id = ?", Long.class, territoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Territorio no encontrado."));
        if (owner != playerId) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Esa provincia no está bajo tu mando. Todavía.");
        }
    }

    private void ensureUnitExists(String unitCode) {
        troopDefinitionRow(unitCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unidad no disponible."));
    }

    private boolean ownsTerritory(long playerId, long territoryId) {
        return queryObject("SELECT owner_player_id FROM territories WHERE id = ?", Long.class, territoryId)
                .map(owner -> owner == playerId)
                .orElse(false);
    }

    private int troopReserve(long playerId, String unitCode) {
        return queryObject(
                        "SELECT amount FROM player_troops WHERE player_id = ? AND unit_code = ?",
                        Integer.class,
                        playerId,
                        unitCode)
                .orElse(0);
    }

    private int garrisonAmount(long playerId, long territoryId, String unitCode) {
        return queryObject(
                        "SELECT amount FROM city_garrisons WHERE player_id = ? AND territory_id = ? AND unit_code = ?",
                        Integer.class,
                        playerId,
                        territoryId,
                        unitCode)
                .orElse(0);
    }

    private long factionId(long playerId) {
        return jdbc.queryForObject("SELECT faction_id FROM players WHERE id = ?", Long.class, playerId);
    }

    private void ensureAllianceFaction(long playerId, long allianceId) {
        var allianceFactionId = queryObject("SELECT faction_id FROM alliances WHERE id = ?", Long.class, allianceId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Coalición no encontrada."));
        if (allianceFactionId != factionId(playerId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Las alianzas son internas del partido. Aquí no se pacta con el rival, se le filtra algo.");
        }
    }

    private void spendActionPoint(long playerId) {
        var points = jdbc.queryForObject("SELECT action_points FROM players WHERE id = ?", Integer.class, playerId);
        if (points <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No te quedan puntos de acción. Ni el populismo rompe las leyes del cansancio.");
        }
        jdbc.update("UPDATE players SET action_points = action_points - 1 WHERE id = ?", playerId);
    }

    private void charge(long playerId, List<ResourceCostDto> costs) {
        for (var cost : costs) {
            if (cost.amount() <= 0) {
                continue;
            }
            var amount = jdbc.queryForObject(
                    "SELECT amount FROM player_resources WHERE player_id = ? AND resource_code = ?",
                    Integer.class,
                    playerId,
                    cost.code());
            if (amount < cost.amount()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Faltan " + cost.code() + ". La épica no financia facturas.");
            }
        }
        for (var cost : costs) {
            if (cost.amount() > 0) {
                jdbc.update(
                        "UPDATE player_resources SET amount = amount - ? WHERE player_id = ? AND resource_code = ?",
                        cost.amount(),
                        playerId,
                        cost.code());
            }
        }
    }

    private void addResource(long playerId, String code, int amount) {
        jdbc.update(
                "UPDATE player_resources SET amount = LEAST(999999, amount + ?) WHERE player_id = ? AND resource_code = ?",
                amount,
                playerId,
                code);
    }

    private List<ResourceCostDto> resourceCosts(Map<String, Object> row) {
        return costs(
                cost("pesetas", intValue(row, "cost_pesetas")),
                cost("votos", intValue(row, "cost_votos")),
                cost("favores", intValue(row, "cost_favores")));
    }

    private List<ResourceCostDto> multiplyCosts(List<ResourceCostDto> costs, int amount) {
        return costs.stream()
                .map(cost -> cost(cost.code(), cost.amount() * amount))
                .toList();
    }

    private List<ResourceCostDto> scaledBuildingCosts(Map<String, Object> row, int nextLevel) {
        var factor = Math.pow(Math.max(1, nextLevel - 1), 1.48);
        return resourceCosts(row).stream()
                .map(cost -> cost(cost.code(), Math.max(1, (int) Math.round(cost.amount() * factor))))
                .toList();
    }

    private int buildingUpgradeSeconds(long playerId, Map<String, Object> row, int nextLevel) {
        var palace = buildingLevel(playerId, "palacio_plenos");
        var archive = buildingLevel(playerId, "archivo_boe");
        var speed = 1 + palace * 0.025 + archive * 0.01;
        return Math.max(25, (int) Math.round(intValue(row, "duration_seconds") * Math.pow(Math.max(1, nextLevel - 1), 1.35) / speed));
    }

    private int troopTrainingSeconds(long playerId, Map<String, Object> unit, int amount) {
        var office = buildingLevel(playerId, "oficina_infinita");
        var media = buildingLevel(playerId, "redaccion_subvencionada") + buildingLevel(playerId, "plato_24h");
        var command = buildingLevel(playerId, "palacio_plenos");
        var speed = 1 + office * 0.025 + media * 0.014 + command * 0.012;
        return Math.max(12, (int) Math.round(intValue(unit, "training_seconds") * amount / speed));
    }

    private int activeBuildingUpgrades(long playerId) {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM player_city_buildings WHERE player_id = ? AND upgrading = TRUE",
                Integer.class,
                playerId);
    }

    private int constructionQueueLimit(long playerId) {
        return buildingLevel(playerId, "palacio_plenos") >= 5 ? 2 : 1;
    }

    private int buildingLevel(long playerId, String buildingCode) {
        return queryObject(
                        "SELECT level FROM player_city_buildings WHERE player_id = ? AND building_code = ?",
                        Integer.class,
                        playerId,
                        buildingCode)
                .orElse(0);
    }

    private Optional<Map<String, Object>> troopDefinitionRow(String unitCode) {
        return queryMap("SELECT * FROM troop_definitions WHERE code = ?", unitCode);
    }

    private String unlockBuildingName(Map<String, Object> unit) {
        return queryObject(
                        "SELECT name FROM city_building_definitions WHERE code = ?",
                        String.class,
                        string(unit, "unlock_building_code"))
                .orElse("el edificio requerido");
    }

    private void ensureResourceExists(String code) {
        queryObject("SELECT code FROM resource_definitions WHERE code = ?", String.class, code)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Recurso no disponible."));
    }

    private double exchangeRate(String from, String to) {
        if (from.equals("pesetas") && to.equals("votos")) return 0.08;
        if (from.equals("votos") && to.equals("pesetas")) return 8.5;
        if (from.equals("pesetas") && to.equals("favores")) return 0.03;
        if (from.equals("favores") && to.equals("pesetas")) return 22.0;
        if (from.equals("votos") && to.equals("favores")) return 0.25;
        if (from.equals("favores") && to.equals("votos")) return 3.0;
        return 0.5;
    }

    private Map<String, Object> factionByCode(String code) {
        return queryMap("SELECT code, name, color FROM factions WHERE code = ?", code)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Partido político no configurado."));
    }

    private int institutionalSupport(String factionCode) {
        return institutionalSupportRows().stream()
                .filter(row -> row.code().equals(factionCode))
                .mapToInt(InstitutionalSupportRow::support)
                .findFirst()
                .orElse(0);
    }

    private boolean isGoverningFaction(String factionCode) {
        return institutionalSupportRows().stream().anyMatch(row -> row.code().equals(factionCode) && row.governing());
    }

    private List<InstitutionalSupportRow> institutionalSupportRows() {
        return List.of(
                new InstitutionalSupportRow("pisoe", 121, true),
                new InstitutionalSupportRow("pp", 106, false),
                new InstitutionalSupportRow("vox", 47, false),
                new InstitutionalSupportRow("puff", 35, true),
                new InstitutionalSupportRow("gil", 25, false),
                new InstitutionalSupportRow("junts", 16, true));
    }

    private List<MinistryRow> ministryRows() {
        return List.of(
                new MinistryRow("hacienda-peseta", "Ministerio de Hacienda y Peseta Nueva", "Imprime billetes con nostalgia y exige recibos con mirada clínica.", "pisoe", "pesetas", 8, 80, "+8 producción/min de pesetas si tu bloque lo controla."),
                new MinistryRow("orden-palco", "Ministerio de Orden, Puro y Palco", "Promete estabilidad con una mano y reparte invitaciones institucionales con la otra.", "pp", "favores", 5, 70, "+5 favores/min y gestión más resistente."),
                new MinistryRow("obras-espectaculo", "Ministerio de Obras, Orden y Espectáculo", "Convierte solares, ruedas de prensa y grúas en una coreografía de obra pública.", "gil", "pesetas", 8, 25, "+8 pesetas/min y crecimiento urbano acelerado."),
                new MinistryRow("igualdad-puff", "Ministerio del Puñito Federal", "Declara urgente cualquier comité, subcomité o comisión con megáfono reglamentario.", "puff", "votos", 5, 35, "+5 votos/min y movilización de base."),
                new MinistryRow("bombo-fronteras", "Ministerio del Bombo y la Frontera", "Saca pecho, afina mal el xilófono y convierte cada debate en desfile de balcones.", "vox", "votos", 5, 45, "+5 votos/min y presión territorial."),
                new MinistryRow("territorio-bisagra", "Ministerio del Peaje Emocional", "Negocia cada coma como si fuera una frontera y cada frontera como si fuera una factura.", "junts", "favores", 1, 35, "+1 favor/min y pactos territoriales más baratos."));
    }

    private List<RegionalRow> regionalRows() {
        return List.of(
                new RegionalRow("galicia", "Galicia", List.of("A Coruña", "Lugo", "Ourense", "Pontevedra"), 72, 28),
                new RegionalRow("asturias", "Principado de Asturias", List.of("Asturias"), 68, 12),
                new RegionalRow("cantabria", "Cantabria", List.of("Cantabria"), 58, 8),
                new RegionalRow("pais-vasco", "País Vasco", List.of("Álava", "Bizkaia", "Gipuzkoa"), 80, 21),
                new RegionalRow("navarra", "Navarra", List.of("Navarra"), 63, 9),
                new RegionalRow("la-rioja", "La Rioja", List.of("La Rioja"), 55, 7),
                new RegionalRow("castilla-leon", "Castilla y León", List.of("León", "Palencia", "Burgos", "Zamora", "Valladolid", "Soria", "Salamanca", "Ávila", "Segovia"), 66, 33),
                new RegionalRow("madrid", "Comunidad de Madrid", List.of("Madrid"), 74, 32),
                new RegionalRow("castilla-mancha", "Castilla-La Mancha", List.of("Guadalajara", "Toledo", "Cuenca", "Ciudad Real", "Albacete"), 61, 25),
                new RegionalRow("extremadura", "Extremadura", List.of("Cáceres", "Badajoz"), 57, 14),
                new RegionalRow("aragon", "Aragón", List.of("Huesca", "Zaragoza", "Teruel"), 60, 16),
                new RegionalRow("cataluna", "Cataluña", List.of("Barcelona", "Girona", "Lleida", "Tarragona"), 69, 38),
                new RegionalRow("valenciana", "Comunidad Valenciana", List.of("Castellón", "Valencia", "Alicante"), 76, 31),
                new RegionalRow("murcia", "Región de Murcia", List.of("Murcia"), 62, 11),
                new RegionalRow("andalucia", "Andalucía", List.of("Huelva", "Sevilla", "Córdoba", "Jaén", "Cádiz", "Málaga", "Granada", "Almería"), 73, 45),
                new RegionalRow("baleares", "Islas Baleares", List.of("Islas Baleares"), 65, 12),
                new RegionalRow("canarias", "Islas Canarias", List.of("Las Palmas", "Santa Cruz de Tenerife"), 70, 15),
                new RegionalRow("ceuta", "Ceuta", List.of("Ceuta"), 52, 2),
                new RegionalRow("melilla", "Melilla", List.of("Melilla"), 54, 2));
    }

    private int researchBonus(long playerId, String effectType) {
        return jdbc.queryForObject(
                """
                SELECT COALESCE(SUM(rd.effect_value), 0)
                FROM player_research pr
                JOIN research_definitions rd ON rd.id = pr.research_id
                WHERE pr.player_id = ? AND pr.status = 'done' AND rd.effect_type = ?
                """,
                Integer.class,
                playerId,
                effectType);
    }

    private int defense(long territoryId) {
        return jdbc.queryForObject("SELECT defense FROM territories WHERE id = ?", Integer.class, territoryId);
    }

    private int politicalCredit(long playerId) {
        return jdbc.queryForObject("SELECT political_credit FROM players WHERE id = ?", Integer.class, playerId);
    }

    private int reputation(long playerId) {
        return jdbc.queryForObject("SELECT reputation FROM players WHERE id = ?", Integer.class, playerId);
    }

    private int mediaHeat(long playerId) {
        return jdbc.queryForObject("SELECT media_heat FROM players WHERE id = ?", Integer.class, playerId);
    }

    private int factionCorruptionAffinity(long playerId) {
        return jdbc.queryForObject(
                """
                SELECT f.corruption_affinity
                FROM players p
                JOIN factions f ON f.id = p.faction_id
                WHERE p.id = ?
                """,
                Integer.class,
                playerId);
    }

    private long worldId(long playerId) {
        return jdbc.queryForObject("SELECT world_id FROM players WHERE id = ?", Long.class, playerId);
    }

    private Optional<Long> allianceId(long playerId) {
        return queryObject("SELECT alliance_id FROM players WHERE id = ?", Long.class, playerId);
    }

    private Optional<Long> idByCode(String table, String column, String code) {
        return queryObject("SELECT id FROM " + table + " WHERE " + column + " = ?", Long.class, code);
    }

    private Optional<Map<String, Object>> queryMap(String sql, Object... args) {
        try {
            return Optional.of(jdbc.queryForMap(sql, args));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private <T> Optional<T> queryObject(String sql, Class<T> type, Object... args) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, type, args));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    private ResourceCostDto cost(String code, int amount) {
        return new ResourceCostDto(code, amount);
    }

    private List<ResourceCostDto> costs(ResourceCostDto... costs) {
        var filtered = new ArrayList<ResourceCostDto>();
        for (var cost : costs) {
            if (cost.amount() > 0) {
                filtered.add(cost);
            }
        }
        return filtered;
    }

    private boolean roll(int percent) {
        return random.nextInt(100) < percent;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeEmail(String value) {
        return normalize(value);
    }

    private String normalizeCode(String value) {
        return normalize(value).replaceAll("[^a-z0-9_-]", "");
    }

    private String verificationCode() {
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private String recoveryToken() {
        var bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String passwordResetUrl(String resetId, String email, String token) {
        var baseUrl = frontendBaseUrl == null || frontendBaseUrl.isBlank()
                ? "http://localhost:5173"
                : frontendBaseUrl.trim();
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl
                + "/login?resetId=" + encodeQuery(resetId)
                + "&email=" + encodeQuery(email)
                + "&token=" + encodeQuery(token);
    }

    private String encodeQuery(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String string(Map<String, Object> row, String key) {
        var value = row.get(key);
        return value == null ? null : value.toString();
    }

    private boolean booleanValue(Map<String, Object> row, String key) {
        var value = row.get(key);
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }

    private long longValue(Map<String, Object> row, String key) {
        return ((Number) row.get(key)).longValue();
    }

    private int intValue(Map<String, Object> row, String key) {
        return ((Number) row.get(key)).intValue();
    }

    private Instant instant(ResultSet rs, String column) throws SQLException {
        return rs.getTimestamp(column).toInstant();
    }

    private Instant instantOrNull(ResultSet rs, String column) throws SQLException {
        var timestamp = rs.getTimestamp(column);
        return timestamp == null ? null : timestamp.toInstant();
    }

    private Instant instantObject(Object value) {
        return value == null ? null : ((Timestamp) value).toInstant();
    }

    private Map<String, Object> rowFrom(ResultSet rs) throws SQLException {
        var row = new java.util.HashMap<String, Object>();
        var meta = rs.getMetaData();
        for (var i = 1; i <= meta.getColumnCount(); i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
        }
        return row;
    }

    private record InstitutionalSupportRow(String code, int support, boolean governing) {
    }

    private record MinistryRow(
            String code,
            String name,
            String description,
            String controlledByFactionCode,
            String bonusResource,
            int bonusAmount,
            int requiredSupport,
            String effectLabel) {
    }

    private record RegionalRow(String code, String name, List<String> provinces, int stability, int seats) {
    }

    private record DisasterType(String code, String name, String description) {
    }
}
