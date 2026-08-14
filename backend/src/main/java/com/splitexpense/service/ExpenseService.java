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

    /*
     * ==========================================
     * SPRING BOOT CONSTRUCTOR
     * ==========================================
     *
     * Spring Boot uses this constructor.
     * Both repositories are available in the
     * real application.
     */
    @Autowired
    public ExpenseService(
            GroupRepository groupRepository,
            ExpenseRepository expenseRepository) {

        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
    }


    /*
     * ==========================================
     * TEST COMPATIBILITY CONSTRUCTOR
     * ==========================================
     *
     * Existing unit tests use:
     *
     * new ExpenseService()
     *
     * These tests work with the Group object
     * instead of the database.
     */
    public ExpenseService() {

        this.groupRepository = null;
        this.expenseRepository = null;
    }


    /*
     * ==========================================
     * ADD EXPENSE
     * ==========================================
     */

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

        if (group == null) {

            throw new InvalidExpenseException(
                    "Group is required."
            );
        }

        if (description == null ||
                description.isBlank()) {

            throw new InvalidExpenseException(
                    "Expense description is required."
            );
        }

        if (amount <= 0) {

            throw new InvalidExpenseException(
                    "Expense amount must be positive."
            );
        }

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

        if (category == null) {

            throw new InvalidExpenseException(
                    "Expense category is required."
            );
        }

        if (splitType == null) {

            throw new InvalidExpenseException(
                    "Split type is required."
            );
        }


        SplitStrategy strategy = switch (splitType) {

            case EQUAL ->
                    new EqualSplitStrategy();

            case EXACT ->
                    new ExactSplitStrategy();

            case PERCENTAGE ->
                    new PercentageSplitStrategy();
        };


        Map<Student, Double> shares =
                strategy.calculateShares(
                        amount,
                        participants,
                        values
                );


        Expense expense =
                new Expense(
                        description.trim(),
                        amount,
                        payer,
                        category,
                        splitType,
                        LocalDateTime.now(),
                        group,
                        shares
                );


        /*
         * Add expense to group.
         */
        group.addExpense(expense);


        /*
         * REAL APPLICATION
         *
         * Spring Boot has injected the repository.
         */
        if (expenseRepository != null) {

            return expenseRepository.save(expense);
        }


        /*
         * UNIT TESTS
         *
         * No database repository is available.
         * The expense has already been added to
         * the in-memory Group above.
         */
        if (groupRepository != null) {

            groupRepository.save(group);
        }

        return expense;
    }


    /*
     * ==========================================
     * GET EXPENSES
     * ==========================================
     */

    @Transactional(readOnly = true)
    public List<Expense> getExpenses(
            Group group) {

        if (group == null) {

            throw new InvalidExpenseException(
                    "Group is required."
            );
        }


        /*
         * REAL APPLICATION
         */
        if (expenseRepository != null) {

            return new ArrayList<>(
                    expenseRepository.findByGroupId(
                            group.getId()
                    )
            );
        }


        /*
         * UNIT TESTS
         */
        return new ArrayList<>(
                group.getExpenses()
        );
    }


    /*
     * ==========================================
     * DELETE EXPENSE
     * ==========================================
     */

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

    /*
     * Find the expense from the database.
     */
    Expense expense =
            expenseRepository.findById(expenseId)
                    .orElseThrow(() ->
                            new InvalidExpenseException(
                                    "Expense not found: "
                                            + expenseId
                            )
                    );

    /*
     * Verify that this expense belongs
     * to the requested group.
     */
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

    /*
     * Get the MANAGED group associated with
     * the expense.
     *
     * This is important because the Group object
     * received from the controller may be detached.
     */
    Group managedGroup =
            expense.getGroup();

    /*
     * Remove the expense from the managed
     * group's collection.
     *
     * Group.removeExpense() modifies the actual
     * JPA collection internally.
     */
    managedGroup.removeExpense(expense);

    /*
     * Delete the expense itself.
     *
     * ExpenseShare records are also deleted because
     * Expense uses cascade = ALL and orphanRemoval = true.
     */
    expenseRepository.delete(expense);

    /*
     * Force Hibernate to execute the DELETE
     * before returning from this request.
     */
    expenseRepository.flush();
}
}