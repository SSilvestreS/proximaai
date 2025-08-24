package com.proximaai.service.ai;

import com.proximaai.domain.entity.*;
import com.proximaai.repository.TaskRepository;
import com.proximaai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIServiceImpl implements AIService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Integer estimateTaskDuration(Task task, String description) {
        // Lógica baseada em dados históricos e análise de texto
        int baseHours = 4; // Base padrão
        
        // Ajuste baseado no tipo de tarefa
        switch (task.getType()) {
            case BUG:
                baseHours = 2;
                break;
            case FEATURE:
                baseHours = 8;
                break;
            case STORY:
                baseHours = 16;
                break;
            case EPIC:
                baseHours = 40;
                break;
            default:
                baseHours = 4;
        }
        
        // Ajuste baseado na prioridade
        switch (task.getPriority()) {
            case CRITICAL:
            case URGENT:
                baseHours = (int) (baseHours * 0.8); // Tarefas urgentes tendem a ser mais rápidas
                break;
            case HIGH:
                baseHours = (int) (baseHours * 0.9); // Tarefas de alta prioridade são um pouco mais rápidas
                break;
            case MEDIUM:
                baseHours = (int) (baseHours * 1.0); // Tarefas de média prioridade mantêm o tempo base
                break;
            case LOW:
                baseHours = (int) (baseHours * 1.2); // Tarefas de baixa prioridade podem demorar mais
                break;
        }
        
        // Ajuste baseado no tamanho da descrição (proxy para complexidade)
        if (description != null) {
            int wordCount = description.split("\\s+").length;
            if (wordCount > 100) {
                baseHours = (int) (baseHours * 1.3);
            } else if (wordCount < 20) {
                baseHours = (int) (baseHours * 0.7);
            }
        }
        
        // Ajuste baseado em dados históricos do projeto
        if (task.getProject() != null) {
            Double avgHours = taskRepository.getTotalActualHoursByProject(task.getProject().getId());
            if (avgHours != null && avgHours > 0) {
                // Ajuste baseado na média histórica
                double adjustmentFactor = avgHours / (taskRepository.countByProjectAndStatus(
                    task.getProject().getId(), Task.TaskStatus.DONE) * 8.0);
                baseHours = (int) (baseHours * adjustmentFactor);
            }
        }
        
        return Math.max(1, baseHours); // Mínimo de 1 hora
    }

    @Override
    public Double calculateTaskPriorityScore(Task task) {
        double score = 0.0;
        
        // Score baseado na prioridade
        switch (task.getPriority()) {
            case URGENT:
                score += 100;
                break;
            case CRITICAL:
                score += 90;
                break;
            case HIGH:
                score += 70;
                break;
            case MEDIUM:
                score += 50;
                break;
            case LOW:
                score += 30;
                break;
        }
        
        // Score baseado no prazo
        if (task.getDueDate() != null) {
            long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());
            if (daysUntilDue < 0) {
                score += 80; // Tarefa atrasada
            } else if (daysUntilDue <= 3) {
                score += 60; // Prazo muito próximo
            } else if (daysUntilDue <= 7) {
                score += 40; // Prazo próximo
            } else if (daysUntilDue <= 14) {
                score += 20; // Prazo médio
            }
        }
        
        // Score baseado no tipo
        switch (task.getType()) {
            case BUG:
                score += 30; // Bugs têm prioridade alta
                break;
            case FEATURE:
                score += 20;
                break;
            case STORY:
                score += 15;
                break;
            case EPIC:
                score += 25; // Epics são importantes
                break;
            case TASK:
                score += 10; // Tarefas básicas
                break;
            case SUBTASK:
                score += 5; // Subtarefas têm menor prioridade
                break;
        }
        
        // Score baseado no status
        if (Task.TaskStatus.IN_PROGRESS.equals(task.getStatus())) {
            score += 25; // Tarefas em progresso têm prioridade
        }
        
        // Score baseado no assignee (se não estiver atribuída, prioridade alta)
        if (task.getAssignee() == null) {
            score += 35;
        }
        
        // Score baseado no projeto (projetos críticos)
        if (task.getProject() != null && Project.ProjectPriority.CRITICAL.equals(task.getProject().getPriority())) {
            score += 25;
        }
        
        return Math.min(100.0, score);
    }

    @Override
    public boolean detectUserOverload(User user) {
        // Conta tarefas ativas do usuário
        long activeTasks = taskRepository.countByAssigneeAndStatus(user.getId(), Task.TaskStatus.IN_PROGRESS);
        
        // Conta tarefas pendentes
        long pendingTasks = taskRepository.countByAssigneeAndStatus(user.getId(), Task.TaskStatus.TODO);
        
        // Calcula horas estimadas de trabalho
        List<Task> userTasks = taskRepository.findByAssigneeIdAndStatus(user.getId(), Task.TaskStatus.IN_PROGRESS);
        double totalEstimatedHours = userTasks.stream()
            .mapToDouble(t -> t.getEstimatedHours() != null ? t.getEstimatedHours() : 8.0)
            .sum();
        
        // Critérios de sobrecarga
        boolean tooManyActiveTasks = activeTasks > 5;
        boolean tooManyPendingTasks = pendingTasks > 10;
        boolean tooManyHours = totalEstimatedHours > 40;
        
        return tooManyActiveTasks || tooManyPendingTasks || tooManyHours;
    }

    @Override
    public LocalDate suggestTaskDeadline(Task task, Project project) {
        LocalDate suggestedDate = LocalDate.now().plusDays(7); // Padrão: 1 semana
        
        if (task.getEstimatedHours() != null) {
            // Baseado nas horas estimadas
            int estimatedDays = (int) Math.ceil(task.getEstimatedHours() / 8.0);
            suggestedDate = LocalDate.now().plusDays(estimatedDays);
        }
        
        // Ajuste baseado na prioridade
        switch (task.getPriority()) {
            case URGENT:
            case CRITICAL:
                suggestedDate = LocalDate.now().plusDays(1);
                break;
            case HIGH:
                suggestedDate = LocalDate.now().plusDays(3);
                break;
            case MEDIUM:
                suggestedDate = LocalDate.now().plusDays(7);
                break;
            case LOW:
                suggestedDate = LocalDate.now().plusDays(14);
                break;
        }
        
        // Ajuste baseado na capacidade da equipe
        if (project.getTeam() != null) {
            int teamSize = project.getTeam().getMemberCount();
            if (teamSize > 5) {
                suggestedDate = suggestedDate.minusDays(2); // Equipes grandes podem ser mais rápidas
            } else if (teamSize < 3) {
                suggestedDate = suggestedDate.plusDays(3); // Equipes pequenas podem demorar mais
            }
        }
        
        return suggestedDate;
    }

    @Override
    public User recommendAssignee(Task task, List<User> availableUsers) {
        if (availableUsers.isEmpty()) {
            return null;
        }
        
        User bestCandidate = null;
        double bestScore = -1;
        
        for (User user : availableUsers) {
            double score = calculateUserTaskFitScore(user, task);
            if (score > bestScore) {
                bestScore = score;
                bestCandidate = user;
            }
        }
        
        return bestCandidate;
    }
    
    private double calculateUserTaskFitScore(User user, Task task) {
        double score = 0.0;
        
        // Score baseado na experiência (tarefas similares completadas)
        List<Task> completedTasks = taskRepository.findByAssigneeIdAndStatus(user.getId(), Task.TaskStatus.DONE);
        long similarTasks = completedTasks.stream()
            .filter(t -> t.getType().equals(task.getType()))
            .count();
        score += similarTasks * 10;
        
        // Score baseado na disponibilidade (menos tarefas ativas = melhor)
        long activeTasks = taskRepository.countByAssigneeAndStatus(user.getId(), Task.TaskStatus.IN_PROGRESS);
        score += (10 - activeTasks) * 5;
        
        // Score baseado no histórico de performance
        double avgCompletionTime = completedTasks.stream()
            .filter(t -> t.getEstimatedHours() != null && t.getActualHours() != null)
            .mapToDouble(t -> (double) t.getEstimatedHours() / t.getActualHours())
            .average()
            .orElse(1.0);
        score += avgCompletionTime * 20;
        
        return score;
    }

    @Override
    public Map<String, Double> analyzeProjectRisks(Project project) {
        Map<String, Double> risks = new HashMap<>();
        
        // Risco de atraso
        double delayRisk = calculateDelayRisk(project);
        risks.put("DELAY_RISK", delayRisk);
        
        // Risco de sobrecarga da equipe
        double overloadRisk = calculateOverloadRisk(project);
        risks.put("OVERLOAD_RISK", overloadRisk);
        
        // Risco de estimativas imprecisas
        double estimationRisk = calculateEstimationRisk(project);
        risks.put("ESTIMATION_RISK", estimationRisk);
        
        // Risco de dependências
        double dependencyRisk = calculateDependencyRisk(project);
        risks.put("DEPENDENCY_RISK", dependencyRisk);
        
        return risks;
    }
    
    private double calculateDelayRisk(Project project) {
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        long overdueTasks = tasks.stream()
            .filter(Task::isOverdue)
            .count();
        
        return tasks.isEmpty() ? 0.0 : (double) overdueTasks / tasks.size() * 100;
    }
    
    private double calculateOverloadRisk(Project project) {
        if (project.getTeam() == null) return 0.0;
        
        List<User> teamMembers = userRepository.findUsersByTeamId(project.getTeam().getId());
        long overloadedUsers = teamMembers.stream()
            .filter(this::detectUserOverload)
            .count();
        
        return teamMembers.isEmpty() ? 0.0 : (double) overloadedUsers / teamMembers.size() * 100;
    }
    
    private double calculateEstimationRisk(Project project) {
        List<Task> completedTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.DONE);
        
        if (completedTasks.isEmpty()) return 50.0; // Risco médio se não há dados
        
        double avgEstimationAccuracy = completedTasks.stream()
            .filter(t -> t.getEstimatedHours() != null && t.getActualHours() != null)
            .mapToDouble(t -> Math.abs(1.0 - (double) t.getEstimatedHours() / t.getActualHours()))
            .average()
            .orElse(0.5);
        
        return avgEstimationAccuracy * 100;
    }
    
    private double calculateDependencyRisk(Project project) {
        // Implementação simplificada - pode ser expandida com análise de dependências
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        long tasksWithDependencies = tasks.stream()
            .filter(t -> t.getTags() != null && t.getTags().contains("dependency"))
            .count();
        
        return tasks.isEmpty() ? 0.0 : (double) tasksWithDependencies / tasks.size() * 100;
    }

    @Override
    public Map<String, Object> suggestTimeEstimateImprovements(Project project) {
        Map<String, Object> suggestions = new HashMap<>();
        
        List<Task> completedTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.DONE);
        
        if (completedTasks.isEmpty()) {
            suggestions.put("message", "Não há dados suficientes para análise");
            return suggestions;
        }
        
        // Análise de precisão das estimativas
        double avgAccuracy = completedTasks.stream()
            .filter(t -> t.getEstimatedHours() != null && t.getActualHours() != null)
            .mapToDouble(t -> (double) t.getEstimatedHours() / t.getActualHours())
            .average()
            .orElse(1.0);
        
        suggestions.put("average_accuracy", avgAccuracy);
        suggestions.put("recommendation", avgAccuracy < 0.8 ? "Aumentar estimativas" : 
                                      avgAccuracy > 1.2 ? "Reduzir estimativas" : "Estimativas adequadas");
        
        // Tarefas mais problemáticas
        List<Task> overestimatedTasks = taskRepository.findOverestimatedTasks();
        List<Task> underestimatedTasks = taskRepository.findUnderestimatedTasks();
        
        suggestions.put("overestimated_count", overestimatedTasks.size());
        suggestions.put("underestimated_count", underestimatedTasks.size());
        
        return suggestions;
    }

    @Override
    public List<String> identifyProjectBottlenecks(Project project) {
        List<String> bottlenecks = new ArrayList<>();
        
        // Verifica usuários sobrecarregados
        if (project.getTeam() != null) {
            List<User> teamMembers = userRepository.findUsersByTeamId(project.getTeam().getId());
            for (User member : teamMembers) {
                if (detectUserOverload(member)) {
                    bottlenecks.add("Usuário sobrecarregado: " + member.getUsername());
                }
            }
        }
        
        // Verifica tarefas bloqueadas
        List<Task> blockedTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.TODO);
        long longPendingTasks = blockedTasks.stream()
            .filter(t -> t.getCreatedAt() != null && 
                        ChronoUnit.DAYS.between(t.getCreatedAt().toLocalDate(), LocalDate.now()) > 7)
            .count();
        
        if (longPendingTasks > 0) {
            bottlenecks.add("Tarefas pendentes há mais de 7 dias: " + longPendingTasks);
        }
        
        // Verifica dependências
        List<Task> tasksWithDependencies = taskRepository.findByTag("dependency");
        if (!tasksWithDependencies.isEmpty()) {
            bottlenecks.add("Tarefas com dependências: " + tasksWithDependencies.size());
        }
        
        return bottlenecks;
    }

    @Override
    public List<String> generateProjectOptimizationRecommendations(Project project) {
        List<String> recommendations = new ArrayList<>();
        
        // Recomendações baseadas em riscos
        Map<String, Double> risks = analyzeProjectRisks(project);
        
        if (risks.get("DELAY_RISK") > 30) {
            recommendations.add("Revisar prazos e prioridades das tarefas atrasadas");
        }
        
        if (risks.get("OVERLOAD_RISK") > 50) {
            recommendations.add("Redistribuir carga de trabalho entre membros da equipe");
        }
        
        if (risks.get("ESTIMATION_RISK") > 40) {
            recommendations.add("Melhorar processo de estimativa de tempo");
        }
        
        // Recomendações baseadas em gargalos
        List<String> bottlenecks = identifyProjectBottlenecks(project);
        if (!bottlenecks.isEmpty()) {
            recommendations.add("Resolver gargalos identificados: " + String.join(", ", bottlenecks));
        }
        
        // Recomendações de paralelização
        List<Task> parallelizableTasks = identifyParallelizableTasks(project);
        if (!parallelizableTasks.isEmpty()) {
            recommendations.add("Considerar paralelização de " + parallelizableTasks.size() + " tarefas");
        }
        
        return recommendations;
    }

    @Override
    public Map<String, Object> analyzeTeamProductivityPatterns(Project project) {
        Map<String, Object> patterns = new HashMap<>();
        
        List<Task> completedTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.DONE);
        
        if (completedTasks.isEmpty()) {
            patterns.put("message", "Não há dados suficientes para análise");
            return patterns;
        }
        
        // Velocidade média de conclusão
        double avgCompletionTime = completedTasks.stream()
            .filter(t -> t.getStartDate() != null && t.getCompletedDate() != null)
            .mapToDouble(t -> ChronoUnit.DAYS.between(t.getStartDate(), t.getCompletedDate()))
            .average()
            .orElse(0.0);
        
        patterns.put("average_completion_days", avgCompletionTime);
        
        // Produtividade por dia da semana
        Map<String, Long> productivityByDay = completedTasks.stream()
            .filter(t -> t.getCompletedDate() != null)
            .collect(Collectors.groupingBy(
                t -> t.getCompletedDate().getDayOfWeek().toString(),
                Collectors.counting()
            ));
        
        patterns.put("productivity_by_day", productivityByDay);
        
        // Tamanho médio das tarefas
        double avgStoryPoints = completedTasks.stream()
            .filter(t -> t.getStoryPoints() != null)
            .mapToDouble(Task::getStoryPoints)
            .average()
            .orElse(0.0);
        
        patterns.put("average_story_points", avgStoryPoints);
        
        return patterns;
    }

    @Override
    public List<String> suggestProcessImprovements(Project project) {
        List<String> improvements = new ArrayList<>();
        
        // Análise de produtividade
        Map<String, Object> productivityPatterns = analyzeTeamProductivityPatterns(project);
        
        Double avgCompletionDays = (Double) productivityPatterns.get("average_completion_days");
        if (avgCompletionDays != null && avgCompletionDays > 5) {
            improvements.add("Implementar daily standups para melhorar comunicação");
        }
        
        // Análise de estimativas
        Map<String, Object> estimationAnalysis = suggestTimeEstimateImprovements(project);
        Double avgAccuracy = (Double) estimationAnalysis.get("average_accuracy");
        if (avgAccuracy != null && avgAccuracy < 0.8) {
            improvements.add("Implementar retrospectivas para melhorar estimativas");
        }
        
        // Análise de gargalos
        List<String> bottlenecks = identifyProjectBottlenecks(project);
        if (!bottlenecks.isEmpty()) {
            improvements.add("Implementar sistema de alertas para gargalos");
        }
        
        // Recomendações gerais
        improvements.add("Revisar processo de code review");
        improvements.add("Implementar testes automatizados");
        improvements.add("Melhorar documentação das tarefas");
        
        return improvements;
    }

    @Override
    public LocalDate predictProjectCompletionDate(Project project) {
        if (project.getEndDate() != null) {
            return project.getEndDate();
        }
        
        List<Task> remainingTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.TODO);
        remainingTasks.addAll(taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.IN_PROGRESS));
        
        if (remainingTasks.isEmpty()) {
            return LocalDate.now();
        }
        
        // Calcula horas restantes
        double totalRemainingHours = remainingTasks.stream()
            .mapToDouble(t -> t.getEstimatedHours() != null ? t.getEstimatedHours() : 8.0)
            .sum();
        
        // Calcula velocidade da equipe
        List<Task> completedTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.DONE);
        double avgCompletionHours = completedTasks.stream()
            .mapToDouble(t -> t.getActualHours() != null ? t.getActualHours() : 8.0)
            .average()
            .orElse(8.0);
        
        // Calcula dias necessários baseado na velocidade média da equipe
        int teamSize = project.getTeam() != null ? project.getTeam().getMemberCount() : 1;
        double effectiveHoursPerDay = teamSize * 6.0; // 6 horas produtivas por pessoa por dia
        
        // Ajusta baseado na velocidade média de conclusão
        if (avgCompletionHours > 0) {
            effectiveHoursPerDay = effectiveHoursPerDay * (8.0 / avgCompletionHours);
        }
        
        int daysNeeded = (int) Math.ceil(totalRemainingHours / effectiveHoursPerDay);
        
        return LocalDate.now().plusDays(daysNeeded);
    }

    @Override
    public List<Task> identifyParallelizableTasks(Project project) {
        List<Task> parallelizableTasks = new ArrayList<>();
        
        List<Task> todoTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.TODO);
        
        // Tarefas que não dependem de outras podem ser paralelizadas
        for (Task task : todoTasks) {
            if (task.getTags() == null || !task.getTags().contains("dependency")) {
                parallelizableTasks.add(task);
            }
        }
        
        return parallelizableTasks;
    }

    @Override
    public List<Task> suggestOptimalTaskOrder(Project project) {
        List<Task> allTasks = taskRepository.findByProjectId(project.getId());
        
        // Ordena por prioridade e dependências
        return allTasks.stream()
            .sorted((t1, t2) -> {
                // Primeiro por prioridade
                int priorityComparison = t2.getPriority().ordinal() - t1.getPriority().ordinal();
                if (priorityComparison != 0) return priorityComparison;
                
                // Depois por data de criação (mais antigas primeiro)
                if (t1.getCreatedAt() != null && t2.getCreatedAt() != null) {
                    return t1.getCreatedAt().compareTo(t2.getCreatedAt());
                }
                
                return 0;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> analyzeEstimationAccuracy(Project project) {
        Map<String, Object> analysis = new HashMap<>();
        
        List<Task> completedTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.DONE);
        
        if (completedTasks.isEmpty()) {
            analysis.put("message", "Não há dados suficientes para análise");
            return analysis;
        }
        
        // Precisão das estimativas
        List<Task> accurateTasks = completedTasks.stream()
            .filter(t -> t.getEstimatedHours() != null && t.getActualHours() != null)
            .filter(t -> {
                double ratio = (double) t.getEstimatedHours() / t.getActualHours();
                return ratio >= 0.8 && ratio <= 1.2;
            })
            .collect(Collectors.toList());
        
        double accuracyPercentage = (double) accurateTasks.size() / completedTasks.size() * 100;
        analysis.put("accuracy_percentage", accuracyPercentage);
        
        // Tarefas superestimadas vs subestimadas
        long overestimated = taskRepository.findOverestimatedTasks().size();
        long underestimated = taskRepository.findUnderestimatedTasks().size();
        
        analysis.put("overestimated_count", overestimated);
        analysis.put("underestimated_count", underestimated);
        
        return analysis;
    }

    @Override
    public Map<Long, Map<String, Object>> generateTeamMemberInsights(Project project) {
        Map<Long, Map<String, Object>> insights = new HashMap<>();
        
        if (project.getTeam() == null) {
            return insights;
        }
        
        List<User> teamMembers = userRepository.findUsersByTeamId(project.getTeam().getId());
        
        for (User member : teamMembers) {
            Map<String, Object> memberInsights = new HashMap<>();
            
            // Tarefas completadas
            long completedTasks = taskRepository.countByAssigneeAndStatus(member.getId(), Task.TaskStatus.DONE);
            memberInsights.put("completed_tasks", completedTasks);
            
            // Tarefas ativas
            long activeTasks = taskRepository.countByAssigneeAndStatus(member.getId(), Task.TaskStatus.IN_PROGRESS);
            memberInsights.put("active_tasks", activeTasks);
            
            // Precisão das estimativas
            List<Task> memberCompletedTasks = taskRepository.findByAssigneeIdAndStatus(member.getId(), Task.TaskStatus.DONE);
            double estimationAccuracy = memberCompletedTasks.stream()
                .filter(t -> t.getEstimatedHours() != null && t.getActualHours() != null)
                .mapToDouble(t -> (double) t.getEstimatedHours() / t.getActualHours())
                .average()
                .orElse(1.0);
            memberInsights.put("estimation_accuracy", estimationAccuracy);
            
            // Sobrecarga
            boolean isOverloaded = detectUserOverload(member);
            memberInsights.put("is_overloaded", isOverloaded);
            
            insights.put(member.getId(), memberInsights);
        }
        
        return insights;
    }

    @Override
    public Map<String, Object> suggestWorkloadDistributionImprovements(Project project) {
        Map<String, Object> suggestions = new HashMap<>();
        
        Map<Long, Map<String, Object>> memberInsights = generateTeamMemberInsights(project);
        
        List<User> overloadedMembers = new ArrayList<>();
        List<User> underutilizedMembers = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : memberInsights.entrySet()) {
            Map<String, Object> insights = entry.getValue();
            Boolean isOverloaded = (Boolean) insights.get("is_overloaded");
            Long completedTasks = (Long) insights.get("completed_tasks");
            
            if (Boolean.TRUE.equals(isOverloaded)) {
                overloadedMembers.add(userRepository.findById(entry.getKey()).orElse(null));
            } else if (completedTasks != null && completedTasks < 3) {
                underutilizedMembers.add(userRepository.findById(entry.getKey()).orElse(null));
            }
        }
        
        suggestions.put("overloaded_members", overloadedMembers.size());
        suggestions.put("underutilized_members", underutilizedMembers.size());
        
        if (!overloadedMembers.isEmpty()) {
            suggestions.put("recommendation", "Redistribuir tarefas dos membros sobrecarregados");
        }
        
        return suggestions;
    }

    @Override
    public Map<String, List<String>> identifyDelayPatterns(Project project) {
        Map<String, List<String>> patterns = new HashMap<>();
        
        List<Task> overdueTasks = taskRepository.findByDueDateBeforeAndStatusNot(
            LocalDate.now(), Task.TaskStatus.DONE);
        
        // Padrões por tipo de tarefa
        Map<Task.TaskType, Long> delaysByType = overdueTasks.stream()
            .collect(Collectors.groupingBy(Task::getType, Collectors.counting()));
        
        patterns.put("delays_by_type", delaysByType.entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.toList()));
        
        // Padrões por assignee
        Map<Long, Long> delaysByAssignee = overdueTasks.stream()
            .filter(t -> t.getAssignee() != null)
            .collect(Collectors.groupingBy(t -> t.getAssignee().getId(), Collectors.counting()));
        
        patterns.put("delays_by_assignee", delaysByAssignee.entrySet().stream()
            .map(e -> "User " + e.getKey() + ": " + e.getValue())
            .collect(Collectors.toList()));
        
        // Padrões por prioridade
        Map<Task.TaskPriority, Long> delaysByPriority = overdueTasks.stream()
            .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));
        
        patterns.put("delays_by_priority", delaysByPriority.entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.toList()));
        
        return patterns;
    }

    @Override
    public Map<String, String> suggestRiskMitigationStrategies(Project project) {
        Map<String, String> strategies = new HashMap<>();
        
        Map<String, Double> risks = analyzeProjectRisks(project);
        
        if (risks.get("DELAY_RISK") > 30) {
            strategies.put("DELAY_RISK", "Implementar buffer de tempo e revisar prioridades");
        }
        
        if (risks.get("OVERLOAD_RISK") > 50) {
            strategies.put("OVERLOAD_RISK", "Redistribuir trabalho e contratar recursos adicionais");
        }
        
        if (risks.get("ESTIMATION_RISK") > 40) {
            strategies.put("ESTIMATION_RISK", "Implementar processo de estimativa em equipe");
        }
        
        if (risks.get("DEPENDENCY_RISK") > 30) {
            strategies.put("DEPENDENCY_RISK", "Mapear dependências e criar planos de contingência");
        }
        
        return strategies;
    }

    @Override
    public Map<String, Object> analyzeReviewProcessEfficiency(Project project) {
        Map<String, Object> analysis = new HashMap<>();
        
        List<Task> reviewTasks = taskRepository.findByProjectIdAndStatus(project.getId(), Task.TaskStatus.IN_REVIEW);
        
        analysis.put("tasks_in_review", reviewTasks.size());
        
        // Tempo médio em revisão
        double avgReviewTime = reviewTasks.stream()
            .filter(t -> t.getStartDate() != null)
            .mapToDouble(t -> ChronoUnit.DAYS.between(t.getStartDate(), LocalDate.now()))
            .average()
            .orElse(0.0);
        
        analysis.put("average_review_days", avgReviewTime);
        
        // Tarefas bloqueadas em revisão
        long longReviewTasks = reviewTasks.stream()
            .filter(t -> t.getStartDate() != null && 
                        ChronoUnit.DAYS.between(t.getStartDate(), LocalDate.now()) > 3)
            .count();
        
        analysis.put("long_review_tasks", longReviewTasks);
        
        return analysis;
    }

    @Override
    public List<String> suggestCommunicationImprovements(Project project) {
        List<String> improvements = new ArrayList<>();
        
        // Análise de gargalos
        List<String> bottlenecks = identifyProjectBottlenecks(project);
        if (!bottlenecks.isEmpty()) {
            improvements.add("Implementar reuniões de sincronização mais frequentes");
        }
        
        // Análise de produtividade
        Map<String, Object> productivityPatterns = analyzeTeamProductivityPatterns(project);
        Double avgCompletionDays = (Double) productivityPatterns.get("average_completion_days");
        if (avgCompletionDays != null && avgCompletionDays > 5) {
            improvements.add("Implementar daily standups");
        }
        
        // Recomendações gerais
        improvements.add("Criar canal de comunicação dedicado para o projeto");
        improvements.add("Implementar sistema de notificações para mudanças críticas");
        improvements.add("Documentar decisões importantes em reuniões");
        improvements.add("Estabelecer processo de escalação para bloqueios");
        
        return improvements;
    }
}
