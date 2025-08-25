package com.proximaai.domain.entity.ai;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import com.proximaai.domain.entity.Project;

@Entity
@Table(name = "ai_project_summaries")
@EntityListeners(AuditingEntityListener.class)
public class ProjectSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @NotBlank
    @Size(min = 10, max = 200)
    @Column(name = "summary_title", nullable = false)
    private String summaryTitle;

    @NotBlank
    @Size(min = 50, max = 2000)
    @Column(name = "summary_content", nullable = false, length = 2000)
    private String summaryContent; // Resumo em linguagem natural

    @Column(name = "summary_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SummaryType summaryType = SummaryType.DAILY;

    @Column(name = "summary_date", nullable = false)
    private LocalDateTime summaryDate;

    @Column(name = "ai_model_used")
    private String aiModelUsed; // Modelo de IA utilizado

    @Column(name = "generation_time_ms")
    private Long generationTimeMs; // Tempo de geração em milissegundos

    @Column(name = "summary_metrics")
    private String summaryMetrics; // JSON com métricas do resumo

    @Column(name = "key_highlights")
    private String keyHighlights; // JSON com destaques principais

    @Column(name = "risk_indicators")
    private String riskIndicators; // JSON com indicadores de risco

    @Column(name = "recommendations")
    private String recommendations; // JSON com recomendações da IA

    @Column(name = "next_steps")
    private String nextSteps; // JSON com próximos passos sugeridos

    @Column(name = "stakeholder_notes")
    private String stakeholderNotes; // Notas específicas para stakeholders

    @Column(name = "is_approved")
    private Boolean isApproved = false; // Se foi aprovado por gestor

    @Column(name = "approved_by")
    private String approvedBy; // Quem aprovou

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "feedback_score")
    private Integer feedbackScore; // Score de feedback dos usuários (1-5)

    @Column(name = "is_shared")
    private Boolean isShared = false; // Se foi compartilhado com stakeholders

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum SummaryType {
        DAILY,      // Resumo diário
        WEEKLY,     // Resumo semanal
        MONTHLY,    // Resumo mensal
        MILESTONE,  // Resumo de marco
        SPRINT,     // Resumo de sprint
        CUSTOM      // Resumo customizado
    }

    // Constructors
    public ProjectSummary() {}

    public ProjectSummary(Project project, String summaryTitle, String summaryContent, SummaryType summaryType) {
        this.project = project;
        this.summaryTitle = summaryTitle;
        this.summaryContent = summaryContent;
        this.summaryType = summaryType;
        this.summaryDate = LocalDateTime.now();
    }

    // Helper methods
    public void approve(String approver) {
        this.isApproved = true;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    public boolean isReadyForSharing() {
        return this.isApproved && this.summaryContent != null && !this.summaryContent.trim().isEmpty();
    }

    public String getFormattedSummary() {
        StringBuilder formatted = new StringBuilder();
        formatted.append("📊 ").append(this.summaryTitle).append("\n\n");
        formatted.append(this.summaryContent).append("\n\n");
        
        if (this.keyHighlights != null && !this.keyHighlights.isEmpty()) {
            formatted.append("🎯 **Destaques:**\n").append(this.keyHighlights).append("\n\n");
        }
        
        if (this.riskIndicators != null && !this.riskIndicators.isEmpty()) {
            formatted.append("⚠️ **Riscos:**\n").append(this.riskIndicators).append("\n\n");
        }
        
        if (this.recommendations != null && !this.recommendations.isEmpty()) {
            formatted.append("💡 **Recomendações:**\n").append(this.recommendations).append("\n\n");
        }
        
        if (this.nextSteps != null && !this.nextSteps.isEmpty()) {
            formatted.append("🚀 **Próximos Passos:**\n").append(this.nextSteps);
        }
        
        return formatted.toString();
    }

    public boolean isHighPriority() {
        return this.riskIndicators != null && 
               (this.riskIndicators.contains("CRITICAL") || this.riskIndicators.contains("HIGH"));
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

    public String getSummaryTitle() {
        return summaryTitle;
    }

    public void setSummaryTitle(String summaryTitle) {
        this.summaryTitle = summaryTitle;
    }

    public String getSummaryContent() {
        return summaryContent;
    }

    public void setSummaryContent(String summaryContent) {
        this.summaryContent = summaryContent;
    }

    public SummaryType getSummaryType() {
        return summaryType;
    }

    public void setSummaryType(SummaryType summaryType) {
        this.summaryType = summaryType;
    }

    public LocalDateTime getSummaryDate() {
        return summaryDate;
    }

    public void setSummaryDate(LocalDateTime summaryDate) {
        this.summaryDate = summaryDate;
    }

    public String getAiModelUsed() {
        return aiModelUsed;
    }

    public void setAiModelUsed(String aiModelUsed) {
        this.aiModelUsed = aiModelUsed;
    }

    public Long getGenerationTimeMs() {
        return generationTimeMs;
    }

    public void setGenerationTimeMs(Long generationTimeMs) {
        this.generationTimeMs = generationTimeMs;
    }

    public String getSummaryMetrics() {
        return summaryMetrics;
    }

    public void setSummaryMetrics(String summaryMetrics) {
        this.summaryMetrics = summaryMetrics;
    }

    public String getKeyHighlights() {
        return keyHighlights;
    }

    public void setKeyHighlights(String keyHighlights) {
        this.keyHighlights = keyHighlights;
    }

    public String getRiskIndicators() {
        return riskIndicators;
    }

    public void setRiskIndicators(String riskIndicators) {
        this.riskIndicators = riskIndicators;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getNextSteps() {
        return nextSteps;
    }

    public void setNextSteps(String nextSteps) {
        this.nextSteps = nextSteps;
    }

    public String getStakeholderNotes() {
        return stakeholderNotes;
    }

    public void setStakeholderNotes(String stakeholderNotes) {
        this.stakeholderNotes = stakeholderNotes;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Integer getFeedbackScore() {
        return feedbackScore;
    }

    public void setFeedbackScore(Integer feedbackScore) {
        this.feedbackScore = feedbackScore;
    }

    public Boolean getIsShared() {
        return isShared;
    }

    public void setIsShared(Boolean isShared) {
        this.isShared = isShared;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
