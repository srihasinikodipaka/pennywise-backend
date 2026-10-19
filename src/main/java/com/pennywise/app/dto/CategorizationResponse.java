package com.pennywise.app.dto;

public class CategorizationResponse {
    private String originalDescription;
    private String suggestedCategory;
    private double confidence;
    private String matchedKeyword;
    private String ruleExplanation;

    public CategorizationResponse() {}

    public CategorizationResponse(String originalDescription, String suggestedCategory, double confidence, String matchedKeyword, String ruleExplanation) {
        this.originalDescription = originalDescription;
        this.suggestedCategory = suggestedCategory;
        this.confidence = confidence;
        this.matchedKeyword = matchedKeyword;
        this.ruleExplanation = ruleExplanation;
    }

    public String getOriginalDescription() {
        return originalDescription;
    }

    public void setOriginalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    public String getSuggestedCategory() {
        return suggestedCategory;
    }

    public void setSuggestedCategory(String suggestedCategory) {
        this.suggestedCategory = suggestedCategory;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getMatchedKeyword() {
        return matchedKeyword;
    }

    public void setMatchedKeyword(String matchedKeyword) {
        this.matchedKeyword = matchedKeyword;
    }

    public String getRuleExplanation() {
        return ruleExplanation;
    }

    public void setRuleExplanation(String ruleExplanation) {
        this.ruleExplanation = ruleExplanation;
    }
}
