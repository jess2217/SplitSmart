package com.splitexpense.model;

public class Transaction {

    private final Student from;
    private final Student to;
    private final double amount;

    public Transaction(
            Student from,
            Student to,
            double amount) {

        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Student getFrom() {
        return from;
    }

    public Student getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {

        return from.getName() +
                " → " +
                to.getName() +
                " ₹" +
                String.format("%.2f", amount);
    }
}