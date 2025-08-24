package com.proximaai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_templates")
@EntityListeners(AuditingEntityListener.class)
public class ProjectTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team; // Opcional - template específico da equipe

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateCategory category = TemplateCategory.GENERAL;

    @Column(name = "is_public")
    private Boolean isPublic = false; // Se o template é público para todas as equipes

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "estimated_duration_days")
    private Integer estimatedDurationDays;

    @Column(name = "estimated_budget")
    private Double estimatedBudget;

    @Column(name = "complexity_level")
    @Enumerated(EnumType.STRING)
    private ComplexityLevel complexityLevel = ComplexityLevel.MEDIUM;

    @Column(name = "tags")
    private String tags; // Comma-separated tags

    @Column(name = "icon")
    private String icon; // Ícone do template

    @Column(name = "color")
    private String color; // Cor do template

    @Column(name = "usage_count")
    private Integer usageCount = 0; // Quantas vezes foi usado

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TemplateTask> templateTasks = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TemplateMilestone> templateMilestones = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TemplateResource> templateResources = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum TemplateCategory {
        GENERAL,            // Geral
        SOFTWARE_DEV,       // Desenvolvimento de Software
        MARKETING,          // Marketing
        DESIGN,             // Design
        RESEARCH,           // Pesquisa
        EVENT_PLANNING,     // Planejamento de Eventos
        CONSTRUCTION,       // Construção
        HEALTHCARE,         // Saúde
        EDUCATION,          // Educação
        FINANCE,            // Finanças
        CUSTOM              // Personalizado
    }

    public enum ComplexityLevel {
        SIMPLE,             // Simples
        MEDIUM,             // Médio
        COMPLEX,            // Complexo
        VERY_COMPLEX        // Muito Complexo
    }

    // Constructors
    public ProjectTemplate() {}

    public ProjectTemplate(String name, String description, User createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TemplateCategory getCategory() {
        return category;
    }

    public void setCategory(TemplateCategory category) {
        this.category = category;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getEstimatedDurationDays() {
        return estimatedDurationDays;
    }

    public void setEstimatedDurationDays(Integer estimatedDurationDays) {
        this.estimatedDurationDays = estimatedDurationDays;
    }

    public Double getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(Double estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public ComplexityLevel getComplexityLevel() {
        return complexityLevel;
    }

    public void setComplexityLevel(ComplexityLevel complexityLevel) {
        this.complexityLevel = complexityLevel;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public List<TemplateTask> getTemplateTasks() {
        return templateTasks;
    }

    public void setTemplateTasks(List<TemplateTask> templateTasks) {
        this.templateTasks = templateTasks;
    }

    public List<TemplateMilestone> getTemplateMilestones() {
        return templateMilestones;
    }

    public void setTemplateMilestones(List<TemplateMilestone> templateMilestones) {
        this.templateMilestones = templateMilestones;
    }

    public List<TemplateResource> getTemplateResources() {
        return templateResources;
    }

    public void setTemplateResources(List<TemplateResource> templateResources) {
        this.templateResources = templateResources;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public void addTemplateTask(TemplateTask templateTask) {
        templateTasks.add(templateTask);
        templateTask.setTemplate(this);
    }

    public void removeTemplateTask(TemplateTask templateTask) {
        templateTasks.remove(templateTask);
        templateTask.setTemplate(null);
    }

    public void addTemplateMilestone(TemplateMilestone templateMilestone) {
        templateMilestones.add(templateMilestone);
        templateMilestone.setTemplate(this);
    }

    public void removeTemplateMilestone(TemplateMilestone templateMilestone) {
        templateMilestones.remove(templateMilestone);
        templateMilestone.setTemplate(null);
    }

    public void addTemplateResource(TemplateResource templateResource) {
        templateResources.add(templateResource);
        templateResource.setTemplate(this);
    }

    public void removeTemplateResource(TemplateResource templateResource) {
        templateResources.remove(templateResource);
        templateResource.setTemplate(null);
    }

    public void incrementUsageCount() {
        this.usageCount++;
        this.lastUsedAt = LocalDateTime.now();
    }
}
