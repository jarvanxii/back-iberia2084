package com.iberia2084.api;

import com.iberia2084.api.GameDtos.ActionDto;
import com.iberia2084.api.GameDtos.AllianceDto;
import com.iberia2084.api.GameDtos.AllianceMessageDto;
import com.iberia2084.api.GameDtos.AuthResponse;
import com.iberia2084.api.GameDtos.CreateAllianceRequest;
import com.iberia2084.api.GameDtos.DisasterRequest;
import com.iberia2084.api.GameDtos.DeployTroopsRequest;
import com.iberia2084.api.GameDtos.ExchangeRequest;
import com.iberia2084.api.GameDtos.FactionDto;
import com.iberia2084.api.GameDtos.GameStateDto;
import com.iberia2084.api.GameDtos.JoinAllianceRequest;
import com.iberia2084.api.GameDtos.JoinWorldRequest;
import com.iberia2084.api.GameDtos.LoginRequest;
import com.iberia2084.api.GameDtos.MessageRequest;
import com.iberia2084.api.GameDtos.OnboardingRequest;
import com.iberia2084.api.GameDtos.ResearchDto;
import com.iberia2084.api.GameDtos.ResearchRequest;
import com.iberia2084.api.GameDtos.SchemeRequest;
import com.iberia2084.api.GameDtos.SignupRequest;
import com.iberia2084.api.GameDtos.TargetRequest;
import com.iberia2084.api.GameDtos.TrainTroopsRequest;
import com.iberia2084.api.GameDtos.UpgradeBuildingRequest;
import com.iberia2084.api.GameDtos.WorldDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/auth/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        return gameService.signup(request);
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return gameService.login(request);
    }

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap() {
        return Map.of(
                "worlds", gameService.worlds(),
                "factions", gameService.factions());
    }

    @GetMapping("/worlds")
    public List<WorldDto> worlds() {
        return gameService.worlds();
    }

    @PostMapping("/worlds/join")
    public GameStateDto joinWorld(HttpServletRequest request, @Valid @RequestBody JoinWorldRequest world) {
        return gameService.joinWorld(token(request), world);
    }

    @GetMapping("/factions")
    public List<FactionDto> factions() {
        return gameService.factions();
    }

    @GetMapping("/game/state")
    public GameStateDto state(HttpServletRequest request) {
        return gameService.state(token(request), worldCode(request));
    }

    @PostMapping("/game/collect")
    public GameStateDto collect(HttpServletRequest request) {
        return gameService.collect(token(request), worldCode(request));
    }

    @PostMapping("/game/onboarding")
    public GameStateDto onboarding(HttpServletRequest request, @Valid @RequestBody OnboardingRequest onboarding) {
        return gameService.completeOnboarding(token(request), onboarding, worldCode(request));
    }

    @PostMapping("/game/actions/conquer")
    public ActionDto conquer(HttpServletRequest request, @RequestBody TargetRequest target) {
        return gameService.startConquest(token(request), target.territoryId(), worldCode(request));
    }

    @PostMapping("/game/actions/influence")
    public ActionDto influence(HttpServletRequest request, @RequestBody TargetRequest target) {
        return gameService.startInfluence(token(request), target.territoryId(), worldCode(request));
    }

    @PostMapping("/game/actions/corruption")
    public ActionDto corruption(HttpServletRequest request, @Valid @RequestBody SchemeRequest scheme) {
        return gameService.startCorruption(token(request), scheme.schemeCode(), worldCode(request));
    }

    @PostMapping("/game/actions/disaster")
    public ActionDto disaster(HttpServletRequest request, @Valid @RequestBody DisasterRequest disaster) {
        return gameService.startDisasterPlan(token(request), disaster.eventId(), disaster.planCode(), worldCode(request));
    }

    @PostMapping("/game/research")
    public ResearchDto research(HttpServletRequest request, @Valid @RequestBody ResearchRequest research) {
        return gameService.startResearch(token(request), research.researchCode(), worldCode(request));
    }

    @PostMapping("/game/troops/train")
    public GameStateDto trainTroops(HttpServletRequest request, @Valid @RequestBody TrainTroopsRequest training) {
        return gameService.startTroopTraining(token(request), training.unitCode(), training.amount(), worldCode(request));
    }

    @PostMapping("/game/troops/deploy")
    public GameStateDto deployTroops(HttpServletRequest request, @Valid @RequestBody DeployTroopsRequest deployment) {
        return gameService.deployTroopsToCity(
                token(request),
                deployment.territoryId(),
                deployment.unitCode(),
                deployment.amount(),
                worldCode(request));
    }

    @PostMapping("/game/city/buildings/upgrade")
    public GameStateDto upgradeBuilding(HttpServletRequest request, @Valid @RequestBody UpgradeBuildingRequest building) {
        return gameService.startBuildingUpgrade(token(request), building.buildingCode(), worldCode(request));
    }

    @PostMapping("/game/resources/exchange")
    public GameStateDto exchange(HttpServletRequest request, @Valid @RequestBody ExchangeRequest exchange) {
        return gameService.exchangeResources(
                token(request),
                exchange.fromCode(),
                exchange.toCode(),
                exchange.amount(),
                worldCode(request));
    }

    @GetMapping("/alliances")
    public List<AllianceDto> alliances(HttpServletRequest request) {
        return gameService.alliances(token(request), worldCode(request));
    }

    @PostMapping("/alliances")
    public AllianceDto createAlliance(HttpServletRequest request, @Valid @RequestBody CreateAllianceRequest alliance) {
        return gameService.createAlliance(
                token(request),
                alliance.name(),
                alliance.code(),
                alliance.description(),
                worldCode(request));
    }

    @PostMapping("/alliances/join")
    public AllianceDto joinAlliance(HttpServletRequest request, @Valid @RequestBody JoinAllianceRequest alliance) {
        return gameService.joinAllianceByCode(token(request), alliance.code(), worldCode(request));
    }

    @PostMapping("/alliances/messages")
    public AllianceMessageDto message(HttpServletRequest request, @Valid @RequestBody MessageRequest message) {
        return gameService.sendAllianceMessage(token(request), message.body(), worldCode(request));
    }

    private String token(HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring("Bearer ".length()).trim();
    }

    private String worldCode(HttpServletRequest request) {
        var header = request.getHeader("X-World-Code");
        if (header != null && !header.isBlank()) {
            return header.trim();
        }
        var query = request.getParameter("worldCode");
        return query == null ? null : query.trim();
    }
}
