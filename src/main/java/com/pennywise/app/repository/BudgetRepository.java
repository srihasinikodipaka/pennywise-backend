package com.pennywise.app.repository;

import com.pennywise.app.model.Budget;
import com.pennywise.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserAndMonth(User user, String month);
    List<Budget> findByUser(User user);
    Optional<Budget> findByUserAndCategoryAndMonth(User user, String category, String month);
}
