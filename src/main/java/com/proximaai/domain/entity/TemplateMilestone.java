package com.proximaai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "template_milestones")
@EntityListeners(AuditingEntityListener.class)
public class TemplateMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ProjectTemplate template;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "estimated_days_from_start")
    private Integer estimatedDaysFromStart; // Dias estimados desde o início do projeto

    @Column(name = "is_critical")
    private Boolean isCritical = false; // Se é um marco crítico

    @Column(name = "deliverables")
    private String deliverables; // JSON com entregáveis esperados

    @Column(name = "success_criteria")
    private String successCriteria; // Critérios de sucesso

    @Column(name = "notes")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public TemplateMilestone() {}

    public TemplateMilestone(String name, ProjectTemplate template) {
        this.name = name;
        this.template = template;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectTemplate getTemplate() {
        return template;
    }

    public void setTemplate(ProjectTemplate template) {
        this.template = template;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getEstimatedDaysFromStart() {
        return estimatedDaysFromStart;
    }

    public void setEstimatedDaysFromStart(Integer estimatedDaysFromStart) {
        this.estimatedDaysFromStart = estimatedDaysFromStart;
    }

    public Boolean getIsCritical() {
        return isCritical;
    }

    public void setIsCritical(Boolean isCritical) {
        this.isCritical = isCritical;
    }

    public String getDeliverables() {
        return deliverables;
    }

    public void setDeliverables(String deliverables) {
        this.deliverables = deliverables;
    }

    public String getSuccessCriteria() {
        return successCriteria;
    }

    public void setSuccessCriteria(String successCriteria) {
        this.successCriteria = successCriteria;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
