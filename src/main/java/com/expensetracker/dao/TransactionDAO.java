package com.expensetracker.dao;

import com.expensetracker.model.Transaction;
import com.expensetracker.util.DBConnection;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Add a new transaction to the database
    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, category_id, amount, type, date, note) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transaction.getUserId());
            stmt.setInt(2, transaction.getCategoryId());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getType());
            stmt.setDate(5, java.sql.Date.valueOf(transaction.getDate()));
            stmt.setString(6, transaction.getNote());

            stmt.executeUpdate();
            System.out.println("Transaction added successfully.");

        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
        }
    }

    // Fetch all transactions for a specific user
    public List<Transaction> getTransactionsByUser(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("user_id"),
                        rs.getInt("category_id"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("note"));
                t.setId(rs.getInt("id"));
                transactions.add(t);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }

        return transactions;
    }

    public double getTotalByType(int userId, String type) {
        double total = 0;
        String sql = "SELECT SUM(amount) AS total FROM transactions WHERE user_id = ? AND type = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total: " + e.getMessage());
        }

        return total;
    }

    public List<Transaction> getTransactionsByCategory(int userId, int categoryId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? AND category_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("user_id"),
                        rs.getInt("category_id"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("note"));
                t.setId(rs.getInt("id"));
                transactions.add(t);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching transactions by category: " + e.getMessage());
        }

        return transactions;
    }
}