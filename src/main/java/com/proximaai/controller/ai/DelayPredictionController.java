package com.proximaai.controller.ai;

import com.proximaai.domain.entity.Task;
import com.proximaai.domain.entity.ai.DelayPrediction;
import com.proximaai.service.ai.DelayPredictionService;
import com.proximaai.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai/delay-predictions")
@Tag(name = "Delay Prediction AI", description = "APIs para predição de atrasos com Machine Learning")
public class DelayPredictionController {

    @Autowired
    private DelayPredictionService delayPredictionService;

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/task/{taskId}")
    @Operation(summary = "Gerar predição de atraso para uma tarefa", 
               description = "Usa algoritmos de ML para prever possíveis atrasos em uma tarefa específica")
    public ResponseEntity<DelayPrediction> predictDelayForTask(
            @Parameter(description = "ID da tarefa") @PathVariable @Positive Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        DelayPrediction prediction = delayPredictionService.predictDelayForTask(taskOpt.get());
        return ResponseEntity.ok(prediction);
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Buscar predições de uma tarefa", 
               description = "Retorna todas as predições de atraso para uma tarefa específica")
    public ResponseEntity<List<DelayPrediction>> getPredictionsForTask(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<DelayPrediction> predictions = delayPredictionService.getPredictionsForTask(taskOpt.get());
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/critical")
    @Operation(summary = "Buscar predições críticas", 
               description = "Retorna predições com alto risco de atraso que precisam de atenção")
    public ResponseEntity<List<DelayPrediction>> getCriticalPredictions() {
        List<DelayPrediction> predictions = delayPredictionService.getCriticalPredictions();
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/high-confidence")
    @Operation(summary = "Buscar predições com alta confiança", 
               description = "Retorna predições com score de confiança acima de 80%")
    public ResponseEntity<List<DelayPrediction>> getHighConfidencePredictions() {
        List<DelayPrediction> predictions = delayPredictionService.getHighConfidencePredictions();
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Buscar predições por projeto", 
               description = "Retorna predições recentes para todas as tarefas de um projeto")
    public ResponseEntity<List<DelayPrediction>> getPredictionsForProject(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        
        List<DelayPrediction> predictions = delayPredictionService.getPredictionsForProject(projectId);
        return ResponseEntity.ok(predictions);
    }

    @PutMapping("/{predictionId}/accuracy")
    @Operation(summary = "Atualizar precisão da predição", 
               description = "Atualiza a predição com dados reais de atraso para melhorar o modelo")
    public ResponseEntity<Void> updatePredictionAccuracy(
            @Parameter(description = "ID da predição") @PathVariable Long predictionId,
            @Parameter(description = "Dias de atraso real") @RequestParam Integer actualDelayDays) {
        
        delayPredictionService.updatePredictionAccuracy(predictionId, actualDelayDays);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/alerts")
    @Operation(summary = "Buscar predições que precisam de alertas", 
               description = "Retorna predições críticas que ainda não tiveram alertas enviados")
    public ResponseEntity<List<DelayPrediction>> getPredictionsNeedingAlerts() {
        List<DelayPrediction> predictions = delayPredictionService.getPredictionsNeedingAlerts();
        return ResponseEntity.ok(predictions);
    }

    @PutMapping("/{predictionId}/alert-sent")
    @Operation(summary = "Marcar alerta como enviado", 
               description = "Marca que o alerta para uma predição crítica foi enviado")
    public ResponseEntity<Void> markAlertSent(
            @Parameter(description = "ID da predição") @PathVariable Long predictionId) {
        
        delayPredictionService.markAlertSent(predictionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/accuracy")
    @Operation(summary = "Calcular precisão geral das predições", 
               description = "Retorna a precisão média de todas as predições com dados reais")
    public ResponseEntity<BigDecimal> calculatePredictionAccuracy() {
        BigDecimal accuracy = delayPredictionService.calculatePredictionAccuracy();
        return ResponseEntity.ok(accuracy);
    }

    @PostMapping("/task/{taskId}/recalculate")
    @Operation(summary = "Recalcular predições para uma tarefa", 
               description = "Remove predições antigas e gera nova predição baseada nos dados atuais")
    public ResponseEntity<DelayPrediction> recalculatePredictionsForTask(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        delayPredictionService.recalculatePredictionsForTask(taskOpt.get());
        
        // Retorna a nova predição
        List<DelayPrediction> predictions = delayPredictionService.getPredictionsForTask(taskOpt.get());
        if (!predictions.isEmpty()) {
            return ResponseEntity.ok(predictions.get(0));
        }
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/task/{taskId}/needs-prediction")
    @Operation(summary = "Verificar se tarefa precisa de nova predição", 
               description = "Verifica se uma tarefa precisa de nova predição baseado em critérios de tempo e mudanças")
    public ResponseEntity<Boolean> needsNewPrediction(
            @Parameter(description = "ID da tarefa") @PathVariable Long taskId) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        boolean needsPrediction = delayPredictionService.needsNewPrediction(taskOpt.get());
        return ResponseEntity.ok(needsPrediction);
    }
}
