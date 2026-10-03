package com.expensetracker.dao;

import com.expensetracker.model.Transaction;
import com.expensetracker.util.DBConnection;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class TransactionDAO {

    // Add a new transaction to the database
    public boolean addTransaction(Transaction transaction) {
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
            return true;

        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
            return false;
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

    // Total amount per category name for one type (e.g. "expense")
    public Map<String, Double> getTotalsByCategory(int userId, String type) {
        Map<String, Double> totals = new LinkedHashMap<>();
        String sql = "SELECT c.name, SUM(t.amount) AS total "
                + "FROM transactions t JOIN categories c ON t.category_id = c.id "
                + "WHERE t.user_id = ? AND t.type = ? "
                + "GROUP BY c.name ORDER BY total DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                totals.put(rs.getString("name"), rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching category totals: " + e.getMessage());
        }

        return totals;
    }

    // Total expenses for one month, e.g. yearMonth = "2026-10"
    public double getExpenseTotalForMonth(int userId, String yearMonth) {
        double total = 0;
        java.time.YearMonth ym = java.time.YearMonth.parse(yearMonth);
        String sql = "SELECT SUM(amount) AS total FROM transactions "
                + "WHERE user_id = ? AND type = 'expense' AND date >= ? AND date <= ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(ym.atDay(1)));
            stmt.setDate(3, java.sql.Date.valueOf(ym.atEndOfMonth()));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.out.println("Error calculating monthly total: " + e.getMessage());
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

    public boolean deleteTransaction(int transactionId, int userId) {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Transaction deleted successfully.");
                return true;
            } else {
                System.out.println("No transaction found with that ID.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error deleting transaction: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTransaction(int transactionId, int userId, double amount, String type, String note) {
        String sql = "UPDATE transactions SET amount = ?, type = ?, note = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, amount);
            stmt.setString(2, type);
            stmt.setString(3, note);
            stmt.setInt(4, transactionId);
            stmt.setInt(5, userId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Transaction updated successfully.");
                return true;
            } else {
                System.out.println("No transaction found with that ID.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error updating transaction: " + e.getMessage());
            return false;
        }
    }
}