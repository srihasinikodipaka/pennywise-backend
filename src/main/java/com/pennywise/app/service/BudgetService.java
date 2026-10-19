package com.pennywise.app.service;

import com.pennywise.app.dto.BudgetRequest;
import com.pennywise.app.dto.BudgetStatusResponse;
import com.pennywise.app.model.Budget;
import com.pennywise.app.model.User;
import com.pennywise.app.repository.BudgetRepository;
import com.pennywise.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    public BudgetService(BudgetRepository budgetRepository,
                         TransactionRepository transactionRepository,
                         AuthService authService) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.authService = authService;
    }

    public List<BudgetStatusResponse> getBudgetStatuses(String month) {
        User user = authService.getCurrentAuthenticatedUser();
        String targetMonth = (month != null && !month.trim().isEmpty()) ? month : YearMonth.now().toString();

        List<Budget> budgets = budgetRepository.findByUserAndMonth(user, targetMonth);
        YearMonth ym = YearMonth.parse(targetMonth);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<BudgetStatusResponse> result = new ArrayList<>();
        for (Budget b : budgets) {
            BigDecimal spent = transactionRepository.sumExpenseByUserAndCategoryAndDateBetween(user, b.getCategory(), start, end);
            if (spent == null) spent = BigDecimal.ZERO;

            BudgetStatusResponse status = new BudgetStatusResponse();
            status.setId(b.getId());
            status.setCategory(b.getCategory());
            status.setLimitAmount(b.getLimitAmount());
            status.setSpentAmount(spent);
            status.setRemainingAmount(b.getLimitAmount().subtract(spent));
            status.setMonth(b.getMonth());

            double percentage = 0.0;
            if (b.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
                percentage = spent.divide(b.getLimitAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
            }
            status.setPercentageUsed(Math.round(percentage * 100.0) / 100.0);

            if (percentage >= 100.0) {
                status.setStatus("EXCEEDED");
                BigDecimal over = spent.subtract(b.getLimitAmount());
                status.setAlertMessage("Exceeded budget by " + user.getCurrency() + over.toPlainString() + "! Immediate attention recommended.");
            } else if (percentage >= 80.0) {
                status.setStatus("WARNING");
                status.setAlertMessage("High spending warning: " + Math.round(percentage) + "% of limit used.");
            } else {
                status.setStatus("SAFE");
                status.setAlertMessage("Spending is within budget limits (" + Math.round(percentage) + "% used).");
            }

            result.add(status);
        }

        return result;
    }

    @Transactional
    public BudgetStatusResponse createOrUpdateBudget(BudgetRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        String month = request.getMonth() != null ? request.getMonth() : YearMonth.now().toString();

        Optional<Budget> existing = budgetRepository.findByUserAndCategoryAndMonth(user, request.getCategory(), month);
        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setLimitAmount(request.getLimitAmount());
        } else {
            budget = new Budget(user, request.getCategory(), request.getLimitAmount(), month);
        }

        Budget saved = budgetRepository.save(budget);

        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        BigDecimal spent = transactionRepository.sumExpenseByUserAndCategoryAndDateBetween(user, saved.getCategory(), start, end);
        if (spent == null) spent = BigDecimal.ZERO;

        BudgetStatusResponse response = new BudgetStatusResponse();
        response.setId(saved.getId());
        response.setCategory(saved.getCategory());
        response.setLimitAmount(saved.getLimitAmount());
        response.setSpentAmount(spent);
        response.setRemainingAmount(saved.getLimitAmount().subtract(spent));
        response.setMonth(saved.getMonth());

        double percentage = 0.0;
        if (saved.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentage = spent.divide(saved.getLimitAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
        }
        response.setPercentageUsed(Math.round(percentage * 100.0) / 100.0);
        response.setStatus(percentage >= 100.0 ? "EXCEEDED" : (percentage >= 80.0 ? "WARNING" : "SAFE"));
        response.setAlertMessage(response.getStatus().equals("EXCEEDED") ? "Budget exceeded!" : "Budget set successfully.");

        return response;
    }

    @Transactional
    public void deleteBudget(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized delete: budget does not belong to you");
        }

        budgetRepository.delete(budget);
    }
}
