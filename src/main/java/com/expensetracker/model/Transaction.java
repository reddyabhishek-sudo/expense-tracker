package com.expensetracker.model;

import java.time.LocalDate;

public class Transaction {

    private int id;
    private int userId;
    private int categoryId;
    private double amount;
    private String type;
    private LocalDate date;
    private String note;

    public Transaction(int userId, int categoryId, double amount, String type, LocalDate date, String note) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.note = note;
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

    public int getCategoryId() {
        return categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}