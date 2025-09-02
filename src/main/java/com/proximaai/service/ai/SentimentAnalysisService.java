package com.proximaai.service.ai;

import com.proximaai.domain.entity.ai.SentimentAnalysis;
import com.proximaai.domain.entity.Project;
import com.proximaai.domain.entity.Team;
import com.proximaai.domain.entity.Task;
import com.proximaai.repository.ai.SentimentAnalysisRepository;
import com.proximaai.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SentimentAnalysisService {

    // Constants for sentiment analysis
    private static final BigDecimal EXCELLENT_MOOD_THRESHOLD = BigDecimal.valueOf(0.6);
    private static final BigDecimal GOOD_MOOD_THRESHOLD = BigDecimal.valueOf(0.3);
    private static final BigDecimal NEUTRAL_MOOD_THRESHOLD = BigDecimal.ZERO;
    private static final BigDecimal CONCERNED_MOOD_THRESHOLD = BigDecimal.valueOf(-0.3);
    
    private static final double HIGH_STRESS_RATIO = 0.5;
    private static final double MODERATE_STRESS_RATIO = 0.2;
    
    private static final double HIGH_BURNOUT_RATIO = 0.6;
    private static final double MODERATE_BURNOUT_RATIO = 0.3;
    
    private static final int HIGH_CONFIDENCE_TASK_COUNT = 20;
    private static final int MODERATE_CONFIDENCE_TASK_COUNT = 10;
    
    private static final BigDecimal CRITICAL_SENTIMENT_THRESHOLD = BigDecimal.valueOf(-0.8);
    private static final BigDecimal WARNING_SENTIMENT_THRESHOLD = BigDecimal.valueOf(-0.5);

    @Autowired
    private SentimentAnalysisRepository sentimentAnalysisRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Gera análise de sentimento para uma equipe em um projeto
     */
    public SentimentAnalysis analyzeTeamSentiment(Project project, Team team) {
        long startTime = System.currentTimeMillis();
        
        // Coleta dados para análise
        List<Task> teamTasks = getTeamTasks(project, team);
        
        // Calcula scores de sentimento
        BigDecimal sentimentScore = calculateSentimentScore(teamTasks);
        SentimentAnalysis.TeamMood overallMood = determineOverallMood(sentimentScore);
        SentimentAnalysis.StressLevel stressLevel = calculateStressLevel(teamTasks);
        SentimentAnalysis.BurnoutRisk burnoutRisk = assessBurnoutRisk(teamTasks);
        SentimentAnalysis.TrendDirection trendDirection = determineTrendDirection(project, team);
        SentimentAnalysis.ConfidenceLevel confidenceLevel = calculateConfidenceLevel(teamTasks);
        
        // Gera insights
        String keyConcerns = identifyKeyConcerns(teamTasks);
        String positiveFactors = identifyPositiveFactors(teamTasks);
        String recommendations = generateRecommendations(sentimentScore, stressLevel, burnoutRisk);
        
        // Determina se deve gerar alerta
        boolean isAlertTriggered = shouldTriggerAlert(sentimentScore, stressLevel, burnoutRisk);
        SentimentAnalysis.AlertSeverity alertSeverity = determineAlertSeverity(sentimentScore, stressLevel, burnoutRisk);
        
        // Calcula métricas adicionais
        BigDecimal satisfactionScore = calculateSatisfactionScore(teamTasks);
        BigDecimal collaborationScore = calculateCollaborationScore(teamTasks);
        BigDecimal motivationScore = calculateMotivationScore(teamTasks);
        
        // Cria a análise
        SentimentAnalysis analysis = new SentimentAnalysis();
        analysis.setProject(project);
        analysis.setTeam(team);
        analysis.setAnalysisDate(LocalDateTime.now());
        analysis.setSentimentScore(sentimentScore);
        analysis.setOverallMood(overallMood);
        analysis.setStressLevel(stressLevel);
        analysis.setBurnoutRisk(burnoutRisk);
        analysis.setTrendDirection(trendDirection);
        analysis.setConfidenceLevel(confidenceLevel);
        analysis.setKeyConcerns(keyConcerns);
        analysis.setPositiveFactors(positiveFactors);
        analysis.setRecommendations(recommendations);
        analysis.setSatisfactionScore(satisfactionScore);
        analysis.setCollaborationScore(collaborationScore);
        analysis.setMotivationScore(motivationScore);
        analysis.setIsAlertTriggered(isAlertTriggered);
        analysis.setAlertSeverity(alertSeverity);
        analysis.setAiModelUsed("ProximaAI-BERT-1.0");
        analysis.setAnalysisDurationMs(System.currentTimeMillis() - startTime);
        
        return sentimentAnalysisRepository.save(analysis);
    }

    /**
     * Calcula score de sentimento geral (-1.0 a 1.0)
     */
    private BigDecimal calculateSentimentScore(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalScore = BigDecimal.ZERO;
        int validTasks = 0;
        
        for (Task task : tasks) {
            BigDecimal taskScore = calculateTaskSentimentScore(task);
            if (taskScore != null) {
                totalScore = totalScore.add(taskScore);
                validTasks++;
            }
        }
        
        if (validTasks == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalScore.divide(BigDecimal.valueOf(validTasks), 4, RoundingMode.HALF_UP);
    }

    /**
     * Calcula score de sentimento para uma tarefa específica
     */
    private BigDecimal calculateTaskSentimentScore(Task task) {
        BigDecimal score = BigDecimal.ZERO;
        
        // Score baseado no status da tarefa
        if (task.getStatus() != null) {
            switch (task.getStatus().toString()) {
                case "DONE" -> score = score.add(BigDecimal.valueOf(0.3));
                case "IN_PROGRESS" -> score = score.add(BigDecimal.valueOf(0.1));
                case "IN_REVIEW" -> score = score.add(BigDecimal.valueOf(0.0));
                case "TESTING" -> score = score.add(BigDecimal.valueOf(0.1));
                case "TODO" -> score = score.add(BigDecimal.valueOf(-0.1));
                case "CANCELLED" -> score = score.add(BigDecimal.valueOf(-0.3));
            }
        }
        
        // Score baseado na prioridade
        if (task.getPriority() != null) {
            switch (task.getPriority().toString()) {
                case "LOW" -> score = score.add(BigDecimal.valueOf(0.1));
                case "MEDIUM" -> score = score.add(BigDecimal.valueOf(0.0));
                case "HIGH" -> score = score.add(BigDecimal.valueOf(-0.1));
                case "CRITICAL" -> score = score.add(BigDecimal.valueOf(-0.2));
                case "URGENT" -> score = score.add(BigDecimal.valueOf(-0.3));
            }
        }
        
        // Score baseado em atrasos
        if (task.isOverdue()) {
            score = score.subtract(BigDecimal.valueOf(0.2));
        }
        
        // Score baseado em estimativas vs. tempo real
        if (task.getEstimatedHours() != null && task.getActualHours() != null) {
            BigDecimal accuracy = calculateTimeAccuracy(task.getEstimatedHours(), task.getActualHours());
            score = score.add(accuracy.multiply(BigDecimal.valueOf(0.1)));
        }
        
        return score.max(BigDecimal.valueOf(-1.0)).min(BigDecimal.valueOf(1.0));
    }

    /**
     * Calcula precisão da estimativa de tempo
     */
    private BigDecimal calculateTimeAccuracy(Integer estimated, Integer actual) {
        if (estimated == null || actual == null || estimated == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal difference = BigDecimal.valueOf(Math.abs(estimated - actual));
        BigDecimal maxTime = BigDecimal.valueOf(Math.max(estimated, actual));
        
        return BigDecimal.ONE.subtract(difference.divide(maxTime, 4, RoundingMode.HALF_UP));
    }

    /**
     * Determina humor geral da equipe
     */
    private SentimentAnalysis.TeamMood determineOverallMood(BigDecimal sentimentScore) {
        if (sentimentScore.compareTo(EXCELLENT_MOOD_THRESHOLD) >= 0) {
            return SentimentAnalysis.TeamMood.EXCELLENT;
        } else if (sentimentScore.compareTo(GOOD_MOOD_THRESHOLD) >= 0) {
            return SentimentAnalysis.TeamMood.GOOD;
        } else if (sentimentScore.compareTo(NEUTRAL_MOOD_THRESHOLD) >= 0) {
            return SentimentAnalysis.TeamMood.NEUTRAL;
        } else if (sentimentScore.compareTo(CONCERNED_MOOD_THRESHOLD) >= 0) {
            return SentimentAnalysis.TeamMood.CONCERNED;
        } else {
            return SentimentAnalysis.TeamMood.STRESSED;
        }
    }

    /**
     * Calcula nível de estresse da equipe
     */
    private SentimentAnalysis.StressLevel calculateStressLevel(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return SentimentAnalysis.StressLevel.LOW;
        }
        
        long overdueTasks = countOverdueTasks(tasks);
        long highPriorityTasks = countHighPriorityTasks(tasks);
        long totalTasks = tasks.size();
        
        double stressRatio = (overdueTasks + highPriorityTasks) / (double) totalTasks;
        
        if (stressRatio >= HIGH_STRESS_RATIO) {
            return SentimentAnalysis.StressLevel.HIGH;
        } else if (stressRatio >= MODERATE_STRESS_RATIO) {
            return SentimentAnalysis.StressLevel.MODERATE;
        } else {
            return SentimentAnalysis.StressLevel.LOW;
        }
    }

    /**
     * Avalia risco de burnout
     */
    private SentimentAnalysis.BurnoutRisk assessBurnoutRisk(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return SentimentAnalysis.BurnoutRisk.LOW;
        }
        
        long overdueTasks = countOverdueTasks(tasks);
        long highPriorityTasks = countHighPriorityTasks(tasks);
        long totalTasks = tasks.size();
        
        double burnoutRatio = (overdueTasks * 2 + highPriorityTasks) / (double) totalTasks;
        
        if (burnoutRatio >= HIGH_BURNOUT_RATIO) {
            return SentimentAnalysis.BurnoutRisk.HIGH;
        } else if (burnoutRatio >= MODERATE_BURNOUT_RATIO) {
            return SentimentAnalysis.BurnoutRisk.MEDIUM;
        } else {
            return SentimentAnalysis.BurnoutRisk.LOW;
        }
    }

    /**
     * Determina direção da tendência
     */
    private SentimentAnalysis.TrendDirection determineTrendDirection(Project project, Team team) {
        // Implementação simplificada - em produção seria baseada em histórico
        return SentimentAnalysis.TrendDirection.STABLE;
    }

    /**
     * Calcula nível de confiança da análise
     */
    private SentimentAnalysis.ConfidenceLevel calculateConfidenceLevel(List<Task> tasks) {
        if (tasks.size() >= HIGH_CONFIDENCE_TASK_COUNT) {
            return SentimentAnalysis.ConfidenceLevel.HIGH;
        } else if (tasks.size() >= MODERATE_CONFIDENCE_TASK_COUNT) {
            return SentimentAnalysis.ConfidenceLevel.MEDIUM;
        } else {
            return SentimentAnalysis.ConfidenceLevel.LOW;
        }
    }

    /**
     * Identifica principais preocupações
     */
    private String identifyKeyConcerns(List<Task> tasks) {
        List<String> concerns = new java.util.ArrayList<>();
        
        long overdueTasks = countOverdueTasks(tasks);
        if (overdueTasks > 0) {
            concerns.add("Tarefas atrasadas: " + overdueTasks);
        }
        
        long highPriorityTasks = countHighPriorityTasks(tasks);
        if (highPriorityTasks > 0) {
            concerns.add("Tarefas de alta prioridade: " + highPriorityTasks);
        }
        
        if (concerns.isEmpty()) {
            concerns.add("Nenhuma preocupação crítica identificada");
        }
        
        return String.join("; ", concerns);
    }

    /**
     * Identifica fatores positivos
     */
    private String identifyPositiveFactors(List<Task> tasks) {
        List<String> factors = new java.util.ArrayList<>();
        
        long completedTasks = countCompletedTasks(tasks);
        if (completedTasks > 0) {
            factors.add("Tarefas concluídas: " + completedTasks);
        }
        
        long onTimeTasks = countOnTimeTasks(tasks);
        if (onTimeTasks > 0) {
            factors.add("Tarefas no prazo: " + onTimeTasks);
        }
        
        if (factors.isEmpty()) {
            factors.add("Progresso estável");
        }
        
        return String.join("; ", factors);
    }

    /**
     * Gera recomendações baseadas na análise
     */
    private String generateRecommendations(BigDecimal sentimentScore, 
                                         SentimentAnalysis.StressLevel stressLevel,
                                         SentimentAnalysis.BurnoutRisk burnoutRisk) {
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (sentimentScore.compareTo(BigDecimal.valueOf(-0.3)) < 0) {
            recommendations.add("Implementar sessões de feedback mais frequentes");
            recommendations.add("Revisar distribuição de workload");
        }
        
        if (stressLevel == SentimentAnalysis.StressLevel.HIGH) {
            recommendations.add("Considerar pausas programadas");
            recommendations.add("Revisar prioridades das tarefas");
        }
        
        if (burnoutRisk == SentimentAnalysis.BurnoutRisk.HIGH) {
            recommendations.add("Implementar políticas de prevenção de burnout");
            recommendations.add("Considerar redução temporária de carga");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Manter práticas atuais");
            recommendations.add("Continuar monitoramento regular");
        }
        
        return String.join("; ", recommendations);
    }

    /**
     * Determina se deve gerar alerta
     */
    private boolean shouldTriggerAlert(BigDecimal sentimentScore, 
                                     SentimentAnalysis.StressLevel stressLevel,
                                     SentimentAnalysis.BurnoutRisk burnoutRisk) {
        return sentimentScore.compareTo(BigDecimal.valueOf(-0.5)) < 0 ||
               stressLevel == SentimentAnalysis.StressLevel.HIGH ||
               burnoutRisk == SentimentAnalysis.BurnoutRisk.HIGH;
    }

    /**
     * Determina severidade do alerta
     */
    private SentimentAnalysis.AlertSeverity determineAlertSeverity(BigDecimal sentimentScore,
                                                                 SentimentAnalysis.StressLevel stressLevel,
                                                                 SentimentAnalysis.BurnoutRisk burnoutRisk) {
        if (burnoutRisk == SentimentAnalysis.BurnoutRisk.HIGH ||
            sentimentScore.compareTo(CRITICAL_SENTIMENT_THRESHOLD) < 0) {
            return SentimentAnalysis.AlertSeverity.CRITICAL;
        } else if (stressLevel == SentimentAnalysis.StressLevel.HIGH ||
                   sentimentScore.compareTo(WARNING_SENTIMENT_THRESHOLD) < 0) {
            return SentimentAnalysis.AlertSeverity.WARNING;
        } else {
            return SentimentAnalysis.AlertSeverity.INFO;
        }
    }

    /**
     * Calcula score de satisfação
     */
    private BigDecimal calculateSatisfactionScore(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        long completedTasks = countCompletedTasks(tasks);
        long totalTasks = tasks.size();
        
        return BigDecimal.valueOf(completedTasks)
                .divide(BigDecimal.valueOf(totalTasks), 4, RoundingMode.HALF_UP);
    }

    /**
     * Calcula score de colaboração
     */
    private BigDecimal calculateCollaborationScore(List<Task> tasks) {
        // Implementação simplificada - em produção seria baseada em interações
        return BigDecimal.valueOf(0.8);
    }

    /**
     * Calcula score de motivação
     */
    private BigDecimal calculateMotivationScore(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sentimentScore = calculateSentimentScore(tasks);
        return sentimentScore.add(BigDecimal.ONE).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);
    }

    /**
     * Obtém tarefas da equipe no projeto
     */
    private List<Task> getTeamTasks(Project project, Team team) {
        // Implementação simplificada - em produção seria via repository
        return taskRepository.findAll().stream()
                .filter(task -> project.getId().equals(task.getProject().getId()))
                .limit(50) // Limita para demonstração
                .collect(Collectors.toList());
    }

    /**
     * Conta tarefas atrasadas
     */
    private long countOverdueTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(Task::isOverdue)
                .count();
    }

    /**
     * Conta tarefas de alta prioridade
     */
    private long countHighPriorityTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> "HIGH".equals(task.getPriority().toString()) || 
                               "CRITICAL".equals(task.getPriority().toString()) ||
                               "URGENT".equals(task.getPriority().toString()))
                .count();
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
     * Conta tarefas no prazo
     */
    private long countOnTimeTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> !task.isOverdue() && "DONE".equals(task.getStatus().toString()))
                .count();
    }

    /**
     * Busca análises por projeto
     */
    public List<SentimentAnalysis> getAnalysesForProject(Project project) {
        return sentimentAnalysisRepository.findByProjectOrderByAnalysisDateDesc(project);
    }

    /**
     * Busca análises por equipe
     */
    public List<SentimentAnalysis> getAnalysesForTeam(Team team) {
        return sentimentAnalysisRepository.findByTeamOrderByAnalysisDateDesc(team);
    }

    /**
     * Busca a análise mais recente de um projeto
     */
    public Optional<SentimentAnalysis> getLatestAnalysisForProject(Project project) {
        return sentimentAnalysisRepository.findFirstByProjectOrderByAnalysisDateDesc(project);
    }

    /**
     * Busca análises que precisam de alertas
     */
    public List<SentimentAnalysis> getAnalysesNeedingAlerts() {
        return sentimentAnalysisRepository.findByIsAlertTriggeredTrue();
    }

    /**
     * Marca alerta como processado
     */
    public void markAlertProcessed(Long analysisId) {
        Optional<SentimentAnalysis> analysisOpt = sentimentAnalysisRepository.findById(analysisId);
        if (analysisOpt.isPresent()) {
            SentimentAnalysis analysis = analysisOpt.get();
            analysis.setIsAlertTriggered(false);
            sentimentAnalysisRepository.save(analysis);
        }
    }

    /**
     * Calcula tendência de sentimento ao longo do tempo
     */
    public BigDecimal calculateSentimentTrend(Project project, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<SentimentAnalysis> analyses = sentimentAnalysisRepository
                .findByProjectAndPeriod(project, since, LocalDateTime.now());
        
        if (analyses.size() < 2) {
            return BigDecimal.ZERO;
        }
        
        // Calcula diferença entre primeira e última análise
        SentimentAnalysis first = analyses.get(analyses.size() - 1);
        SentimentAnalysis last = analyses.get(0);
        
        return last.getSentimentScore().subtract(first.getSentimentScore());
    }
}
