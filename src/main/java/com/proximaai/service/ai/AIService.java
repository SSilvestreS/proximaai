package com.proximaai.service.ai;

import com.proximaai.domain.entity.Task;
import com.proximaai.domain.entity.User;
import com.proximaai.domain.entity.Project;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AIService {

    /**
     * Estima a duração de uma tarefa baseada em dados históricos e descrição
     */
    Integer estimateTaskDuration(Task task, String description);

    /**
     * Calcula um score de prioridade para uma tarefa baseado em múltiplos fatores
     */
    Double calculateTaskPriorityScore(Task task);

    /**
     * Detecta sobrecarga de trabalho para um usuário
     */
    boolean detectUserOverload(User user);

    /**
     * Sugere prazos para tarefas baseado em capacidade da equipe
     */
    LocalDate suggestTaskDeadline(Task task, Project project);

    /**
     * Recomenda a melhor pessoa para uma tarefa baseado em skills e disponibilidade
     */
    User recommendAssignee(Task task, List<User> availableUsers);

    /**
     * Analisa riscos do projeto baseado em padrões históricos
     */
    Map<String, Double> analyzeProjectRisks(Project project);

    /**
     * Sugere melhorias para estimativas de tempo baseado em dados históricos
     */
    Map<String, Object> suggestTimeEstimateImprovements(Project project);

    /**
     * Identifica gargalos e dependências críticas no projeto
     */
    List<String> identifyProjectBottlenecks(Project project);

    /**
     * Gera recomendações de otimização para o projeto
     */
    List<String> generateProjectOptimizationRecommendations(Project project);

    /**
     * Analisa padrões de produtividade da equipe
     */
    Map<String, Object> analyzeTeamProductivityPatterns(Project project);

    /**
     * Sugere melhorias no processo de desenvolvimento
     */
    List<String> suggestProcessImprovements(Project project);

    /**
     * Prediz a data de conclusão do projeto baseado no progresso atual
     */
    LocalDate predictProjectCompletionDate(Project project);

    /**
     * Identifica tarefas que podem ser paralelizadas
     */
    List<Task> identifyParallelizableTasks(Project project);

    /**
     * Sugere reordenação de tarefas para otimizar o fluxo
     */
    List<Task> suggestOptimalTaskOrder(Project project);

    /**
     * Analisa a qualidade das estimativas passadas
     */
    Map<String, Object> analyzeEstimationAccuracy(Project project);

    /**
     * Gera insights sobre performance individual dos membros da equipe
     */
    Map<Long, Map<String, Object>> generateTeamMemberInsights(Project project);

    /**
     * Sugere melhorias na distribuição de trabalho
     */
    Map<String, Object> suggestWorkloadDistributionImprovements(Project project);

    /**
     * Identifica padrões de atraso e suas causas
     */
    Map<String, List<String>> identifyDelayPatterns(Project project);

    /**
     * Sugere estratégias para mitigar riscos identificados
     */
    Map<String, String> suggestRiskMitigationStrategies(Project project);

    /**
     * Analisa a eficiência do processo de revisão
     */
    Map<String, Object> analyzeReviewProcessEfficiency(Project project);

    /**
     * Gera recomendações para melhorar a comunicação da equipe
     */
    List<String> suggestCommunicationImprovements(Project project);
}
