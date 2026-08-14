package com.splitexpense.service;

import com.splitexpense.model.Expense;
import com.splitexpense.model.ExpenseCategory;
import com.splitexpense.model.Group;
import com.splitexpense.model.SplitType;
import com.splitexpense.model.Student;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BalanceServiceTest {

    @Test
    void shouldCalculateBalancesCorrectly() {

        // Arrange
        Student ak = new Student(
                1,
                "AK",
                "ak@gmail.com",
                "KIIT"
        );

        Student rahul = new Student(
                2,
                "Rahul",
                "rahul@gmail.com",
                "KIIT"
        );

        Student priya = new Student(
                3,
                "Priya",
                "priya@gmail.com",
                "KIIT"
        );

        Group group = new Group(
                1,
                "College Trip"
        );

        group.addMember(ak);
        group.addMember(rahul);
        group.addMember(priya);

        Map<Student, Double> shares = Map.of(
                ak, 300.0,
                rahul, 300.0,
                priya, 300.0
        );

        Expense expense = new Expense(
                1,
                "Dinner",
                900.0,
                ak,
                ExpenseCategory.FOOD,
                SplitType.EQUAL,
                LocalDateTime.now(),
                shares
        );

        group.addExpense(expense);

        BalanceService balanceService =
                new BalanceService();

        // Act
        Map<Student, Double> balances =
                balanceService.calculateBalances(group);

        // Assert
        assertEquals(600.0, balances.get(ak), 0.000001);
        assertEquals(-300.0, balances.get(rahul), 0.000001);
        assertEquals(-300.0, balances.get(priya), 0.000001);
    }

    @Test
    void shouldReturnZeroWhenEveryoneIsSettled() {

        // Arrange
        Student ak = new Student(
                1,
                "AK",
                "ak@gmail.com",
                "KIIT"
        );

        Student rahul = new Student(
                2,
                "Rahul",
                "rahul@gmail.com",
                "KIIT"
        );

        Group group = new Group(
                1,
                "College Trip"
        );

        group.addMember(ak);
        group.addMember(rahul);

        Expense dinner = new Expense(
                1,
                "Dinner",
                500.0,
                ak,
                ExpenseCategory.FOOD,
                SplitType.EQUAL,
                LocalDateTime.now(),
                Map.of(
                        ak, 250.0,
                        rahul, 250.0
                )
        );

        Expense tickets = new Expense(
                2,
                "Movie Tickets",
                500.0,
                rahul,
                ExpenseCategory.ENTERTAINMENT,
                SplitType.EQUAL,
                LocalDateTime.now(),
                Map.of(
                        ak, 250.0,
                        rahul, 250.0
                )
        );

        group.addExpense(dinner);
        group.addExpense(tickets);

        BalanceService balanceService =
                new BalanceService();

        // Act
        Map<Student, Double> balances =
                balanceService.calculateBalances(group);

        // Assert
        assertEquals(0.0, balances.get(ak), 0.000001);
        assertEquals(0.0, balances.get(rahul), 0.000001);
    }

    @Test
    void totalBalancesShouldAlwaysBeZero() {

        // Arrange
        Student ak = new Student(
                1,
                "AK",
                "ak@gmail.com",
                "KIIT"
        );

        Student rahul = new Student(
                2,
                "Rahul",
                "rahul@gmail.com",
                "KIIT"
        );

        Student priya = new Student(
                3,
                "Priya",
                "priya@gmail.com",
                "KIIT"
        );

        Group group = new Group(
                1,
                "Trip"
        );

        group.addMember(ak);
        group.addMember(rahul);
        group.addMember(priya);

        Expense expense = new Expense(
                1,
                "Food",
                900.0,
                ak,
                ExpenseCategory.FOOD,
                SplitType.EQUAL,
                LocalDateTime.now(),
                Map.of(
                        ak, 300.0,
                        rahul, 300.0,
                        priya, 300.0
                )
        );

        group.addExpense(expense);

        BalanceService balanceService =
                new BalanceService();

        // Act
        Map<Student, Double> balances =
                balanceService.calculateBalances(group);

        double totalBalance = balances.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Assert
        assertEquals(0.0, totalBalance, 0.000001);
    }
}