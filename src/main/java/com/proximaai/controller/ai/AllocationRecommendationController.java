package com.proximaai.controller.ai;

import com.proximaai.domain.entity.Task;
import com.proximaai.domain.entity.User;
import com.proximaai.domain.entity.ai.AllocationRecommendation;
import com.proximaai.service.ai.AllocationRecommendationService;
import com.proximaai.repository.TaskRepository;
import com.proximaai.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai/allocation-recommendations")
@Tag(name = "Allocation Recommendation AI", description = "APIs para recomendações inteligentes de alocação de recursos")
public class AllocationRecommendationController {

    @Autowired
    private AllocationRecommendationService allocationRecommendationService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/task/{taskId}")
    @Operation(summary = "Gerar recomendações de alocação para uma tarefa", 
               description = "Usa IA para sugerir os melhores profissionais para uma tarefa específica")
    public ResponseEntity<List<AllocationRecommendation>> generateRecommendationsForTask(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<AllocationRecommendation> recommendations = 
            allocationRecommendationService.generateRecommendationsForTask(taskOpt.get());
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Buscar recomendações de uma tarefa", 
               description = "Retorna todas as recomendações de alocação para uma tarefa específica")
    public ResponseEntity<List<AllocationRecommendation>> getRecommendationsForTask(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<AllocationRecommendation> recommendations = 
            allocationRecommendationService.getRecommendationsForTask(taskOpt.get());
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/task/{taskId}/best")
    @Operation(summary = "Buscar melhor recomendação para uma tarefa", 
               description = "Retorna a melhor recomendação de alocação para uma tarefa específica")
    public ResponseEntity<AllocationRecommendation> getBestRecommendationForTask(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<AllocationRecommendation> recommendation = 
            allocationRecommendationService.getBestRecommendationForTask(taskOpt.get());
        
        return recommendation.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Buscar recomendações para um usuário", 
               description = "Retorna todas as recomendações onde um usuário foi sugerido")
    public ResponseEntity<List<AllocationRecommendation>> getRecommendationsForUser(
            @Parameter(description = "ID do usuário") @PathVariable Long userId) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<AllocationRecommendation> recommendations = 
            allocationRecommendationService.getRecommendationsForUser(userOpt.get());
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/high-confidence")
    @Operation(summary = "Buscar recomendações com alta confiança", 
               description = "Retorna recomendações com nível de confiança alto")
    public ResponseEntity<List<AllocationRecommendation>> getHighConfidenceRecommendations() {
        List<AllocationRecommendation> recommendations = 
            allocationRecommendationService.getHighConfidenceRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @PutMapping("/{recommendationId}/implement")
    @Operation(summary = "Marcar recomendação como implementada", 
               description = "Marca que uma recomendação foi aceita e implementada")
    public ResponseEntity<Void> markRecommendationAsImplemented(
            @Parameter(description = "ID da recomendação") @PathVariable Long recommendationId) {
        
        allocationRecommendationService.markRecommendationAsImplemented(recommendationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{recommendationId}/performance")
    @Operation(summary = "Atualizar performance real da recomendação", 
               description = "Atualiza a recomendação com dados reais de performance para melhorar o modelo")
    public ResponseEntity<Void> updateRecommendationPerformance(
            @Parameter(description = "ID da recomendação") @PathVariable Long recommendationId,
            @Parameter(description = "Score de performance real") @RequestParam BigDecimal actualPerformanceScore) {
        
        allocationRecommendationService.updateRecommendationPerformance(recommendationId, actualPerformanceScore);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/task/{taskId}/alternatives")
    @Operation(summary = "Buscar recomendações alternativas", 
               description = "Retorna recomendações alternativas excluindo um usuário específico")
    public ResponseEntity<List<AllocationRecommendation>> getAlternativeRecommendations(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId,
            @Parameter(description = "ID do usuário a excluir") @RequestParam Long excludeUserId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<User> userOpt = userRepository.findById(excludeUserId);
        
        if (taskOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<AllocationRecommendation> recommendations = 
            allocationRecommendationService.getAlternativeRecommendations(taskOpt.get(), userOpt.get());
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/task/{taskId}/recalculate")
    @Operation(summary = "Recalcular recomendações para uma tarefa", 
               description = "Remove recomendações antigas e gera novas baseadas nos dados atuais")
    public ResponseEntity<List<AllocationRecommendation>> recalculateRecommendationsForTask(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        allocationRecommendationService.recalculateRecommendationsForTask(taskOpt.get());
        
        // Retorna as novas recomendações
        List<AllocationRecommendation> recommendations = 
            allocationRecommendationService.getRecommendationsForTask(taskOpt.get());
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/quality")
    @Operation(summary = "Calcular qualidade geral das recomendações", 
               description = "Retorna a qualidade média de todas as recomendações implementadas")
    public ResponseEntity<BigDecimal> calculateRecommendationQuality() {
        BigDecimal quality = allocationRecommendationService.calculateRecommendationQuality();
        return ResponseEntity.ok(quality);
    }

    @GetMapping("/task/{taskId}/recommendation-summary")
    @Operation(summary = "Resumo de recomendações para uma tarefa", 
               description = "Retorna um resumo das recomendações com estatísticas")
    public ResponseEntity<Object> getRecommendationSummary(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<AllocationRecommendation> recommendations = 
            allocationRecommendationService.getRecommendationsForTask(taskOpt.get());
        
        if (recommendations.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Nenhuma recomendação disponível");
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> response = calculateRecommendationStatistics(recommendations);
        return ResponseEntity.ok(response);
    }

    /**
     * Calcula estatísticas das recomendações
     */
    private Map<String, Object> calculateRecommendationStatistics(List<AllocationRecommendation> recommendations) {
        BigDecimal avgScore = recommendations.stream()
            .map(AllocationRecommendation::getRecommendationScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(recommendations.size()), 4, java.math.RoundingMode.HALF_UP);
        
        long highConfidenceCount = recommendations.stream()
            .filter(r -> r.getConfidenceLevel() == AllocationRecommendation.ConfidenceLevel.HIGH)
            .count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalRecommendations", recommendations.size());
        response.put("averageScore", avgScore);
        response.put("highConfidenceCount", highConfidenceCount);
        response.put("bestRecommendation", recommendations.get(0)); // Primeira é a melhor (ordenada por score)
        return response;
    }
}
