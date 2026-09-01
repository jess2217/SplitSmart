package com.splitexpense.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "expense_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "owner_id")
private User owner;

@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id"),
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_group_student",
                        columnNames = {"group_id", "student_id"}
                )
        }
)
private final List<Student> members =
        new ArrayList<>();
   @OneToMany(
        mappedBy = "group",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
)
private final List<Expense> expenses =
        new ArrayList<>();

    protected Group() {
    }

    public Group(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Group(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(expenses);
    }
    public boolean removeExpense(Expense expense) {
    return expenses.remove(expense);
}

    public boolean addMember(Student student) {

        if (student == null ||
                members.contains(student)) {

            return false;
        }

        return members.add(student);
    }

    public boolean removeMember(Student student) {
        return members.remove(student);
    }

    public void addExpense(Expense expense) {

    if (expense == null) {
        return;
    }

    expense.setGroup(this);
    expenses.add(expense);
}
public User getOwner() {
    return owner;
}

public void setOwner(User owner) {
    this.owner = owner;
}

    @Override
    public String toString() {

        return "Group ID: " + id +
                " | Name: " + name +
                " | Members: " + members.size() +
                " | Expenses: " + expenses.size();
    }
}