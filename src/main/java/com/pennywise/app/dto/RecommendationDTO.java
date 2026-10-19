package com.pennywise.app.dto;

import java.math.BigDecimal;

public class RecommendationDTO {
    private String id;
    private String type; // "SAVINGS", "BUDGET_ADVICE", "ANOMALY_WARNING", "PATTERN_INSIGHT"
    private String priority; // "HIGH", "MEDIUM", "LOW"
    private String title;
    private String description;
    private String actionSuggestion;
    private BigDecimal potentialMonthlySavings;

    public RecommendationDTO() {}

    public RecommendationDTO(String id, String type, String priority, String title, String description,
                             String actionSuggestion, BigDecimal potentialMonthlySavings) {
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.actionSuggestion = actionSuggestion;
        this.potentialMonthlySavings = potentialMonthlySavings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

    public String getActionSuggestion() {
        return actionSuggestion;
    }

    public void setActionSuggestion(String actionSuggestion) {
        this.actionSuggestion = actionSuggestion;
    }

    public BigDecimal getPotentialMonthlySavings() {
        return potentialMonthlySavings;
    }

    public void setPotentialMonthlySavings(BigDecimal potentialMonthlySavings) {
        this.potentialMonthlySavings = potentialMonthlySavings;
    }
}
