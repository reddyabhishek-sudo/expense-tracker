package com.expensetracker.dao;

import com.expensetracker.model.User;
import com.expensetracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Register a new user. Returns true if successful.
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, username, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Error code 1062 = duplicate entry (username already taken)
            if (e.getErrorCode() == 1062) {
                System.out.println("That username is already taken. Try another.");
            } else {
                System.out.println("Error registering user: " + e.getMessage());
            }
            return false;
        }
    }

    // Check username and password. Returns the User if valid, or null if not.
    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("password"));
                user.setId(rs.getInt("id"));
                return user;
            }

        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }

        return null; // wrong username or password
    }
}