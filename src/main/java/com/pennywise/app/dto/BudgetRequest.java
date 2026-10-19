package com.pennywise.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BudgetRequest {

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Limit amount is required")
    @DecimalMin(value = "1.0", message = "Limit must be at least 1.0")
    private BigDecimal limitAmount;

    @NotBlank(message = "Month is required in format YYYY-MM")
    private String month;

    public BudgetRequest() {}

    public BudgetRequest(String category, BigDecimal limitAmount, String month) {
        this.category = category;
        this.limitAmount = limitAmount;
        this.month = month;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
