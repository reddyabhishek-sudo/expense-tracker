package com.expensetracker.ui;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionsView {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final VBox root = new VBox(20);
    private final TableView<Transaction> table = new TableView<>();
    private final Map<Integer, String> categoryNames = new HashMap<>();
    private final Label messageLabel = new Label();

    public TransactionsView() {
        for (Category c : new CategoryDAO().getAllCategories()) {
            categoryNames.put(c.getId(), c.getName());
        }

        Label heading = new Label("Transactions");
        heading.getStyleClass().add("page-title");
        Label sub = new Label("All your income and expenses");
        sub.getStyleClass().add("page-subtitle");

        buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        messageLabel.setWrapText(true);

        root.getChildren().addAll(heading, sub, table, messageLabel);
        root.getStyleClass().add("page");
        loadData();
    }

    public Node getRoot() {
        return root;
    }

    private void showMessage(String text, boolean success) {
        messageLabel.getStyleClass().removeAll("error-text", "success-text");
        messageLabel.getStyleClass().add(success ? "success-text" : "error-text");
        messageLabel.setText(text);
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        table.getStyleClass().add("dark-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No transactions yet. Add one to get started."));

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().toString()));

        TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(c -> new SimpleStringProperty(
                categoryNames.getOrDefault(c.getValue().getCategoryId(), "Unknown")));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));

        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol
                .setCellValueFactory(c -> new SimpleStringProperty(String.format("₹ %,.2f", c.getValue().getAmount())));
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                Transaction t = getTableRow().getItem();
                boolean income = t != null && "income".equalsIgnoreCase(t.getType());
                setStyle("-fx-text-fill: " + (income ? "#4ade80" : "#f87171")
                        + "; -fx-font-weight: bold;");
            }
        });

        TableColumn<Transaction, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getNote() == null ? "" : c.getValue().getNote()));

        TableColumn<Transaction, Void> actionCol = new TableColumn<>("");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button edit = new Button("Edit");
            private final Button delete = new Button("Delete");
            private final HBox box = new HBox(8, edit, delete);
            {
                edit.getStyleClass().add("edit-button");
                delete.getStyleClass().add("delete-button");
                box.setAlignment(Pos.CENTER_LEFT);
                edit.setOnAction(e -> showEditDialog(getTableRow().getItem()));
                delete.setOnAction(e -> confirmDelete(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(dateCol, catCol, typeCol, amountCol, noteCol, actionCol);
    }

    private void loadData() {
        List<Transaction> list = transactionDAO.getTransactionsByUser(Session.getUser().getId());
        list.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        table.setItems(FXCollections.observableArrayList(list));
    }

    private void confirmDelete(Transaction t) {
        if (t == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this ₹ " + String.format("%,.2f", t.getAmount()) + " transaction?",
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm delete");
        alert.showAndWait().ifPresent(answer -> {
            if (answer == ButtonType.YES) {
                if (transactionDAO.deleteTransaction(t.getId(), Session.getUser().getId())) {
                    showMessage("Transaction deleted.", true);
                    loadData();
                } else {
                    showMessage("Could not delete. Check the terminal.", false);
                }
            }
        });
    }

    private void showEditDialog(Transaction t) {
        if (t == null)
            return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaction");
        dialog.setHeaderText("Update this transaction");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("income", "expense");
        typeBox.setValue(t.getType());
        typeBox.setMaxWidth(Double.MAX_VALUE);

        TextField amountField = new TextField(String.valueOf(t.getAmount()));
        TextField noteField = new TextField(t.getNote() == null ? "" : t.getNote());
        Label error = new Label();
        error.setStyle("-fx-text-fill: #f87171;");

        VBox box = new VBox(10, new Label("Type"), typeBox,
                new Label("Amount"), amountField,
                new Label("Note"), noteField, error);
        box.setPrefWidth(320);
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button ok = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        ok.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                if (amt <= 0) {
                    error.setText("Amount must be greater than 0.");
                    ev.consume();
                }
            } catch (NumberFormatException ex) {
                error.setText("Amount must be a number.");
                ev.consume();
            }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                double amt = Double.parseDouble(amountField.getText().trim());
                boolean saved = transactionDAO.updateTransaction(t.getId(),
                        Session.getUser().getId(), amt, typeBox.getValue(),
                        noteField.getText().trim());
                showMessage(saved ? "Transaction updated." : "Could not update. Check the terminal.", saved);
                if (saved)
                    loadData();
            }
        });
    }
}