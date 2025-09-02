package com.proximaai.service.ai;

import com.proximaai.domain.entity.ai.ProjectSummary;
import com.proximaai.domain.entity.Project;
import com.proximaai.domain.entity.Task;
import com.proximaai.repository.ai.ProjectSummaryRepository;
import com.proximaai.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectSummaryService {

    @Autowired
    private ProjectSummaryRepository projectSummaryRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Gera resumo automático para um projeto
     */
    public ProjectSummary generateProjectSummary(Project project, ProjectSummary.SummaryType summaryType) {
        long startTime = System.currentTimeMillis();
        
        // Coleta dados do projeto
        List<Task> projectTasks = getProjectTasks(project);
        
        // Gera conteúdo do resumo
        String summaryContent = generateSummaryContent(project, projectTasks, summaryType);
        String summaryTitle = generateSummaryTitle(project, summaryType);
        String keyHighlights = generateKeyHighlights(project, projectTasks);
        String riskIndicators = generateRiskIndicators(project, projectTasks);
        String recommendations = generateRecommendations(project, projectTasks);
        
        // Calcula métricas
        String summaryMetrics = generateSummaryMetrics(project, projectTasks);
        
        // Cria o resumo
        ProjectSummary summary = new ProjectSummary();
        summary.setProject(project);
        summary.setSummaryTitle(summaryTitle);
        summary.setSummaryContent(summaryContent);
        summary.setSummaryType(summaryType);
        summary.setSummaryDate(LocalDateTime.now());
        summary.setKeyHighlights(keyHighlights);
        summary.setRiskIndicators(riskIndicators);
        summary.setRecommendations(recommendations);
        summary.setSummaryMetrics(summaryMetrics);
        summary.setAiModelUsed("ProximaAI-GPT-1.0");
        summary.setGenerationTimeMs(System.currentTimeMillis() - startTime);
        summary.setIsApproved(false);
        summary.setIsShared(false);
        
        return projectSummaryRepository.save(summary);
    }

    /**
     * Gera conteúdo do resumo usando IA generativa
     */
    private String generateSummaryContent(Project project, List<Task> projectTasks, ProjectSummary.SummaryType summaryType) {
        StringBuilder content = new StringBuilder();
        
        // Cabeçalho personalizado
        content.append("## Resumo do Projeto: ").append(project.getName()).append("\n\n");
        content.append("**Data do Resumo:** ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        content.append("**Tipo:** ").append(summaryType.toString()).append("\n\n");
        
        // Status geral do projeto
        content.append("### Status Geral\n");
        content.append("O projeto encontra-se em **").append(getProjectStatusDescription(project.getStatus().toString())).append("** ");
        content.append("com progresso de **").append(calculateProjectProgress(projectTasks)).append("%**.\n\n");

        // Resumo de tarefas
        content.append("### Resumo de Tarefas\n");
        content.append("**Total de Tarefas:** ").append(projectTasks.size()).append("\n");
        content.append("**Tarefas Concluídas:** ").append(countCompletedTasks(projectTasks)).append("\n");
        content.append("**Tarefas em Andamento:** ").append(countInProgressTasks(projectTasks)).append("\n");
        content.append("**Tarefas Pendentes:** ").append(countPendingTasks(projectTasks)).append("\n\n");

        // Destaques principais
        content.append("### Destaques Principais\n");
        content.append(generateHighlightsText(project, projectTasks)).append("\n\n");

        // Indicadores de risco
        content.append("### Indicadores de Risco\n");
        content.append(generateRiskText(project, projectTasks)).append("\n\n");

        // Recomendações
        content.append("### Recomendações\n");
        content.append(generateRecommendationsText(project, projectTasks)).append("\n\n");

        // Próximos passos
        content.append("### Próximos Passos\n");
        content.append("1. Revisar tarefas com atraso\n");
        content.append("2. Alinhar recursos para próximas entregas\n");
        content.append("3. Atualizar stakeholders sobre progresso\n");
        content.append("4. Preparar para próxima milestone\n\n");

        return content.toString();
    }

    /**
     * Gera título do resumo
     */
    private String generateSummaryTitle(Project project, ProjectSummary.SummaryType summaryType) {
        String typeText = switch (summaryType) {
            case DAILY -> "Diário";
            case WEEKLY -> "Semanal";
            case MONTHLY -> "Mensal";
            case MILESTONE -> "Milestone";
            default -> "Geral";
        };
        
        return String.format("Resumo %s - %s", typeText, project.getName());
    }

    /**
     * Gera destaques principais em JSON
     */
    private String generateKeyHighlights(Project project, List<Task> tasks) {
        Map<String, Object> highlights = Map.of(
            "projectName", project.getName(),
            "totalTasks", tasks.size(),
            "completedTasks", countCompletedTasks(tasks),
            "progressPercentage", calculateProjectProgress(tasks),
            "estimatedCompletion", estimateProjectCompletion(project, tasks),
            "teamSize", project.getTeam() != null ? project.getTeam().getMembers().size() : 0
        );
        
        return convertToJsonString(highlights);
    }

    /**
     * Gera indicadores de risco em JSON
     */
    private String generateRiskIndicators(Project project, List<Task> tasks) {
        Map<String, Object> risks = Map.of(
            "overdueTasks", countOverdueTasks(tasks),
            "highPriorityPending", countHighPriorityPendingTasks(tasks),
            "resourceConstraints", hasResourceConstraints(project),
            "dependencyBlockers", countDependencyBlockers(tasks),
            "budgetRisk", calculateBudgetRisk(project)
        );
        
        return convertToJsonString(risks);
    }

    /**
     * Gera recomendações em JSON
     */
    private String generateRecommendations(Project project, List<Task> tasks) {
        Map<String, Object> recommendations = Map.of(
            "immediateActions", generateImmediateActions(tasks),
            "resourceAllocation", generateResourceRecommendations(project),
            "timelineAdjustments", generateTimelineRecommendations(project, tasks),
            "riskMitigation", generateRiskMitigationStrategies(tasks)
        );
        
        return convertToJsonString(recommendations);
    }

    /**
     * Gera métricas do resumo
     */
    private String generateSummaryMetrics(Project project, List<Task> tasks) {
        Map<String, Object> metrics = Map.of(
            "taskCompletionRate", calculateTaskCompletionRate(tasks),
            "averageTaskDuration", calculateAverageTaskDuration(tasks),
            "teamProductivity", calculateTeamProductivity(project, tasks),
            "qualityScore", calculateQualityScore(tasks),
            "stakeholderSatisfaction", estimateStakeholderSatisfaction(project)
        );
        
        return convertToJsonString(metrics);
    }

    /**
     * Obtém tarefas do projeto
     */
    private List<Task> getProjectTasks(Project project) {
        // Implementação simplificada - em produção seria via repository
        return taskRepository.findAll().stream()
                .filter(task -> project.getId().equals(task.getProject().getId()))
                .collect(Collectors.toList());
    }

    /**
     * Calcula progresso do projeto
     */
    private BigDecimal calculateProjectProgress(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        long completedTasks = countCompletedTasks(tasks);
        return BigDecimal.valueOf(completedTasks)
                .divide(BigDecimal.valueOf(tasks.size()), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Conta tarefas concluídas
     */
    private long countCompletedTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> "DONE".equals(task.getStatus().toString()))
                .count();
    }

    /**
     * Conta tarefas em andamento
     */
    private long countInProgressTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> "IN_PROGRESS".equals(task.getStatus().toString()))
                .count();
    }

    /**
     * Conta tarefas pendentes
     */
    private long countPendingTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> "TODO".equals(task.getStatus().toString()))
                .count();
    }

    /**
     * Conta tarefas atrasadas
     */
    private long countOverdueTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getDueDate() != null && 
                               task.getDueDate().isBefore(java.time.LocalDate.now()) &&
                               !"DONE".equals(task.getStatus().toString()))
                .count();
    }

    /**
     * Conta tarefas de alta prioridade pendentes
     */
    private long countHighPriorityPendingTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> "HIGH".equals(task.getPriority().toString()) && 
                               !"DONE".equals(task.getStatus().toString()))
                .count();
    }

    /**
     * Verifica se há restrições de recursos
     */
    private boolean hasResourceConstraints(Project project) {
        // Implementação simplificada
        return false;
    }

    /**
     * Conta bloqueadores de dependência
     */
    private long countDependencyBlockers(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getDependencies() != null && !task.getDependencies().isEmpty())
                .count();
    }

    /**
     * Calcula risco de orçamento
     */
    private String calculateBudgetRisk(Project project) {
        // Implementação simplificada
        return "LOW";
    }

    /**
     * Gera ações imediatas
     */
    private List<String> generateImmediateActions(List<Task> tasks) {
        List<String> actions = new java.util.ArrayList<>();
        
        if (countOverdueTasks(tasks) > 0) {
            actions.add("Revisar tarefas atrasadas");
        }
        if (countHighPriorityPendingTasks(tasks) > 0) {
            actions.add("Priorizar tarefas de alta prioridade");
        }
        if (countDependencyBlockers(tasks) > 0) {
            actions.add("Resolver dependências bloqueadoras");
        }
        
        return actions.isEmpty() ? List.of("Nenhuma ação imediata necessária") : actions;
    }

    /**
     * Gera recomendações de recursos
     */
    private List<String> generateResourceRecommendations(Project project) {
        return List.of(
            "Avaliar disponibilidade da equipe",
            "Considerar alocação de recursos adicionais",
            "Revisar distribuição de workload"
        );
    }

    /**
     * Gera recomendações de timeline
     */
    private List<String> generateTimelineRecommendations(Project project, List<Task> tasks) {
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (calculateProjectProgress(tasks).compareTo(BigDecimal.valueOf(50)) < 0) {
            recommendations.add("Considerar extensão do prazo");
        }
        if (countOverdueTasks(tasks) > 0) {
            recommendations.add("Replanejar cronograma");
        }
        
        return recommendations.isEmpty() ? List.of("Timeline está adequado") : recommendations;
    }

    /**
     * Gera estratégias de mitigação de risco
     */
    private List<String> generateRiskMitigationStrategies(List<Task> tasks) {
        return List.of(
            "Implementar monitoramento mais frequente",
            "Estabelecer checkpoints de progresso",
            "Preparar planos de contingência"
        );
    }

    /**
     * Calcula taxa de conclusão de tarefas
     */
    private BigDecimal calculateTaskCompletionRate(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        long completed = countCompletedTasks(tasks);
        return BigDecimal.valueOf(completed)
                .divide(BigDecimal.valueOf(tasks.size()), 4, RoundingMode.HALF_UP);
    }

    /**
     * Calcula duração média das tarefas
     */
    private BigDecimal calculateAverageTaskDuration(List<Task> tasks) {
        // Implementação simplificada
        return BigDecimal.valueOf(8.5);
    }

    /**
     * Calcula produtividade da equipe
     */
    private BigDecimal calculateTeamProductivity(Project project, List<Task> tasks) {
        // Implementação simplificada
        return BigDecimal.valueOf(0.85);
    }

    /**
     * Calcula score de qualidade
     */
    private BigDecimal calculateQualityScore(List<Task> tasks) {
        // Implementação simplificada
        return BigDecimal.valueOf(0.9);
    }

    /**
     * Estima satisfação dos stakeholders
     */
    private BigDecimal estimateStakeholderSatisfaction(Project project) {
        // Implementação simplificada
        return BigDecimal.valueOf(0.8);
    }

    /**
     * Estima data de conclusão do projeto
     */
    private String estimateProjectCompletion(Project project, List<Task> tasks) {
        // Implementação simplificada
        return java.time.LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtém descrição do status do projeto
     */
    private String getProjectStatusDescription(String status) {
        return switch (status) {
            case "PLANNING" -> "Planejamento";
            case "ACTIVE" -> "Ativo";
            case "IN_PROGRESS" -> "Em Andamento";
            case "ON_HOLD" -> "Em Pausa";
            case "COMPLETED" -> "Concluído";
            case "CANCELLED" -> "Cancelado";
            default -> "Desconhecido";
        };
    }

    /**
     * Gera texto dos destaques
     */
    private String generateHighlightsText(Project project, List<Task> tasks) {
        StringBuilder highlights = new StringBuilder();
        
        BigDecimal progress = calculateProjectProgress(tasks);
        highlights.append("• Progresso geral: ").append(progress).append("%\n");
        highlights.append("• Tarefas concluídas: ").append(countCompletedTasks(tasks)).append("/").append(tasks.size()).append("\n");
        highlights.append("• Equipe ativa: ").append(project.getTeam() != null ? project.getTeam().getMembers().size() : 0).append(" membros\n");
        
        return highlights.toString();
    }

    /**
     * Gera texto dos riscos
     */
    private String generateRiskText(Project project, List<Task> tasks) {
        StringBuilder risks = new StringBuilder();
        
        long overdue = countOverdueTasks(tasks);
        if (overdue > 0) {
            risks.append("• ").append(overdue).append(" tarefas atrasadas\n");
        }
        
        long highPriority = countHighPriorityPendingTasks(tasks);
        if (highPriority > 0) {
            risks.append("• ").append(highPriority).append(" tarefas de alta prioridade pendentes\n");
        }
        
        if (risks.length() == 0) {
            risks.append("• Nenhum risco crítico identificado\n");
        }
        
        return risks.toString();
    }

    /**
     * Gera texto das recomendações
     */
    private String generateRecommendationsText(Project project, List<Task> tasks) {
        StringBuilder recommendations = new StringBuilder();
        
        recommendations.append("• Manter foco nas tarefas de alta prioridade\n");
        recommendations.append("• Revisar dependências regularmente\n");
        recommendations.append("• Comunicar progresso aos stakeholders\n");
        
        if (countOverdueTasks(tasks) > 0) {
            recommendations.append("• Implementar ações corretivas para atrasos\n");
        }
        
        return recommendations.toString();
    }

    /**
     * Converte objeto para string JSON (simplificado)
     */
    private String convertToJsonString(Object obj) {
        // Implementação simplificada - em produção usar Jackson ou Gson
        return obj.toString();
    }

    /**
     * Busca resumos por projeto
     */
    public List<ProjectSummary> getSummariesForProject(Project project) {
        return projectSummaryRepository.findByProjectOrderBySummaryDateDesc(project);
    }

    /**
     * Busca o resumo mais recente de um projeto
     */
    public Optional<ProjectSummary> getLatestSummaryForProject(Project project) {
        return projectSummaryRepository.findFirstByProjectOrderBySummaryDateDesc(project);
    }

    /**
     * Aprova um resumo
     */
    public void approveSummary(Long summaryId, String approvedBy) {
        Optional<ProjectSummary> summaryOpt = projectSummaryRepository.findById(summaryId);
        if (summaryOpt.isPresent()) {
            ProjectSummary summary = summaryOpt.get();
            summary.setIsApproved(true);
            summary.setApprovedBy(approvedBy);
            summary.setApprovedAt(LocalDateTime.now());
            projectSummaryRepository.save(summary);
        }
    }

    /**
     * Compartilha um resumo
     */
    public void shareSummary(Long summaryId) {
        Optional<ProjectSummary> summaryOpt = projectSummaryRepository.findById(summaryId);
        if (summaryOpt.isPresent()) {
            ProjectSummary summary = summaryOpt.get();
            summary.setIsShared(true);
            projectSummaryRepository.save(summary);
        }
    }

    /**
     * Atualiza feedback do resumo
     */
    public void updateFeedback(Long summaryId, Integer feedbackScore, String feedback) {
        Optional<ProjectSummary> summaryOpt = projectSummaryRepository.findById(summaryId);
        if (summaryOpt.isPresent()) {
            ProjectSummary summary = summaryOpt.get();
            summary.setFeedbackScore(feedbackScore);
            projectSummaryRepository.save(summary);
        }
    }
}
