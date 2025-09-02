package com.proximaai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@ConfigurationProperties(prefix = "proxima.ai")
public class AIConfig {

    private String openaiApiKey;
    private String openaiModel = "gpt-3.5-turbo";
    private String openaiBaseUrl = "https://api.openai.com/v1";
    private int maxTokens = 1000;
    private double temperature = 0.7;
    
    // ML Configuration
    private boolean enableMLPredictions = true;
    private boolean enableSentimentAnalysis = true;
    private boolean enableDelayPrediction = true;
    private boolean enableAllocationRecommendation = true;
    private boolean enableProjectSummary = true;
    
    // Performance Configuration
    private int predictionCacheSize = 1000;
    private int analysisCacheSize = 500;
    private long cacheExpirationMinutes = 60;
    
    // Thread Pool Configuration
    private int corePoolSize = 5;
    private int maxPoolSize = 20;
    private int queueCapacity = 100;

    @Bean(name = "aiTaskExecutor")
    public Executor aiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("AI-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    // Getters and Setters
    public String getOpenaiApiKey() {
        return openaiApiKey;
    }

    public void setOpenaiApiKey(String openaiApiKey) {
        this.openaiApiKey = openaiApiKey;
    }

    public String getOpenaiModel() {
        return openaiModel;
    }

    public void setOpenaiModel(String openaiModel) {
        this.openaiModel = openaiModel;
    }

    public String getOpenaiBaseUrl() {
        return openaiBaseUrl;
    }

    public void setOpenaiBaseUrl(String openaiBaseUrl) {
        this.openaiBaseUrl = openaiBaseUrl;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean isEnableMLPredictions() {
        return enableMLPredictions;
    }

    public void setEnableMLPredictions(boolean enableMLPredictions) {
        this.enableMLPredictions = enableMLPredictions;
    }

    public boolean isEnableSentimentAnalysis() {
        return enableSentimentAnalysis;
    }

    public void setEnableSentimentAnalysis(boolean enableSentimentAnalysis) {
        this.enableSentimentAnalysis = enableSentimentAnalysis;
    }

    public boolean isEnableDelayPrediction() {
        return enableDelayPrediction;
    }

    public void setEnableDelayPrediction(boolean enableDelayPrediction) {
        this.enableDelayPrediction = enableDelayPrediction;
    }

    public boolean isEnableAllocationRecommendation() {
        return enableAllocationRecommendation;
    }

    public void setEnableAllocationRecommendation(boolean enableAllocationRecommendation) {
        this.enableAllocationRecommendation = enableAllocationRecommendation;
    }

    public boolean isEnableProjectSummary() {
        return enableProjectSummary;
    }

    public void setEnableProjectSummary(boolean enableProjectSummary) {
        this.enableProjectSummary = enableProjectSummary;
    }

    public int getPredictionCacheSize() {
        return predictionCacheSize;
    }

    public void setPredictionCacheSize(int predictionCacheSize) {
        this.predictionCacheSize = predictionCacheSize;
    }

    public int getAnalysisCacheSize() {
        return analysisCacheSize;
    }

    public void setAnalysisCacheSize(int analysisCacheSize) {
        this.analysisCacheSize = analysisCacheSize;
    }

    public long getCacheExpirationMinutes() {
        return cacheExpirationMinutes;
    }

    public void setCacheExpirationMinutes(long cacheExpirationMinutes) {
        this.cacheExpirationMinutes = cacheExpirationMinutes;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}
