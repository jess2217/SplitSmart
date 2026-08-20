package com.splitexpense.service;

import com.splitexpense.exception.InvalidExpenseException;
import com.splitexpense.model.Expense;
import com.splitexpense.model.ExpenseCategory;
import com.splitexpense.model.Group;
import com.splitexpense.model.SplitType;
import com.splitexpense.model.Student;
import com.splitexpense.repository.ExpenseRepository;
import com.splitexpense.repository.GroupRepository;
import com.splitexpense.strategy.EqualSplitStrategy;
import com.splitexpense.strategy.ExactSplitStrategy;
import com.splitexpense.strategy.PercentageSplitStrategy;
import com.splitexpense.strategy.SplitStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private GroupRepository groupRepository;
    private ExpenseRepository expenseRepository;

    // =========================
    // SPRING BOOT CONSTRUCTOR
    // =========================

    @Autowired
    public ExpenseService(
            GroupRepository groupRepository,
            ExpenseRepository expenseRepository) {

        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
    }

    // =========================
    // TEST CONSTRUCTOR
    // =========================

    public ExpenseService() {

        this.groupRepository = null;
        this.expenseRepository = null;
    }

    // =====================================================
    // ADD EXPENSE - OLD SIGNATURE
    // =====================================================
    //
    // Keeps existing tests and older code working.
    //
    // =====================================================

    @Transactional
    public Expense addExpense(
            Group group,
            String description,
            double amount,
            Student payer,
            List<Student> participants,
            ExpenseCategory category,
            SplitType splitType,
            Map<Student, Double> values) {

        return addExpense(
                group,
                description,
                amount,
                payer,
                participants,
                category,
                null,
                splitType,
                values
        );
    }

    // =====================================================
    // ADD EXPENSE - NEW SIGNATURE
    // =====================================================

    @Transactional
    public Expense addExpense(
            Group group,
            String description,
            double amount,
            Student payer,
            List<Student> participants,
            ExpenseCategory category,
            String customCategory,
            SplitType splitType,
            Map<Student, Double> values) {

        // -------------------------
        // VALIDATE GROUP
        // -------------------------

        if (group == null) {

            throw new InvalidExpenseException(
                    "Group is required."
            );
        }

        // -------------------------
        // VALIDATE DESCRIPTION
        // -------------------------

        if (description == null ||
                description.isBlank()) {

            throw new InvalidExpenseException(
                    "Expense description is required."
            );
        }

        // -------------------------
        // VALIDATE AMOUNT
        // -------------------------

        if (amount <= 0) {

            throw new InvalidExpenseException(
                    "Expense amount must be positive."
            );
        }

        // -------------------------
        // VALIDATE PAYER
        // -------------------------

        if (payer == null) {

            throw new InvalidExpenseException(
                    "Payer is required."
            );
        }

        if (!group.getMembers().contains(payer)) {

            throw new InvalidExpenseException(
                    "Payer must belong to the group."
            );
        }

        // -------------------------
        // VALIDATE PARTICIPANTS
        // -------------------------

        if (participants == null ||
                participants.isEmpty()) {

            throw new InvalidExpenseException(
                    "At least one participant is required."
            );
        }

        for (Student student : participants) {

            if (student == null) {

                throw new InvalidExpenseException(
                        "Participant cannot be null."
                );
            }

            if (!group.getMembers().contains(student)) {

                throw new InvalidExpenseException(
                        "All participants must belong to the group."
                );
            }
        }

        // -------------------------
        // VALIDATE CATEGORY
        // -------------------------

        if (category == null) {

            throw new InvalidExpenseException(
                    "Expense category is required."
            );
        }

        // -------------------------
        // HANDLE OTHER CATEGORY
        // -------------------------

        if (category == ExpenseCategory.OTHER) {

            if (customCategory == null ||
                    customCategory.isBlank()) {

                throw new InvalidExpenseException(
                        "Custom category is required when category is OTHER."
                );
            }

            customCategory =
                    customCategory.trim();

        } else {

            /*
             * Normal categories don't need
             * a custom category.
             */
            customCategory = null;
        }

        // -------------------------
        // VALIDATE SPLIT TYPE
        // -------------------------

        if (splitType == null) {

            throw new InvalidExpenseException(
                    "Split type is required."
            );
        }

        // -------------------------
        // SELECT SPLIT STRATEGY
        // -------------------------

        SplitStrategy strategy =
                switch (splitType) {

                    case EQUAL ->
                            new EqualSplitStrategy();

                    case EXACT ->
                            new ExactSplitStrategy();

                    case PERCENTAGE ->
                            new PercentageSplitStrategy();
                };

        // -------------------------
        // CALCULATE SHARES
        // -------------------------

        Map<Student, Double> shares =
                strategy.calculateShares(
                        amount,
                        participants,
                        values
                );

        // -------------------------
        // CREATE EXPENSE
        // -------------------------

        Expense expense =
                new Expense(
                        description.trim(),
                        amount,
                        payer,
                        category,
                        customCategory,
                        splitType,
                        LocalDateTime.now(),
                        group,
                        shares
                );

        // -------------------------
        // ADD EXPENSE TO GROUP
        // -------------------------

        group.addExpense(expense);

        // -------------------------
        // SAVE TO DATABASE
        // -------------------------

        if (expenseRepository != null) {

            return expenseRepository.save(expense);
        }

        // -------------------------
        // TEST MODE
        // -------------------------

        if (groupRepository != null) {

            groupRepository.save(group);
        }

        return expense;
    }

    // =========================
    // GET EXPENSES
    // =========================

    @Transactional(readOnly = true)
    public List<Expense> getExpenses(
            Group group) {

        if (group == null) {

            throw new InvalidExpenseException(
                    "Group is required."
            );
        }

        // -------------------------
        // DATABASE MODE
        // -------------------------

        if (expenseRepository != null) {

            return new ArrayList<>(
                    expenseRepository.findByGroupId(
                            group.getId()
                    )
            );
        }

        // -------------------------
        // TEST MODE
        // -------------------------

        return new ArrayList<>(
                group.getExpenses()
        );
    }

    // =========================
    // DELETE EXPENSE
    // =========================

    @Transactional
    public void deleteExpense(
            Group group,
            int expenseId) {

        if (group == null) {

            throw new InvalidExpenseException(
                    "Group is required."
            );
        }

        if (expenseRepository == null) {

            throw new InvalidExpenseException(
                    "Expense repository is unavailable."
            );
        }

        // -------------------------
        // FIND EXPENSE
        // -------------------------

        Expense expense =
                expenseRepository.findById(expenseId)
                        .orElseThrow(() ->
                                new InvalidExpenseException(
                                        "Expense not found: "
                                                + expenseId
                                )
                        );

        // -------------------------
        // VALIDATE GROUP
        // -------------------------

        if (expense.getGroup() == null) {

            throw new InvalidExpenseException(
                    "Expense is not assigned to a group."
            );
        }

        if (expense.getGroup().getId()
                != group.getId()) {

            throw new InvalidExpenseException(
                    "Expense does not belong to this group."
            );
        }

        // -------------------------
        // MANAGED GROUP
        // -------------------------

        Group managedGroup =
                expense.getGroup();

        // -------------------------
        // REMOVE FROM GROUP
        // -------------------------

        managedGroup.removeExpense(expense);

        // -------------------------
        // DELETE EXPENSE
        // -------------------------

        expenseRepository.delete(expense);

        // -------------------------
        // FORCE DELETE
        // -------------------------

        expenseRepository.flush();
    }
}