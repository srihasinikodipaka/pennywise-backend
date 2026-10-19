package com.pennywise.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AnomalyDTO {
    private Long transactionId;
    private String title;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private BigDecimal averageForCategory;
    private double multiplier;
    private String explanation;
    private String severity; // "HIGH", "MEDIUM"

    public AnomalyDTO() {}

    public AnomalyDTO(Long transactionId, String title, BigDecimal amount, String category, LocalDate date,
                      BigDecimal averageForCategory, double multiplier, String explanation, String severity) {
        this.transactionId = transactionId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.averageForCategory = averageForCategory;
        this.multiplier = multiplier;
        this.explanation = explanation;
        this.severity = severity;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAverageForCategory() {
        return averageForCategory;
    }

    public void setAverageForCategory(BigDecimal averageForCategory) {
        this.averageForCategory = averageForCategory;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
