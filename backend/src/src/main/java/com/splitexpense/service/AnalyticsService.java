package com.splitexpense.service;

import com.splitexpense.model.Expense;
import com.splitexpense.model.ExpenseCategory;
import com.splitexpense.model.Group;
import com.splitexpense.model.Student;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsService {

    public Map<ExpenseCategory, Double> spendingByCategory(Group group) {
        Map<ExpenseCategory, Double> result = new EnumMap<>(ExpenseCategory.class);

        for (Expense expense : group.getExpenses()) {
            result.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }
        return result;
    }

    public Map<Student, Double> contributionByStudent(Group group) {
        Map<Student, Double> result = new LinkedHashMap<>();

        for (Student student : group.getMembers()) {
            result.put(student, 0.0);
        }

        for (Expense expense : group.getExpenses()) {
            result.merge(expense.getPayer(), expense.getAmount(), Double::sum);
        }
        return result;
    }

    public double totalSpending(Group group) {
        return group.getExpenses().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double averageExpense(Group group) {
        if (group.getExpenses().isEmpty()) return 0.0;
        return totalSpending(group) / group.getExpenses().size();
    }

    public Expense highestExpense(Group group) {
        return group.getExpenses().stream()
                .max(java.util.Comparator.comparingDouble(Expense::getAmount))
                .orElse(null);
    }
}
