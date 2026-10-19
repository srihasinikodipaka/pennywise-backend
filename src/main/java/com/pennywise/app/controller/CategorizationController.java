package com.pennywise.app.controller;

import com.pennywise.app.dto.CategorizationRequest;
import com.pennywise.app.dto.CategorizationResponse;
import com.pennywise.app.service.CategorizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class CategorizationController {

    private final CategorizationService categorizationService;

    public CategorizationController(CategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    @PostMapping("/categorize")
    public ResponseEntity<CategorizationResponse> categorize(
            @Valid @RequestBody CategorizationRequest request) {
        CategorizationResponse response = categorizationService.categorize(request.getDescription());
        return ResponseEntity.ok(response);
    }
}
