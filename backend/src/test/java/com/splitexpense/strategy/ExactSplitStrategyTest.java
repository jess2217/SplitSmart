package com.splitexpense.strategy;
import com.splitexpense.exception.InvalidSplitException;

import com.splitexpense.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExactSplitStrategyTest {

    @Test
    void shouldSplitExpenseUsingExactAmounts() {

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

        List<Student> participants =
                List.of(ak, rahul, priya, aman);

        Map<Student, Double> exactAmounts =
                Map.of(
                        ak, 400.0,
                        rahul, 300.0,
                        priya, 200.0,
                        aman, 100.0
                );

        ExactSplitStrategy strategy =
                new ExactSplitStrategy();

        // Act
        Map<Student, Double> shares =
                strategy.calculateShares(
                        1000.0,
                        participants,
                        exactAmounts
                );

        // Assert
        assertEquals(400.0, shares.get(ak));
        assertEquals(300.0, shares.get(rahul));
        assertEquals(200.0, shares.get(priya));
        assertEquals(100.0, shares.get(aman));
    }

    @Test
    void shouldRejectExactAmountsThatDoNotMatchExpense() {

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

        List<Student> participants =
                List.of(ak, rahul, priya, aman);

        // Total = ₹950, but expense = ₹1000
        Map<Student, Double> exactAmounts =
                Map.of(
                        ak, 400.0,
                        rahul, 300.0,
                        priya, 200.0,
                        aman, 50.0
                );

        ExactSplitStrategy strategy =
                new ExactSplitStrategy();

        // Act + Assert
        assertThrows(
                RuntimeException.class,
                () -> strategy.calculateShares(
                        1000.0,
                        participants,
                        exactAmounts
                )
        );
    }

    @Test
    void shouldRejectNegativeExactShare() {

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

        List<Student> participants =
                List.of(ak, rahul);

        Map<Student, Double> exactAmounts =
                Map.of(
                        ak, 700.0,
                        rahul, -200.0
                );

        ExactSplitStrategy strategy =
                new ExactSplitStrategy();

        // Act + Assert
        assertThrows(
                RuntimeException.class,
                () -> strategy.calculateShares(
                        500.0,
                        participants,
                        exactAmounts
                )
        );
    }

    @Test
    void shouldRejectMissingExactAmount() {

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

        List<Student> participants =
                List.of(ak, rahul);

        // Only AK has an amount
        Map<Student, Double> exactAmounts =
                Map.of(
                        ak, 500.0
                );

        ExactSplitStrategy strategy =
                new ExactSplitStrategy();

        // Act + Assert
        assertThrows(
                InvalidSplitException.class,
                () -> strategy.calculateShares(
                        500.0,
                        participants,
                        exactAmounts
                )
        );
    }
}