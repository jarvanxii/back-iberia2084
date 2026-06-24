package com.iberia2084.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class GameDtos {
    private GameDtos() {
    }

    public record SignupRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Size(min = 3, max = 90) String displayName,
            @NotBlank @Email @Size(max = 190) String email,
            @NotBlank
            @Size(min = 8, max = 120)
            @Pattern(regexp = "^(?=.*[0-9\\W_]).{8,}$",
                    message = "La contraseña debe tener al menos 8 caracteres e incluir un número o un símbolo.")
            String password) {
    }

    public record SignupConfirmRequest(
            @NotBlank @Email @Size(max = 190) String email,
            @NotBlank @Size(min = 6, max = 6) String code) {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record PasswordRecoveryStartRequest(@NotBlank @Email @Size(max = 190) String email) {
    }

    public record PasswordRecoveryConfirmRequest(
            @NotBlank @Size(max = 36) String resetId,
            @NotBlank @Email @Size(max = 190) String email,
            @NotBlank @Size(min = 24, max = 160) String token,
            @NotBlank
            @Size(min = 8, max = 120)
            @Pattern(regexp = "^(?=.*[0-9\\W_]).{8,}$",
                    message = "La contraseña debe tener al menos 8 caracteres e incluir un número o un símbolo.")
            String password) {
    }

    public record AuthMessageResponse(boolean ok, String message, String email, Instant expiresAt) {
    }

    public record AuthProviderDto(String id, String label, String description, boolean configured) {
    }

    public record AuthResponse(String token, UserDto user, PlayerDto player) {
    }

    public record UserDto(long id, String username, String displayName, String email) {
    }

    public record PlayerDto(
            long id,
            long worldId,
            String leaderName,
            FactionDto faction,
            AllianceDto alliance,
            int votes,
            int politicalCredit,
            int reputation,
            int mediaHeat,
            int actionPoints,
            String capitalCityName,
            boolean onboardingDone) {
    }

    public record WorldDto(
            long id,
            String code,
            String name,
            String description,
            int maxPlayers,
            int currentPlayers,
            int tickSeconds,
            String status,
            String difficultyCode,
            String difficultyName,
            int difficultyLevel,
            Instant opensAt,
            Instant closedAt,
            String winningAllianceName,
            int controlledTerritories,
            int totalTerritories,
            boolean joinable) {
    }

    public record FactionDto(
            long id,
            String code,
            String name,
            String shortName,
            String color,
            String motto,
            String satire) {
    }

    public record AllianceDto(
            long id,
            String name,
            String code,
            String description,
            String factionCode,
            String factionName,
            String factionColor) {
    }

    public record ResourceDto(
            String code,
            String name,
            String description,
            String icon,
            int amount,
            int productionPerMinute) {
    }

    public record TerritoryDto(
            long id,
            String code,
            String name,
            String region,
            int mapX,
            int mapY,
            Long ownerPlayerId,
            String ownerName,
            String ownerFactionCode,
            String ownerFactionShortName,
            String ownerFactionColor,
            int defense,
            int population,
            int baseVotes,
            String resourceFocus,
            String resourceName,
            String buildingName,
            String satire) {
    }

    public record ActionDto(
            long id,
            String actionType,
            Long targetTerritoryId,
            String schemeCode,
            String status,
            int riskPercent,
            int successPercent,
            Instant startedAt,
            Instant resolvesAt,
            Instant resolvedAt,
            String resultTitle,
            String resultBody) {
    }

    public record CorruptionSchemeDto(
            String code,
            String name,
            String description,
            int baseRiskPercent,
            int durationSeconds,
            String rewardLabel,
            String caughtLabel,
            List<ResourceCostDto> costs) {
    }

    public record DisasterPlanDto(
            String code,
            String name,
            String description,
            int baseSuccessPercent,
            int durationSeconds,
            String upside,
            String downside,
            List<ResourceCostDto> costs) {
    }

    public record EventDefinitionDto(
            String code,
            String name,
            String category,
            String description,
            String imageKey,
            int baseSeverity,
            int durationSeconds,
            String scopeLabel,
            String impactLabel,
            String responseLabel) {
    }

    public record ResourceCostDto(String code, int amount) {
    }

    public record WorldEventDto(
            long id,
            long territoryId,
            String territoryName,
            String eventType,
            String name,
            String description,
            int severity,
            String status,
            Instant spawnedAt,
            Instant expiresAt,
            String resultSummary) {
    }

    public record ResearchDto(
            long id,
            String code,
            String name,
            String description,
            int costPesetas,
            int costVotos,
            int costFavores,
            int durationSeconds,
            String effectType,
            int effectValue,
            String status,
            Instant finishesAt) {
    }

    public record ResearchDefinitionDto(
            String code,
            String name,
            String category,
            String description,
            String imageKey,
            String factionCode,
            String factionName,
            String factionShortName,
            String factionColor,
            int costPesetas,
            int costVotos,
            int costFavores,
            int durationSeconds,
            String effectType,
            int effectValue,
            String effectLabel) {
    }

    public record CityBuildingDto(
            long id,
            String code,
            String name,
            String category,
            String description,
            String imageKey,
            int mapX,
            int mapY,
            int width,
            int height,
            int level,
            int maxLevel,
            boolean upgrading,
            Instant upgradeStartedAt,
            Instant upgradeFinishesAt,
            List<ResourceCostDto> nextCosts,
            int nextDurationSeconds,
            List<String> effects) {
    }

    public record BuildingDefinitionDto(
            String code,
            String name,
            String category,
            String description,
            String imageKey,
            int mapX,
            int mapY,
            int width,
            int height,
            int maxLevel,
            List<ResourceCostDto> costs,
            int durationSeconds,
            List<String> effects) {
    }

    public record TroopDefinitionDto(
            String code,
            String name,
            String role,
            String description,
            String imageKey,
            String factionCode,
            String factionName,
            String factionShortName,
            String factionColor,
            int tier,
            int attack,
            String attackType,
            String attackTypeLabel,
            String transportType,
            String transportTypeLabel,
            int defenseBureaucratic,
            int defenseIncisive,
            int defenseMedia,
            int influence,
            int speed,
            int slots,
            int trainingSeconds,
            String unlockBuildingCode,
            int unlockBuildingLevel,
            List<ResourceCostDto> costs) {
    }

    public record PlayerTroopDto(
            String code,
            String name,
            String imageKey,
            String factionCode,
            String factionName,
            String factionShortName,
            String factionColor,
            int amount,
            int attack,
            String attackType,
            String attackTypeLabel,
            String transportType,
            String transportTypeLabel,
            int defenseBureaucratic,
            int defenseIncisive,
            int defenseMedia,
            int influence,
            int speed,
            int slots) {
    }

    public record CityDto(
            long id,
            String code,
            String name,
            String region,
            int mapX,
            int mapY,
            int defense,
            int population,
            int baseVotes,
            String resourceName,
            String buildingName,
            String satire,
            boolean capital) {
    }

    public record CityGarrisonDto(
            long territoryId,
            String territoryName,
            String unitCode,
            String unitName,
            String imageKey,
            String factionCode,
            String factionName,
            String factionShortName,
            String factionColor,
            int amount,
            int attack,
            String attackType,
            String attackTypeLabel,
            String transportType,
            String transportTypeLabel,
            int defenseBureaucratic,
            int defenseIncisive,
            int defenseMedia,
            int influence,
            int slots) {
    }

    public record TrainingQueueDto(
            long id,
            String unitCode,
            String unitName,
            String imageKey,
            int amount,
            Instant startedAt,
            Instant finishesAt,
            int totalSeconds) {
    }

    public record MinistryDto(
            String code,
            String name,
            String description,
            String controlledByFactionCode,
            String controlledByFactionName,
            String color,
            String bonusResource,
            int bonusAmount,
            int requiredSupport,
            boolean activeForPlayer,
            String effectLabel) {
    }

    public record RegionalGovernmentDto(
            String code,
            String name,
            List<String> provinces,
            int stability,
            int seats) {
    }

    public record AllianceMessageDto(long id, String author, String body, Instant createdAt) {
    }

    public record AllianceScoreDto(
            long id,
            String name,
            String code,
            String factionName,
            String factionColor,
            int members,
            int totalVotes,
            int territories,
            int troopPower,
            int score) {
    }

    public record GameStateDto(
            PlayerDto player,
            List<PlayerDto> players,
            List<WorldDto> worlds,
            List<FactionDto> factions,
            List<ResourceDto> resources,
            List<TerritoryDto> territories,
            List<ActionDto> actions,
            List<CorruptionSchemeDto> corruptionSchemes,
            List<DisasterPlanDto> disasterPlans,
            List<EventDefinitionDto> eventDefinitions,
            List<WorldEventDto> events,
            List<ResearchDefinitionDto> researchDefinitions,
            List<ResearchDto> research,
            List<CityDto> cities,
            List<BuildingDefinitionDto> buildingDefinitions,
            List<CityBuildingDto> cityBuildings,
            List<TroopDefinitionDto> troopDefinitions,
            List<PlayerTroopDto> troops,
            List<CityGarrisonDto> garrisons,
            List<TrainingQueueDto> trainingQueue,
            List<MinistryDto> ministries,
            List<RegionalGovernmentDto> regionalGovernments,
            List<AllianceScoreDto> allianceScores,
            List<AllianceMessageDto> allianceMessages) {
    }

    public record TargetRequest(long territoryId) {
    }

    public record SchemeRequest(@NotBlank String schemeCode) {
    }

    public record DisasterRequest(long eventId, @NotBlank String planCode) {
    }

    public record ResearchRequest(@NotBlank String researchCode) {
    }

    public record TrainTroopsRequest(@NotBlank String unitCode, int amount) {
    }

    public record UpgradeBuildingRequest(@NotBlank String buildingCode) {
    }

    public record ExchangeRequest(@NotBlank String fromCode, @NotBlank String toCode, int amount) {
    }

    public record JoinWorldRequest(
            @NotBlank String worldCode,
            @NotBlank String factionCode,
            @NotBlank @Size(min = 3, max = 90) String leaderName,
            @Size(max = 80) String provinceCode) {
    }

    public record OnboardingRequest(
            @Size(max = 100) String allianceName,
            @Size(max = 16) String allianceCode,
            @Size(max = 255) String allianceDescription,
            @Size(max = 16) String joinAllianceCode) {
    }

    public record DeployTroopsRequest(long territoryId, @NotBlank String unitCode, int amount) {
    }

    public record CreateAllianceRequest(
            @NotBlank @Size(min = 3, max = 100) String name,
            @NotBlank @Size(min = 2, max = 16) String code,
            @NotBlank @Size(min = 5, max = 255) String description) {
    }

    public record JoinAllianceRequest(@NotBlank String code) {
    }

    public record MessageRequest(@NotBlank @Size(min = 1, max = 500) String body) {
    }
}
