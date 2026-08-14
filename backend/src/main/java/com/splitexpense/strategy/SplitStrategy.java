package com.splitexpense.strategy;

import com.splitexpense.model.Student;
import java.util.List;
import java.util.Map;

public interface SplitStrategy {
    Map<Student, Double> calculateShares(double amount, List<Student> participants,
                                         Map<Student, Double> values);
}
