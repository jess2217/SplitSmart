package com.splitexpense.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    private double amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payer_id")
    private Student payer;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    private SplitType splitType;

    private LocalDateTime dateTime;

    /*
     * Group to which this expense belongs.
     *
     * JsonIgnore prevents circular JSON:
     * Group -> Expense -> Group -> Expense ...
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(
            mappedBy = "expense",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private final java.util.List<ExpenseShare> shareEntities =
            new java.util.ArrayList<>();

    /*
     * JPA requires a no-argument constructor.
     */
    protected Expense() {
    }

    /*
     * Constructor with ID and Group.
     */
    public Expense(
            int id,
            String description,
            double amount,
            Student payer,
            ExpenseCategory category,
            SplitType splitType,
            LocalDateTime dateTime,
            Group group,
            Map<Student, Double> shares) {

        this.id = id;
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.category = category;
        this.splitType = splitType;
        this.dateTime = dateTime;
        this.group = group;

        if (shares != null) {

            for (Map.Entry<Student, Double> entry :
                    shares.entrySet()) {

                shareEntities.add(
                        new ExpenseShare(
                                this,
                                entry.getKey(),
                                entry.getValue()
                        )
                );
            }
        }
    }

    /*
     * Constructor used when creating a NEW expense.
     *
     * Group is intentionally not required here because
     * Group.addExpense(expense) will set it.
     */
/*
 * Constructor used when creating a new expense
 * with an associated group.
 */
public Expense(
        String description,
        double amount,
        Student payer,
        ExpenseCategory category,
        SplitType splitType,
        LocalDateTime dateTime,
        Group group,
        Map<Student, Double> shares) {

    this(
            0,
            description,
            amount,
            payer,
            category,
            splitType,
            dateTime,
            group,
            shares
    );
}

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public Student getPayer() {
        return payer;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
    public Expense(
        int id,
        String description,
        double amount,
        Student payer,
        ExpenseCategory category,
        SplitType splitType,
        LocalDateTime dateTime,
        Map<Student, Double> shares) {

    this(
            id,
            description,
            amount,
            payer,
            category,
            splitType,
            dateTime,
            null,
            shares
    );
}

    public Map<Student, Double> getShares() {

        Map<Student, Double> result =
                new LinkedHashMap<>();

        for (ExpenseShare share : shareEntities) {

            result.put(
                    share.getStudent(),
                    share.getAmount()
            );
        }

        return result;
    }

    @Override
    public String toString() {

        return "Expense ID: " + id +
                " | " + description +
                " | Amount: ₹" +
                String.format("%.2f", amount) +
                " | Paid by: " +
                payer.getName() +
                " | Category: " +
                category +
                " | Split: " +
                splitType;
    }
}