package com.expensetracker;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.model.Budget;

import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CategoryDAO categoryDAO = new CategoryDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        BudgetDAO budgetDAO = new BudgetDAO();
        UserDAO userDAO = new UserDAO();
        boolean running = true;

        // ---- Login / Register screen ----
        User currentUser = null;
        while (currentUser == null) {
            System.out.println("\n===== Welcome to Expense Tracker =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Enter your choice: ");

            int authChoice = scanner.nextInt();
            scanner.nextLine();

            if (authChoice == 1) {
                System.out.print("Username: ");
                String loginUsername = scanner.nextLine();
                System.out.print("Password: ");
                String loginPassword = scanner.nextLine();

                currentUser = userDAO.loginUser(loginUsername, loginPassword);
                if (currentUser == null) {
                    System.out.println("Invalid username or password. Try again.");
                } else {
                    System.out.println("Welcome back, " + currentUser.getName() + "!");
                }

            } else if (authChoice == 2) {
                System.out.print("Your name: ");
                String regName = scanner.nextLine();
                System.out.print("Choose a username: ");
                String regUsername = scanner.nextLine();
                System.out.print("Choose a password: ");
                String regPassword = scanner.nextLine();

                User newUser = new User(regName, regUsername, regPassword);
                if (userDAO.registerUser(newUser)) {
                    System.out.println("Registration successful! Please login.");
                }

            } else {
                System.out.println("Invalid choice, try again.");
            }
        }
        int userId = currentUser.getId();

        while (running) {
            System.out.println("\n===== Expense Tracker =====");
            System.out.println("1. Add Category");
            System.out.println("2. View All Categories");
            System.out.println("3. Add Transaction");
            System.out.println("4. View All Transactions");
            System.out.println("5. View Summary (Income/Expense/Balance)");
            System.out.println("6. View Transactions by Category");
            System.out.println("7. Delete Transaction");
            System.out.println("8. Update Transaction");
            System.out.println("9. Set Monthly Budget");
            System.out.println("10. Check Budget Status");
            System.out.println("11. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // clears leftover newline

            switch (choice) {
                case 1:
                    System.out.print("Enter category name: ");
                    String catName = scanner.nextLine();
                    categoryDAO.addCategory(catName);
                    break;

                case 2:
                    List<Category> categories = categoryDAO.getAllCategories();
                    System.out.println("All categories:");
                    for (Category c : categories) {
                        System.out.println("- " + c.getName());
                    }
                    break;

                case 3:
                    System.out.print("Enter category ID: ");
                    int categoryId = scanner.nextInt();
                    scanner.nextLine();

                    double amount;
                    while (true) {
                        System.out.print("Enter amount: ");
                        amount = scanner.nextDouble();
                        scanner.nextLine();
                        if (amount > 0) {
                            break;
                        }
                        System.out.println("Amount must be greater than 0. Try again.");
                    }

                    String type;
                    while (true) {
                        System.out.print("Enter type (income/expense): ");
                        type = scanner.nextLine().trim().toLowerCase();
                        if (type.equals("income") || type.equals("expense")) {
                            break;
                        }
                        System.out.println("Invalid type. Please type exactly 'income' or 'expense'.");
                    }

                    System.out.print("Enter note: ");
                    String note = scanner.nextLine();

                    Transaction t = new Transaction(userId, categoryId, amount, type, LocalDate.now(), note);
                    transactionDAO.addTransaction(t);

                    break;

                case 4:
                    List<Transaction> transactions = transactionDAO.getTransactionsByUser(userId);
                    System.out.println("All transactions:");
                    for (Transaction tx : transactions) {
                        System.out.println("ID: " + tx.getId() + " | " + tx.getAmount() + " | " + tx.getType() + " | "
                                + tx.getDate() + " | " + tx.getNote());
                    }
                    break;

                case 5:
                    double totalIncome = transactionDAO.getTotalByType(userId, "income");
                    double totalExpense = transactionDAO.getTotalByType(userId, "expense");
                    double balance = totalIncome - totalExpense;

                    System.out.println("\n--- Summary ---");
                    System.out.println("Total Income: " + totalIncome);
                    System.out.println("Total Expense: " + totalExpense);
                    System.out.println("Balance: " + balance);
                    break;

                case 6:
                    System.out.print("Enter category ID: ");
                    int filterCategoryId = scanner.nextInt();
                    scanner.nextLine();

                    List<Transaction> filtered = transactionDAO.getTransactionsByCategory(userId, filterCategoryId);
                    System.out.println("Transactions for category ID " + filterCategoryId + ":");
                    for (Transaction tx : filtered) {
                        System.out.println("- " + tx.getAmount() + " | " + tx.getType() + " | " + tx.getDate() + " | "
                                + tx.getNote());
                    }
                    break;

                case 7:
                    System.out.print("Enter transaction ID to delete: ");
                    int deleteId = scanner.nextInt();
                    scanner.nextLine();
                    transactionDAO.deleteTransaction(deleteId, userId);
                    break;

                case 8:
                    System.out.print("Enter transaction ID to update: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine();

                    double newAmount;
                    while (true) {
                        System.out.print("Enter new amount: ");
                        newAmount = scanner.nextDouble();
                        scanner.nextLine();
                        if (newAmount > 0) {
                            break;
                        }
                        System.out.println("Amount must be greater than 0. Try again.");
                    }

                    String newType;
                    while (true) {
                        System.out.print("Enter new type (income/expense): ");
                        newType = scanner.nextLine().trim().toLowerCase();
                        if (newType.equals("income") || newType.equals("expense")) {
                            break;
                        }
                        System.out.println("Invalid type. Please type exactly 'income' or 'expense'.");
                    }

                    System.out.print("Enter new note: ");
                    String newNote = scanner.nextLine();

                    transactionDAO.updateTransaction(updateId, userId, newAmount, newType, newNote);
                    break;

                case 9:
                    System.out.print("Enter month (e.g., September): ");
                    String budgetMonth = scanner.nextLine();

                    double limitAmount;
                    while (true) {
                        System.out.print("Enter budget limit: ");
                        limitAmount = scanner.nextDouble();
                        scanner.nextLine();
                        if (limitAmount > 0) {
                            break;
                        }
                        System.out.println("Budget limit must be greater than 0. Try again.");
                    }

                    budgetDAO.setBudget(userId, budgetMonth, limitAmount);
                    break;

                case 10:
                    System.out.print("Enter month to check (e.g., September): ");
                    String checkMonth = scanner.nextLine();

                    Budget budget = budgetDAO.getBudget(userId, checkMonth);
                    if (budget == null) {
                        System.out.println("No budget set for " + checkMonth + ".");
                    } else {
                        double totalExpenseForBudget = transactionDAO.getTotalByType(userId, "expense");
                        System.out.println("Budget for " + checkMonth + ": " + budget.getLimitAmount());
                        System.out.println("Total Expense: " + totalExpenseForBudget);

                        if (totalExpenseForBudget > budget.getLimitAmount()) {
                            System.out.println("Warning: You have exceeded your budget by "
                                    + (totalExpenseForBudget - budget.getLimitAmount()));
                        } else {
                            System.out.println(
                                    "You are within budget. Remaining: "
                                            + (budget.getLimitAmount() - totalExpenseForBudget));
                        }
                    }
                    break;

                case 11:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }

        scanner.close();
    }
}