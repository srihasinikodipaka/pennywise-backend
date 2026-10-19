package com.pennywise.app.service;

import com.pennywise.app.dto.CategorizationResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategorizationService {

    private static class CategoryRule {
        String category;
        String ruleCode;
        List<String> keywords;

        CategoryRule(String category, String ruleCode, String... keywords) {
            this.category = category;
            this.ruleCode = ruleCode;
            this.keywords = Arrays.asList(keywords);
        }
    }

    private final List<CategoryRule> rules = new ArrayList<>();

    public CategorizationService() {
        initRules();
    }

    private void initRules() {
        // Food & Dining
        rules.add(new CategoryRule("Food & Dining", "RULE_FOOD_01",
                "restaurant", "swiggy", "zomato", "cafe", "coffee", "starbucks",
                "mcdonalds", "burger", "pizza", "dominos", "kfc", "subway",
                "diner", "lunch", "dinner", "breakfast", "snack", "bakery",
                "biryani", "bistro", "tea", "chai", "food", "eat"));

        // Groceries
        rules.add(new CategoryRule("Groceries", "RULE_GROC_02",
                "grocery", "groceries", "supermarket", "walmart", "target",
                "blinkit", "zepto", "instamart", "bigbasket", "milk", "vegetables",
                "fruits", "market", "spices", "provisions", "bread", "eggs", "costco"));

        // Transportation
        rules.add(new CategoryRule("Transportation", "RULE_TRANS_03",
                "uber", "ola", "lyft", "rapido", "taxi", "cab", "metro", "bus",
                "train", "irctc", "flight", "indigo", "airfare", "petrol", "diesel",
                "fuel", "gas station", "parking", "toll", "fastag", "auto"));

        // Utilities
        rules.add(new CategoryRule("Utilities", "RULE_UTIL_04",
                "electricity", "power bill", "water bill", "wifi", "broadband",
                "internet", "airtel", "jio", "vi", "mobile recharge", "gas cylinder",
                "lpg", "maintenance", "utility", "bill"));

        // Entertainment
        rules.add(new CategoryRule("Entertainment", "RULE_ENT_05",
                "netflix", "spotify", "prime video", "amazon prime", "disney",
                "hotstar", "youtube premium", "movie", "cinema", "pvr", "inox",
                "bookmyshow", "steam", "playstation", "xbox", "game", "concert", "theatre"));

        // Shopping
        rules.add(new CategoryRule("Shopping", "RULE_SHOP_06",
                "amazon", "flipkart", "myntra", "zara", "h&m", "nike", "adidas",
                "clothing", "shoes", "apparel", "electronics", "gadget", "croma",
                "reliance digital", "mall", "shopping", "store"));

        // Healthcare
        rules.add(new CategoryRule("Healthcare", "RULE_HLTH_07",
                "pharmacy", "medicine", "doctor", "hospital", "clinic", "medplus",
                "apollo", "lab test", "dental", "dentist", "optician", "physio", "health"));

        // Housing
        rules.add(new CategoryRule("Housing", "RULE_HOUS_08",
                "rent", "house rent", "mortgage", "society dues", "plumber",
                "carpenter", "electrician", "furniture", "ikea", "home decor"));

        // Education
        rules.add(new CategoryRule("Education", "RULE_EDU_09",
                "tuition", "course", "udemy", "coursera", "college fee", "school fee",
                "books", "library", "exam fee", "certification", "training"));

        // Income / Salary
        rules.add(new CategoryRule("Salary", "RULE_INC_10",
                "salary", "payroll", "paycheck", "wages", "bonus", "stipend"));

        // Investments
        rules.add(new CategoryRule("Investment", "RULE_INV_11",
                "mutual fund", "sip", "stocks", "zerodha", "groww", "crypto",
                "bitcoin", "dividend", "fixed deposit", "fd interest"));

        // Freelance
        rules.add(new CategoryRule("Freelance", "RULE_FREE_12",
                "freelance", "upwork", "fiverr", "client payment", "consulting", "gig"));
    }

    public CategorizationResponse categorize(String description) {
        if (description == null || description.trim().isEmpty()) {
            return new CategorizationResponse(
                    description,
                    "Other",
                    0.1,
                    "none",
                    "No description provided; defaulted to 'Other'"
            );
        }

        String normalized = description.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] words = normalized.split("\\s+");
        Set<String> wordSet = new HashSet<>(Arrays.asList(words));

        // Check exact match and phrase matches
        for (CategoryRule rule : rules) {
            for (String keyword : rule.keywords) {
                // If keyword contains space (phrase match)
                if (keyword.contains(" ")) {
                    if (normalized.contains(keyword)) {
                        return new CategorizationResponse(
                                description,
                                rule.category,
                                0.95,
                                keyword,
                                "Matched phrase '" + keyword + "' using rule " + rule.ruleCode + " (" + rule.category + ")"
                        );
                    }
                } else {
                    // Single word match
                    if (wordSet.contains(keyword)) {
                        return new CategorizationResponse(
                                description,
                                rule.category,
                                0.90,
                                keyword,
                                "Matched keyword '" + keyword + "' using rule " + rule.ruleCode + " (" + rule.category + ")"
                        );
                    } else if (normalized.contains(keyword) && keyword.length() >= 4) {
                        return new CategorizationResponse(
                                description,
                                rule.category,
                                0.80,
                                keyword,
                                "Matched substring '" + keyword + "' using rule " + rule.ruleCode + " (" + rule.category + ")"
                        );
                    }
                }
            }
        }

        return new CategorizationResponse(
                description,
                "Other",
                0.3,
                "default",
                "No strong rule matched for '" + description + "'; categorized as 'Other'"
        );
    }
}
