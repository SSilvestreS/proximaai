package com.proximaai.repository.ai;

import com.proximaai.domain.entity.ai.ProjectSummary;
import com.proximaai.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectSummaryRepository extends JpaRepository<ProjectSummary, Long> {

    /**
     * Busca resumos por projeto
     */
    List<ProjectSummary> findByProjectOrderBySummaryDateDesc(Project project);

    /**
     * Busca resumos por tipo
     */
    List<ProjectSummary> findBySummaryType(ProjectSummary.SummaryType summaryType);

    /**
     * Busca resumos por projeto e tipo
     */
    List<ProjectSummary> findByProjectAndSummaryTypeOrderBySummaryDateDesc(Project project, ProjectSummary.SummaryType summaryType);

    /**
     * Busca resumos por período
     */
    List<ProjectSummary> findBySummaryDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca resumos por projeto e período
     */
    @Query("SELECT ps FROM ProjectSummary ps WHERE ps.project = :project AND ps.summaryDate BETWEEN :startDate AND :endDate")
    List<ProjectSummary> findByProjectAndPeriod(@Param("project") Project project, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Busca o resumo mais recente de um projeto
     */
    Optional<ProjectSummary> findFirstByProjectOrderBySummaryDateDesc(Project project);

    /**
     * Busca resumos por modelo de IA
     */
    List<ProjectSummary> findByAiModelUsed(String aiModelUsed);

    /**
     * Busca resumos aprovados
     */
    List<ProjectSummary> findByIsApprovedTrue();

    /**
     * Busca resumos não aprovados
     */
    List<ProjectSummary> findByIsApprovedFalse();

    /**
     * Busca resumos compartilhados
     */
    List<ProjectSummary> findByIsSharedTrue();

    /**
     * Busca resumos por feedback score
     */
    @Query("SELECT ps FROM ProjectSummary ps WHERE ps.feedbackScore >= :minScore")
    List<ProjectSummary> findByFeedbackScoreAbove(@Param("minScore") Integer minScore);

    /**
     * Busca resumos por período de criação
     */
    List<ProjectSummary> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca resumos por projeto e tipo de resumo
     */
    @Query("SELECT ps FROM ProjectSummary ps WHERE ps.project = :project AND ps.summaryType = :summaryType ORDER BY ps.summaryDate DESC")
    List<ProjectSummary> findByProjectAndSummaryTypeOrdered(@Param("project") Project project, @Param("summaryType") ProjectSummary.SummaryType summaryType);

    /**
     * Busca resumos com tempo de geração abaixo do threshold
     */
    @Query("SELECT ps FROM ProjectSummary ps WHERE ps.generationTimeMs <= :maxTime")
    List<ProjectSummary> findByGenerationTimeBelow(@Param("maxTime") Long maxTime);

    /**
     * Busca resumos por aprovador
     */
    List<ProjectSummary> findByApprovedBy(String approvedBy);

    /**
     * Busca resumos por período de aprovação
     */
    @Query("SELECT ps FROM ProjectSummary ps WHERE ps.approvedAt BETWEEN :startDate AND :endDate")
    List<ProjectSummary> findByApprovalPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Conta resumos por tipo
     */
    @Query("SELECT ps.summaryType, COUNT(ps) FROM ProjectSummary ps GROUP BY ps.summaryType")
    List<Object[]> countBySummaryType();

    /**
     * Busca resumos com métricas específicas
     */
    @Query("SELECT ps FROM ProjectSummary ps WHERE ps.summaryMetrics LIKE %:metric%")
    List<ProjectSummary> findByMetricContaining(@Param("metric") String metric);

    /**
     * Busca resumos por projeto e período de resumo
     */
    @Query("SELECT ps FROM ProjectSummary ps WHERE ps.project = :project AND ps.summaryDate >= :since ORDER BY ps.summaryDate DESC")
    List<ProjectSummary> findRecentByProject(@Param("project") Project project, @Param("since") LocalDateTime since);

    /**
     * Busca resumos por projeto e status de aprovação
     */
    List<ProjectSummary> findByProjectAndIsApprovedOrderBySummaryDateDesc(Project project, Boolean isApproved);
}
