# Expense Tracker

A desktop expense tracking application built with **Java, JavaFX and MySQL**.
Users can register, log in, record income and expenses, see where their money
goes, and track a monthly budget.

## Screenshots

| Login | Dashboard |
|---|---|
| ![Login](docs/screenshots/login.png) | ![Dashboard](docs/screenshots/dashboard.png) |

| Transactions | Summary |
|---|---|
| ![Transactions](docs/screenshots/transactions.png) | ![Summary](docs/screenshots/summary.png) |

![Budget](docs/screenshots/budget.png)

## Features

- User registration and login, with each user seeing only their own data
- Add, edit and delete transactions (income or expense) with categories and dates
- Dashboard with total income, total expense and balance
- Transactions table with colour-coded income and expense
- Summary screen with a pie chart of expenses by category
- Monthly budget with a progress bar (green, orange near the limit, red when exceeded)
- Dark themed UI styled with a single CSS file

## Tech Stack

- Java 21
- JavaFX 21
- MySQL with JDBC (`mysql-connector-java` 8.0.33)
- Maven

## Project Structure

```
src/main/java/com/expensetracker/
├── App.java              JavaFX entry point
├── Main.java             Original console version
├── model/                User, Category, Transaction, Budget
├── dao/                  UserDAO, CategoryDAO, TransactionDAO, BudgetDAO
├── ui/                   Login, Dashboard, Transactions, Summary, Budget screens
└── util/                 DBConnection

src/main/resources/
├── styles/theme.css      UI theme
└── config.properties     DB credentials (not committed)
```

## How to Run

1. Install Java 21 or newer, Maven and MySQL.
2. Create the `expense_tracker` database with the tables `users`, `categories`,
   `transactions` and `budgets`, and add some categories.
3. Create `src/main/resources/config.properties` with your database details
   (this file is in `.gitignore`).
4. Start the JavaFX app:

```
mvn clean javafx:run
```

To run the original console version, run `Main.java` instead.

## Known Limitations

- Passwords are stored as plain text and should be hashed (for example with BCrypt)
- The Edit dialog changes type, amount and note, but not date or category
- A new database connection is opened for every query (no connection pool) 

Abhishek Btech 2nd year