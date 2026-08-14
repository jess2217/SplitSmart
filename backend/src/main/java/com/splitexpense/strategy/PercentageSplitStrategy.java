package com.splitexpense.strategy;

import com.splitexpense.exception.InvalidSplitException;
import com.splitexpense.model.Student;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PercentageSplitStrategy implements SplitStrategy {
    @Override
    public Map<Student, Double> calculateShares(double amount, List<Student> participants,
                                                Map<Student, Double> values) {
        if (participants == null || participants.isEmpty()) {
            throw new InvalidSplitException("At least one participant is required.");
        }
        if (values == null || values.size() != participants.size()) {
            throw new InvalidSplitException("Percentages are required for every participant.");
        }

        double totalPercentage = 0;
        for (Student student : participants) {
            Double percentage = values.get(student);
            if (percentage == null || percentage < 0) {
                throw new InvalidSplitException("Percentages must be non-negative.");
            }
            totalPercentage += percentage;
        }

        if (Math.abs(totalPercentage - 100.0) > 0.01) {
            throw new InvalidSplitException("Percentages must total 100%.");
        }

        Map<Student, Double> shares = new LinkedHashMap<>();
        double assigned = 0;
        for (int i = 0; i < participants.size(); i++) {
            Student student = participants.get(i);
            double percentage = values.get(student);
            double share = (i == participants.size() - 1)
                    ? amount - assigned : amount * percentage / 100.0;
            shares.put(student, share);
            assigned += share;
        }
        return shares;
    }
}
