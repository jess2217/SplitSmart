[SplitSmart]

SplitSmart is a group expense management application that helps users
create groups, add members, record shared expenses, calculate individual
balances, and generate simplified settlements.

The application provides a simple way to track who paid, who owes money,
and who needs to pay whom.

---

[ Features ]

1. User Authentication

- User registration
- User login
- Password change
- User-specific dashboard
- User-to-Student relationship

2. Group Management

- Create groups
- View groups belonging to the logged-in user
- View individual group details
- Delete groups
- Group ownership and access control

3. Member Management

- Add members to groups using name and email
- Reuse existing Student records based on email
- Prevent duplicate Student records
- Remove group members
- Only the group owner can remove members

4. Expense Management

- Add expenses to groups
- Delete expenses
- Select the person who paid
- Select expense participants
- Expense categories
- Custom category using `OTHER`

5. Split Strategies

SplitSmart supports three ways of dividing an expense:

i. Equal Split

The expense is divided equally among all participants.

Example:

text
Total: ₹600
Participants: 3

Each person: ₹200

ii. Exact Split

Each member can be assigned a specific amount.

Expense: ₹1,000

User1      ₹200
User2     ₹300
User3   ₹500

The amounts must add up to the total expense.

iii. Percentage Split

Each member can be assigned a percentage.

Expense: ₹1,000

User1      20%
User2      30%
User3      50%

The percentages must add up to 100%.

6. Balance Calculation

For each member, SplitSmart calculates:

Balance = Amount Paid - Amount Owed

A positive balance means the member should receive money.

A negative balance means the member needs to pay money.

For example:

User1      = +₹300
User2     =   ₹0
User3    =  -₹300

The settlement would be:

User3 → User1   = ₹300

Technology Stack :-
i. Frontend -
React,
Vite,
JavaScript,
CSS
ii. Backend -
Java 25,
Spring Boot 4,
Spring Data JPA,
Hibernate,
Maven
iii. Database -
PostgreSQL,
Neon
iv.Deployment -
Vercel — Frontend
Render — Backend
Neon — Database

The Live Application:-
https://split-smart-lake.vercel.app/
