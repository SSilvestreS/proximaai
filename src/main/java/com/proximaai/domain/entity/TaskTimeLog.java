package com.proximaai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_time_logs")
@EntityListeners(AuditingEntityListener.class)
public class TaskTimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "hours_spent", nullable = false)
    private Double hoursSpent;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeLogType type = TimeLogType.WORK;

    @Column(name = "is_billable")
    private Boolean isBillable = true;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public TaskTimeLog() {}

    public TaskTimeLog(Task task, User user, Double hoursSpent, String description) {
        this.task = task;
        this.user = user;
        this.hoursSpent = hoursSpent;
        this.description = description;
        this.logDate = LocalDate.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getHoursSpent() {
        return hoursSpent;
    }

    public void setHoursSpent(Double hoursSpent) {
        this.hoursSpent = hoursSpent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public TimeLogType getType() {
        return type;
    }

    public void setType(TimeLogType type) {
        this.type = type;
    }

    public Boolean getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(Boolean isBillable) {
        this.isBillable = isBillable;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public Double getTotalCost() {
        if (hourlyRate != null && hoursSpent != null) {
            return hourlyRate * hoursSpent;
        }
        return 0.0;
    }

    public boolean isBillable() {
        return Boolean.TRUE.equals(isBillable);
    }

    public void setTimeRange(LocalDateTime start, LocalDateTime end) {
        this.startTime = start;
        this.endTime = end;
        if (start != null && end != null) {
            long minutes = java.time.Duration.between(start, end).toMinutes();
            this.hoursSpent = minutes / 60.0;
        }
    }

    // Enums
    public enum TimeLogType {
        WORK, MEETING, RESEARCH, TRAINING, SUPPORT, OTHER
    }
}
