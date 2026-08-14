package com.splitexpense.model;

import jakarta.persistence.*;

@Entity
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    private Expense expense;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    private double amount;

    protected ExpenseShare() {
    }

    public ExpenseShare(
            Expense expense,
            Student student,
            double amount) {

        this.expense = expense;
        this.student = student;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public Expense getExpense() {
        return expense;
    }

    public Student getStudent() {
        return student;
    }

    public double getAmount() {
        return amount;
    }
}