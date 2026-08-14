package com.splitexpense.algorithm;

import com.splitexpense.model.Student;
import com.splitexpense.model.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DebtSimplifier {

    private static class BalanceEntry {
        Student student;
        double amount;

        BalanceEntry(Student student, double amount) {
            this.student = student;
            this.amount = amount;
        }
    }

    public List<Transaction> simplify(Map<Student, Double> balances) {
        List<BalanceEntry> creditors = new ArrayList<>();
        List<BalanceEntry> debtors = new ArrayList<>();

        for (Map.Entry<Student, Double> entry : balances.entrySet()) {
            if (entry.getValue() > 0.005) {
                creditors.add(new BalanceEntry(entry.getKey(), entry.getValue()));
            } else if (entry.getValue() < -0.005) {
                debtors.add(new BalanceEntry(entry.getKey(), -entry.getValue()));
            }
        }

        creditors.sort(Comparator.comparingDouble((BalanceEntry e) -> e.amount).reversed());
        debtors.sort(Comparator.comparingDouble((BalanceEntry e) -> e.amount).reversed());

        List<Transaction> result = new ArrayList<>();
        int creditorIndex = 0;
        int debtorIndex = 0;

        while (creditorIndex < creditors.size() && debtorIndex < debtors.size()) {
            BalanceEntry creditor = creditors.get(creditorIndex);
            BalanceEntry debtor = debtors.get(debtorIndex);

            double amount = Math.min(creditor.amount, debtor.amount);

            result.add(new Transaction(debtor.student, creditor.student, amount));

            creditor.amount -= amount;
            debtor.amount -= amount;

            if (Math.abs(creditor.amount) < 0.005) creditorIndex++;
            if (Math.abs(debtor.amount) < 0.005) debtorIndex++;
        }

        return result;
    }
}
