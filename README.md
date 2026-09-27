# Expense Tracker

A console-based Expense Tracker application built in Java, using JDBC to connect to a MySQL database. Built as a BTech 2nd year end-semester project.

## Features

- Add and view expense categories (Food, Travel, Rent, etc.)
- Add transactions (income/expense) linked to a category
- View all recorded transactions
- Input validation for transaction type and amount
- Data persisted in a MySQL database with proper foreign key relationships

## Tech Stack

- **Language:** Java
- **Database:** MySQL
- **Build Tool:** Maven
- **Key Concepts Used:** OOP (classes, encapsulation), JDBC, DAO design pattern, PreparedStatement, exception handling, Scanner-based console menu

## Project Structure


## Database Schema

The application uses 4 tables: `users`, `categories`, `transactions`, `budgets`, with foreign key relationships linking transactions to users and categories.

## How to Run

1. Clone this repository
2. Set up a MySQL database named `expense_tracker` using the schema (see below)
3. Create a `src/main/resources/config.properties` file with your own database credentials:
4. Run `mvn clean install` to build the project
5. Run `Main.java` to start the console application

## Sample Menu


## Author

Abhishek — BTech 2nd Year