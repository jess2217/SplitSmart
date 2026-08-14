package com.splitexpense.service;

import com.splitexpense.model.Group;
import com.splitexpense.model.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class SettlementService {

    private final BalanceService balanceService;

    public SettlementService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public List<Settlement> calculateSettlements(Group group) {

        Map<Student, Double> balances =
                balanceService.calculateBalances(group);

        List<BalanceEntry> creditors = new ArrayList<>();
        List<BalanceEntry> debtors = new ArrayList<>();

        for (Map.Entry<Student, Double> entry :
                balances.entrySet()) {

            double balance = entry.getValue();

            if (balance > 0.005) {
                creditors.add(
                        new BalanceEntry(
                                entry.getKey(),
                                balance
                        )
                );
            } else if (balance < -0.005) {
                debtors.add(
                        new BalanceEntry(
                                entry.getKey(),
                                -balance
                        )
                );
            }
        }

        creditors.sort(
                Comparator.comparingDouble(
                        BalanceEntry::amount
                ).reversed()
        );

        debtors.sort(
                Comparator.comparingDouble(
                        BalanceEntry::amount
                ).reversed()
        );

        List<Settlement> settlements =
                new ArrayList<>();

        int creditorIndex = 0;
        int debtorIndex = 0;

        while (
                creditorIndex < creditors.size() &&
                debtorIndex < debtors.size()
        ) {

            BalanceEntry creditor =
                    creditors.get(creditorIndex);

            BalanceEntry debtor =
                    debtors.get(debtorIndex);

            double amount =
                    Math.min(
                            creditor.amount(),
                            debtor.amount()
                    );

            amount =
                    Math.round(amount * 100.0) / 100.0;

            settlements.add(
                    new Settlement(
                            debtor.student(),
                            creditor.student(),
                            amount
                    )
            );

            creditor =
                    new BalanceEntry(
                            creditor.student(),
                            creditor.amount() - amount
                    );

            debtor =
                    new BalanceEntry(
                            debtor.student(),
                            debtor.amount() - amount
                    );

            creditors.set(creditorIndex, creditor);
            debtors.set(debtorIndex, debtor);

            if (creditor.amount() < 0.005) {
                creditorIndex++;
            }

            if (debtor.amount() < 0.005) {
                debtorIndex++;
            }
        }

        return settlements;
    }

    private record BalanceEntry(
            Student student,
            double amount
    ) {
    }

    public record Settlement(
            Student from,
            Student to,
            double amount
    ) {
    }
}