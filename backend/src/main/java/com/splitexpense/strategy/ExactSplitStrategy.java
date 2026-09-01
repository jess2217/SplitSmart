package com.splitexpense.strategy;

import com.splitexpense.exception.InvalidSplitException;
import com.splitexpense.model.Student;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExactSplitStrategy implements SplitStrategy {

    @Override
    public Map<Student, Double> calculateShares(
            double amount,
            List<Student> participants,
            Map<Student, Double> values) {

        if (participants == null || participants.isEmpty()) {
            throw new InvalidSplitException(
                    "At least one participant is required."
            );
        }

        if (values == null ||
        values.size() != participants.size()) {

    throw new InvalidSplitException(
            "Exact amounts are required for every participant."
    );
}

if (!values.keySet().containsAll(participants) ||
        !participants.containsAll(values.keySet())) {

    throw new InvalidSplitException(
            "Exact amounts must be provided only for the selected participants."
    );
}

        double total = 0;

        Map<Student, Double> shares = new LinkedHashMap<>();

        for (Student student : participants) {

            Double value = values.get(student);

            if (value == null || value < 0) {
                throw new InvalidSplitException(
                        "Every exact share must be non-negative."
                );
            }

            total += value;
            shares.put(student, value);
        }

        if (Math.abs(total - amount) > 0.01) {
            throw new InvalidSplitException(
                    "Exact shares must add up to the expense amount."
            );
        }

        return shares;
    }
}