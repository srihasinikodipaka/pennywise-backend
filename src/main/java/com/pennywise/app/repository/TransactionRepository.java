package com.pennywise.app.repository;

import com.pennywise.app.model.Transaction;
import com.pennywise.app.model.TransactionType;
import com.pennywise.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDescIdDesc(User user);

    List<Transaction> findByUserAndDateBetweenOrderByDateDescIdDesc(User user, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUserAndTypeOrderByDateDescIdDesc(User user, TransactionType type);

    List<Transaction> findByUserAndCategoryOrderByDateDescIdDesc(User user, String category);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = :type")
    BigDecimal sumAmountByUserAndType(@Param("user") User user, @Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserAndTypeAndDateBetween(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = 'EXPENSE' AND t.category = :category AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumExpenseByUserAndCategoryAndDateBetween(
            @Param("user") User user,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t.category, COALESCE(SUM(t.amount), 0), COUNT(t) FROM Transaction t WHERE t.user = :user AND t.type = 'EXPENSE' AND t.date BETWEEN :startDate AND :endDate GROUP BY t.category ORDER BY SUM(t.amount) DESC")
    List<Object[]> getCategoryExpenseBreakdown(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
