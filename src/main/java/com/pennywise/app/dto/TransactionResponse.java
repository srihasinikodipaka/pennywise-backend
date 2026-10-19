package com.pennywise.app.dto;

import com.pennywise.app.model.Transaction;
import com.pennywise.app.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionResponse {
    private Long id;
    private String title;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate date;
    private String paymentMethod;
    private String notes;
    private String matchedRule;
    private LocalDateTime createdAt;

    public TransactionResponse() {}

    public TransactionResponse(Transaction tx) {
        this.id = tx.getId();
        this.title = tx.getTitle();
        this.amount = tx.getAmount();
        this.type = tx.getType();
        this.category = tx.getCategory();
        this.date = tx.getDate();
        this.paymentMethod = tx.getPaymentMethod();
        this.notes = tx.getNotes();
        this.matchedRule = tx.getMatchedRule();
        this.createdAt = tx.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMatchedRule() {
        return matchedRule;
    }

    public void setMatchedRule(String matchedRule) {
        this.matchedRule = matchedRule;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
