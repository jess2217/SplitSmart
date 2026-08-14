package com.splitexpense.algorithm;

import com.splitexpense.model.Student;
import com.splitexpense.model.Transaction;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DebtSimplifierTest {

    @Test
    void shouldCreateCorrectSettlement() {

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

        Map<Student, Double> balances =
                new LinkedHashMap<>();

        balances.put(ak, 600.0);
        balances.put(rahul, -300.0);
        balances.put(priya, -300.0);

        DebtSimplifier simplifier =
                new DebtSimplifier();

        // Act
        List<Transaction> transactions =
                simplifier.simplify(balances);

        // Assert
        assertEquals(2, transactions.size());

        assertEquals(
                300.0,
                transactions.get(0).getAmount(),
                0.000001
        );

        assertEquals(
                300.0,
                transactions.get(1).getAmount(),
                0.000001
        );
    }

    @Test
    void shouldCreateMinimumTransactions() {

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

        Map<Student, Double> balances =
                new LinkedHashMap<>();

        balances.put(ak, 500.0);
        balances.put(rahul, -300.0);
        balances.put(priya, -200.0);

        DebtSimplifier simplifier =
                new DebtSimplifier();

        // Act
        List<Transaction> transactions =
                simplifier.simplify(balances);

        // Assert
        assertEquals(2, transactions.size());

        double totalSettled =
                transactions.stream()
                        .mapToDouble(Transaction::getAmount)
                        .sum();

        assertEquals(
                500.0,
                totalSettled,
                0.000001
        );
    }

    @Test
    void shouldHandleMultipleCreditorsAndDebtors() {

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

        Student aman = new Student(
                4,
                "Aman",
                "aman@gmail.com",
                "KIIT"
        );

        Map<Student, Double> balances =
                new LinkedHashMap<>();

        balances.put(ak, 700.0);
        balances.put(rahul, 300.0);
        balances.put(priya, -600.0);
        balances.put(aman, -400.0);

        DebtSimplifier simplifier =
                new DebtSimplifier();

        // Act
        List<Transaction> transactions =
                simplifier.simplify(balances);

        // Assert
        assertEquals(3, transactions.size());

        double totalSettled =
                transactions.stream()
                        .mapToDouble(Transaction::getAmount)
                        .sum();

        assertEquals(
                1000.0,
                totalSettled,
                0.000001
        );
    }

    @Test
    void shouldReturnEmptyListWhenEveryoneIsSettled() {

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

        Map<Student, Double> balances =
                new LinkedHashMap<>();

        balances.put(ak, 0.0);
        balances.put(rahul, 0.0);

        DebtSimplifier simplifier =
                new DebtSimplifier();

        // Act
        List<Transaction> transactions =
                simplifier.simplify(balances);

        // Assert
        assertEquals(0, transactions.size());
    }

    @Test
    void shouldIgnoreVerySmallBalances() {

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

        Map<Student, Double> balances =
                new LinkedHashMap<>();

        balances.put(ak, 0.003);
        balances.put(rahul, -0.003);

        DebtSimplifier simplifier =
                new DebtSimplifier();

        // Act
        List<Transaction> transactions =
                simplifier.simplify(balances);

        // Assert
        assertEquals(0, transactions.size());
    }
}