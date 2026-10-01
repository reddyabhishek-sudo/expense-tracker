package com.expensetracker.dao;

import com.expensetracker.model.Budget;
import com.expensetracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BudgetDAO {

    // Set a budget for a month: updates it if one exists, otherwise inserts a new
    // one
    public void setBudget(int userId, String month, double limitAmount) {
        Budget existing = getBudget(userId, month);

        String sql;
        if (existing == null) {
            sql = "INSERT INTO budgets (limit_amount, user_id, month) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE budgets SET limit_amount = ? WHERE user_id = ? AND month = ?";
        }

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, limitAmount);
            stmt.setInt(2, userId);
            stmt.setString(3, month);
            stmt.executeUpdate();

            if (existing == null) {
                System.out.println("Budget set successfully for " + month);
            } else {
                System.out.println("Budget updated for " + month);
            }

        } catch (SQLException e) {
            System.out.println("Error setting budget: " + e.getMessage());
        }
    }

    // Get the budget for a given month
    public Budget getBudget(int userId, String month) {
        String sql = "SELECT * FROM budgets WHERE user_id = ? AND month = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, month);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Budget budget = new Budget(
                        rs.getInt("user_id"),
                        rs.getString("month"),
                        rs.getDouble("limit_amount"));
                budget.setId(rs.getInt("id"));
                return budget;
            }

        } catch (SQLException e) {
            System.out.println("Error fetching budget: " + e.getMessage());
        }

        return null; // no budget found for this month
    }
}