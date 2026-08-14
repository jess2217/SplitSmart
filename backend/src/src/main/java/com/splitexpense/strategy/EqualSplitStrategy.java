package com.splitexpense.strategy;

import com.splitexpense.exception.InvalidSplitException;
import com.splitexpense.model.Student;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EqualSplitStrategy implements SplitStrategy {

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

        Map<Student, Double> shares = new LinkedHashMap<>();

        double each = amount / participants.size();
        double assigned = 0;

        for (int i = 0; i < participants.size(); i++) {

            double share;

            // Give any rounding remainder to the last participant
            if (i == participants.size() - 1) {
                share = amount - assigned;
            } else {
                share = each;
            }

            shares.put(participants.get(i), share);
            assigned += share;
        }

        return shares;
    }
}
