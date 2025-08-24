package com.proximaai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 200)
    @Column(nullable = false)
    private String title;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type = TaskType.TASK;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "actual_hours")
    private Integer actualHours;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "kanban_column")
    private String kanbanColumn = "TODO";

    @Column(name = "kanban_order")
    private Integer kanbanOrder = 0;

    @Column(name = "tags")
    private String tags; // Comma-separated tags

    @Column(name = "attachments")
    private String attachments; // Comma-separated file URLs

    @Column(name = "ai_recommendations")
    private String aiRecommendations; // JSON with AI suggestions

    @Column(name = "ai_estimated_duration")
    private Integer aiEstimatedDuration; // AI estimated duration in hours

    @Column(name = "ai_priority_score")
    private Double aiPriorityScore; // AI calculated priority score

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskTimeLog> timeLogs = new ArrayList<>();

    @OneToMany(mappedBy = "dependentTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskDependency> dependencies = new ArrayList<>();

    @OneToMany(mappedBy = "prerequisiteTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskDependency> prerequisites = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Task() {}

    public Task(String title, String description, Project project, User reporter) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.reporter = reporter;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        if (TaskStatus.DONE.equals(status)) {
            this.completedDate = LocalDate.now();
        }
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Integer getActualHours() {
        return actualHours;
    }

    public void setActualHours(Integer actualHours) {
        this.actualHours = actualHours;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public String getKanbanColumn() {
        return kanbanColumn;
    }

    public void setKanbanColumn(String kanbanColumn) {
        this.kanbanColumn = kanbanColumn;
    }

    public Integer getKanbanOrder() {
        return kanbanOrder;
    }

    public void setKanbanOrder(Integer kanbanOrder) {
        this.kanbanOrder = kanbanOrder;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getAiRecommendations() {
        return aiRecommendations;
    }

    public void setAiRecommendations(String aiRecommendations) {
        this.aiRecommendations = aiRecommendations;
    }

    public Integer getAiEstimatedDuration() {
        return aiEstimatedDuration;
    }

    public void setAiEstimatedDuration(Integer aiEstimatedDuration) {
        this.aiEstimatedDuration = aiEstimatedDuration;
    }

    public Double getAiPriorityScore() {
        return aiPriorityScore;
    }

    public void setAiPriorityScore(Double aiPriorityScore) {
        this.aiPriorityScore = aiPriorityScore;
    }

    public List<TaskComment> getComments() {
        return comments;
    }

    public void setComments(List<TaskComment> comments) {
        this.comments = comments;
    }

    public List<TaskTimeLog> getTimeLogs() {
        return timeLogs;
    }

    public void setTimeLogs(List<TaskTimeLog> timeLogs) {
        this.timeLogs = timeLogs;
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
    public void addComment(TaskComment comment) {
        comments.add(comment);
        comment.setTask(this);
    }

    public void addTimeLog(TaskTimeLog timeLog) {
        timeLogs.add(timeLog);
        timeLog.setTask(this);
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate) && 
               !TaskStatus.DONE.equals(status);
    }

    public boolean isAssigned() {
        return assignee != null;
    }

    public boolean isCompleted() {
        return TaskStatus.DONE.equals(status);
    }

    public boolean isInProgress() {
        return TaskStatus.IN_PROGRESS.equals(status);
    }

    public void moveToColumn(String column) {
        this.kanbanColumn = column;
        if ("IN_PROGRESS".equals(column) && startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    // Enums
    public enum TaskStatus {
        TODO, IN_PROGRESS, IN_REVIEW, TESTING, DONE, CANCELLED
    }

    public enum TaskPriority {
        LOW, MEDIUM, HIGH, CRITICAL, URGENT
    }

    public enum TaskType {
        TASK, BUG, FEATURE, STORY, EPIC, SUBTASK
    }
}
