package com.proximaai.domain.entity.ai;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.proximaai.domain.entity.Task;

@Entity
@Table(name = "ai_cluster_tasks")
@EntityListeners(AuditingEntityListener.class)
public class ClusterTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    private TaskCluster cluster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "similarity_score", precision = 5, scale = 4)
    @Positive
    private BigDecimal similarityScore; // Score de similaridade com o cluster

    @Column(name = "cluster_confidence")
    @Positive
    private BigDecimal clusterConfidence; // Confiança da classificação

    @Column(name = "assignment_date", nullable = false)
    private LocalDateTime assignmentDate;

    @Column(name = "is_primary_assignment")
    private Boolean isPrimaryAssignment = true; // Se é a atribuição principal

    @Column(name = "cluster_rank")
    private Integer clusterRank; // Ranking dentro do cluster

    @Column(name = "feature_vector")
    private String featureVector; // JSON com vetor de features da tarefa

    @Column(name = "distance_to_centroid")
    private BigDecimal distanceToCentroid; // Distância ao centroide do cluster

    @Column(name = "cluster_contribution_score")
    private BigDecimal clusterContributionScore; // Quanto a tarefa contribui para o cluster

    @Column(name = "is_outlier")
    private Boolean isOutlier = false; // Se é um outlier no cluster

    @Column(name = "outlier_score")
    private BigDecimal outlierScore; // Score de outlier (quanto maior, mais outlier)

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public ClusterTask() {}

    public ClusterTask(TaskCluster cluster, Task task, BigDecimal similarityScore) {
        this.cluster = cluster;
        this.task = task;
        this.similarityScore = similarityScore;
        this.assignmentDate = LocalDateTime.now();
    }

    // Helper methods
    public boolean isHighSimilarity() {
        return this.similarityScore != null && 
               this.similarityScore.compareTo(new BigDecimal("0.8")) > 0;
    }

    public boolean isLowConfidence() {
        return this.clusterConfidence != null && 
               this.clusterConfidence.compareTo(new BigDecimal("0.5")) < 0;
    }

    public boolean shouldBeReassigned() {
        return this.isOutlier || 
               (this.similarityScore != null && this.similarityScore.compareTo(new BigDecimal("0.3")) < 0);
    }

    public void updateSimilarity(BigDecimal newSimilarityScore) {
        this.similarityScore = newSimilarityScore;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getSimilarityLevel() {
        if (this.similarityScore == null) return "UNKNOWN";
        
        if (this.similarityScore.compareTo(new BigDecimal("0.9")) > 0) return "VERY_HIGH";
        if (this.similarityScore.compareTo(new BigDecimal("0.8")) > 0) return "HIGH";
        if (this.similarityScore.compareTo(new BigDecimal("0.6")) > 0) return "MEDIUM";
        if (this.similarityScore.compareTo(new BigDecimal("0.4")) > 0) return "LOW";
        return "VERY_LOW";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskCluster getCluster() {
        return cluster;
    }

    public void setCluster(TaskCluster cluster) {
        this.cluster = cluster;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public BigDecimal getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(BigDecimal similarityScore) {
        this.similarityScore = similarityScore;
    }

    public BigDecimal getClusterConfidence() {
        return clusterConfidence;
    }

    public void setClusterConfidence(BigDecimal clusterConfidence) {
        this.clusterConfidence = clusterConfidence;
    }

    public LocalDateTime getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Boolean getIsPrimaryAssignment() {
        return isPrimaryAssignment;
    }

    public void setIsPrimaryAssignment(Boolean isPrimaryAssignment) {
        this.isPrimaryAssignment = isPrimaryAssignment;
    }

    public Integer getClusterRank() {
        return clusterRank;
    }

    public void setClusterRank(Integer clusterRank) {
        this.clusterRank = clusterRank;
    }

    public String getFeatureVector() {
        return featureVector;
    }

    public void setFeatureVector(String featureVector) {
        this.featureVector = featureVector;
    }

    public BigDecimal getDistanceToCentroid() {
        return distanceToCentroid;
    }

    public void setDistanceToCentroid(BigDecimal distanceToCentroid) {
        this.distanceToCentroid = distanceToCentroid;
    }

    public BigDecimal getClusterContributionScore() {
        return clusterContributionScore;
    }

    public void setClusterContributionScore(BigDecimal clusterContributionScore) {
        this.clusterContributionScore = clusterContributionScore;
    }

    public Boolean getIsOutlier() {
        return isOutlier;
    }

    public void setIsOutlier(Boolean isOutlier) {
        this.isOutlier = isOutlier;
    }

    public BigDecimal getOutlierScore() {
        return outlierScore;
    }

    public void setOutlierScore(BigDecimal outlierScore) {
        this.outlierScore = outlierScore;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
