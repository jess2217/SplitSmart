package com.splitexpense.strategy;
import com.splitexpense.exception.InvalidSplitException;
import com.splitexpense.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EqualSplitStrategyTest {

    @Test
    void shouldSplitExpenseEqually() {

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

        EqualSplitStrategy strategy =
                new EqualSplitStrategy();

        // Act
        Map<Student, Double> shares =
                strategy.calculateShares(
                        1500,
                        participants,
                        Map.of()
                );

        // Assert
        assertEquals(375.0, shares.get(ak));
        assertEquals(375.0, shares.get(rahul));
        assertEquals(375.0, shares.get(priya));
        assertEquals(375.0, shares.get(aman));
    }

    @Test
    void shouldRejectEmptyParticipantList() {

        // Arrange
        EqualSplitStrategy strategy =
                new EqualSplitStrategy();

        // Act + Assert
        assertThrows(
                 InvalidSplitException.class,
                () -> strategy.calculateShares(
                        1500,
                        List.of(),
                        Map.of()
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

        EqualSplitStrategy strategy =
                new EqualSplitStrategy();

        // Act
        Map<Student, Double> shares =
                strategy.calculateShares(
                        100,
                        participants,
                        Map.of()
                );

        double total = shares.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Assert
        assertEquals(100.0, total, 0.000001);
    }
}