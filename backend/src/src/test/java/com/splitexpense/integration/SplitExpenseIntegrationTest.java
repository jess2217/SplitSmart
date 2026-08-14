package com.splitexpense.integration;

import com.splitexpense.algorithm.DebtSimplifier;
import com.splitexpense.model.Expense;
import com.splitexpense.model.ExpenseCategory;
import com.splitexpense.model.Group;
import com.splitexpense.model.SplitType;
import com.splitexpense.model.Student;
import com.splitexpense.model.Transaction;
import com.splitexpense.service.BalanceService;
import com.splitexpense.service.ExpenseService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SplitExpenseIntegrationTest {

    @Test
    void shouldCompleteFullExpenseSettlementFlow() {

        // ==========================================
        // 1. CREATE STUDENTS
        // ==========================================

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

        // ==========================================
        // 2. CREATE GROUP
        // ==========================================

        Group group = new Group(
                1,
                "College Trip"
        );

        group.addMember(ak);
        group.addMember(rahul);
        group.addMember(priya);

        // ==========================================
        // 3. ADD EXPENSE
        // ==========================================

        ExpenseService expenseService =
                new ExpenseService();

        Expense expense = expenseService.addExpense(
                group,
                "Dinner",
                900.0,
                ak,
                List.of(ak, rahul, priya),
                ExpenseCategory.FOOD,
                SplitType.EQUAL,
                Map.of()
        );

        // ==========================================
        // 4. VERIFY EXPENSE
        // ==========================================

        assertNotNull(expense);

        assertEquals(
                900.0,
                expense.getAmount(),
                0.000001
        );

        assertEquals(
                ak,
                expense.getPayer()
        );

        assertEquals(
                300.0,
                expense.getShares().get(ak),
                0.000001
        );

        assertEquals(
                300.0,
                expense.getShares().get(rahul),
                0.000001
        );

        assertEquals(
                300.0,
                expense.getShares().get(priya),
                0.000001
        );

        // ==========================================
        // 5. CALCULATE BALANCES
        // ==========================================

        BalanceService balanceService =
                new BalanceService();

        Map<Student, Double> balances =
                balanceService.calculateBalances(group);

        // AK paid ₹900 but owes ₹300.
        // Therefore AK should receive ₹600.

        assertEquals(
                600.0,
                balances.get(ak),
                0.000001
        );

        // Rahul owes ₹300.
        assertEquals(
                -300.0,
                balances.get(rahul),
                0.000001
        );

        // Priya owes ₹300.
        assertEquals(
                -300.0,
                balances.get(priya),
                0.000001
        );

        // ==========================================
        // 6. SIMPLIFY DEBTS
        // ==========================================

        DebtSimplifier debtSimplifier =
                new DebtSimplifier();

        List<Transaction> transactions =
                debtSimplifier.simplify(balances);

        // ==========================================
        // 7. VERIFY TRANSACTIONS
        // ==========================================

        assertEquals(
                2,
                transactions.size()
        );

        Transaction first =
                transactions.get(0);

        Transaction second =
                transactions.get(1);

        // Both Rahul and Priya should pay AK ₹300.

        assertEquals(
                300.0,
                first.getAmount(),
                0.000001
        );

        assertEquals(
                300.0,
                second.getAmount(),
                0.000001
        );

        assertEquals(
                ak,
                first.getTo()
        );

        assertEquals(
                ak,
                second.getTo()
        );

        // ==========================================
        // 8. VERIFY DEBTORS
        // ==========================================

        boolean rahulPresent =
                (first.getFrom() == rahul)
                        || (second.getFrom() == rahul);

        boolean priyaPresent =
                (first.getFrom() == priya)
                        || (second.getFrom() == priya);

        assertEquals(
                true,
                rahulPresent
        );

        assertEquals(
                true,
                priyaPresent
        );
    }

    @Test
    void shouldSettleMultipleExpensesCorrectly() {

        // ==========================================
        // CREATE STUDENTS
        // ==========================================

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

        // ==========================================
        // CREATE GROUP
        // ==========================================

        Group group = new Group(
                1,
                "Trip"
        );

        group.addMember(ak);
        group.addMember(rahul);
        group.addMember(priya);

        ExpenseService expenseService =
                new ExpenseService();

        // ==========================================
        // EXPENSE 1
        // AK PAYS ₹900
        // ==========================================

        expenseService.addExpense(
                group,
                "Dinner",
                900.0,
                ak,
                List.of(ak, rahul, priya),
                ExpenseCategory.FOOD,
                SplitType.EQUAL,
                Map.of()
        );

        // ==========================================
        // EXPENSE 2
        // RAHUL PAYS ₹600
        // ==========================================

        expenseService.addExpense(
                group,
                "Cab",
                600.0,
                rahul,
                List.of(ak, rahul, priya),
                ExpenseCategory.CAB,
                SplitType.EQUAL,
                Map.of()
        );

        // ==========================================
        // CALCULATE BALANCES
        // ==========================================

        BalanceService balanceService =
                new BalanceService();

        Map<Student, Double> balances =
                balanceService.calculateBalances(group);

        /*
         * Dinner:
         *
         * AK pays 900
         * Each owes 300
         *
         * AK     +600
         * Rahul  -300
         * Priya  -300
         *
         * Cab:
         *
         * Rahul pays 600
         * Each owes 200
         *
         * AK     -200
         * Rahul  +400
         * Priya  -200
         *
         * FINAL:
         *
         * AK     +400
         * Rahul  +100
         * Priya  -500
         */

        assertEquals(
                400.0,
                balances.get(ak),
                0.000001
        );

        assertEquals(
                100.0,
                balances.get(rahul),
                0.000001
        );

        assertEquals(
                -500.0,
                balances.get(priya),
                0.000001
        );

        // ==========================================
        // SIMPLIFY DEBTS
        // ==========================================

        DebtSimplifier simplifier =
                new DebtSimplifier();

        List<Transaction> transactions =
                simplifier.simplify(balances);

        // Priya pays AK ₹400
        // Priya pays Rahul ₹100

        assertEquals(
                2,
                transactions.size()
        );

        double total =
                transactions.stream()
                        .mapToDouble(Transaction::getAmount)
                        .sum();

        assertEquals(
                500.0,
                total,
                0.000001
        );
    }
}