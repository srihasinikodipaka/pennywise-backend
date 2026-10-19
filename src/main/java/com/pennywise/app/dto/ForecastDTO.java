package com.pennywise.app.dto;

import java.math.BigDecimal;

public class ForecastDTO {
    private BigDecimal currentMonthSpend;
    private int daysPassedInMonth;
    private int daysRemainingInMonth;
    private BigDecimal dailyBurnRate;
    private BigDecimal projectedMonthEndSpend;
    private BigDecimal nextMonthForecast;
    private BigDecimal totalBudget;
    private String trendDirection; // "STABLE", "INCREASING", "DECREASING"
    private String budgetStatus;   // "ON_TRACK", "AT_RISK", "PROJECTED_OVERRUN"
    private String explanation;

    public ForecastDTO() {}

    public BigDecimal getCurrentMonthSpend() {
        return currentMonthSpend;
    }

    public void setCurrentMonthSpend(BigDecimal currentMonthSpend) {
        this.currentMonthSpend = currentMonthSpend;
    }

    public int getDaysPassedInMonth() {
        return daysPassedInMonth;
    }

    public void setDaysPassedInMonth(int daysPassedInMonth) {
        this.daysPassedInMonth = daysPassedInMonth;
    }

    public int getDaysRemainingInMonth() {
        return daysRemainingInMonth;
    }

    public void setDaysRemainingInMonth(int daysRemainingInMonth) {
        this.daysRemainingInMonth = daysRemainingInMonth;
    }

    public BigDecimal getDailyBurnRate() {
        return dailyBurnRate;
    }

    public void setDailyBurnRate(BigDecimal dailyBurnRate) {
        this.dailyBurnRate = dailyBurnRate;
    }

    public BigDecimal getProjectedMonthEndSpend() {
        return projectedMonthEndSpend;
    }

    public void setProjectedMonthEndSpend(BigDecimal projectedMonthEndSpend) {
        this.projectedMonthEndSpend = projectedMonthEndSpend;
    }

    public BigDecimal getNextMonthForecast() {
        return nextMonthForecast;
    }

    public void setNextMonthForecast(BigDecimal nextMonthForecast) {
        this.nextMonthForecast = nextMonthForecast;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(String trendDirection) {
        this.trendDirection = trendDirection;
    }

    public String getBudgetStatus() {
        return budgetStatus;
    }

    public void setBudgetStatus(String budgetStatus) {
        this.budgetStatus = budgetStatus;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
