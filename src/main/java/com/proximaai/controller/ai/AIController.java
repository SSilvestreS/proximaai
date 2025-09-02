package com.proximaai.controller.ai;

import com.proximaai.domain.entity.Project;
import com.proximaai.domain.entity.Task;
import com.proximaai.domain.entity.User;
import com.proximaai.service.ai.AIService;
import com.proximaai.repository.ProjectRepository;
import com.proximaai.repository.TaskRepository;
import com.proximaai.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Services", description = "APIs principais para funcionalidades de IA e Machine Learning")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/tasks/{taskId}/estimate-duration")
    @Operation(summary = "Estimar duração de uma tarefa", 
               description = "Usa IA para estimar a duração de uma tarefa baseada em dados históricos")
    public ResponseEntity<Map<String, Object>> estimateTaskDuration(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId,
            @Parameter(description = "Descrição da tarefa") @RequestParam(required = false) String description) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Integer estimatedHours = aiService.estimateTaskDuration(taskOpt.get(), description);
        
        return ResponseEntity.ok(Map.of(
            "taskId", taskId,
            "estimatedHours", estimatedHours,
            "estimatedDays", Math.ceil(estimatedHours / 8.0)
        ));
    }

    @GetMapping("/tasks/{taskId}/priority-score")
    @Operation(summary = "Calcular score de prioridade", 
               description = "Calcula um score de prioridade para uma tarefa baseado em múltiplos fatores")
    public ResponseEntity<Map<String, Object>> calculateTaskPriorityScore(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Double priorityScore = aiService.calculateTaskPriorityScore(taskOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "taskId", taskId,
            "priorityScore", priorityScore,
            "priorityLevel", getPriorityLevel(priorityScore)
        ));
    }

    @GetMapping("/users/{userId}/overload-check")
    @Operation(summary = "Verificar sobrecarga de usuário", 
               description = "Verifica se um usuário está sobrecarregado com tarefas")
    public ResponseEntity<Map<String, Object>> detectUserOverload(
            @Parameter(description = "ID do usuário") @PathVariable Long userId) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        boolean isOverloaded = aiService.detectUserOverload(userOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "isOverloaded", isOverloaded,
            "status", isOverloaded ? "OVERLOADED" : "NORMAL"
        ));
    }

    @PostMapping("/tasks/{taskId}/suggest-deadline")
    @Operation(summary = "Sugerir prazo para tarefa", 
               description = "Sugere um prazo para uma tarefa baseado na capacidade da equipe")
    public ResponseEntity<Map<String, Object>> suggestTaskDeadline(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Task task = taskOpt.get();
        LocalDate suggestedDeadline = aiService.suggestTaskDeadline(task, task.getProject());
        
        return ResponseEntity.ok(Map.of(
            "taskId", taskId,
            "suggestedDeadline", suggestedDeadline,
            "daysFromNow", java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), suggestedDeadline)
        ));
    }

    @PostMapping("/tasks/{taskId}/recommend-assignee")
    @Operation(summary = "Recomendar responsável para tarefa", 
               description = "Recomenda o melhor profissional para uma tarefa baseado em skills e disponibilidade")
    public ResponseEntity<Map<String, Object>> recommendAssignee(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Busca usuários disponíveis (implementação simplificada)
        List<User> availableUsers = userRepository.findAll().stream().limit(5).toList();
        User recommendedUser = aiService.recommendAssignee(taskOpt.get(), availableUsers);
        
        if (recommendedUser == null) {
            return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "recommendedUser", null,
                "message", "Nenhum usuário disponível encontrado"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "taskId", taskId,
            "recommendedUser", Map.of(
                "id", recommendedUser.getId(),
                "username", recommendedUser.getUsername(),
                "email", recommendedUser.getEmail()
            )
        ));
    }

    @GetMapping("/projects/{projectId}/risks")
    @Operation(summary = "Analisar riscos do projeto", 
               description = "Analisa riscos do projeto baseado em padrões históricos")
    public ResponseEntity<Map<String, Object>> analyzeProjectRisks(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Double> risks = aiService.analyzeProjectRisks(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "risks", risks,
            "overallRiskLevel", calculateOverallRiskLevel(risks)
        ));
    }

    @GetMapping("/projects/{projectId}/bottlenecks")
    @Operation(summary = "Identificar gargalos do projeto", 
               description = "Identifica gargalos e dependências críticas no projeto")
    public ResponseEntity<Map<String, Object>> identifyProjectBottlenecks(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<String> bottlenecks = aiService.identifyProjectBottlenecks(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "bottlenecks", bottlenecks,
            "bottleneckCount", bottlenecks.size(),
            "hasBottlenecks", !bottlenecks.isEmpty()
        ));
    }

    @GetMapping("/projects/{projectId}/optimization-recommendations")
    @Operation(summary = "Recomendações de otimização", 
               description = "Gera recomendações de otimização para o projeto")
    public ResponseEntity<Map<String, Object>> generateProjectOptimizationRecommendations(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<String> recommendations = aiService.generateProjectOptimizationRecommendations(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "recommendations", recommendations,
            "recommendationCount", recommendations.size()
        ));
    }

    @GetMapping("/projects/{projectId}/productivity-patterns")
    @Operation(summary = "Analisar padrões de produtividade", 
               description = "Analisa padrões de produtividade da equipe")
    public ResponseEntity<Map<String, Object>> analyzeTeamProductivityPatterns(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> patterns = aiService.analyzeTeamProductivityPatterns(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "productivityPatterns", patterns
        ));
    }

    @GetMapping("/projects/{projectId}/completion-prediction")
    @Operation(summary = "Predizer data de conclusão", 
               description = "Prediz a data de conclusão do projeto baseado no progresso atual")
    public ResponseEntity<Map<String, Object>> predictProjectCompletionDate(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        LocalDate predictedDate = aiService.predictProjectCompletionDate(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "predictedCompletionDate", predictedDate,
            "daysFromNow", java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), predictedDate)
        ));
    }

    @GetMapping("/projects/{projectId}/parallelizable-tasks")
    @Operation(summary = "Identificar tarefas paralelizáveis", 
               description = "Identifica tarefas que podem ser executadas em paralelo")
    public ResponseEntity<Map<String, Object>> identifyParallelizableTasks(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Task> parallelizableTasks = aiService.identifyParallelizableTasks(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "parallelizableTasks", parallelizableTasks.stream()
                .map(task -> Map.of(
                    "id", task.getId(),
                    "title", task.getTitle(),
                    "type", task.getType()
                )).toList(),
            "parallelizableCount", parallelizableTasks.size()
        ));
    }

    @GetMapping("/projects/{projectId}/optimal-task-order")
    @Operation(summary = "Sugerir ordem ótima de tarefas", 
               description = "Sugere a ordem ideal de execução das tarefas para otimizar o fluxo")
    public ResponseEntity<Map<String, Object>> suggestOptimalTaskOrder(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Task> optimalOrder = aiService.suggestOptimalTaskOrder(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "optimalTaskOrder", optimalOrder.stream()
                .map(task -> Map.of(
                    "id", task.getId(),
                    "title", task.getTitle(),
                    "priority", task.getPriority(),
                    "type", task.getType()
                )).toList(),
            "totalTasks", optimalOrder.size()
        ));
    }

    @GetMapping("/projects/{projectId}/estimation-accuracy")
    @Operation(summary = "Analisar precisão das estimativas", 
               description = "Analisa a qualidade das estimativas de tempo passadas")
    public ResponseEntity<Map<String, Object>> analyzeEstimationAccuracy(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> analysis = aiService.analyzeEstimationAccuracy(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "estimationAnalysis", analysis
        ));
    }

    @GetMapping("/projects/{projectId}/team-insights")
    @Operation(summary = "Insights dos membros da equipe", 
               description = "Gera insights sobre performance individual dos membros da equipe")
    public ResponseEntity<Map<String, Object>> generateTeamMemberInsights(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<Long, Map<String, Object>> insights = aiService.generateTeamMemberInsights(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "teamInsights", insights
        ));
    }

    @GetMapping("/projects/{projectId}/delay-patterns")
    @Operation(summary = "Identificar padrões de atraso", 
               description = "Identifica padrões de atraso e suas causas")
    public ResponseEntity<Map<String, Object>> identifyDelayPatterns(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, List<String>> patterns = aiService.identifyDelayPatterns(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "delayPatterns", patterns
        ));
    }

    @GetMapping("/projects/{projectId}/risk-mitigation")
    @Operation(summary = "Estratégias de mitigação de riscos", 
               description = "Sugere estratégias para mitigar riscos identificados")
    public ResponseEntity<Map<String, Object>> suggestRiskMitigationStrategies(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, String> strategies = aiService.suggestRiskMitigationStrategies(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "mitigationStrategies", strategies
        ));
    }

    @GetMapping("/projects/{projectId}/communication-improvements")
    @Operation(summary = "Melhorias de comunicação", 
               description = "Gera recomendações para melhorar a comunicação da equipe")
    public ResponseEntity<Map<String, Object>> suggestCommunicationImprovements(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<String> improvements = aiService.suggestCommunicationImprovements(projectOpt.get());
        
        return ResponseEntity.ok(Map.of(
            "projectId", projectId,
            "communicationImprovements", improvements
        ));
    }

    private String getPriorityLevel(Double score) {
        if (score >= 80) return "CRITICAL";
        if (score >= 60) return "HIGH";
        if (score >= 40) return "MEDIUM";
        if (score >= 20) return "LOW";
        return "VERY_LOW";
    }

    private String calculateOverallRiskLevel(Map<String, Double> risks) {
        double avgRisk = risks.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        if (avgRisk >= 70) return "HIGH";
        if (avgRisk >= 40) return "MEDIUM";
        return "LOW";
    }
}
