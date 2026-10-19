package com.pennywise.app.service;

import com.pennywise.app.dto.CategorizationResponse;
import com.pennywise.app.dto.TransactionRequest;
import com.pennywise.app.dto.TransactionResponse;
import com.pennywise.app.model.Transaction;
import com.pennywise.app.model.TransactionType;
import com.pennywise.app.model.User;
import com.pennywise.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuthService authService;
    private final CategorizationService categorizationService;

    public TransactionService(TransactionRepository transactionRepository,
                              AuthService authService,
                              CategorizationService categorizationService) {
        this.transactionRepository = transactionRepository;
        this.authService = authService;
        this.categorizationService = categorizationService;
    }

    public List<TransactionResponse> getTransactions(TransactionType type, String category,
                                                     LocalDate startDate, LocalDate endDate, String search) {
        User user = authService.getCurrentAuthenticatedUser();
        List<Transaction> transactions;

        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDescIdDesc(user, startDate, endDate);
        } else {
            transactions = transactionRepository.findByUserOrderByDateDescIdDesc(user);
        }

        return transactions.stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> category == null || category.equalsIgnoreCase("ALL") || t.getCategory().equalsIgnoreCase(category))
                .filter(t -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String s = search.toLowerCase();
                    return t.getTitle().toLowerCase().contains(s) ||
                           (t.getNotes() != null && t.getNotes().toLowerCase().contains(s)) ||
                           t.getCategory().toLowerCase().contains(s);
                })
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to transaction");
        }

        return new TransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = authService.getCurrentAuthenticatedUser();

        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setTitle(request.getTitle().trim());
        tx.setAmount(request.getAmount());
        tx.setType(request.getType());
        tx.setDate(request.getDate());
        tx.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "UPI");
        tx.setNotes(request.getNotes());

        // Smart explainable categorization check
        CategorizationResponse catResult = categorizationService.categorize(request.getTitle());
        if (request.getCategory() == null || request.getCategory().trim().isEmpty() || request.getCategory().equalsIgnoreCase("Other")) {
            tx.setCategory(catResult.getSuggestedCategory());
        } else {
            tx.setCategory(request.getCategory().trim());
        }
        tx.setMatchedRule(catResult.getRuleExplanation());

        Transaction saved = transactionRepository.save(tx);
        return new TransactionResponse(saved);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if (!tx.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized update: transaction does not belong to you");
        }

        tx.setTitle(request.getTitle().trim());
        tx.setAmount(request.getAmount());
        tx.setType(request.getType());
        tx.setCategory(request.getCategory().trim());
        tx.setDate(request.getDate());
        tx.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "UPI");
        tx.setNotes(request.getNotes());

        // Update matched rule if title changed
        CategorizationResponse catResult = categorizationService.categorize(request.getTitle());
        tx.setMatchedRule(catResult.getRuleExplanation());

        Transaction updated = transactionRepository.save(tx);
        return new TransactionResponse(updated);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if (!tx.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized delete: transaction does not belong to you");
        }

        transactionRepository.delete(tx);
    }
}
