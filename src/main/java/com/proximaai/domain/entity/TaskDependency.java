package com.proximaai.domain.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_dependencies")
@EntityListeners(AuditingEntityListener.class)
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_task_id", nullable = false)
    private Task dependentTask; // Tarefa que depende de outra

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_task_id", nullable = false)
    private Task prerequisiteTask; // Tarefa que deve ser completada primeiro

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DependencyType type = DependencyType.FINISH_TO_START;

    @Column(name = "lag_days")
    private Integer lagDays = 0; // Dias de espera após a conclusão da tarefa pré-requisito

    @Column(name = "lead_days")
    private Integer leadDays = 0; // Dias que podem ser executados em paralelo

    @Column(name = "is_critical")
    private Boolean isCritical = false; // Se é uma dependência crítica para o projeto

    @Column(name = "notes")
    private String notes; // Notas sobre a dependência

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enums para tipos de dependência
    public enum DependencyType {
        FINISH_TO_START,    // A tarefa dependente só pode começar após a pré-requisito terminar
        START_TO_START,     // A tarefa dependente só pode começar após a pré-requisito começar
        FINISH_TO_FINISH,   // A tarefa dependente só pode terminar após a pré-requisito terminar
        START_TO_FINISH     // A tarefa dependente só pode terminar após a pré-requisito começar
    }

    // Constructors
    public TaskDependency() {}

    public TaskDependency(Task dependentTask, Task prerequisiteTask) {
        this.dependentTask = dependentTask;
        this.prerequisiteTask = prerequisiteTask;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getDependentTask() {
        return dependentTask;
    }

    public void setDependentTask(Task dependentTask) {
        this.dependentTask = dependentTask;
    }

    public Task getPrerequisiteTask() {
        return prerequisiteTask;
    }

    public void setPrerequisiteTask(Task prerequisiteTask) {
        this.prerequisiteTask = prerequisiteTask;
    }

    public DependencyType getType() {
        return type;
    }

    public void setType(DependencyType type) {
        this.type = type;
    }

    public Integer getLagDays() {
        return lagDays;
    }

    public void setLagDays(Integer lagDays) {
        this.lagDays = lagDays;
    }

    public Integer getLeadDays() {
        return leadDays;
    }

    public void setLeadDays(Integer leadDays) {
        this.leadDays = leadDays;
    }

    public Boolean getIsCritical() {
        return isCritical;
    }

    public void setIsCritical(Boolean isCritical) {
        this.isCritical = isCritical;
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
