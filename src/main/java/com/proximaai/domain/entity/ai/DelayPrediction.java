package com.proximaai.domain.entity.ai;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.proximaai.domain.entity.Task;

@Entity
@Table(name = "ai_delay_predictions")
@EntityListeners(AuditingEntityListener.class)
public class DelayPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @NotNull
    private Task task;

    @Column(name = "predicted_delay_days")
    @Positive
    private Integer predictedDelayDays;

    @Column(name = "confidence_score", precision = 5, scale = 4)
    @Positive
    private BigDecimal confidenceScore; // 0.0000 a 1.0000

    @Column(name = "prediction_date", nullable = false)
    @NotNull
    private LocalDateTime predictionDate;

    @Column(name = "actual_delay_days")
    private Integer actualDelayDays; // Preenchido quando a tarefa é concluída

    @Column(name = "model_version")
    private String modelVersion; // Versão do modelo de ML usado

    @Column(name = "features_used")
    private String featuresUsed; // JSON com features utilizadas na predição

    @Column(name = "prediction_accuracy")
    private BigDecimal predictionAccuracy; // Acurácia após validação

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    @NotNull
    private RiskLevel riskLevel = RiskLevel.LOW;

    @Column(name = "mitigation_suggestions")
    private String mitigationSuggestions; // Sugestões para evitar atrasos

    @Column(name = "is_alert_sent")
    private Boolean isAlertSent = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum RiskLevel {
        LOW,        // Baixo risco de atraso
        MEDIUM,     // Risco moderado
        HIGH,       // Alto risco
        CRITICAL    // Risco crítico
    }

    // Constructors
    public DelayPrediction() {}

    public DelayPrediction(Task task, Integer predictedDelayDays, BigDecimal confidenceScore) {
        this.task = task;
        this.predictedDelayDays = predictedDelayDays;
        this.confidenceScore = confidenceScore;
        this.predictionDate = LocalDateTime.now();
        this.riskLevel = calculateRiskLevel(predictedDelayDays, confidenceScore);
    }

    // Helper methods
    public void updateAccuracy(Integer actualDelay) {
        this.actualDelayDays = actualDelay;
        if (this.predictedDelayDays != null && actualDelay != null) {
            int difference = Math.abs(this.predictedDelayDays - actualDelay);
            this.predictionAccuracy = BigDecimal.ONE.subtract(
                BigDecimal.valueOf(difference).divide(
                    BigDecimal.valueOf(Math.max(this.predictedDelayDays, actualDelay)), 
                    4, 
                    RoundingMode.HALF_UP
                )
            );
        }
    }

    public boolean isHighRisk() {
        return this.riskLevel == RiskLevel.HIGH || this.riskLevel == RiskLevel.CRITICAL;
    }

    public boolean needsImmediateAttention() {
        return this.riskLevel == RiskLevel.CRITICAL && 
               this.confidenceScore.compareTo(new BigDecimal("0.8")) > 0;
    }

    public boolean isPredictionAccurate() {
        return this.predictionAccuracy != null && 
               this.predictionAccuracy.compareTo(new BigDecimal("0.8")) > 0;
    }

    public void markAlertSent() {
        this.isAlertSent = true;
    }

    public void recalculateRiskLevel() {
        this.riskLevel = calculateRiskLevel(this.predictedDelayDays, this.confidenceScore);
    }

    private RiskLevel calculateRiskLevel(Integer delayDays, BigDecimal confidence) {
        if (delayDays == null || confidence == null) {
            return RiskLevel.LOW;
        }
        
        if (delayDays >= 7 && confidence.compareTo(new BigDecimal("0.9")) > 0) {
            return RiskLevel.CRITICAL;
        } else if (delayDays >= 5 && confidence.compareTo(new BigDecimal("0.8")) > 0) {
            return RiskLevel.HIGH;
        } else if (delayDays >= 3 && confidence.compareTo(new BigDecimal("0.7")) > 0) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
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

    public Integer getPredictedDelayDays() {
        return predictedDelayDays;
    }

    public void setPredictedDelayDays(Integer predictedDelayDays) {
        this.predictedDelayDays = predictedDelayDays;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public LocalDateTime getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDateTime predictionDate) {
        this.predictionDate = predictionDate;
    }

    public Integer getActualDelayDays() {
        return actualDelayDays;
    }

    public void setActualDelayDays(Integer actualDelayDays) {
        this.actualDelayDays = actualDelayDays;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getFeaturesUsed() {
        return featuresUsed;
    }

    public void setFeaturesUsed(String featuresUsed) {
        this.featuresUsed = featuresUsed;
    }

    public BigDecimal getPredictionAccuracy() {
        return predictionAccuracy;
    }

    public void setPredictionAccuracy(BigDecimal predictionAccuracy) {
        this.predictionAccuracy = predictionAccuracy;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getMitigationSuggestions() {
        return mitigationSuggestions;
    }

    public void setMitigationSuggestions(String mitigationSuggestions) {
        this.mitigationSuggestions = mitigationSuggestions;
    }

    public Boolean getIsAlertSent() {
        return isAlertSent;
    }

    public void setIsAlertSent(Boolean isAlertSent) {
        this.isAlertSent = isAlertSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
