package com.proximaai.controller.ai;

import com.proximaai.domain.entity.Project;
import com.proximaai.domain.entity.Team;
import com.proximaai.domain.entity.ai.SentimentAnalysis;
import com.proximaai.service.ai.SentimentAnalysisService;
import com.proximaai.repository.ProjectRepository;
import com.proximaai.repository.TeamRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai/sentiment-analysis")
@Tag(name = "Sentiment Analysis AI", description = "APIs para análise de sentimento e bem-estar da equipe")
public class SentimentAnalysisController {

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PostMapping("/project/{projectId}/team/{teamId}")
    @Operation(summary = "Analisar sentimento da equipe em um projeto", 
               description = "Usa NLP para analisar o bem-estar e sentimento da equipe baseado em dados do projeto")
    public ResponseEntity<SentimentAnalysis> analyzeTeamSentiment(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId,
            @Parameter(description = "ID da equipe") @PathVariable Long teamId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        
        if (projectOpt.isEmpty() || teamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        SentimentAnalysis analysis = sentimentAnalysisService.analyzeTeamSentiment(projectOpt.get(), teamOpt.get());
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Buscar análises de sentimento por projeto", 
               description = "Retorna todas as análises de sentimento realizadas para um projeto")
    public ResponseEntity<List<SentimentAnalysis>> getAnalysesForProject(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<SentimentAnalysis> analyses = sentimentAnalysisService.getAnalysesForProject(projectOpt.get());
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Buscar análises de sentimento por equipe", 
               description = "Retorna todas as análises de sentimento realizadas para uma equipe")
    public ResponseEntity<List<SentimentAnalysis>> getAnalysesForTeam(
            @Parameter(description = "ID da equipe") @PathVariable Long teamId) {
        
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<SentimentAnalysis> analyses = sentimentAnalysisService.getAnalysesForTeam(teamOpt.get());
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/project/{projectId}/latest")
    @Operation(summary = "Buscar análise mais recente de um projeto", 
               description = "Retorna a análise de sentimento mais recente para um projeto")
    public ResponseEntity<SentimentAnalysis> getLatestAnalysisForProject(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<SentimentAnalysis> analysis = sentimentAnalysisService.getLatestAnalysisForProject(projectOpt.get());
        
        return analysis.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alerts")
    @Operation(summary = "Buscar análises que precisam de alertas", 
               description = "Retorna análises com indicadores críticos que precisam de atenção")
    public ResponseEntity<List<SentimentAnalysis>> getAnalysesNeedingAlerts() {
        List<SentimentAnalysis> analyses = sentimentAnalysisService.getAnalysesNeedingAlerts();
        return ResponseEntity.ok(analyses);
    }

    @PutMapping("/{analysisId}/alert-processed")
    @Operation(summary = "Marcar alerta como processado", 
               description = "Marca que o alerta de uma análise crítica foi processado")
    public ResponseEntity<Void> markAlertProcessed(
            @Parameter(description = "ID da análise") @PathVariable Long analysisId) {
        
        sentimentAnalysisService.markAlertProcessed(analysisId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/project/{projectId}/trend")
    @Operation(summary = "Calcular tendência de sentimento", 
               description = "Calcula a tendência de sentimento ao longo de um período específico")
    public ResponseEntity<Map<String, Object>> calculateSentimentTrend(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId,
            @Parameter(description = "Período em dias") @RequestParam(defaultValue = "30") int days) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        BigDecimal trend = sentimentAnalysisService.calculateSentimentTrend(projectOpt.get(), days);
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "periodDays", days,
            "sentimentTrend", trend,
            "trendDescription", getTrendDescription(trend)
        ));
    }

    @GetMapping("/project/{projectId}/sentiment-dashboard")
    @Operation(summary = "Dashboard de sentimento do projeto", 
               description = "Retorna dados consolidados para dashboard de sentimento")
    public ResponseEntity<Map<String, Object>> getSentimentDashboard(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<SentimentAnalysis> analyses = sentimentAnalysisService.getAnalysesForProject(projectOpt.get());
        
        if (analyses.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Nenhuma análise disponível"));
        }
        
        // Calcula métricas do dashboard
        SentimentAnalysis latest = analyses.get(0);
        BigDecimal avgSentiment = analyses.stream()
            .map(SentimentAnalysis::getSentimentScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(analyses.size()), 4, java.math.RoundingMode.HALF_UP);
        
        long alertCount = analyses.stream()
            .filter(SentimentAnalysis::getIsAlertTriggered)
            .count();
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "totalAnalyses", analyses.size(),
            "latestAnalysis", latest,
            "averageSentimentScore", avgSentiment,
            "currentMood", latest.getOverallMood(),
            "currentStressLevel", latest.getStressLevel(),
            "currentBurnoutRisk", latest.getBurnoutRisk(),
            "alertCount", alertCount,
            "trendDirection", latest.getTrendDirection(),
            "confidenceLevel", latest.getConfidenceLevel()
        ));
    }

    @GetMapping("/project/{projectId}/wellness-report")
    @Operation(summary = "Relatório de bem-estar da equipe", 
               description = "Gera relatório detalhado sobre o bem-estar e saúde mental da equipe")
    public ResponseEntity<Map<String, Object>> getWellnessReport(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<SentimentAnalysis> analyses = sentimentAnalysisService.getAnalysesForProject(projectOpt.get());
        
        if (analyses.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Dados insuficientes para relatório"));
        }
        
        SentimentAnalysis latest = analyses.get(0);
        
        Map<String, Object> response = new HashMap<>();
        response.put("projectId", projectId);
        response.put("reportDate", latest.getAnalysisDate());
        response.put("overallWellnessScore", calculateWellnessScore(latest));
        response.put("sentimentScore", latest.getSentimentScore());
        response.put("satisfactionScore", latest.getSatisfactionScore());
        response.put("collaborationScore", latest.getCollaborationScore());
        response.put("motivationScore", latest.getMotivationScore());
        response.put("stressLevel", latest.getStressLevel());
        response.put("burnoutRisk", latest.getBurnoutRisk());
        response.put("keyConcerns", latest.getKeyConcerns());
        response.put("positiveFactors", latest.getPositiveFactors());
        response.put("recommendations", latest.getRecommendations());
        response.put("alertStatus", latest.getIsAlertTriggered() ? "CRITICAL" : "NORMAL");
        response.put("nextAnalysisRecommended", "Em 7 dias");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}/stress-indicators")
    @Operation(summary = "Indicadores de estresse", 
               description = "Retorna indicadores específicos de estresse da equipe")
    public ResponseEntity<Map<String, Object>> getStressIndicators(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<SentimentAnalysis> analyses = sentimentAnalysisService.getAnalysesForProject(projectOpt.get());
        
        if (analyses.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Nenhuma análise disponível"));
        }
        
        SentimentAnalysis latest = analyses.get(0);
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "stressLevel", latest.getStressLevel(),
            "burnoutRisk", latest.getBurnoutRisk(),
            "sentimentScore", latest.getSentimentScore(),
            "isAlertTriggered", latest.getIsAlertTriggered(),
            "alertSeverity", latest.getAlertSeverity(),
            "recommendations", latest.getRecommendations(),
            "interventionNeeded", latest.getStressLevel() == SentimentAnalysis.StressLevel.HIGH ||
                                 latest.getBurnoutRisk() == SentimentAnalysis.BurnoutRisk.HIGH
        ));
    }

    @GetMapping("/project/{projectId}/team-health-score")
    @Operation(summary = "Score de saúde da equipe", 
               description = "Calcula um score geral de saúde e bem-estar da equipe")
    public ResponseEntity<Map<String, Object>> getTeamHealthScore(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<SentimentAnalysis> analyses = sentimentAnalysisService.getAnalysesForProject(projectOpt.get());
        
        if (analyses.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Dados insuficientes para cálculo"));
        }
        
        SentimentAnalysis latest = analyses.get(0);
        BigDecimal healthScore = calculateWellnessScore(latest);
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "teamHealthScore", healthScore,
            "healthLevel", getHealthLevel(healthScore),
            "components", Map.of(
                "sentiment", latest.getSentimentScore(),
                "satisfaction", latest.getSatisfactionScore(),
                "collaboration", latest.getCollaborationScore(),
                "motivation", latest.getMotivationScore()
            ),
            "riskFactors", Map.of(
                "stressLevel", latest.getStressLevel(),
                "burnoutRisk", latest.getBurnoutRisk()
            ),
            "recommendations", latest.getRecommendations()
        ));
    }

    private String getTrendDescription(BigDecimal trend) {
        if (trend.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            return "Sentimento em melhoria";
        } else if (trend.compareTo(BigDecimal.valueOf(-0.1)) < 0) {
            return "Sentimento em declínio";
        } else {
            return "Sentimento estável";
        }
    }

    private BigDecimal calculateWellnessScore(SentimentAnalysis analysis) {
        BigDecimal sentiment = analysis.getSentimentScore().add(BigDecimal.ONE).divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
        BigDecimal satisfaction = analysis.getSatisfactionScore();
        BigDecimal collaboration = analysis.getCollaborationScore();
        BigDecimal motivation = analysis.getMotivationScore();
        
        return sentiment.multiply(BigDecimal.valueOf(0.3))
                .add(satisfaction.multiply(BigDecimal.valueOf(0.3)))
                .add(collaboration.multiply(BigDecimal.valueOf(0.2)))
                .add(motivation.multiply(BigDecimal.valueOf(0.2)));
    }

    private String getHealthLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(0.8)) >= 0) {
            return "EXCELLENT";
        } else if (score.compareTo(BigDecimal.valueOf(0.6)) >= 0) {
            return "GOOD";
        } else if (score.compareTo(BigDecimal.valueOf(0.4)) >= 0) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }
}
