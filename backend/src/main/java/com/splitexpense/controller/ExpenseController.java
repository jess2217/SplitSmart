package com.splitexpense.controller;

import com.splitexpense.model.Expense;
import com.splitexpense.model.ExpenseCategory;
import com.splitexpense.model.Group;
import com.splitexpense.model.SplitType;
import com.splitexpense.model.Student;
import com.splitexpense.repository.GroupRepository;
import com.splitexpense.service.ExpenseService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {

    private final GroupRepository groupRepository;
    private final ExpenseService expenseService;

    public ExpenseController(
            GroupRepository groupRepository,
            ExpenseService expenseService) {

        this.groupRepository = groupRepository;
        this.expenseService = expenseService;
    }

    // =========================
    // ADD EXPENSE
    // =========================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense addExpense(
            @PathVariable int groupId,
            @RequestBody CreateExpenseRequest request) {

        // -------------------------
        // VALIDATE REQUEST
        // -------------------------

        if (request == null) {

            throw new IllegalArgumentException(
                    "Expense data is required."
            );
        }

        if (request.description() == null ||
                request.description().isBlank()) {

            throw new IllegalArgumentException(
                    "Expense description is required."
            );
        }

        if (request.amount() <= 0) {

            throw new IllegalArgumentException(
                    "Expense amount must be positive."
            );
        }

        if (request.category() == null) {

            throw new IllegalArgumentException(
                    "Expense category is required."
            );
        }

        if (request.splitType() == null) {

            throw new IllegalArgumentException(
                    "Split type is required."
            );
        }

        // -------------------------
        // CUSTOM CATEGORY
        // -------------------------

        String customCategory = null;

        if (request.category() == ExpenseCategory.OTHER) {

            if (request.customCategory() == null ||
                    request.customCategory().isBlank()) {

                throw new IllegalArgumentException(
                        "Custom category is required when category is OTHER."
                );
            }

            customCategory =
                    request.customCategory().trim();
        }

        // -------------------------
        // FIND GROUP
        // -------------------------

        Group group =
                groupRepository.findById(groupId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Group not found: " + groupId
                                )
                        );

        // -------------------------
        // FIND PAYER
        // -------------------------

        Student payer =
                findStudent(
                        group,
                        request.payerId()
                );

        // -------------------------
        // VALIDATE PARTICIPANTS
        // -------------------------

        if (request.participantIds() == null ||
                request.participantIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one participant is required."
            );
        }

        List<Student> participants =
                new ArrayList<>();

        for (Integer studentId :
                request.participantIds()) {

            Student student =
                    findStudent(
                            group,
                            studentId
                    );

            if (!participants.contains(student)) {

                participants.add(student);
            }
        }

        // -------------------------
        // CUSTOM SPLIT VALUES
        // -------------------------

        Map<Student, Double> values =
                new HashMap<>();

        if (request.values() != null) {

            for (Map.Entry<Integer, Double> entry :
                    request.values().entrySet()) {

                Student student =
                        findStudent(
                                group,
                                entry.getKey()
                        );

                values.put(
                        student,
                        entry.getValue()
                );
            }
        }

        // -------------------------
        // SAVE EXPENSE
        // -------------------------

        return expenseService.addExpense(
                group,
                request.description().trim(),
                request.amount(),
                payer,
                participants,
                request.category(),
                customCategory,
                request.splitType(),
                values
        );
    }

    // =========================
    // GET EXPENSES
    // =========================

    @GetMapping
    public List<Expense> getExpenses(
            @PathVariable int groupId) {

        Group group =
                groupRepository.findById(groupId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Group not found: " + groupId
                                )
                        );

        return expenseService.getExpenses(group);
    }

    // =========================
    // DELETE EXPENSE
    // =========================

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(
            @PathVariable int groupId,
            @PathVariable int expenseId) {

        Group group =
                groupRepository.findById(groupId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Group not found: " + groupId
                                )
                        );

        expenseService.deleteExpense(
                group,
                expenseId
        );
    }

    // =========================
    // FIND STUDENT
    // =========================

    private Student findStudent(
            Group group,
            Integer studentId) {

        if (studentId == null) {

            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        return group.getMembers()
                .stream()
                .filter(student ->
                        student.getId() == studentId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student " +
                                        studentId +
                                        " does not belong to this group."
                        )
                );
    }

    // =========================
    // REQUEST DTO
    // =========================

    public record CreateExpenseRequest(
            String description,
            double amount,
            int payerId,
            List<Integer> participantIds,
            ExpenseCategory category,
            String customCategory,
            SplitType splitType,
            Map<Integer, Double> values
    ) {
    }
}