package com.proximaai.domain.entity.ai;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.proximaai.domain.entity.Project;
import com.proximaai.domain.entity.Team;

@Entity
@Table(name = "ai_sentiment_analysis")
@EntityListeners(AuditingEntityListener.class)
public class SentimentAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // Opcional - pode ser análise geral

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team; // Opcional - pode ser análise geral

    @Column(name = "analysis_date", nullable = false)
    private LocalDateTime analysisDate;

    @Column(name = "sentiment_score", precision = 5, scale = 4)
    @Positive
    private BigDecimal sentimentScore; // -1.0000 (muito negativo) a 1.0000 (muito positivo)

    @Column(name = "positive_percentage")
    @Positive
    private BigDecimal positivePercentage; // % de comentários positivos

    @Column(name = "neutral_percentage")
    @Positive
    private BigDecimal neutralPercentage; // % de comentários neutros

    @Column(name = "negative_percentage")
    @Positive
    private BigDecimal negativePercentage; // % de comentários negativos

    @Column(name = "overall_mood")
    @Enumerated(EnumType.STRING)
    private TeamMood overallMood = TeamMood.NEUTRAL;

    @Column(name = "stress_level")
    @Enumerated(EnumType.STRING)
    private StressLevel stressLevel = StressLevel.LOW;

    @Column(name = "satisfaction_score")
    @Positive
    private BigDecimal satisfactionScore; // 0.0000 a 1.0000

    @Column(name = "burnout_risk")
    @Enumerated(EnumType.STRING)
    private BurnoutRisk burnoutRisk = BurnoutRisk.LOW;

    @Column(name = "key_concerns")
    private String keyConcerns; // JSON com principais preocupações identificadas

    @Column(name = "positive_factors")
    private String positiveFactors; // JSON com fatores positivos

    @Column(name = "improvement_suggestions")
    private String improvementSuggestions; // JSON com sugestões de melhoria

    @Column(name = "trend_direction")
    @Enumerated(EnumType.STRING)
    private TrendDirection trendDirection = TrendDirection.STABLE;

    @Column(name = "confidence_level")
    @Enumerated(EnumType.STRING)
    private ConfidenceLevel confidenceLevel = ConfidenceLevel.MEDIUM;

    @Column(name = "data_sources")
    private String dataSources; // JSON com fontes de dados analisadas

    @Column(name = "sample_size")
    private Integer sampleSize; // Quantidade de comentários/feedback analisados

    @Column(name = "ai_model_used")
    private String aiModelUsed; // Modelo de IA utilizado

    @Column(name = "analysis_duration_ms")
    private Long analysisDurationMs; // Duração da análise em milissegundos

    @Column(name = "is_alert_triggered")
    private Boolean isAlertTriggered = false; // Se gerou alerta

    @Column(name = "alert_severity")
    @Enumerated(EnumType.STRING)
    private AlertSeverity alertSeverity = AlertSeverity.NONE;

    @Column(name = "collaboration_score")
    @Positive
    private BigDecimal collaborationScore; // 0.0000 a 1.0000

    @Column(name = "motivation_score")
    @Positive
    private BigDecimal motivationScore; // 0.0000 a 1.0000

    @Column(name = "recommendations")
    private String recommendations; // JSON com recomendações

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum TeamMood {
        EXCELLENT,  // Excelente
        GOOD,       // Bom
        NEUTRAL,    // Neutro
        CONCERNED,  // Preocupado
        STRESSED    // Estressado
    }

    public enum StressLevel {
        LOW,        // Baixo
        MODERATE,   // Moderado
        HIGH,       // Alto
        CRITICAL    // Crítico
    }

    public enum BurnoutRisk {
        LOW,        // Baixo risco
        MEDIUM,     // Risco moderado
        HIGH,       // Alto risco
        CRITICAL    // Risco crítico
    }

    public enum TrendDirection {
        IMPROVING,  // Melhorando
        STABLE,     // Estável
        DECLINING,  // Piorando
        VOLATILE    // Volátil
    }

    public enum ConfidenceLevel {
        LOW,        // Baixa confiança
        MEDIUM,     // Confiança moderada
        HIGH,       // Alta confiança
        VERY_HIGH   // Muito alta confiança
    }

    public enum AlertSeverity {
        NONE,       // Nenhum alerta
        INFO,       // Informativo
        WARNING,    // Aviso
        CRITICAL    // Crítico
    }

    // Constructors
    public SentimentAnalysis() {}

    public SentimentAnalysis(BigDecimal sentimentScore, BigDecimal positivePercentage, BigDecimal negativePercentage) {
        this.sentimentScore = sentimentScore;
        this.positivePercentage = positivePercentage;
        this.negativePercentage = negativePercentage;
        this.analysisDate = LocalDateTime.now();
    }

    // Helper methods
    public void calculateOverallMood() {
        if (this.sentimentScore != null) {
            if (this.sentimentScore.compareTo(new BigDecimal("0.6")) > 0) {
                this.overallMood = TeamMood.EXCELLENT;
            } else if (this.sentimentScore.compareTo(new BigDecimal("0.2")) > 0) {
                this.overallMood = TeamMood.GOOD;
            } else if (this.sentimentScore.compareTo(new BigDecimal("-0.2")) > 0) {
                this.overallMood = TeamMood.NEUTRAL;
            } else if (this.sentimentScore.compareTo(new BigDecimal("-0.6")) > 0) {
                this.overallMood = TeamMood.CONCERNED;
            } else {
                this.overallMood = TeamMood.STRESSED;
            }
        }
    }

    public void assessBurnoutRisk() {
        if (this.stressLevel == StressLevel.CRITICAL || 
            (this.sentimentScore != null && this.sentimentScore.compareTo(new BigDecimal("-0.8")) < 0)) {
            this.burnoutRisk = BurnoutRisk.CRITICAL;
        } else if (this.stressLevel == StressLevel.HIGH || 
                   (this.sentimentScore != null && this.sentimentScore.compareTo(new BigDecimal("-0.5")) < 0)) {
            this.burnoutRisk = BurnoutRisk.HIGH;
        } else if (this.stressLevel == StressLevel.MODERATE || 
                   (this.sentimentScore != null && this.sentimentScore.compareTo(new BigDecimal("-0.2")) < 0)) {
            this.burnoutRisk = BurnoutRisk.MEDIUM;
        } else {
            this.burnoutRisk = BurnoutRisk.LOW;
        }
    }

    public boolean needsImmediateAttention() {
        return this.burnoutRisk == BurnoutRisk.CRITICAL || 
               this.alertSeverity == AlertSeverity.CRITICAL ||
               (this.sentimentScore != null && this.sentimentScore.compareTo(new BigDecimal("-0.9")) < 0);
    }

    public String getMoodEmoji() {
        switch (this.overallMood) {
            case EXCELLENT: return "😄";
            case GOOD: return "🙂";
            case NEUTRAL: return "😐";
            case CONCERNED: return "😟";
            case STRESSED: return "😰";
            default: return "❓";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public LocalDateTime getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDateTime analysisDate) {
        this.analysisDate = analysisDate;
    }

    public BigDecimal getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(BigDecimal sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public BigDecimal getPositivePercentage() {
        return positivePercentage;
    }

    public void setPositivePercentage(BigDecimal positivePercentage) {
        this.positivePercentage = positivePercentage;
    }

    public BigDecimal getNeutralPercentage() {
        return neutralPercentage;
    }

    public void setNeutralPercentage(BigDecimal neutralPercentage) {
        this.neutralPercentage = neutralPercentage;
    }

    public BigDecimal getNegativePercentage() {
        return negativePercentage;
    }

    public void setNegativePercentage(BigDecimal negativePercentage) {
        this.negativePercentage = negativePercentage;
    }

    public TeamMood getOverallMood() {
        return overallMood;
    }

    public void setOverallMood(TeamMood overallMood) {
        this.overallMood = overallMood;
    }

    public StressLevel getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(StressLevel stressLevel) {
        this.stressLevel = stressLevel;
    }

    public BigDecimal getSatisfactionScore() {
        return satisfactionScore;
    }

    public void setSatisfactionScore(BigDecimal satisfactionScore) {
        this.satisfactionScore = satisfactionScore;
    }

    public BurnoutRisk getBurnoutRisk() {
        return burnoutRisk;
    }

    public void setBurnoutRisk(BurnoutRisk burnoutRisk) {
        this.burnoutRisk = burnoutRisk;
    }

    public String getKeyConcerns() {
        return keyConcerns;
    }

    public void setKeyConcerns(String keyConcerns) {
        this.keyConcerns = keyConcerns;
    }

    public String getPositiveFactors() {
        return positiveFactors;
    }

    public void setPositiveFactors(String positiveFactors) {
        this.positiveFactors = positiveFactors;
    }

    public String getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(String improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public TrendDirection getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(TrendDirection trendDirection) {
        this.trendDirection = trendDirection;
    }

    public ConfidenceLevel getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(ConfidenceLevel confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getDataSources() {
        return dataSources;
    }

    public void setDataSources(String dataSources) {
        this.dataSources = dataSources;
    }

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    public String getAiModelUsed() {
        return aiModelUsed;
    }

    public void setAiModelUsed(String aiModelUsed) {
        this.aiModelUsed = aiModelUsed;
    }

    public Long getAnalysisDurationMs() {
        return analysisDurationMs;
    }

    public void setAnalysisDurationMs(Long analysisDurationMs) {
        this.analysisDurationMs = analysisDurationMs;
    }

    public Boolean getIsAlertTriggered() {
        return isAlertTriggered;
    }

    public void setIsAlertTriggered(Boolean isAlertTriggered) {
        this.isAlertTriggered = isAlertTriggered;
    }

    public AlertSeverity getAlertSeverity() {
        return alertSeverity;
    }

    public void setAlertSeverity(AlertSeverity alertSeverity) {
        this.alertSeverity = alertSeverity;
    }

    public BigDecimal getCollaborationScore() {
        return collaborationScore;
    }

    public void setCollaborationScore(BigDecimal collaborationScore) {
        this.collaborationScore = collaborationScore;
    }

    public BigDecimal getMotivationScore() {
        return motivationScore;
    }

    public void setMotivationScore(BigDecimal motivationScore) {
        this.motivationScore = motivationScore;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
