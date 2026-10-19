package com.pennywise.app.service;

import com.pennywise.app.dto.*;
import com.pennywise.app.model.Budget;
import com.pennywise.app.model.Transaction;
import com.pennywise.app.model.TransactionType;
import com.pennywise.app.model.User;
import com.pennywise.app.repository.BudgetRepository;
import com.pennywise.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetService budgetService;
    private final AuthService authService;

    public AnalyticsService(TransactionRepository transactionRepository,
                            BudgetRepository budgetRepository,
                            BudgetService budgetService,
                            AuthService authService) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.budgetService = budgetService;
        this.authService = authService;
    }

    public DashboardSummaryResponse getDashboardSummary(String month) {
        User user = authService.getCurrentAuthenticatedUser();
        String targetMonth = (month != null && !month.trim().isEmpty()) ? month : YearMonth.now().toString();
        YearMonth ym = YearMonth.parse(targetMonth);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                user, TransactionType.INCOME, startDate, endDate
        );
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        BigDecimal totalExpenses = transactionRepository.sumAmountByUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate
        );
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        double savingsRate = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = netBalance.divide(totalIncome, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
            savingsRate = Math.max(0.0, Math.round(savingsRate * 10.0) / 10.0);
        }

        // Category breakdown
        List<Object[]> rawBreakdown = transactionRepository.getCategoryExpenseBreakdown(user, startDate, endDate);
        List<CategoryBreakdownDTO> breakdown = new ArrayList<>();
        for (Object[] row : rawBreakdown) {
            String category = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            long count = ((Number) row[2]).longValue();
            double pct = 0.0;
            if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                pct = amount.divide(totalExpenses, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
                pct = Math.round(pct * 10.0) / 10.0;
            }
            breakdown.add(new CategoryBreakdownDTO(category, amount, pct, count));
        }

        // Monthly trends (last 6 months)
        List<TrendPointDTO> monthlyTrends = getMonthlyTrends(user, ym, 6);

        // Recent 5 transactions
        List<Transaction> recentTx = transactionRepository.findByUserOrderByDateDescIdDesc(user);
        List<TransactionResponse> recentResponses = recentTx.stream()
                .limit(5)
                .map(TransactionResponse::new)
                .collect(Collectors.toList());

        // Budgets status
        List<BudgetStatusResponse> budgets = budgetService.getBudgetStatuses(targetMonth);

        // Quick AI Insight snippet
        String quickInsight = generateQuickInsight(user, totalIncome, totalExpenses, savingsRate, breakdown);

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setTotalIncome(totalIncome);
        response.setTotalExpenses(totalExpenses);
        response.setNetBalance(netBalance);
        response.setSavingsRate(savingsRate);
        response.setCategoryBreakdown(breakdown);
        response.setMonthlyTrends(monthlyTrends);
        response.setRecentTransactions(recentResponses);
        response.setBudgets(budgets);
        response.setQuickInsight(quickInsight);

        return response;
    }

    public InsightsResponse getInsights(String month) {
        User user = authService.getCurrentAuthenticatedUser();
        String targetMonth = (month != null && !month.trim().isEmpty()) ? month : YearMonth.now().toString();
        YearMonth ym = YearMonth.parse(targetMonth);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Transaction> monthTransactions = transactionRepository.findByUserAndDateBetweenOrderByDateDescIdDesc(user, startDate, endDate);
        List<Transaction> allTransactions = transactionRepository.findByUserOrderByDateDescIdDesc(user);

        // 1. Spending Patterns
        List<String> spendingPatterns = analyzeSpendingPatterns(monthTransactions, user);

        // 2. Anomaly Detection (Unusual Expenses)
        List<AnomalyDTO> anomalies = detectAnomalies(monthTransactions, allTransactions, user);

        // 3. Spending Forecast
        ForecastDTO forecast = computeForecast(user, ym, monthTransactions);

        // 4. Actionable AI Recommendations
        List<RecommendationDTO> recommendations = generateRecommendations(user, ym, monthTransactions, anomalies, forecast);

        return new InsightsResponse(spendingPatterns, anomalies, forecast, recommendations);
    }

    private List<TrendPointDTO> getMonthlyTrends(User user, YearMonth currentYm, int monthsBack) {
        List<TrendPointDTO> trends = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (int i = monthsBack - 1; i >= 0; i--) {
            YearMonth target = currentYm.minusMonths(i);
            LocalDate start = target.atDay(1);
            LocalDate end = target.atEndOfMonth();

            BigDecimal inc = transactionRepository.sumAmountByUserAndTypeAndDateBetween(user, TransactionType.INCOME, start, end);
            BigDecimal exp = transactionRepository.sumAmountByUserAndTypeAndDateBetween(user, TransactionType.EXPENSE, start, end);

            trends.add(new TrendPointDTO(target.format(formatter), inc, exp));
        }

        return trends;
    }

    private String generateQuickInsight(User user, BigDecimal income, BigDecimal expenses, double savingsRate, List<CategoryBreakdownDTO> breakdown) {
        if (income.compareTo(BigDecimal.ZERO) == 0 && expenses.compareTo(BigDecimal.ZERO) == 0) {
            return "Welcome to pennywise! Record your first income or expense to activate intelligent financial analytics.";
        }
        if (savingsRate >= 20.0) {
            return "Excellent financial discipline! Your savings rate is " + savingsRate + "%, surpassing the recommended 20% benchmark.";
        }
        if (!breakdown.isEmpty()) {
            CategoryBreakdownDTO top = breakdown.get(0);
            return "Your highest spending driver is " + top.getCategory() + " (" + top.getPercentage() + "% of monthly outgo). Consider optimizing here.";
        }
        return "Expenses are currently tracking at " + user.getCurrency() + expenses.toPlainString() + " for this period.";
    }

    private List<String> analyzeSpendingPatterns(List<Transaction> transactions, User user) {
        List<String> patterns = new ArrayList<>();
        List<Transaction> expenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.toList());

        if (expenses.isEmpty()) {
            patterns.add("No expense transactions recorded yet for this billing cycle.");
            return patterns;
        }

        // Weekend vs Weekday analysis
        BigDecimal weekendSpend = BigDecimal.ZERO;
        BigDecimal weekdaySpend = BigDecimal.ZERO;
        int weekendCount = 0;
        int weekdayCount = 0;

        for (Transaction t : expenses) {
            int dayOfWeek = t.getDate().getDayOfWeek().getValue();
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                weekendSpend = weekendSpend.add(t.getAmount());
                weekendCount++;
            } else {
                weekdaySpend = weekdaySpend.add(t.getAmount());
                weekdayCount++;
            }
        }

        BigDecimal totalSpend = weekendSpend.add(weekdaySpend);
        if (totalSpend.compareTo(BigDecimal.ZERO) > 0) {
            double weekendPct = weekendSpend.divide(totalSpend, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
            if (weekendPct > 35.0) {
                patterns.add(String.format("Weekend Velocity: %.1f%% of your total expenses occur on Saturdays & Sundays (%s%s).",
                        weekendPct, user.getCurrency(), weekendSpend.setScale(0, RoundingMode.HALF_UP)));
            } else {
                patterns.add(String.format("Balanced Rhythm: Spending is evenly distributed, with weekdays accounting for %.1f%%.",
                        100.0 - weekendPct));
            }
        }

        // Frequent Payment Mode
        Map<String, Long> paymentModes = expenses.stream()
                .collect(Collectors.groupingBy(t -> t.getPaymentMethod() != null ? t.getPaymentMethod() : "UPI", Collectors.counting()));
        paymentModes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> patterns.add("Primary Transaction Medium: " + entry.getKey() + " is used for " + entry.getValue() + " transactions."));

        // Top Category share
        Map<String, BigDecimal> catTotals = new HashMap<>();
        for (Transaction t : expenses) {
            catTotals.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
        }

        catTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(top -> {
                    double pct = top.getValue().divide(totalSpend, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
                    patterns.add(String.format("Concentrated Outgo: %s represents %.1f%% (%s%s) of total expenses this month.",
                            top.getKey(), pct, user.getCurrency(), top.getValue().setScale(0, RoundingMode.HALF_UP)));
                });

        return patterns;
    }

    private List<AnomalyDTO> detectAnomalies(List<Transaction> currentMonthTx, List<Transaction> allTx, User user) {
        List<AnomalyDTO> anomalies = new ArrayList<>();

        // Group historical expenses by category to calculate mean and standard deviation
        Map<String, List<BigDecimal>> categoryHistory = allTx.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.mapping(Transaction::getAmount, Collectors.toList())
                ));

        Map<String, BigDecimal> categoryAverages = new HashMap<>();
        for (Map.Entry<String, List<BigDecimal>> entry : categoryHistory.entrySet()) {
            List<BigDecimal> amounts = entry.getValue();
            if (amounts.isEmpty()) continue;
            BigDecimal sum = amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(BigDecimal.valueOf(amounts.size()), 2, RoundingMode.HALF_UP);
            categoryAverages.put(entry.getKey(), avg);
        }

        // Scan current month expense transactions
        for (Transaction t : currentMonthTx) {
            if (t.getType() != TransactionType.EXPENSE) continue;

            List<BigDecimal> otherAmounts = allTx.stream()
                    .filter(other -> other.getType() == TransactionType.EXPENSE &&
                                     other.getCategory().equalsIgnoreCase(t.getCategory()) &&
                                     (other.getId() == null || !other.getId().equals(t.getId())))
                    .map(Transaction::getAmount)
                    .collect(Collectors.toList());

            BigDecimal baselineAvg;
            if (!otherAmounts.isEmpty()) {
                BigDecimal sum = otherAmounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                baselineAvg = sum.divide(BigDecimal.valueOf(otherAmounts.size()), 2, RoundingMode.HALF_UP);
            } else {
                baselineAvg = categoryAverages.get(t.getCategory());
            }

            if (baselineAvg != null && baselineAvg.compareTo(BigDecimal.ZERO) > 0) {
                double ratio = t.getAmount().divide(baselineAvg, 2, RoundingMode.HALF_UP).doubleValue();
                if (ratio >= 1.5 && t.getAmount().compareTo(BigDecimal.valueOf(500)) > 0) {
                    String severity = ratio >= 2.5 ? "HIGH" : "MEDIUM";
                    String explanation = String.format("This %s%s expense is %.1fx the historical baseline (%s%s) for %s.",
                            user.getCurrency(), t.getAmount().toPlainString(), ratio, user.getCurrency(), baselineAvg.toPlainString(), t.getCategory());

                    anomalies.add(new AnomalyDTO(
                            t.getId(),
                            t.getTitle(),
                            t.getAmount(),
                            t.getCategory(),
                            t.getDate(),
                            baselineAvg,
                            ratio,
                            explanation,
                            severity
                    ));
                }
            }
        }

        // Sort by multiplier descending
        anomalies.sort((a, b) -> Double.compare(b.getMultiplier(), a.getMultiplier()));
        return anomalies;
    }

    public ForecastDTO computeForecast(User user, YearMonth ym, List<Transaction> monthTransactions) {
        LocalDate today = LocalDate.now();
        int daysInMonth = ym.lengthOfMonth();

        int daysPassed;
        if (ym.equals(YearMonth.from(today))) {
            daysPassed = Math.max(1, today.getDayOfMonth());
        } else if (ym.isBefore(YearMonth.from(today))) {
            daysPassed = daysInMonth;
        } else {
            daysPassed = 1;
        }

        int daysRemaining = Math.max(0, daysInMonth - daysPassed);

        BigDecimal currentMonthSpend = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal dailyBurnRate = currentMonthSpend.divide(BigDecimal.valueOf(daysPassed), 2, RoundingMode.HALF_UP);
        BigDecimal projectedMonthEndSpend = currentMonthSpend.add(dailyBurnRate.multiply(BigDecimal.valueOf(daysRemaining)));

        // Total budget for this month
        List<Budget> budgets = budgetRepository.findByUserAndMonth(user, ym.toString());
        BigDecimal totalBudget = budgets.stream()
                .map(Budget::getLimitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Next month forecast (daily burn rate * 30.5)
        BigDecimal nextMonthForecast = dailyBurnRate.multiply(BigDecimal.valueOf(30.5)).setScale(2, RoundingMode.HALF_UP);

        String budgetStatus = "ON_TRACK";
        String explanation;
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            if (projectedMonthEndSpend.compareTo(totalBudget) > 0) {
                budgetStatus = "PROJECTED_OVERRUN";
                BigDecimal diff = projectedMonthEndSpend.subtract(totalBudget).setScale(0, RoundingMode.HALF_UP);
                explanation = String.format("Current velocity indicates you will exceed your total budget by %s%s by end of month.",
                        user.getCurrency(), diff.toPlainString());
            } else if (projectedMonthEndSpend.divide(totalBudget, 2, RoundingMode.HALF_UP).doubleValue() > 0.85) {
                budgetStatus = "AT_RISK";
                explanation = "Spending is approaching your total monthly budget limit with little buffer remaining.";
            } else {
                budgetStatus = "ON_TRACK";
                BigDecimal surplus = totalBudget.subtract(projectedMonthEndSpend).setScale(0, RoundingMode.HALF_UP);
                explanation = String.format("Projected spend is well within budget. Expected surplus: %s%s.",
                        user.getCurrency(), surplus.toPlainString());
            }
        } else {
            explanation = String.format("Based on %d days of data, average burn rate is %s%s/day.",
                    daysPassed, user.getCurrency(), dailyBurnRate.toPlainString());
        }

        ForecastDTO dto = new ForecastDTO();
        dto.setCurrentMonthSpend(currentMonthSpend);
        dto.setDaysPassedInMonth(daysPassed);
        dto.setDaysRemainingInMonth(daysRemaining);
        dto.setDailyBurnRate(dailyBurnRate);
        dto.setProjectedMonthEndSpend(projectedMonthEndSpend.setScale(2, RoundingMode.HALF_UP));
        dto.setNextMonthForecast(nextMonthForecast);
        dto.setTotalBudget(totalBudget);
        dto.setTrendDirection(projectedMonthEndSpend.compareTo(totalBudget) > 0 ? "INCREASING" : "STABLE");
        dto.setBudgetStatus(budgetStatus);
        dto.setExplanation(explanation);

        return dto;
    }

    private List<RecommendationDTO> generateRecommendations(User user, YearMonth ym, List<Transaction> currentMonthTx,
                                                            List<AnomalyDTO> anomalies, ForecastDTO forecast) {
        List<RecommendationDTO> recs = new ArrayList<>();

        BigDecimal totalIncome = currentMonthTx.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = forecast.getCurrentMonthSpend();

        // 1. Budget Overrun recommendation
        if ("PROJECTED_OVERRUN".equals(forecast.getBudgetStatus())) {
            BigDecimal overrun = forecast.getProjectedMonthEndSpend().subtract(forecast.getTotalBudget());
            recs.add(new RecommendationDTO(
                    "REC_OVERRUN",
                    "BUDGET_ADVICE",
                    "HIGH",
                    "Slow Down Spending Velocity",
                    "At your current burn rate of " + user.getCurrency() + forecast.getDailyBurnRate() + "/day, you will exceed your budget.",
                    "Cap discretionary spending for the next " + forecast.getDaysRemainingInMonth() + " days to keep within limits.",
                    overrun.setScale(0, RoundingMode.HALF_UP)
            ));
        }

        // 2. Anomaly Alert Recommendation
        if (!anomalies.isEmpty()) {
            AnomalyDTO topAnomaly = anomalies.get(0);
            recs.add(new RecommendationDTO(
                    "REC_ANOMALY",
                    "ANOMALY_WARNING",
                    "HIGH",
                    "Review Outlier Expense in " + topAnomaly.getCategory(),
                    topAnomaly.getExplanation(),
                    "Verify if '" + topAnomaly.getTitle() + "' was a one-time essential purchase or can be avoided in future cycles.",
                    topAnomaly.getAmount().subtract(topAnomaly.getAverageForCategory()).setScale(0, RoundingMode.HALF_UP)
            ));
        }

        // 3. Category Optimization (Food & Dining or Shopping)
        Map<String, BigDecimal> catTotals = new HashMap<>();
        for (Transaction t : currentMonthTx) {
            if (t.getType() == TransactionType.EXPENSE) {
                catTotals.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
            }
        }

        BigDecimal foodSpend = catTotals.getOrDefault("Food & Dining", BigDecimal.ZERO);
        if (totalExpense.compareTo(BigDecimal.ZERO) > 0 && foodSpend.divide(totalExpense, 2, RoundingMode.HALF_UP).doubleValue() > 0.25) {
            BigDecimal potentialSavings = foodSpend.multiply(BigDecimal.valueOf(0.15)).setScale(0, RoundingMode.HALF_UP);
            recs.add(new RecommendationDTO(
                    "REC_FOOD_OPT",
                    "SAVINGS",
                    "MEDIUM",
                    "Optimize Dining & Food Delivery",
                    "Food & Dining constitutes over 25% of your total spend this month.",
                    "Cooking at home two extra days per week can save you an estimated " + user.getCurrency() + potentialSavings + " every month.",
                    potentialSavings
            ));
        }

        // 4. 50/30/20 Rule Check
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal savings = totalIncome.subtract(totalExpense);
            BigDecimal targetSavings = totalIncome.multiply(BigDecimal.valueOf(0.20));

            if (savings.compareTo(targetSavings) < 0) {
                BigDecimal shortfall = targetSavings.subtract(savings).setScale(0, RoundingMode.HALF_UP);
                recs.add(new RecommendationDTO(
                        "REC_503020",
                        "SAVINGS",
                        "MEDIUM",
                        "Target 20% Savings Rate",
                        "The standard 50/30/20 financial rule recommends saving at least 20% of your earnings.",
                        "Aim to trim non-essential entertainment and shopping to capture an additional " + user.getCurrency() + shortfall + " into emergency funds.",
                        shortfall
                ));
            } else {
                recs.add(new RecommendationDTO(
                        "REC_INVEST",
                        "SAVINGS",
                        "LOW",
                        "Deploy Surplus Capital",
                        "You are currently saving " + user.getCurrency() + savings.setScale(0, RoundingMode.HALF_UP) + " this month.",
                        "Consider automating monthly SIP or index fund contributions with your surplus funds.",
                        BigDecimal.ZERO
                ));
            }
        }

        return recs;
    }
}
