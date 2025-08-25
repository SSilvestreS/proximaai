package com.proximaai.repository.ai;

import com.proximaai.domain.entity.ai.SentimentAnalysis;
import com.proximaai.domain.entity.Project;
import com.proximaai.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {

    /**
     * Busca análises por projeto
     */
    List<SentimentAnalysis> findByProjectOrderByAnalysisDateDesc(Project project);

    /**
     * Busca análises por equipe
     */
    List<SentimentAnalysis> findByTeamOrderByAnalysisDateDesc(Team team);

    /**
     * Busca análises por projeto e equipe
     */
    List<SentimentAnalysis> findByProjectAndTeamOrderByAnalysisDateDesc(Project project, Team team);

    /**
     * Busca análises por período
     */
    List<SentimentAnalysis> findByAnalysisDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca análises por humor geral
     */
    List<SentimentAnalysis> findByOverallMood(SentimentAnalysis.TeamMood overallMood);

    /**
     * Busca análises por nível de estresse
     */
    List<SentimentAnalysis> findByStressLevel(SentimentAnalysis.StressLevel stressLevel);

    /**
     * Busca análises por risco de burnout
     */
    List<SentimentAnalysis> findByBurnoutRisk(SentimentAnalysis.BurnoutRisk burnoutRisk);

    /**
     * Busca análises por direção da tendência
     */
    List<SentimentAnalysis> findByTrendDirection(SentimentAnalysis.TrendDirection trendDirection);

    /**
     * Busca análises por nível de confiança
     */
    List<SentimentAnalysis> findByConfidenceLevel(SentimentAnalysis.ConfidenceLevel confidenceLevel);

    /**
     * Busca análises com score de sentimento acima do threshold
     */
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.sentimentScore >= :minScore")
    List<SentimentAnalysis> findBySentimentScoreAbove(@Param("minScore") BigDecimal minScore);

    /**
     * Busca análises com score de sentimento abaixo do threshold
     */
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.sentimentScore <= :maxScore")
    List<SentimentAnalysis> findBySentimentScoreBelow(@Param("maxScore") BigDecimal maxScore);

    /**
     * Busca análises com satisfação acima do threshold
     */
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.satisfactionScore >= :minSatisfaction")
    List<SentimentAnalysis> findBySatisfactionScoreAbove(@Param("minSatisfaction") BigDecimal minSatisfaction);

    /**
     * Busca análises com alertas ativados
     */
    List<SentimentAnalysis> findByIsAlertTriggeredTrue();

    /**
     * Busca análises por severidade do alerta
     */
    List<SentimentAnalysis> findByAlertSeverity(SentimentAnalysis.AlertSeverity alertSeverity);

    /**
     * Busca análises por modelo de IA
     */
    List<SentimentAnalysis> findByAiModelUsed(String aiModelUsed);

    /**
     * Busca análises com tempo de processamento abaixo do threshold
     */
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.analysisDurationMs <= :maxDuration")
    List<SentimentAnalysis> findByAnalysisDurationBelow(@Param("maxDuration") Long maxDuration);

    /**
     * Busca análises por período de criação
     */
    List<SentimentAnalysis> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca a análise mais recente de um projeto
     */
    Optional<SentimentAnalysis> findFirstByProjectOrderByAnalysisDateDesc(Project project);

    /**
     * Busca a análise mais recente de uma equipe
     */
    Optional<SentimentAnalysis> findFirstByTeamOrderByAnalysisDateDesc(Team team);

    /**
     * Busca análises por projeto e período
     */
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.project = :project AND sa.analysisDate BETWEEN :startDate AND :endDate")
    List<SentimentAnalysis> findByProjectAndPeriod(@Param("project") Project project, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Busca análises por equipe e período
     */
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.team = :team AND sa.analysisDate BETWEEN :startDate AND :endDate")
    List<SentimentAnalysis> findByTeamAndPeriod(@Param("team") Team team, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Conta análises por humor geral
     */
    @Query("SELECT sa.overallMood, COUNT(sa) FROM SentimentAnalysis sa GROUP BY sa.overallMood")
    List<Object[]> countByOverallMood();

    /**
     * Conta análises por nível de estresse
     */
    @Query("SELECT sa.stressLevel, COUNT(sa) FROM SentimentAnalysis sa GROUP BY sa.stressLevel")
    List<Object[]> countByStressLevel();

    /**
     * Busca análises com preocupações específicas
     */
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.keyConcerns LIKE %:concern%")
    List<SentimentAnalysis> findByConcernContaining(@Param("concern") String concern);

    /**
     * Busca análises com fatores positivos específicos
     */
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.positiveFactors LIKE %:factor%")
    List<SentimentAnalysis> findByPositiveFactorContaining(@Param("factor") String factor);

    /**
     * Busca análises por projeto e humor
     */
    List<SentimentAnalysis> findByProjectAndOverallMoodOrderByAnalysisDateDesc(Project project, SentimentAnalysis.TeamMood overallMood);

    /**
     * Busca análises por equipe e nível de estresse
     */
    List<SentimentAnalysis> findByTeamAndStressLevelOrderByAnalysisDateDesc(Team team, SentimentAnalysis.StressLevel stressLevel);
}
