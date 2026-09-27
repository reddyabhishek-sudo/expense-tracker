package com.expensetracker;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CategoryDAO categoryDAO = new CategoryDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        boolean running = true;

        while (running) {
            System.out.println("\n===== Expense Tracker =====");
            System.out.println("1. Add Category");
            System.out.println("2. View All Categories");
            System.out.println("3. Add Transaction");
            System.out.println("4. View All Transactions (user 1)");
            System.out.println("5. View Summary (Income/Expense/Balance)");
            System.out.println("6. Exit");
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

                    Transaction t = new Transaction(1, categoryId, amount, type, LocalDate.now(), note);
                    transactionDAO.addTransaction(t);
                    break;

                case 4:
                    List<Transaction> transactions = transactionDAO.getTransactionsByUser(1);
                    System.out.println("All transactions:");
                    for (Transaction tx : transactions) {
                        System.out.println("- " + tx.getAmount() + " | " + tx.getType() + " | " + tx.getDate() + " | "
                                + tx.getNote());
                    }
                    break;

                case 5:
                    double totalIncome = transactionDAO.getTotalByType(1, "income");
                    double totalExpense = transactionDAO.getTotalByType(1, "expense");
                    double balance = totalIncome - totalExpense;

                    System.out.println("\n--- Summary ---");
                    System.out.println("Total Income: " + totalIncome);
                    System.out.println("Total Expense: " + totalExpense);
                    System.out.println("Balance: " + balance);
                    break;

                case 6:
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