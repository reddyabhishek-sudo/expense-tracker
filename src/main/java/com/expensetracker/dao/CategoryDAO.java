package com.expensetracker.dao;

import com.expensetracker.model.Category;
import com.expensetracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    // Add a new category to the database
    public void addCategory(String name) {
        String sql = "INSERT INTO categories (name) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("Category added: " + name);

        } catch (SQLException e) {
            System.out.println("Error adding category: " + e.getMessage());
        }
    }

    // Fetch all categories from the database
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(rs.getString("name"));
                category.setId(rs.getInt("id"));
                categories.add(category);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching categories: " + e.getMessage());
        }

        return categories;
    }
}