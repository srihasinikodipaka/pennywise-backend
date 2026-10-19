package com.pennywise.app.controller;

import com.pennywise.app.dto.DashboardSummaryResponse;
import com.pennywise.app.dto.ForecastDTO;
import com.pennywise.app.dto.InsightsResponse;
import com.pennywise.app.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryResponse> getDashboard(
            @RequestParam(required = false) String month) {
        DashboardSummaryResponse response = analyticsService.getDashboardSummary(month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/insights")
    public ResponseEntity<InsightsResponse> getInsights(
            @RequestParam(required = false) String month) {
        InsightsResponse response = analyticsService.getInsights(month);
        return ResponseEntity.ok(response);
    }

    
@PostMapping("/ai-query")
public ResponseEntity<Map<String, String>> queryFinancialAi(
        @RequestBody Map<String, String> body,
        @RequestParam(required = false) String month) {

    String query = body.getOrDefault("query", "").trim();
    InsightsResponse insights = analyticsService.getInsights(month);
    DashboardSummaryResponse dashboard = analyticsService.getDashboardSummary(month);

    String q = query.toLowerCase();
    String answer;

    if (q.isBlank()) {
        answer = "Please enter a financial question so I can help you.";
    } else if (q.contains("hello") || q.contains("hi ")
            || q.equals("hi") || q.contains("hey")) {
        answer = "Hello! I'm Pennywise, your financial assistant. Ask me about your spending, savings, budgets, or forecast.";
    } else if (q.contains("poem") || q.contains("joke")
            || q.contains("story")) {
        answer = "I'm currently designed to help with your finances rather than creative writing. I can explain your spending patterns, forecast, or ways to save money.";
    } else if (q.contains("afford") || q.contains("buy")
            || q.contains("trip") || q.contains("vacation")) {

        BigDecimal projectedSavings = dashboard.getTotalIncome()
                .subtract(insights.getForecast().getProjectedMonthEndSpend());

        answer = "Your current net balance is "
                + dashboard.getNetBalance()
                + ". Your projected month-end spending is "
                + insights.getForecast().getProjectedMonthEndSpend()
                + ", and your projected savings based on income minus projected spending is "
                + projectedSavings
                + ". Before making a large purchase, check your remaining budget and upcoming expenses.";

    } else if (q.contains("save") || q.contains("more money")
            || q.contains("cut") || q.contains("reduce")) {
        answer = "Your savings rate is " + dashboard.getSavingsRate()
                + "%. Review your largest spending categories first, especially Shopping and Food & Dining. "
                + "Set a limit for discretionary purchases and track your progress in the Budgets tab.";

    } else if (q.contains("forecast") || q.contains("predict")
            || q.contains("future") || q.contains("next month")) {
        answer = insights.getForecast().getExplanation()
                + " Your estimated next-month spending is approximately "
                + insights.getForecast().getNextMonthForecast() + ".";

    } else if (q.contains("budget") || q.contains("overspend")) {
        answer = "You have " + dashboard.getBudgets().size()
                + " active budgets. Current spending status: "
                + insights.getForecast().getBudgetStatus()
                + ". Check the Budgets tab for category-level details.";

    } else if (q.contains("expense") || q.contains("spend")
            || q.contains("shopping") || q.contains("category")) {
        answer = "Your total expenses are "
                + dashboard.getTotalExpenses()
                + ". Your savings rate is " + dashboard.getSavingsRate()
                + "%. Check the Analytics page for your category breakdown and unusual expenses.";

    } else if (q.contains("balance") || q.contains("income")
            || q.contains("saving")) {
        answer = "Your total income is " + dashboard.getTotalIncome()
                + ", your total expenses are " + dashboard.getTotalExpenses()
                + ", and your net balance is " + dashboard.getNetBalance()
                + ". Your savings rate is " + dashboard.getSavingsRate() + "%.";

    } else {
        answer = "I couldn't match that question to one of my supported financial topics yet. "
                + "Try asking about your balance, expenses, savings, budget, spending forecast, or how to reduce spending.";
    }

    return ResponseEntity.ok(Map.of("query", query, "answer", answer));
}
}
