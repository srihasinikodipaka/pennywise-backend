package com.pennywise.app.controller;

import com.pennywise.app.dto.BudgetRequest;
import com.pennywise.app.dto.BudgetStatusResponse;
import com.pennywise.app.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/status")
    public ResponseEntity<List<BudgetStatusResponse>> getBudgetStatuses(
            @RequestParam(required = false) String month) {
        List<BudgetStatusResponse> response = budgetService.getBudgetStatuses(month);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BudgetStatusResponse> createOrUpdateBudget(
            @Valid @RequestBody BudgetRequest request) {
        BudgetStatusResponse response = budgetService.createOrUpdateBudget(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
