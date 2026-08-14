# SplitSmart — Student Expense & Settlement Manager

A Core Java console application designed around realistic college-student shared expenses.

## Features

- Student management
- Group management
- Equal, exact and percentage expense splitting
- Balance calculation
- Debt simplification
- Who-do-I-owe / who-owes-me
- What-if expense simulation
- Spending analytics
- Recurring expenses
- Custom exceptions
- Clean package structure

## Requirements

- Java 17 or later
- VS Code with Extension Pack for Java (recommended)

## Structure

`src` is the Java source root.

```text
src/com/splitexpense/Main.java
src/com/splitexpense/model/...
```

## Compile

From the project root:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
```

## Run

```powershell
java -cp out com.splitexpense.Main
```

## Important algorithm

`DebtSimplifier` separates creditors and debtors and greedily matches the largest outstanding balances to generate a compact settlement list.

## Future learning stages

- JUnit testing
- SQL and JDBC persistence
- Spring Boot REST API
- JPA/Hibernate

These are intentionally not included in this first Core Java version so the project remains understandable while learning Java fundamentals.
