package com.expensetracker.ui;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class AddTransactionView {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final VBox root = new VBox(20);

    private final ComboBox<String> typeBox = new ComboBox<>();
    private final ComboBox<Category> categoryBox = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final TextField noteField = new TextField();
    private final Label messageLabel = new Label();

    public AddTransactionView() {
        Label heading = new Label("Add Transaction");
        heading.getStyleClass().add("page-title");
        Label sub = new Label("Record money coming in or going out");
        sub.getStyleClass().add("page-subtitle");

        typeBox.getItems().addAll("income", "expense");
        typeBox.setPromptText("Type");
        categoryBox.getItems().addAll(new CategoryDAO().getAllCategories());
        categoryBox.setPromptText("Category");
        amountField.setPromptText("Amount (e.g. 250.50)");
        noteField.setPromptText("Note (optional)");

        for (Control c : new Control[] { typeBox, categoryBox, amountField, datePicker, noteField }) {
            c.getStyleClass().add("input");
            c.setMaxWidth(Double.MAX_VALUE);
        }

        Button save = new Button("Save Transaction");
        save.getStyleClass().add("primary-button");
        save.setMaxWidth(Double.MAX_VALUE);
        save.setOnAction(e -> handleSave());

        messageLabel.setWrapText(true);

        VBox form = new VBox(14, label("Type"), typeBox, label("Category"), categoryBox,
                label("Amount"), amountField, label("Date"), datePicker,
                label("Note"), noteField, save, messageLabel);
        form.getStyleClass().add("form-card");
        form.setPadding(new javafx.geometry.Insets(28));
        form.setMaxWidth(480);

        root.getChildren().addAll(heading, sub, form);
        root.getStyleClass().add("page");
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

    private void handleSave() {
        String type = typeBox.getValue();
        Category category = categoryBox.getValue();
        LocalDate date = datePicker.getValue();

        if (type == null || category == null || date == null) {
            showMessage("Please choose a type, category and date.", false);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException ex) {
            showMessage("Amount must be a number.", false);
            return;
        }
        if (amount <= 0) {
            showMessage("Amount must be greater than 0.", false);
            return;
        }

        Transaction t = new Transaction(Session.getUser().getId(), category.getId(),
                amount, type, date, noteField.getText().trim());

        if (transactionDAO.addTransaction(t)) {
            amountField.clear();
            noteField.clear();
            typeBox.setValue(null);
            categoryBox.setValue(null);
            datePicker.setValue(LocalDate.now());
            showMessage("Transaction saved!", true);
        } else {
            showMessage("Could not save. Check the terminal for the error.", false);
        }
    }
}