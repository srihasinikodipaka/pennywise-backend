package com.pennywise.app;

import com.pennywise.app.dto.CategorizationResponse;
import com.pennywise.app.service.CategorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategorizationServiceTest {

    private CategorizationService categorizationService;

    @BeforeEach
    void setUp() {
        categorizationService = new CategorizationService();
    }

    @Test
    @DisplayName("Should categorize food and dining keywords correctly")
    void testFoodAndDiningCategorization() {
        CategorizationResponse res1 = categorizationService.categorize("Starbucks cold brew");
        assertEquals("Food & Dining", res1.getSuggestedCategory());
        assertTrue(res1.getConfidence() >= 0.8);
        assertTrue(res1.getRuleExplanation().contains("Food & Dining"));

        CategorizationResponse res2 = categorizationService.categorize("Swiggy team dinner");
        assertEquals("Food & Dining", res2.getSuggestedCategory());
    }

    @Test
    @DisplayName("Should categorize transportation keywords correctly")
    void testTransportationCategorization() {
        CategorizationResponse res = categorizationService.categorize("Uber ride to airport");
        assertEquals("Transportation", res.getSuggestedCategory());
        assertEquals("uber", res.getMatchedKeyword());
    }

    @Test
    @DisplayName("Should categorize utility bills correctly")
    void testUtilitiesCategorization() {
        CategorizationResponse res = categorizationService.categorize("Monthly electricity power bill");
        assertEquals("Utilities", res.getSuggestedCategory());
    }

    @Test
    @DisplayName("Should categorize entertainment subscriptions correctly")
    void testEntertainmentCategorization() {
        CategorizationResponse res = categorizationService.categorize("Netflix monthly plan");
        assertEquals("Entertainment", res.getSuggestedCategory());
    }

    @Test
    @DisplayName("Should categorize income salary correctly")
    void testSalaryCategorization() {
        CategorizationResponse res = categorizationService.categorize("Monthly company salary credit");
        assertEquals("Salary", res.getSuggestedCategory());
    }

    @Test
    @DisplayName("Should fallback gracefully to Other for unrecognized text")
    void testFallbackCategorization() {
        CategorizationResponse res = categorizationService.categorize("Unknown mysterious artifact widget");
        assertEquals("Other", res.getSuggestedCategory());
        assertTrue(res.getConfidence() <= 0.5);
    }
}
