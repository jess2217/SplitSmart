package com.splitexpense.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
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

    /*
     * Used when category is OTHER.
     *
     * Example:
     * category = OTHER
     * customCategory = "Hotel"
     */
    private String customCategory;

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
            String customCategory,
            SplitType splitType,
            LocalDateTime dateTime,
            Group group,
            Map<Student, Double> shares) {

        this.id = id;
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.category = category;
        this.customCategory = customCategory;
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
     */
    public Expense(
            String description,
            double amount,
            Student payer,
            ExpenseCategory category,
            String customCategory,
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
                customCategory,
                splitType,
                dateTime,
                group,
                shares
        );
    }

    /*
     * Backward-compatible constructor.
     *
     * Used by existing tests/code that does not provide
     * a custom category.
     */
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
                null,
                splitType,
                dateTime,
                null,
                shares
        );
    }

    // =========================
    // GETTERS
    // =========================

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

    public String getCustomCategory() {
        return customCategory;
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

    // =========================
    // SETTERS
    // =========================

    public void setCustomCategory(
            String customCategory) {

        this.customCategory = customCategory;
    }

    public void setGroup(Group group) {

        this.group = group;
    }

    // =========================
    // SHARES
    // =========================

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

    // =========================
    // TO STRING
    // =========================

    @Override
    public String toString() {

        String displayCategory =
                category == ExpenseCategory.OTHER &&
                customCategory != null &&
                !customCategory.isBlank()
                        ? customCategory
                        : String.valueOf(category);

        return "Expense ID: " + id +
                " | " + description +
                " | Amount: ₹" +
                String.format("%.2f", amount) +
                " | Paid by: " +
                payer.getName() +
                " | Category: " +
                displayCategory +
                " | Split: " +
                splitType;
    }
}