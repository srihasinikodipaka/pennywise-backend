package com.pennywise.app.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private double savingsRate;
    private List<CategoryBreakdownDTO> categoryBreakdown;
    private List<TrendPointDTO> monthlyTrends;
    private List<TransactionResponse> recentTransactions;
    private List<BudgetStatusResponse> budgets;
    private String quickInsight;

    public DashboardSummaryResponse() {}

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public double getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(double savingsRate) {
        this.savingsRate = savingsRate;
    }

    public List<CategoryBreakdownDTO> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategoryBreakdownDTO> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public List<TrendPointDTO> getMonthlyTrends() {
        return monthlyTrends;
    }

    public void setMonthlyTrends(List<TrendPointDTO> monthlyTrends) {
        this.monthlyTrends = monthlyTrends;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public List<BudgetStatusResponse> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<BudgetStatusResponse> budgets) {
        this.budgets = budgets;
    }

    public String getQuickInsight() {
        return quickInsight;
    }

    public void setQuickInsight(String quickInsight) {
        this.quickInsight = quickInsight;
    }
}
