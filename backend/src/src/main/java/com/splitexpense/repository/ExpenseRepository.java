package com.splitexpense.repository;

import com.splitexpense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository
        extends JpaRepository<Expense, Integer> {

    List<Expense> findByGroupId(int groupId);
}