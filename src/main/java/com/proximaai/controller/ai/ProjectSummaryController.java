package com.proximaai.controller.ai;

import com.proximaai.domain.entity.Project;
import com.proximaai.domain.entity.ai.ProjectSummary;
import com.proximaai.service.ai.ProjectSummaryService;
import com.proximaai.repository.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai/project-summaries")
@Tag(name = "Project Summary AI", description = "APIs para geração automática de resumos de projetos com IA")
public class ProjectSummaryController {

    @Autowired
    private ProjectSummaryService projectSummaryService;

    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping("/project/{projectId}")
    @Operation(summary = "Gerar resumo automático para um projeto", 
               description = "Usa IA generativa para criar resumos inteligentes de status do projeto")
    public ResponseEntity<ProjectSummary> generateProjectSummary(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId,
            @Parameter(description = "Tipo do resumo") @RequestParam(defaultValue = "WEEKLY") ProjectSummary.SummaryType summaryType) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ProjectSummary summary = projectSummaryService.generateProjectSummary(projectOpt.get(), summaryType);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Buscar resumos de um projeto", 
               description = "Retorna todos os resumos gerados para um projeto específico")
    public ResponseEntity<List<ProjectSummary>> getSummariesForProject(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<ProjectSummary> summaries = projectSummaryService.getSummariesForProject(projectOpt.get());
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/project/{projectId}/latest")
    @Operation(summary = "Buscar resumo mais recente de um projeto", 
               description = "Retorna o resumo mais recente gerado para um projeto")
    public ResponseEntity<ProjectSummary> getLatestSummaryForProject(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<ProjectSummary> summary = projectSummaryService.getLatestSummaryForProject(projectOpt.get());
        
        return summary.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}/type/{summaryType}")
    @Operation(summary = "Buscar resumos por tipo", 
               description = "Retorna resumos de um projeto filtrados por tipo (DAILY, WEEKLY, MONTHLY, MILESTONE)")
    public ResponseEntity<List<ProjectSummary>> getSummariesByType(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId,
            @Parameter(description = "Tipo do resumo") @PathVariable ProjectSummary.SummaryType summaryType) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<ProjectSummary> summaries = projectSummaryService.getSummariesForProject(projectOpt.get())
            .stream()
            .filter(s -> s.getSummaryType() == summaryType)
            .toList();
        
        return ResponseEntity.ok(summaries);
    }

    @PutMapping("/{summaryId}/approve")
    @Operation(summary = "Aprovar um resumo", 
               description = "Aprova um resumo gerado pela IA para compartilhamento")
    public ResponseEntity<Void> approveSummary(
            @Parameter(description = "ID do resumo") @PathVariable Long summaryId,
            @Parameter(description = "Usuário que aprovou") @RequestParam String approvedBy) {
        
        projectSummaryService.approveSummary(summaryId, approvedBy);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{summaryId}/share")
    @Operation(summary = "Compartilhar um resumo", 
               description = "Marca um resumo como compartilhado com stakeholders")
    public ResponseEntity<Void> shareSummary(
            @Parameter(description = "ID do resumo") @PathVariable Long summaryId) {
        
        projectSummaryService.shareSummary(summaryId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{summaryId}/feedback")
    @Operation(summary = "Atualizar feedback do resumo", 
               description = "Adiciona feedback sobre a qualidade do resumo gerado")
    public ResponseEntity<Void> updateFeedback(
            @Parameter(description = "ID do resumo") @PathVariable Long summaryId,
            @Parameter(description = "Score de feedback (1-5)") @RequestParam Integer feedbackScore,
            @Parameter(description = "Comentário do feedback") @RequestParam(required = false) String feedback) {
        
        projectSummaryService.updateFeedback(summaryId, feedbackScore, feedback);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/project/{projectId}/summary-stats")
    @Operation(summary = "Estatísticas de resumos do projeto", 
               description = "Retorna estatísticas sobre os resumos gerados para um projeto")
    public ResponseEntity<Map<String, Object>> getProjectSummaryStats(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<ProjectSummary> summaries = projectSummaryService.getSummariesForProject(projectOpt.get());
        
        if (summaries.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Nenhum resumo disponível"));
        }
        
        // Calcula estatísticas
        long totalSummaries = summaries.size();
        long approvedSummaries = summaries.stream()
            .filter(ProjectSummary::getIsApproved)
            .count();
        long sharedSummaries = summaries.stream()
            .filter(ProjectSummary::getIsShared)
            .count();
        
        double avgGenerationTime = summaries.stream()
            .mapToLong(ProjectSummary::getGenerationTimeMs)
            .average()
            .orElse(0.0);
        
        double avgFeedbackScore = summaries.stream()
            .filter(s -> s.getFeedbackScore() != null)
            .mapToInt(ProjectSummary::getFeedbackScore)
            .average()
            .orElse(0.0);
        
        return ResponseEntity.ok(Map.of(
            "totalSummaries", totalSummaries,
            "approvedSummaries", approvedSummaries,
            "sharedSummaries", sharedSummaries,
            "averageGenerationTimeMs", avgGenerationTime,
            "averageFeedbackScore", avgFeedbackScore,
            "latestSummaryDate", summaries.get(0).getSummaryDate() // Primeiro é o mais recente
        ));
    }

    @PostMapping("/project/{projectId}/generate-all-types")
    @Operation(summary = "Gerar resumos de todos os tipos", 
               description = "Gera resumos de todos os tipos disponíveis para um projeto")
    public ResponseEntity<Map<String, ProjectSummary>> generateAllSummaryTypes(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Project project = projectOpt.get();
        Map<String, ProjectSummary> summaries = Map.of(
            "daily", projectSummaryService.generateProjectSummary(project, ProjectSummary.SummaryType.DAILY),
            "weekly", projectSummaryService.generateProjectSummary(project, ProjectSummary.SummaryType.WEEKLY),
            "monthly", projectSummaryService.generateProjectSummary(project, ProjectSummary.SummaryType.MONTHLY),
            "milestone", projectSummaryService.generateProjectSummary(project, ProjectSummary.SummaryType.MILESTONE)
        );
        
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/{summaryId}/content")
    @Operation(summary = "Obter conteúdo do resumo", 
               description = "Retorna apenas o conteúdo textual do resumo")
    public ResponseEntity<Map<String, String>> getSummaryContent(
            @Parameter(description = "ID do resumo") @PathVariable Long summaryId) {
        
        // Implementação simplificada - em produção seria via service
        return ResponseEntity.ok(Map.of(
            "message", "Conteúdo do resumo seria retornado aqui",
            "summaryId", summaryId.toString()
        ));
    }

    @GetMapping("/project/{projectId}/trend-analysis")
    @Operation(summary = "Análise de tendências dos resumos", 
               description = "Analisa tendências ao longo do tempo baseado nos resumos gerados")
    public ResponseEntity<Map<String, Object>> getTrendAnalysis(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<ProjectSummary> summaries = projectSummaryService.getSummariesForProject(projectOpt.get());
        
        if (summaries.size() < 2) {
            return ResponseEntity.ok(Map.of("message", "Dados insuficientes para análise de tendências"));
        }
        
        // Análise simplificada de tendências
        return ResponseEntity.ok(Map.of(
            "totalSummaries", summaries.size(),
            "timeSpan", "Últimos " + summaries.size() + " resumos",
            "trend", "Análise de tendências seria implementada aqui",
            "recommendations", List.of(
                "Continuar monitoramento regular",
                "Ajustar frequência de resumos conforme necessário"
            )
        ));
    }
}
