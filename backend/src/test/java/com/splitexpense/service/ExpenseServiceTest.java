package com.splitexpense.service;

import com.splitexpense.exception.InvalidExpenseException;
import com.splitexpense.model.Expense;
import com.splitexpense.model.ExpenseCategory;
import com.splitexpense.model.Group;
import com.splitexpense.model.SplitType;
import com.splitexpense.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExpenseServiceTest {

    @Test
    void shouldAddEqualSplitExpense() {

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

        ExpenseService service =
                new ExpenseService();

        // Act
        Expense expense = service.addExpense(
                group,
                "Dinner",
                900.0,
                ak,
                List.of(ak, rahul, priya),
                ExpenseCategory.FOOD,
                SplitType.EQUAL,
                Map.of()
        );

        // Assert
        assertNotNull(expense);

        assertEquals(
                "Dinner",
                expense.getDescription()
        );

        assertEquals(
                900.0,
                expense.getAmount(),
                0.000001
        );

        assertEquals(
                ak,
                expense.getPayer()
        );

        assertEquals(
                300.0,
                expense.getShares().get(ak),
                0.000001
        );

        assertEquals(
                300.0,
                expense.getShares().get(rahul),
                0.000001
        );

        assertEquals(
                300.0,
                expense.getShares().get(priya),
                0.000001
        );

        assertEquals(
                1,
                group.getExpenses().size()
        );
    }

    @Test
    void shouldAddExactSplitExpense() {

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

        ExpenseService service =
                new ExpenseService();

        Map<Student, Double> exactAmounts =
                Map.of(
                        ak, 400.0,
                        rahul, 300.0,
                        priya, 300.0
                );

        // Act
        Expense expense = service.addExpense(
                group,
                "Hotel",
                1000.0,
                ak,
                List.of(ak, rahul, priya),
                ExpenseCategory.HOSTEL,
                SplitType.EXACT,
                exactAmounts
        );

        // Assert
        assertNotNull(expense);

        assertEquals(
                400.0,
                expense.getShares().get(ak),
                0.000001
        );

        assertEquals(
                300.0,
                expense.getShares().get(rahul),
                0.000001
        );

        assertEquals(
                300.0,
                expense.getShares().get(priya),
                0.000001
        );
    }

    @Test
    void shouldAddPercentageSplitExpense() {

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

        ExpenseService service =
                new ExpenseService();

        Map<Student, Double> percentages =
                Map.of(
                        ak, 50.0,
                        rahul, 30.0,
                        priya, 20.0
                );

        // Act
        Expense expense = service.addExpense(
                group,
                "Travel",
                1000.0,
                ak,
                List.of(ak, rahul, priya),
                ExpenseCategory.TRAVEL,
                SplitType.PERCENTAGE,
                percentages
        );

        // Assert
        assertEquals(
                500.0,
                expense.getShares().get(ak),
                0.000001
        );

        assertEquals(
                300.0,
                expense.getShares().get(rahul),
                0.000001
        );

        assertEquals(
                200.0,
                expense.getShares().get(priya),
                0.000001
        );
    }

    @Test
    void shouldRejectNonPositiveExpense() {

        // Arrange
        Student ak = new Student(
                1,
                "AK",
                "ak@gmail.com",
                "KIIT"
        );

        Group group = new Group(
                1,
                "Trip"
        );

        group.addMember(ak);

        ExpenseService service =
                new ExpenseService();

        // Act + Assert
        assertThrows(
                InvalidExpenseException.class,
                () -> service.addExpense(
                        group,
                        "Invalid Expense",
                        0.0,
                        ak,
                        List.of(ak),
                        ExpenseCategory.OTHER,
                        SplitType.EQUAL,
                        Map.of()
                )
        );
    }

    @Test
    void shouldRejectPayerOutsideGroup() {

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
                "Trip"
        );

        group.addMember(rahul);

        ExpenseService service =
                new ExpenseService();

        // Act + Assert
        assertThrows(
                InvalidExpenseException.class,
                () -> service.addExpense(
                        group,
                        "Dinner",
                        500.0,
                        ak,
                        List.of(rahul),
                        ExpenseCategory.FOOD,
                        SplitType.EQUAL,
                        Map.of()
                )
        );
    }

    @Test
    void shouldRejectEmptyParticipants() {

        // Arrange
        Student ak = new Student(
                1,
                "AK",
                "ak@gmail.com",
                "KIIT"
        );

        Group group = new Group(
                1,
                "Trip"
        );

        group.addMember(ak);

        ExpenseService service =
                new ExpenseService();

        // Act + Assert
        assertThrows(
                InvalidExpenseException.class,
                () -> service.addExpense(
                        group,
                        "Dinner",
                        500.0,
                        ak,
                        List.of(),
                        ExpenseCategory.FOOD,
                        SplitType.EQUAL,
                        Map.of()
                )
        );
    }

    @Test
    void shouldRejectParticipantOutsideGroup() {

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
                "Trip"
        );

        group.addMember(ak);

        ExpenseService service =
                new ExpenseService();

        // Act + Assert
        assertThrows(
                InvalidExpenseException.class,
                () -> service.addExpense(
                        group,
                        "Dinner",
                        500.0,
                        ak,
                        List.of(ak, rahul),
                        ExpenseCategory.FOOD,
                        SplitType.EQUAL,
                        Map.of()
                )
        );
    }

    @Test
    void shouldReturnAllGroupExpenses() {

        // Arrange
        Student ak = new Student(
                1,
                "AK",
                "ak@gmail.com",
                "KIIT"
        );

        Group group = new Group(
                1,
                "Trip"
        );

        group.addMember(ak);

        ExpenseService service =
                new ExpenseService();

        service.addExpense(
                group,
                "Food",
                500.0,
                ak,
                List.of(ak),
                ExpenseCategory.FOOD,
                SplitType.EQUAL,
                Map.of()
        );

        service.addExpense(
                group,
                "Cab",
                300.0,
                ak,
                List.of(ak),
                ExpenseCategory.CAB,
                SplitType.EQUAL,
                Map.of()
        );

        // Act
        List<Expense> expenses =
                service.getExpenses(group);

        // Assert
        assertEquals(
                2,
                expenses.size()
        );

        assertEquals(
                "Food",
                expenses.get(0).getDescription()
        );

        assertEquals(
                "Cab",
                expenses.get(1).getDescription()
        );
    }
}