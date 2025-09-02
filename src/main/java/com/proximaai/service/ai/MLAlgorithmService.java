package com.proximaai.service.ai;

import org.springframework.stereotype.Service;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MLAlgorithmService {

    // Constants for ML algorithms
    private static final int MIN_CORRELATION_SAMPLES = 2;
    private static final double ANOMALY_Z_SCORE_THRESHOLD = 2.0;
    private static final double CENTROID_CONVERGENCE_THRESHOLD = 0.001;
    private static final int DEFAULT_CENTROID_DIMENSIONS = 10;

    /**
     * Algoritmo de regressão linear para predição de atrasos
     */
    public BigDecimal predictDelayUsingLinearRegression(List<Double> features, List<Double> targets) {
        if (features.size() != targets.size() || features.isEmpty()) {
            return BigDecimal.ZERO;
        }

        SimpleRegression regression = new SimpleRegression();
        
        for (int i = 0; i < features.size(); i++) {
            regression.addData(features.get(i), targets.get(i));
        }

        // Predição baseada na média das features
        double avgFeature = features.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double prediction = regression.predict(avgFeature);
        
        return BigDecimal.valueOf(Math.max(0, prediction))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Algoritmo de correlação para análise de dependências
     */
    public BigDecimal calculateCorrelation(List<Double> x, List<Double> y) {
        if (x.size() != y.size() || x.size() < MIN_CORRELATION_SAMPLES) {
            return BigDecimal.ZERO;
        }

        PearsonsCorrelation correlation = new PearsonsCorrelation();
        double[] xArray = x.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yArray = y.stream().mapToDouble(Double::doubleValue).toArray();
        
        double correlationValue = correlation.correlation(xArray, yArray);
        
        return BigDecimal.valueOf(correlationValue)
                .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Algoritmo de clustering K-means simplificado para agrupamento de tarefas
     */
    public List<List<Integer>> performKMeansClustering(List<double[]> dataPoints, int k, int maxIterations) {
        if (dataPoints.isEmpty() || k <= 0) {
            return List.of();
        }

        int dimensions = dataPoints.get(0).length;
        double[][] centroids = initializeCentroids(dataPoints, k, dimensions);
        
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<List<Integer>> clusters = assignPointsToClusters(dataPoints, centroids);
            double[][] newCentroids = calculateNewCentroids(dataPoints, clusters, k, dimensions);
            
            if (centroidsConverged(centroids, newCentroids)) {
                break;
            }
            
            centroids = newCentroids;
        }
        
        return assignPointsToClusters(dataPoints, centroids);
    }

    /**
     * Algoritmo de análise de sentimento baseado em palavras-chave
     */
    public BigDecimal analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Palavras-chave positivas e negativas (simplificado)
        Map<String, Integer> positiveWords = new HashMap<>();
        positiveWords.put("excellent", 2);
        positiveWords.put("great", 2);
        positiveWords.put("good", 1);
        positiveWords.put("amazing", 2);
        positiveWords.put("wonderful", 2);
        positiveWords.put("perfect", 2);
        positiveWords.put("fantastic", 2);
        positiveWords.put("outstanding", 2);
        positiveWords.put("superb", 2);
        positiveWords.put("brilliant", 2);
        positiveWords.put("success", 1);
        positiveWords.put("complete", 1);
        positiveWords.put("done", 1);
        positiveWords.put("finished", 1);
        positiveWords.put("achieved", 1);

        Map<String, Integer> negativeWords = new HashMap<>();
        negativeWords.put("terrible", -2);
        negativeWords.put("awful", -2);
        negativeWords.put("bad", -1);
        negativeWords.put("horrible", -2);
        negativeWords.put("disappointing", -2);
        negativeWords.put("failed", -2);
        negativeWords.put("broken", -1);
        negativeWords.put("error", -1);
        negativeWords.put("problem", -1);
        negativeWords.put("issue", -1);
        negativeWords.put("delay", -1);
        negativeWords.put("late", -1);
        negativeWords.put("overdue", -2);
        negativeWords.put("stuck", -1);
        negativeWords.put("blocked", -1);

        String[] words = text.toLowerCase().split("\\s+");
        int sentimentScore = 0;
        int wordCount = 0;

        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "");
            if (positiveWords.containsKey(word)) {
                sentimentScore += positiveWords.get(word);
                wordCount++;
            } else if (negativeWords.containsKey(word)) {
                sentimentScore += negativeWords.get(word);
                wordCount++;
            }
        }

        if (wordCount == 0) {
            return BigDecimal.ZERO;
        }

        // Normaliza o score entre -1 e 1
        double normalizedScore = (double) sentimentScore / (wordCount * 2);
        return BigDecimal.valueOf(Math.max(-1.0, Math.min(1.0, normalizedScore)))
                .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Algoritmo de recomendação baseado em similaridade
     */
    public BigDecimal calculateSimilarityScore(List<Double> userProfile, List<Double> taskRequirements) {
        if (userProfile.size() != taskRequirements.size() || userProfile.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Calcula similaridade usando produto escalar normalizado
        double dotProduct = 0.0;
        double userNorm = 0.0;
        double taskNorm = 0.0;

        for (int i = 0; i < userProfile.size(); i++) {
            dotProduct += userProfile.get(i) * taskRequirements.get(i);
            userNorm += userProfile.get(i) * userProfile.get(i);
            taskNorm += taskRequirements.get(i) * taskRequirements.get(i);
        }

        if (userNorm == 0.0 || taskNorm == 0.0) {
            return BigDecimal.ZERO;
        }

        double similarity = dotProduct / (Math.sqrt(userNorm) * Math.sqrt(taskNorm));
        return BigDecimal.valueOf(Math.max(0.0, Math.min(1.0, similarity)))
                .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Algoritmo de detecção de anomalias usando Z-score
     */
    public boolean isAnomaly(double value, List<Double> historicalData) {
        if (historicalData.size() < MIN_CORRELATION_SAMPLES + 1) {
            return false;
        }

        DescriptiveStatistics stats = new DescriptiveStatistics();
        historicalData.forEach(stats::addValue);

        double mean = stats.getMean();
        double stdDev = stats.getStandardDeviation();

        if (stdDev == 0) {
            return false;
        }

        double zScore = Math.abs((value - mean) / stdDev);
        return zScore > ANOMALY_Z_SCORE_THRESHOLD;
    }

    /**
     * Algoritmo de predição de tendência usando média móvel
     */
    public BigDecimal predictTrend(List<Double> timeSeries, int windowSize) {
        if (timeSeries.size() < windowSize) {
            return BigDecimal.ZERO;
        }

        // Calcula média móvel das últimas observações
        double recentAverage = timeSeries.stream()
                .skip(timeSeries.size() - windowSize)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // Calcula média móvel das observações anteriores
        double previousAverage = timeSeries.stream()
                .limit(timeSeries.size() - windowSize)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        if (previousAverage == 0) {
            return BigDecimal.ZERO;
        }

        double trend = (recentAverage - previousAverage) / previousAverage;
        return BigDecimal.valueOf(trend)
                .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Algoritmo de otimização de recursos usando algoritmo guloso
     */
    public List<Integer> optimizeResourceAllocation(List<Double> taskComplexities, List<Double> userCapacities) {
        if (taskComplexities.isEmpty() || userCapacities.isEmpty()) {
            return List.of();
        }

        List<Integer> allocation = new java.util.ArrayList<>();
        double[] remainingCapacity = userCapacities.stream().mapToDouble(Double::doubleValue).toArray();

        for (double complexity : taskComplexities) {
            int bestUser = -1;
            double bestFit = Double.MAX_VALUE;

            for (int i = 0; i < remainingCapacity.length; i++) {
                if (remainingCapacity[i] >= complexity) {
                    double fit = remainingCapacity[i] - complexity;
                    if (fit < bestFit) {
                        bestFit = fit;
                        bestUser = i;
                    }
                }
            }

            if (bestUser != -1) {
                allocation.add(bestUser);
                remainingCapacity[bestUser] -= complexity;
            } else {
                allocation.add(-1); // Não foi possível alocar
            }
        }

        return allocation;
    }

    // Métodos auxiliares para K-means
    private double[][] initializeCentroids(List<double[]> dataPoints, int k, int dimensions) {
        double[][] centroids = new double[k][dimensions];
        
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < dimensions; j++) {
                centroids[i][j] = Math.random() * DEFAULT_CENTROID_DIMENSIONS;
            }
        }
        
        return centroids;
    }

    private List<List<Integer>> assignPointsToClusters(List<double[]> dataPoints, double[][] centroids) {
        List<List<Integer>> clusters = new java.util.ArrayList<>();
        for (int i = 0; i < centroids.length; i++) {
            clusters.add(new java.util.ArrayList<>());
        }

        for (int i = 0; i < dataPoints.size(); i++) {
            double[] point = dataPoints.get(i);
            int closestCentroid = 0;
            double minDistance = Double.MAX_VALUE;

            for (int j = 0; j < centroids.length; j++) {
                double distance = calculateEuclideanDistance(point, centroids[j]);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCentroid = j;
                }
            }

            clusters.get(closestCentroid).add(i);
        }

        return clusters;
    }

    private double[][] calculateNewCentroids(List<double[]> dataPoints, List<List<Integer>> clusters, int k, int dimensions) {
        double[][] newCentroids = new double[k][dimensions];

        for (int i = 0; i < k; i++) {
            List<Integer> cluster = clusters.get(i);
            if (cluster.isEmpty()) {
                continue;
            }

            for (int j = 0; j < dimensions; j++) {
                double sum = 0.0;
                for (int pointIndex : cluster) {
                    sum += dataPoints.get(pointIndex)[j];
                }
                newCentroids[i][j] = sum / cluster.size();
            }
        }

        return newCentroids;
    }

    private boolean centroidsConverged(double[][] oldCentroids, double[][] newCentroids) {
        double threshold = CENTROID_CONVERGENCE_THRESHOLD;
        
        for (int i = 0; i < oldCentroids.length; i++) {
            for (int j = 0; j < oldCentroids[i].length; j++) {
                if (Math.abs(oldCentroids[i][j] - newCentroids[i][j]) > threshold) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private double calculateEuclideanDistance(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }
}
