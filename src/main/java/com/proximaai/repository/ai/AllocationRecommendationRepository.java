package com.proximaai.repository.ai;

import com.proximaai.domain.entity.ai.AllocationRecommendation;
import com.proximaai.domain.entity.Task;
import com.proximaai.domain.entity.User;
import com.proximaai.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationRecommendationRepository extends JpaRepository<AllocationRecommendation, Long> {

    /**
     * Busca recomendações por tarefa
     */
    List<AllocationRecommendation> findByTaskOrderByRecommendationScoreDesc(Task task);

    /**
     * Busca recomendações por usuário recomendado
     */
    List<AllocationRecommendation> findByRecommendedUserOrderByRecommendationScoreDesc(User user);

    /**
     * Busca recomendações por projeto
     */
    @Query("SELECT ar FROM AllocationRecommendation ar WHERE ar.task.project = :project")
    List<AllocationRecommendation> findByProject(@Param("project") Project project);

    /**
     * Busca recomendações com score acima do threshold
     */
    @Query("SELECT ar FROM AllocationRecommendation ar WHERE ar.recommendationScore >= :minScore")
    List<AllocationRecommendation> findByRecommendationScoreAbove(@Param("minScore") BigDecimal minScore);

    /**
     * Busca recomendações por nível de confiança
     */
    List<AllocationRecommendation> findByConfidenceLevel(AllocationRecommendation.ConfidenceLevel confidenceLevel);

    /**
     * Busca recomendações não implementadas
     */
    List<AllocationRecommendation> findByIsImplementedFalse();

    /**
     * Busca recomendações por período de criação
     */
    List<AllocationRecommendation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca a melhor recomendação para uma tarefa
     */
    Optional<AllocationRecommendation> findFirstByTaskOrderByRecommendationScoreDesc(Task task);

    /**
     * Busca recomendações por usuário e projeto
     */
    @Query("SELECT ar FROM AllocationRecommendation ar WHERE ar.recommendedUser = :user AND ar.task.project = :project")
    List<AllocationRecommendation> findByUserAndProject(@Param("user") User user, @Param("project") Project project);

    /**
     * Busca recomendações com match de skills acima do threshold
     */
    @Query("SELECT ar FROM AllocationRecommendation ar WHERE ar.skillMatchPercentage >= :minSkillMatch")
    List<AllocationRecommendation> findBySkillMatchAbove(@Param("minSkillMatch") BigDecimal minSkillMatch);

    /**
     * Busca recomendações por disponibilidade
     */
    @Query("SELECT ar FROM AllocationRecommendation ar WHERE ar.availabilityScore >= :minAvailability")
    List<AllocationRecommendation> findByAvailabilityAbove(@Param("minAvailability") BigDecimal minAvailability);

    /**
     * Busca recomendações por carga de trabalho
     */
    @Query("SELECT ar FROM AllocationRecommendation ar WHERE ar.workloadScore <= :maxWorkload")
    List<AllocationRecommendation> findByWorkloadBelow(@Param("maxWorkload") BigDecimal maxWorkload);

    /**
     * Busca recomendações implementadas com performance real
     */
    @Query("SELECT ar FROM AllocationRecommendation ar WHERE ar.isImplemented = true AND ar.actualPerformanceScore IS NOT NULL")
    List<AllocationRecommendation> findImplementedWithPerformance();

    /**
     * Conta recomendações por nível de confiança
     */
    @Query("SELECT ar.confidenceLevel, COUNT(ar) FROM AllocationRecommendation ar GROUP BY ar.confidenceLevel")
    List<Object[]> countByConfidenceLevel();

    /**
     * Busca recomendações por tarefa e usuário
     */
    Optional<AllocationRecommendation> findByTaskAndRecommendedUser(Task task, User user);

    /**
     * Busca recomendações alternativas para uma tarefa
     */
    @Query("SELECT ar FROM AllocationRecommendation ar WHERE ar.task = :task AND ar.recommendedUser != :excludeUser ORDER BY ar.recommendationScore DESC")
    List<AllocationRecommendation> findAlternativesForTask(@Param("task") Task task, @Param("excludeUser") User excludeUser);
}
