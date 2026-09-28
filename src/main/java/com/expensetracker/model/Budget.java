package com.expensetracker.model;

public class Budget {
    private int id;
    private int userId;
    private String month;
    private double limitAmount;

    public Budget(int userId, String month, double limitAmount) {
        this.userId = userId;
        this.month = month;
        this.limitAmount = limitAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public String getMonth() {
        return month;
    }

    public double getLimitAmount() {
        return limitAmount;
    }
}