package com.splitexpense.model;

import java.time.LocalDate;

public class RecurringExpense {
    private final int id;
    private final String description;
    private final double amount;
    private final Student payer;
    private final ExpenseCategory category;
    private final LocalDate nextDueDate;
    private final String recurrence;

    public RecurringExpense(int id, String description, double amount,
                            Student payer, ExpenseCategory category,
                            LocalDate nextDueDate, String recurrence) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.category = category;
        this.nextDueDate = nextDueDate;
        this.recurrence = recurrence;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public Student getPayer() { return payer; }
    public ExpenseCategory getCategory() { return category; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public String getRecurrence() { return recurrence; }

    @Override
    public String toString() {
        return "ID: " + id + " | " + description + " | ₹" +
               String.format("%.2f", amount) + " | Next due: " +
               nextDueDate + " | " + recurrence;
    }
}
