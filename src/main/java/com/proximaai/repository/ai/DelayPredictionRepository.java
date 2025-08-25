package com.proximaai.repository.ai;

import com.proximaai.domain.entity.ai.DelayPrediction;
import com.proximaai.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DelayPredictionRepository extends JpaRepository<DelayPrediction, Long> {

    /**
     * Busca predições por tarefa
     */
    List<DelayPrediction> findByTaskOrderByPredictionDateDesc(Task task);

    /**
     * Busca predições por nível de risco
     */
    List<DelayPrediction> findByRiskLevel(DelayPrediction.RiskLevel riskLevel);

    /**
     * Busca predições com score de confiança acima do threshold
     */
    @Query("SELECT dp FROM DelayPrediction dp WHERE dp.confidenceScore >= :minConfidence")
    List<DelayPrediction> findByConfidenceScoreAbove(@Param("minConfidence") Double minConfidence);

    /**
     * Busca predições recentes por projeto
     */
    @Query("SELECT dp FROM DelayPrediction dp WHERE dp.task.project.id = :projectId AND dp.predictionDate >= :since")
    List<DelayPrediction> findRecentByProject(@Param("projectId") Long projectId, @Param("since") LocalDateTime since);

    /**
     * Busca predições críticas (alto risco) não alertadas
     */
    @Query("SELECT dp FROM DelayPrediction dp WHERE dp.riskLevel = 'CRITICAL' AND dp.isAlertSent = false")
    List<DelayPrediction> findCriticalPredictionsWithoutAlert();

    /**
     * Busca a predição mais recente para uma tarefa
     */
    Optional<DelayPrediction> findFirstByTaskOrderByPredictionDateDesc(Task task);

    /**
     * Busca predições por modelo de IA
     */
    List<DelayPrediction> findByModelVersion(String modelVersion);

    /**
     * Conta predições por nível de risco
     */
    @Query("SELECT dp.riskLevel, COUNT(dp) FROM DelayPrediction dp GROUP BY dp.riskLevel")
    List<Object[]> countByRiskLevel();

    /**
     * Busca predições com atraso real para análise de precisão
     */
    @Query("SELECT dp FROM DelayPrediction dp WHERE dp.actualDelayDays IS NOT NULL")
    List<DelayPrediction> findPredictionsWithActualDelay();

    /**
     * Busca predições por período de criação
     */
    List<DelayPrediction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca predições por tarefa e período
     */
    @Query("SELECT dp FROM DelayPrediction dp WHERE dp.task = :task AND dp.predictionDate BETWEEN :startDate AND :endDate")
    List<DelayPrediction> findByTaskAndPeriod(@Param("task") Task task, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
