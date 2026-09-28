# Expense Tracker

A console-based Expense Tracker application built in Java, using JDBC to connect to a MySQL database. Built as a BTech 2nd year end-semester project.

## Features

- User registration and login (each user sees only their own data)
- Add and view expense categories (Food, Travel, Rent, Shopping, Entertainment, Bills, Health, Education, Subscriptions)
- Add transactions (income/expense) linked to a category, with input validation
- View all transactions, including their unique IDs
- View a financial summary: total income, total expense and balance
- Filter transactions by category
- Update and delete transactions (restricted to the logged-in user's own data)
- Set a monthly budget and check spending against it
- Data stored in MySQL with foreign key relationships

## Tech Stack

- **Language:** Java
- **Database:** MySQL
- **Build Tool:** Maven
- **Key Concepts:** OOP (classes, encapsulation), JDBC, DAO design pattern, PreparedStatement (prevents SQL injection), SQL aggregate functions (SUM), exception handling, input validation, CRUD operations

## Project Structure

```
src/main/java/com/expensetracker/
├── model/     # User, Category, Transaction, Budget
├── dao/       # UserDAO, CategoryDAO, TransactionDAO, BudgetDAO
├── util/      # DBConnection - handles the MySQL connection
└── Main.java  # Login screen, console menu, program entry point

src/main/resources/
└── config.properties   # Database credentials (not committed to Git)
```

## Database Schema

Four tables: `users`, `categories`, `transactions`, `budgets`. Transactions link to users and categories through foreign keys, and budgets link to users.

## How to Run

1. Clone this repository
2. Create a MySQL database named `expense_tracker` and its four tables
3. Create `src/main/resources/config.properties` with your own credentials:
```
   db.url=jdbc:mysql://localhost:3306/expense_tracker
   db.username=your_username
   db.password=your_password
```
4. Run `mvn clean install`
5. Run `Main.java`

## Sample Menu

```
===== Expense Tracker =====
1. Add Category
2. View All Categories
3. Add Transaction
4. View All Transactions
5. View Summary (Income/Expense/Balance)
6. View Transactions by Category
7. Delete Transaction
8. Update Transaction
9. Set Monthly Budget
10. Check Budget Status
11. Exit
```

## Known Limitations / Future Improvements

- Passwords are stored as plain text; a production app would hash them (e.g. BCrypt)
- Budget checks compare against total expenses, not month-specific expenses
- Setting a budget twice for the same month creates duplicate rows

## Author

Abhishek, BTech 2nd Year