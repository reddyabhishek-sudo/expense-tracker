package com.expensetracker.ui;

import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Budget;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.YearMonth;

public class BudgetView {

    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final VBox root = new VBox(20);

    private final ComboBox<String> monthBox = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final Label messageLabel = new Label();

    private final Label spentLabel = new Label();
    private final Label percentLabel = new Label();
    private final Label warningLabel = new Label();
    private final ProgressBar bar = new ProgressBar(0);

    public BudgetView() {
        Label heading = new Label("Budget");
        heading.getStyleClass().add("page-title");
        Label sub = new Label("Set a monthly limit and track your spending");
        sub.getStyleClass().add("page-subtitle");

        // ----- Set budget card -----
        YearMonth now = YearMonth.now();
        for (int i = -6; i <= 6; i++) {
            monthBox.getItems().add(now.plusMonths(i).toString());
        }
        monthBox.setValue(now.toString());
        monthBox.getStyleClass().add("input");
        monthBox.setMaxWidth(Double.MAX_VALUE);
        monthBox.setOnAction(e -> refreshStatus());

        amountField.setPromptText("Budget limit (e.g. 5000)");
        amountField.getStyleClass().add("input");

        Button save = new Button("Save Budget");
        save.getStyleClass().add("primary-button");
        save.setMaxWidth(Double.MAX_VALUE);
        save.setOnAction(e -> handleSave());

        messageLabel.setWrapText(true);

        VBox setCard = new VBox(14, label("Month"), monthBox,
                label("Monthly limit"), amountField, save, messageLabel);
        setCard.getStyleClass().add("form-card");
        setCard.setPadding(new Insets(28));
        setCard.setMinWidth(340);
        setCard.setPrefWidth(340);
        setCard.setMaxHeight(Region.USE_PREF_SIZE);

        // ----- Status card -----
        Label statusTitle = label("This month's progress");
        spentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");
        spentLabel.setWrapText(true);
        spentLabel.setMinHeight(Region.USE_PREF_SIZE);
        percentLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        warningLabel.setWrapText(true);
        warningLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 13px;");

        bar.getStyleClass().add("budget-bar");
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(14);

        VBox statusCard = new VBox(14, statusTitle, spentLabel, bar, percentLabel, warningLabel);
        statusCard.getStyleClass().add("form-card");
        statusCard.setPadding(new Insets(28));
        statusCard.setMaxHeight(Region.USE_PREF_SIZE);
        HBox.setHgrow(statusCard, Priority.ALWAYS);

        HBox row = new HBox(24, setCard, statusCard);

        root.getChildren().addAll(heading, sub, row);
        root.getStyleClass().add("page");
        refreshStatus();
    }

    public Node getRoot() {
        return root;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("stat-title");
        return l;
    }

    private void showMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().removeAll("error-text", "success-text");
        messageLabel.getStyleClass().add(success ? "success-text" : "error-text");
    }

    private void refreshStatus() {
        String month = monthBox.getValue();
        int uid = Session.getUser().getId();
        Budget budget = budgetDAO.getBudget(uid, month);

        warningLabel.setText("");

        if (budget == null) {
            amountField.clear();
            spentLabel.setText("No budget set for " + month);
            percentLabel.setText("Set a limit on the left to start tracking.");
            bar.setProgress(0);
            return;
        }

        double limit = budget.getLimitAmount();
        double spent = transactionDAO.getExpenseTotalForMonth(uid, month);
        double ratio = limit > 0 ? spent / limit : 0;

        amountField.setText(String.valueOf(limit));
        spentLabel.setText(String.format("₹ %,.2f of ₹ %,.2f spent", spent, limit));
        percentLabel.setText(String.format("%.0f%% of your budget used", ratio * 100));
        bar.setProgress(Math.min(ratio, 1.0));

        String color;
        if (ratio > 1.0) {
            color = "#ef4444";
            warningLabel.setText(String.format("You are over budget by ₹ %,.2f!", spent - limit));
        } else if (ratio >= 0.8) {
            color = "#f59e0b";
            warningLabel.setText("Careful: you have used more than 80% of your budget.");
            warningLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 13px;");
        } else {
            color = "#22c55e";
        }
        if (ratio > 1.0) {
            warningLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 13px;");
        }
        bar.setStyle("-fx-accent: " + color + ";");
    }

    private void handleSave() {
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException ex) {
            showMessage("Limit must be a number.", false);
            return;
        }
        if (amount <= 0) {
            showMessage("Limit must be greater than 0.", false);
            return;
        }

        int uid = Session.getUser().getId();
        String month = monthBox.getValue();
        budgetDAO.setBudget(uid, month, amount);

        // setBudget returns nothing, so confirm by reading it back
        Budget saved = budgetDAO.getBudget(uid, month);
        if (saved != null && saved.getLimitAmount() == amount) {
            showMessage("Budget saved for " + month + ".", true);
            refreshStatus();
        } else {
            showMessage("Could not save. Check the terminal for the error.", false);
        }
    }
}