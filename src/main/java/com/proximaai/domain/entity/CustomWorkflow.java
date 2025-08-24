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
@Table(name = "custom_workflows")
@EntityListeners(AuditingEntityListener.class)
public class CustomWorkflow {

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
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // Opcional - pode ser workflow específico do projeto

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowType type = WorkflowType.TASK_WORKFLOW;

    @Column(name = "is_default")
    private Boolean isDefault = false; // Se é o workflow padrão da equipe

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "color_theme")
    private String colorTheme; // JSON com cores personalizadas

    @Column(name = "icon")
    private String icon; // Ícone do workflow

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<WorkflowStatus> statuses = new ArrayList<>();

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<WorkflowTransition> transitions = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum WorkflowType {
        TASK_WORKFLOW,     // Workflow para tarefas
        PROJECT_WORKFLOW,  // Workflow para projetos
        REQUEST_WORKFLOW,  // Workflow para solicitações
        APPROVAL_WORKFLOW  // Workflow para aprovações
    }

    // Constructors
    public CustomWorkflow() {}

    public CustomWorkflow(String name, Team team) {
        this.name = name;
        this.team = team;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public WorkflowType getType() {
        return type;
    }

    public void setType(WorkflowType type) {
        this.type = type;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<WorkflowStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<WorkflowStatus> statuses) {
        this.statuses = statuses;
    }

    public List<WorkflowTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<WorkflowTransition> transitions) {
        this.transitions = transitions;
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
    public void addStatus(WorkflowStatus status) {
        statuses.add(status);
        status.setWorkflow(this);
    }

    public void removeStatus(WorkflowStatus status) {
        statuses.remove(status);
        status.setWorkflow(null);
    }

    public void addTransition(WorkflowTransition transition) {
        transitions.add(transition);
        transition.setWorkflow(this);
    }

    public void removeTransition(WorkflowTransition transition) {
        transitions.remove(transition);
        transition.setWorkflow(null);
    }
}
