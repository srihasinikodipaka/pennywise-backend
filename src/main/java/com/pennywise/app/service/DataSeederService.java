package com.pennywise.app.service;

import com.pennywise.app.model.Budget;
import com.pennywise.app.model.Transaction;
import com.pennywise.app.model.TransactionType;
import com.pennywise.app.model.User;
import com.pennywise.app.repository.BudgetRepository;
import com.pennywise.app.repository.TransactionRepository;
import com.pennywise.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataSeederService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeederService.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategorizationService categorizationService;

    public DataSeederService(UserRepository userRepository,
                             TransactionRepository transactionRepository,
                             BudgetRepository budgetRepository,
                             PasswordEncoder passwordEncoder,
                             CategorizationService categorizationService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.passwordEncoder = passwordEncoder;
        this.categorizationService = categorizationService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("demo").isPresent()) {
            log.info("Demo user and sample data already present. Skipping seeder.");
            return;
        }

        log.info("Seeding initial demo user, realistic transactions, and budgets for Pennywise...");

        // 1. Create Demo User
        User demoUser = new User();
        demoUser.setUsername("demo");
        demoUser.setEmail("demo@pennywise.com");
        demoUser.setPassword(passwordEncoder.encode("demo123"));
        demoUser.setFullName("Harini Kodipaka");
        demoUser.setCurrency("₹");
        User savedUser = userRepository.save(demoUser);

        // 2. Current Month Budgets (e.g. October 2026)
        String currentMonth = YearMonth.now().toString();
        List<Budget> budgets = new ArrayList<>();
        budgets.add(new Budget(savedUser, "Food & Dining", new BigDecimal("7500"), currentMonth));
        budgets.add(new Budget(savedUser, "Groceries", new BigDecimal("6000"), currentMonth));
        budgets.add(new Budget(savedUser, "Transportation", new BigDecimal("3500"), currentMonth));
        budgets.add(new Budget(savedUser, "Shopping", new BigDecimal("8000"), currentMonth));
        budgets.add(new Budget(savedUser, "Utilities", new BigDecimal("4000"), currentMonth));
        budgets.add(new Budget(savedUser, "Entertainment", new BigDecimal("2500"), currentMonth));
        budgetRepository.saveAll(budgets);

        // 3. Transactions across current month and past 2 months
        LocalDate today = LocalDate.now();
        List<Transaction> txList = new ArrayList<>();

        // Current Month Income
        txList.add(createTx(savedUser, "Monthly Salary Credit", new BigDecimal("75000.00"), TransactionType.INCOME, "Salary", today.withDayOfMonth(1), "Net Banking"));
        txList.add(createTx(savedUser, "Freelance UI Consultation", new BigDecimal("18500.00"), TransactionType.INCOME, "Freelance", today.withDayOfMonth(10), "UPI"));

        // Current Month Expenses
        txList.add(createTx(savedUser, "Apartment Monthly Rent", new BigDecimal("16000.00"), TransactionType.EXPENSE, "Housing", today.withDayOfMonth(2), "Net Banking"));
        txList.add(createTx(savedUser, "Electricity Board Bill", new BigDecimal("2150.00"), TransactionType.EXPENSE, "Utilities", today.withDayOfMonth(3), "UPI"));
        txList.add(createTx(savedUser, "Blinkit Grocery Order", new BigDecimal("1420.00"), TransactionType.EXPENSE, "Groceries", today.withDayOfMonth(4), "UPI"));
        txList.add(createTx(savedUser, "Uber ride to Office", new BigDecimal("320.00"), TransactionType.EXPENSE, "Transportation", today.withDayOfMonth(5), "UPI"));
        txList.add(createTx(savedUser, "Starbucks Hazelnut Latte & Sandwich", new BigDecimal("680.00"), TransactionType.EXPENSE, "Food & Dining", today.withDayOfMonth(6), "Credit Card"));
        txList.add(createTx(savedUser, "Swiggy Dinner with Team", new BigDecimal("1240.00"), TransactionType.EXPENSE, "Food & Dining", today.withDayOfMonth(7), "UPI"));
        txList.add(createTx(savedUser, "Airtel Fiber Broadband", new BigDecimal("999.00"), TransactionType.EXPENSE, "Utilities", today.withDayOfMonth(8), "Credit Card"));
        txList.add(createTx(savedUser, "Supermarket weekly groceries", new BigDecimal("2650.00"), TransactionType.EXPENSE, "Groceries", today.withDayOfMonth(9), "Debit Card"));
        txList.add(createTx(savedUser, "Petrol Fuel Station refill", new BigDecimal("1800.00"), TransactionType.EXPENSE, "Transportation", today.withDayOfMonth(11), "Credit Card"));
        txList.add(createTx(savedUser, "Netflix 4K UHD Subscription", new BigDecimal("649.00"), TransactionType.EXPENSE, "Entertainment", today.withDayOfMonth(12), "Credit Card"));
        txList.add(createTx(savedUser, "Spotify Premium Family", new BigDecimal("179.00"), TransactionType.EXPENSE, "Entertainment", today.withDayOfMonth(12), "Credit Card"));
        txList.add(createTx(savedUser, "Dominos Pizza weekend party", new BigDecimal("1890.00"), TransactionType.EXPENSE, "Food & Dining", today.withDayOfMonth(13), "UPI"));
        txList.add(createTx(savedUser, "Apollo Pharmacy Vitamins & Cold Relief", new BigDecimal("820.00"), TransactionType.EXPENSE, "Healthcare", today.withDayOfMonth(14), "Cash"));
        
        // Outlier / Anomaly transaction (high shopping spike!)
        txList.add(createTx(savedUser, "Apple AirPods Pro from Croma Electronics", new BigDecimal("19900.00"), TransactionType.EXPENSE, "Shopping", today.withDayOfMonth(15), "Credit Card"));

        // Prior Month Transactions (to calculate trends and baselines)
        LocalDate prevMonth = today.minusMonths(1);
        txList.add(createTx(savedUser, "Monthly Salary Credit", new BigDecimal("75000.00"), TransactionType.INCOME, "Salary", prevMonth.withDayOfMonth(1), "Net Banking"));
        txList.add(createTx(savedUser, "Mutual Fund SIP - Nifty 50", new BigDecimal("10000.00"), TransactionType.EXPENSE, "Investment", prevMonth.withDayOfMonth(5), "Net Banking"));
        txList.add(createTx(savedUser, "Apartment Monthly Rent", new BigDecimal("16000.00"), TransactionType.EXPENSE, "Housing", prevMonth.withDayOfMonth(2), "Net Banking"));
        txList.add(createTx(savedUser, "BigBasket Groceries Bulk", new BigDecimal("5400.00"), TransactionType.EXPENSE, "Groceries", prevMonth.withDayOfMonth(6), "UPI"));
        txList.add(createTx(savedUser, "Fuel & Rapido rides", new BigDecimal("2400.00"), TransactionType.EXPENSE, "Transportation", prevMonth.withDayOfMonth(14), "UPI"));
        txList.add(createTx(savedUser, "Zara Casual Wear", new BigDecimal("4500.00"), TransactionType.EXPENSE, "Shopping", prevMonth.withDayOfMonth(20), "Credit Card"));
        txList.add(createTx(savedUser, "Weekend Dining & Cafes", new BigDecimal("4200.00"), TransactionType.EXPENSE, "Food & Dining", prevMonth.withDayOfMonth(25), "Credit Card"));

        // 2 Months Ago
        LocalDate twoMonthsAgo = today.minusMonths(2);
        txList.add(createTx(savedUser, "Monthly Salary Credit", new BigDecimal("75000.00"), TransactionType.INCOME, "Salary", twoMonthsAgo.withDayOfMonth(1), "Net Banking"));
        txList.add(createTx(savedUser, "Apartment Monthly Rent", new BigDecimal("16000.00"), TransactionType.EXPENSE, "Housing", twoMonthsAgo.withDayOfMonth(2), "Net Banking"));
        txList.add(createTx(savedUser, "General Groceries", new BigDecimal("4900.00"), TransactionType.EXPENSE, "Groceries", twoMonthsAgo.withDayOfMonth(8), "UPI"));
        txList.add(createTx(savedUser, "Dining & Takeout", new BigDecimal("3800.00"), TransactionType.EXPENSE, "Food & Dining", twoMonthsAgo.withDayOfMonth(18), "UPI"));
        txList.add(createTx(savedUser, "Fuel & Commute", new BigDecimal("2800.00"), TransactionType.EXPENSE, "Transportation", twoMonthsAgo.withDayOfMonth(22), "Cash"));

        transactionRepository.saveAll(txList);
        log.info("Successfully seeded {} sample transactions and {} budgets for demo user.", txList.size(), budgets.size());
    }

    private Transaction createTx(User user, String title, BigDecimal amount, TransactionType type,
                                String category, LocalDate date, String paymentMethod) {
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setTitle(title);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setCategory(category);
        tx.setDate(date);
        tx.setPaymentMethod(paymentMethod);
        tx.setMatchedRule(categorizationService.categorize(title).getRuleExplanation());
        return tx;
    }
}
