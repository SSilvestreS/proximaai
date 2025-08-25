package com.proximaai.repository.ai;

import com.proximaai.domain.entity.ai.ClusterTask;
import com.proximaai.domain.entity.ai.TaskCluster;
import com.proximaai.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClusterTaskRepository extends JpaRepository<ClusterTask, Long> {

    /**
     * Busca tarefas por cluster
     */
    List<ClusterTask> findByClusterOrderBySimilarityScoreDesc(TaskCluster cluster);

    /**
     * Busca clusters por tarefa
     */
    List<ClusterTask> findByTaskOrderBySimilarityScoreDesc(Task task);

    /**
     * Busca relacionamento específico tarefa-cluster
     */
    Optional<ClusterTask> findByClusterAndTask(TaskCluster cluster, Task task);

    /**
     * Busca tarefas com score de similaridade acima do threshold
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.similarityScore >= :minSimilarity")
    List<ClusterTask> findBySimilarityScoreAbove(@Param("minSimilarity") BigDecimal minSimilarity);

    /**
     * Busca tarefas com score de confiança acima do threshold
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.clusterConfidence >= :minConfidence")
    List<ClusterTask> findByClusterConfidenceAbove(@Param("minConfidence") BigDecimal minConfidence);

    /**
     * Busca tarefas por cluster e score de similaridade
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.cluster = :cluster AND ct.similarityScore >= :minSimilarity")
    List<ClusterTask> findByClusterAndSimilarityAbove(@Param("cluster") TaskCluster cluster, @Param("minSimilarity") BigDecimal minSimilarity);

    /**
     * Busca tarefas primárias (primary assignment)
     */
    List<ClusterTask> findByIsPrimaryAssignmentTrue();

    /**
     * Busca tarefas secundárias (não primary assignment)
     */
    List<ClusterTask> findByIsPrimaryAssignmentFalse();

    /**
     * Busca tarefas por rank do cluster
     */
    List<ClusterTask> findByClusterRank(Integer clusterRank);

    /**
     * Busca tarefas com rank acima do threshold
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.clusterRank <= :maxRank")
    List<ClusterTask> findByClusterRankBelow(@Param("maxRank") Integer maxRank);

    /**
     * Busca tarefas outliers
     */
    List<ClusterTask> findByIsOutlierTrue();

    /**
     * Busca tarefas não outliers
     */
    List<ClusterTask> findByIsOutlierFalse();

    /**
     * Busca tarefas com score de outlier acima do threshold
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.outlierScore >= :minOutlierScore")
    List<ClusterTask> findByOutlierScoreAbove(@Param("minOutlierScore") BigDecimal minOutlierScore);

    /**
     * Busca tarefas por período de atribuição
     */
    List<ClusterTask> findByAssignmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca tarefas por período de última atualização
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.lastUpdated BETWEEN :startDate AND :endDate")
    List<ClusterTask> findByLastUpdatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Busca tarefas por período de criação
     */
    List<ClusterTask> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca tarefas por cluster e período de atribuição
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.cluster = :cluster AND ct.assignmentDate BETWEEN :startDate AND :endDate")
    List<ClusterTask> findByClusterAndAssignmentPeriod(@Param("cluster") TaskCluster cluster, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Busca tarefas por tarefa e período de atribuição
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.task = :task AND ct.assignmentDate BETWEEN :startDate AND :endDate")
    List<ClusterTask> findByTaskAndAssignmentPeriod(@Param("task") Task task, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Busca tarefas com score de contribuição acima do threshold
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.clusterContributionScore >= :minContribution")
    List<ClusterTask> findByContributionScoreAbove(@Param("minContribution") BigDecimal minContribution);

    /**
     * Busca tarefas por distância ao centróide
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.distanceToCentroid <= :maxDistance")
    List<ClusterTask> findByDistanceToCentroidBelow(@Param("maxDistance") BigDecimal maxDistance);

    /**
     * Busca tarefas com features específicas
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.featureVector LIKE %:feature%")
    List<ClusterTask> findByFeatureContaining(@Param("feature") String feature);

    /**
     * Conta tarefas por cluster
     */
    @Query("SELECT ct.cluster, COUNT(ct) FROM ClusterTask ct GROUP BY ct.cluster")
    List<Object[]> countByCluster();

    /**
     * Conta tarefas por status de outlier
     */
    @Query("SELECT ct.isOutlier, COUNT(ct) FROM ClusterTask ct GROUP BY ct.isOutlier")
    List<Object[]> countByOutlierStatus();

    /**
     * Busca tarefas por cluster e status de outlier
     */
    List<ClusterTask> findByClusterAndIsOutlier(TaskCluster cluster, Boolean isOutlier);

    /**
     * Busca tarefas por cluster e rank ordenadas
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.cluster = :cluster ORDER BY ct.clusterRank ASC")
    List<ClusterTask> findByClusterOrderedByRank(@Param("cluster") TaskCluster cluster);

    /**
     * Busca tarefas por cluster e similaridade ordenadas
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.cluster = :cluster ORDER BY ct.similarityScore DESC")
    List<ClusterTask> findByClusterOrderedBySimilarity(@Param("cluster") TaskCluster cluster);

    /**
     * Busca tarefas por cluster e período de atribuição ordenadas
     */
    @Query("SELECT ct FROM ClusterTask ct WHERE ct.cluster = :cluster AND ct.assignmentDate >= :since ORDER BY ct.assignmentDate DESC")
    List<ClusterTask> findRecentByCluster(@Param("cluster") TaskCluster cluster, @Param("since") LocalDateTime since);
}
