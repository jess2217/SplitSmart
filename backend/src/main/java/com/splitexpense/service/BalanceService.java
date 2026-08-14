package com.splitexpense.service;

import com.splitexpense.model.Expense;
import com.splitexpense.model.Group;
import com.splitexpense.model.Student;
import com.splitexpense.repository.GroupRepository;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class BalanceService {

    private final GroupRepository groupRepository;

    /*
     * Constructor used by Spring Boot
     */
    public BalanceService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /*
     * Constructor used by existing unit tests
     */
    public BalanceService() {
        this.groupRepository = null;
    }

    public Map<Student, Double> calculateBalances(Group group) {

        Map<Student, Double> balances =
                new LinkedHashMap<>();

        for (Student student : group.getMembers()) {
            balances.put(student, 0.0);
        }

        for (Expense expense : group.getExpenses()) {

            // Money paid by the payer
            balances.merge(
                    expense.getPayer(),
                    expense.getAmount(),
                    Double::sum
            );

            // Money owed by each participant
            for (Map.Entry<Student, Double> entry :
                    expense.getShares().entrySet()) {

                balances.merge(
                        entry.getKey(),
                        -entry.getValue(),
                        Double::sum
                );
            }
        }

        // Remove floating-point calculation errors
        balances.replaceAll(
                (student, value) -> {

                    if (Math.abs(value) < 0.005) {
                        return 0.0;
                    }

                    return Math.round(value * 100.0) / 100.0;
                }
        );

        return balances;
    }
}