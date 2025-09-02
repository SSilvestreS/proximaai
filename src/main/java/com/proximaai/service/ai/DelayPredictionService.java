package com.proximaai.service.ai;

import com.proximaai.domain.entity.ai.DelayPrediction;
import com.proximaai.domain.entity.Task;
import com.proximaai.repository.ai.DelayPredictionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DelayPredictionService {

    @Autowired
    private DelayPredictionRepository delayPredictionRepository;



    /**
     * Gera predição de atraso para uma tarefa específica
     */
    public DelayPrediction predictDelayForTask(Task task) {
        // Análise de features da tarefa
        BigDecimal complexityScore = calculateComplexityScore(task);
        BigDecimal teamExperienceScore = calculateTeamExperienceScore(task);
        BigDecimal dependencyRiskScore = calculateDependencyRiskScore(task);
        BigDecimal resourceAvailabilityScore = calculateResourceAvailabilityScore(task);

        // Algoritmo de ML simplificado (regressão ponderada)
        Integer predictedDelayDays = calculatePredictedDelay(
            complexityScore, 
            teamExperienceScore, 
            dependencyRiskScore, 
            resourceAvailabilityScore
        );

        // Score de confiança baseado na qualidade dos dados
        BigDecimal confidenceScore = calculateConfidenceScore(task);

        // Criação da predição
        DelayPrediction prediction = new DelayPrediction(task, predictedDelayDays, confidenceScore);
        
        // Salvamento da predição
        return delayPredictionRepository.save(prediction);
    }

    /**
     * Calcula score de complexidade da tarefa
     */
    private BigDecimal calculateComplexityScore(Task task) {
        BigDecimal score = BigDecimal.ZERO;
        
        // Pontuação por estimativa de tempo
        if (task.getEstimatedHours() != null) {
            if (task.getEstimatedHours() <= 8) {
                score = score.add(BigDecimal.valueOf(0.2));
            } else if (task.getEstimatedHours() <= 24) {
                score = score.add(BigDecimal.valueOf(0.5));
            } else if (task.getEstimatedHours() <= 40) {
                score = score.add(BigDecimal.valueOf(0.8));
            } else {
                score = score.add(BigDecimal.valueOf(1.0));
            }
        }

        // Pontuação por prioridade
        if (task.getPriority() != null) {
            switch (task.getPriority()) {
                case HIGH -> score = score.add(BigDecimal.valueOf(0.3));
                case MEDIUM -> score = score.add(BigDecimal.valueOf(0.1));
                case LOW -> score = score.add(BigDecimal.valueOf(0.0));
                default -> score = score.add(BigDecimal.valueOf(0.1));
            }
        }

        return score.min(BigDecimal.ONE);
    }

    /**
     * Calcula score de experiência da equipe
     */
    private BigDecimal calculateTeamExperienceScore(Task task) {
        // Implementação simplificada - em produção seria baseada em histórico
        return BigDecimal.valueOf(0.7); // Score médio padrão
    }

    /**
     * Calcula score de disponibilidade de recursos
     */
    private BigDecimal calculateResourceAvailabilityScore(Task task) {
        // Implementação simplificada - em produção seria baseada em alocação real
        return BigDecimal.valueOf(0.8); // Boa disponibilidade padrão
    }

    /**
     * Calcula dias de atraso previstos usando algoritmo de ML
     */
    private Integer calculatePredictedDelay(
            BigDecimal complexityScore,
            BigDecimal teamExperienceScore,
            BigDecimal dependencyRiskScore,
            BigDecimal resourceAvailabilityScore) {
        
        // Fórmula de regressão ponderada
        BigDecimal weightedScore = complexityScore.multiply(BigDecimal.valueOf(0.4))
                .add(teamExperienceScore.multiply(BigDecimal.valueOf(0.2)))
                .add(dependencyRiskScore.multiply(BigDecimal.valueOf(0.25)))
                .add(resourceAvailabilityScore.multiply(BigDecimal.valueOf(0.15)));

        // Mapeamento para dias de atraso
        if (weightedScore.compareTo(BigDecimal.valueOf(0.3)) <= 0) {
            return 0; // Sem atraso
        } else if (weightedScore.compareTo(BigDecimal.valueOf(0.5)) <= 0) {
            return 1; // 1 dia de atraso
        } else if (weightedScore.compareTo(BigDecimal.valueOf(0.7)) <= 0) {
            return 3; // 3 dias de atraso
        } else if (weightedScore.compareTo(BigDecimal.valueOf(0.85)) <= 0) {
            return 5; // 5 dias de atraso
        } else {
            return 7; // 7+ dias de atraso
        }
    }

    /**
     * Calcula score de confiança da predição
     */
    private BigDecimal calculateConfidenceScore(Task task) {
        BigDecimal confidence = BigDecimal.valueOf(0.8); // Base de confiança

        // Ajusta confiança baseado na qualidade dos dados
        if (task.getEstimatedHours() != null) {
            confidence = confidence.add(BigDecimal.valueOf(0.1));
        }
        if (task.getPriority() != null) {
            confidence = confidence.add(BigDecimal.valueOf(0.05));
        }
        // Verifica se a tarefa tem dependências (simulado)
        if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
            confidence = confidence.add(BigDecimal.valueOf(0.05));
        }

        return confidence.min(BigDecimal.ONE);
    }

    /**
     * Calcula score de risco de dependências
     */
    private BigDecimal calculateDependencyRiskScore(Task task) {
        // Verifica se a tarefa tem dependências
        if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
            return BigDecimal.valueOf(0.6); // Risco médio por dependências
        }
        return BigDecimal.valueOf(0.2); // Baixo risco sem dependências
    }

    /**
     * Busca predições por tarefa
     */
    public List<DelayPrediction> getPredictionsForTask(Task task) {
        return delayPredictionRepository.findByTaskOrderByPredictionDateDesc(task);
    }

    /**
     * Busca predições críticas (alto risco)
     */
    public List<DelayPrediction> getCriticalPredictions() {
        return delayPredictionRepository.findByRiskLevel(DelayPrediction.RiskLevel.CRITICAL);
    }

    /**
     * Busca predições com alta confiança
     */
    public List<DelayPrediction> getHighConfidencePredictions() {
        return delayPredictionRepository.findByConfidenceScoreAbove(0.8);
    }

    /**
     * Atualiza precisão da predição com dados reais
     */
    public void updatePredictionAccuracy(Long predictionId, Integer actualDelayDays) {
        Optional<DelayPrediction> predictionOpt = delayPredictionRepository.findById(predictionId);
        if (predictionOpt.isPresent()) {
            DelayPrediction prediction = predictionOpt.get();
            prediction.updateAccuracy(actualDelayDays);
            delayPredictionRepository.save(prediction);
        }
    }

    /**
     * Gera alertas para predições críticas
     */
    public List<DelayPrediction> getPredictionsNeedingAlerts() {
        return delayPredictionRepository.findCriticalPredictionsWithoutAlert();
    }

    /**
     * Marca alerta como enviado
     */
    public void markAlertSent(Long predictionId) {
        Optional<DelayPrediction> predictionOpt = delayPredictionRepository.findById(predictionId);
        if (predictionOpt.isPresent()) {
            DelayPrediction prediction = predictionOpt.get();
            prediction.markAlertSent();
            delayPredictionRepository.save(prediction);
        }
    }

    /**
     * Calcula métricas de precisão das predições
     */
    public BigDecimal calculatePredictionAccuracy() {
        List<DelayPrediction> predictionsWithActual = 
            delayPredictionRepository.findPredictionsWithActualDelay();
        
        if (predictionsWithActual.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalAccuracy = predictionsWithActual.stream()
                .map(DelayPrediction::getPredictionAccuracy)
                .filter(acc -> acc != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalAccuracy.divide(
            BigDecimal.valueOf(predictionsWithActual.size()), 
            4, 
            RoundingMode.HALF_UP
        );
    }

    /**
     * Recalcula predições para tarefas específicas
     */
    public void recalculatePredictionsForTask(Task task) {
        // Remove predições antigas
        List<DelayPrediction> oldPredictions = delayPredictionRepository.findByTaskOrderByPredictionDateDesc(task);
        if (!oldPredictions.isEmpty()) {
            delayPredictionRepository.deleteAll(oldPredictions);
        }

        // Gera nova predição
        predictDelayForTask(task);
    }

    /**
     * Busca predições por projeto
     */
    public List<DelayPrediction> getPredictionsForProject(Long projectId) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        return delayPredictionRepository.findRecentByProject(projectId, since);
    }

    /**
     * Verifica se uma tarefa precisa de nova predição
     */
    public boolean needsNewPrediction(Task task) {
        Optional<DelayPrediction> lastPrediction = 
            delayPredictionRepository.findFirstByTaskOrderByPredictionDateDesc(task);
        
        if (lastPrediction.isEmpty()) {
            return true;
        }

        DelayPrediction prediction = lastPrediction.get();
        long daysSincePrediction = ChronoUnit.DAYS.between(
            prediction.getPredictionDate(), 
            LocalDateTime.now()
        );

        // Nova predição a cada 7 dias ou se a tarefa foi modificada
        return daysSincePrediction >= 7 || task.getUpdatedAt() != null;
    }
}
