package com.splitexpense.strategy;
import com.splitexpense.exception.InvalidSplitException;

import com.splitexpense.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PercentageSplitStrategyTest {

    @Test
    void shouldSplitExpenseUsingPercentages() {

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

        Map<Student, Double> percentages =
                Map.of(
                        ak, 40.0,
                        rahul, 30.0,
                        priya, 20.0,
                        aman, 10.0
                );

        PercentageSplitStrategy strategy =
                new PercentageSplitStrategy();

        // Act
        Map<Student, Double> shares =
                strategy.calculateShares(
                        1000.0,
                        participants,
                        percentages
                );

        // Assert
        assertEquals(400.0, shares.get(ak));
        assertEquals(300.0, shares.get(rahul));
        assertEquals(200.0, shares.get(priya));
        assertEquals(100.0, shares.get(aman));
    }

    @Test
    void shouldRejectPercentagesThatDoNotTotal100() {

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

        // 40 + 30 + 20 + 20 = 110%
        Map<Student, Double> percentages =
                Map.of(
                        ak, 40.0,
                        rahul, 30.0,
                        priya, 20.0,
                        aman, 20.0
                );

        PercentageSplitStrategy strategy =
                new PercentageSplitStrategy();

        // Act + Assert
        assertThrows(
                RuntimeException.class,
                () -> strategy.calculateShares(
                        1000.0,
                        participants,
                        percentages
                )
        );
    }

    @Test
    void shouldRejectNegativePercentage() {

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

        Map<Student, Double> percentages =
                Map.of(
                        ak, 120.0,
                        rahul, -20.0
                );

        PercentageSplitStrategy strategy =
                new PercentageSplitStrategy();

        // Act + Assert
        assertThrows(
                InvalidSplitException.class,
                () -> strategy.calculateShares(
                        1000.0,
                        participants,
                        percentages
                )
        );
    }

    @Test
    void shouldRejectMissingPercentage() {

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

        // Only AK has a percentage
        Map<Student, Double> percentages =
                Map.of(
                        ak, 100.0
                );

        PercentageSplitStrategy strategy =
                new PercentageSplitStrategy();

        // Act + Assert
        assertThrows(
                RuntimeException.class,
                () -> strategy.calculateShares(
                        1000.0,
                        participants,
                        percentages
                )
        );
    }

    @Test
    void shouldEnsureTotalSharesEqualExpenseAmount() {

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

        List<Student> participants =
                List.of(ak, rahul, priya);

        // 33.33 + 33.33 + 33.34 = 100%
        Map<Student, Double> percentages =
                Map.of(
                        ak, 33.33,
                        rahul, 33.33,
                        priya, 33.34
                );

        PercentageSplitStrategy strategy =
                new PercentageSplitStrategy();

        // Act
        Map<Student, Double> shares =
                strategy.calculateShares(
                        100.0,
                        participants,
                        percentages
                );

        double total = shares.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Assert
        assertEquals(100.0, total, 0.000001);
    }
}