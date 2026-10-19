package com.pennywise.app.dto;

import jakarta.validation.constraints.NotBlank;

public class CategorizationRequest {
    @NotBlank(message = "Description cannot be empty")
    private String description;

    public CategorizationRequest() {}

    public CategorizationRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
