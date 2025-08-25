package com.proximaai.domain.entity.ai;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.proximaai.domain.entity.Task;
import com.proximaai.domain.entity.User;

@Entity
@Table(name = "ai_allocation_recommendations")
@EntityListeners(AuditingEntityListener.class)
public class AllocationRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_user_id", nullable = false)
    private User recommendedUser;

    @Column(name = "recommendation_score", precision = 5, scale = 4)
    @Positive
    private BigDecimal recommendationScore; // 0.0000 a 1.0000

    @Column(name = "skill_match_percentage")
    @Positive
    private BigDecimal skillMatchPercentage; // % de match com skills necessários

    @Column(name = "availability_score")
    @Positive
    private BigDecimal availabilityScore; // Score de disponibilidade

    @Column(name = "workload_score")
    @Positive
    private BigDecimal workloadScore; // Score de carga de trabalho (menor = melhor)

    @Column(name = "experience_score")
    @Positive
    private BigDecimal experienceScore; // Score baseado em experiência similar

    @Column(name = "collaboration_score")
    @Positive
    private BigDecimal collaborationScore; // Score de colaboração com equipe

    @Column(name = "recommendation_reason")
    private String recommendationReason; // Explicação da recomendação

    @Column(name = "alternative_users")
    private String alternativeUsers; // JSON com usuários alternativos

    @Column(name = "estimated_completion_days")
    @Positive
    private Integer estimatedCompletionDays;

    @Column(name = "confidence_level")
    @Enumerated(EnumType.STRING)
    private ConfidenceLevel confidenceLevel = ConfidenceLevel.MEDIUM;

    @Column(name = "is_implemented")
    private Boolean isImplemented = false; // Se a recomendação foi seguida

    @Column(name = "actual_performance_score")
    private BigDecimal actualPerformanceScore; // Score real após implementação

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum ConfidenceLevel {
        LOW,        // Baixa confiança
        MEDIUM,     // Confiança moderada
        HIGH,       // Alta confiança
        VERY_HIGH   // Muito alta confiança
    }

    // Constructors
    public AllocationRecommendation() {}

    public AllocationRecommendation(Task task, User recommendedUser, BigDecimal recommendationScore) {
        this.task = task;
        this.recommendedUser = recommendedUser;
        this.recommendationScore = recommendationScore;
    }

    // Helper methods
    public void calculateOverallScore() {
        // Fórmula ponderada para score geral
        BigDecimal skillWeight = new BigDecimal("0.3");
        BigDecimal availabilityWeight = new BigDecimal("0.25");
        BigDecimal workloadWeight = new BigDecimal("0.2");
        BigDecimal experienceWeight = new BigDecimal("0.15");
        BigDecimal collaborationWeight = new BigDecimal("0.1");

        BigDecimal overallScore = BigDecimal.ZERO;
        
        if (skillMatchPercentage != null) {
            overallScore = overallScore.add(skillMatchPercentage.multiply(skillWeight));
        }
        if (availabilityScore != null) {
            overallScore = overallScore.add(availabilityScore.multiply(availabilityWeight));
        }
        if (workloadScore != null) {
            // Inverter workload score (menor carga = maior score)
            BigDecimal invertedWorkload = BigDecimal.ONE.subtract(workloadScore);
            overallScore = overallScore.add(invertedWorkload.multiply(workloadWeight));
        }
        if (experienceScore != null) {
            overallScore = overallScore.add(experienceScore.multiply(experienceWeight));
        }
        if (collaborationScore != null) {
            overallScore = overallScore.add(collaborationScore.multiply(collaborationWeight));
        }

        this.recommendationScore = overallScore;
    }

    public boolean isHighConfidence() {
        return this.confidenceLevel == ConfidenceLevel.HIGH || this.confidenceLevel == ConfidenceLevel.VERY_HIGH;
    }

    public boolean shouldBeConsidered() {
        return this.recommendationScore.compareTo(new BigDecimal("0.7")) > 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getRecommendedUser() {
        return recommendedUser;
    }

    public void setRecommendedUser(User recommendedUser) {
        this.recommendedUser = recommendedUser;
    }

    public BigDecimal getRecommendationScore() {
        return recommendationScore;
    }

    public void setRecommendationScore(BigDecimal recommendationScore) {
        this.recommendationScore = recommendationScore;
    }

    public BigDecimal getSkillMatchPercentage() {
        return skillMatchPercentage;
    }

    public void setSkillMatchPercentage(BigDecimal skillMatchPercentage) {
        this.skillMatchPercentage = skillMatchPercentage;
    }

    public BigDecimal getAvailabilityScore() {
        return availabilityScore;
    }

    public void setAvailabilityScore(BigDecimal availabilityScore) {
        this.availabilityScore = availabilityScore;
    }

    public BigDecimal getWorkloadScore() {
        return workloadScore;
    }

    public void setWorkloadScore(BigDecimal workloadScore) {
        this.workloadScore = workloadScore;
    }

    public BigDecimal getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(BigDecimal experienceScore) {
        this.experienceScore = experienceScore;
    }

    public BigDecimal getCollaborationScore() {
        return collaborationScore;
    }

    public void setCollaborationScore(BigDecimal collaborationScore) {
        this.collaborationScore = collaborationScore;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public String getAlternativeUsers() {
        return alternativeUsers;
    }

    public void setAlternativeUsers(String alternativeUsers) {
        this.alternativeUsers = alternativeUsers;
    }

    public Integer getEstimatedCompletionDays() {
        return estimatedCompletionDays;
    }

    public void setEstimatedCompletionDays(Integer estimatedCompletionDays) {
        this.estimatedCompletionDays = estimatedCompletionDays;
    }

    public ConfidenceLevel getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(ConfidenceLevel confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public Boolean getIsImplemented() {
        return isImplemented;
    }

    public void setIsImplemented(Boolean isImplemented) {
        this.isImplemented = isImplemented;
    }

    public BigDecimal getActualPerformanceScore() {
        return actualPerformanceScore;
    }

    public void setActualPerformanceScore(BigDecimal actualPerformanceScore) {
        this.actualPerformanceScore = actualPerformanceScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
