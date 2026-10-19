package com.pennywise.app.dto;

import java.util.List;

public class InsightsResponse {
    private List<String> spendingPatterns;
    private List<AnomalyDTO> anomalies;
    private ForecastDTO forecast;
    private List<RecommendationDTO> recommendations;

    public InsightsResponse() {}

    public InsightsResponse(List<String> spendingPatterns, List<AnomalyDTO> anomalies,
                            ForecastDTO forecast, List<RecommendationDTO> recommendations) {
        this.spendingPatterns = spendingPatterns;
        this.anomalies = anomalies;
        this.forecast = forecast;
        this.recommendations = recommendations;
    }

    public List<String> getSpendingPatterns() {
        return spendingPatterns;
    }

    public void setSpendingPatterns(List<String> spendingPatterns) {
        this.spendingPatterns = spendingPatterns;
    }

    public List<AnomalyDTO> getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(List<AnomalyDTO> anomalies) {
        this.anomalies = anomalies;
    }

    public ForecastDTO getForecast() {
        return forecast;
    }

    public void setForecast(ForecastDTO forecast) {
        this.forecast = forecast;
    }

    public List<RecommendationDTO> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationDTO> recommendations) {
        this.recommendations = recommendations;
    }
}
