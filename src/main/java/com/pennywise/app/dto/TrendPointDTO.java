package com.pennywise.app.dto;

import java.math.BigDecimal;

public class TrendPointDTO {
    private String label; // e.g. "Oct 01", "2026-10", or "Week 1"
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;

    public TrendPointDTO() {}

    public TrendPointDTO(String label, BigDecimal income, BigDecimal expense) {
        this.label = label;
        this.income = income != null ? income : BigDecimal.ZERO;
        this.expense = expense != null ? expense : BigDecimal.ZERO;
        this.net = this.income.subtract(this.expense);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
        recalculateNet();
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
        recalculateNet();
    }

    public BigDecimal getNet() {
        return net;
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }

    private void recalculateNet() {
        BigDecimal inc = this.income != null ? this.income : BigDecimal.ZERO;
        BigDecimal exp = this.expense != null ? this.expense : BigDecimal.ZERO;
        this.net = inc.subtract(exp);
    }
}
