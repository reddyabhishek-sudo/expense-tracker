package com.expensetracker.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import com.expensetracker.dao.TransactionDAO;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class DashboardView {

    private final BorderPane root = new BorderPane();
    private final StackPane content = new StackPane();
    private final List<Button> navButtons = new ArrayList<>();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public DashboardView() {
        root.setLeft(buildSidebar());

        content.getStyleClass().add("content-area");
        content.setAlignment(Pos.TOP_LEFT);
        root.setCenter(content);

        navButtons.get(0).fire(); // open the Dashboard page first
    }

    public BorderPane getRoot() {
        return root;
    }

    // ---------- Sidebar ----------
    private VBox buildSidebar() {
        Label brand = new Label("₹  ExpenseTracker");
        brand.getStyleClass().add("sidebar-brand");

        Button home = navButton("Dashboard", this::buildHome);
        Button add = navButton("Add Transaction", () -> new AddTransactionView().getRoot());
        Button list = navButton("Transactions", () -> new TransactionsView().getRoot());
        Button summary = navButton("Summary", () -> new SummaryView().getRoot());
        Button budget = navButton("Budget", () -> new BudgetView().getRoot());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Signed in as " + Session.getUser().getName());
        userLabel.getStyleClass().add("sidebar-user");

        Button logout = new Button("Log Out");
        logout.getStyleClass().add("logout-button");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> {
            Session.clear();
            SceneManager.showLogin();
        });

        VBox sidebar = new VBox(8, brand, home, add, list, summary, budget,
                spacer, userLabel, logout);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        return sidebar;
    }

    private Button navButton(String text, Supplier<Node> page) {
        Button b = new Button(text);
        b.getStyleClass().add("nav-button");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setOnAction(e -> {
            for (Button other : navButtons) {
                other.getStyleClass().remove("nav-button-active");
            }
            b.getStyleClass().add("nav-button-active");
            Node view = page.get();
            content.getChildren().setAll(view);
            FadeTransition fade = new FadeTransition(Duration.millis(250), view);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        });
        navButtons.add(b);
        return b;
    }

    // ---------- Pages ----------
    private Node buildHome() {
        int uid = Session.getUser().getId();
        double income = transactionDAO.getTotalByType(uid, "income");
        double expense = transactionDAO.getTotalByType(uid, "expense");
        double balance = income - expense;

        Label heading = new Label("Hello, " + Session.getUser().getName());
        heading.getStyleClass().add("page-title");

        Label sub = new Label("Here is your financial overview");
        sub.getStyleClass().add("page-subtitle");

        HBox cards = new HBox(20,
                statCard("Total Income", String.format("₹ %,.2f", income), "income-value"),
                statCard("Total Expense", String.format("₹ %,.2f", expense), "expense-value"),
                statCard("Balance", String.format("₹ %,.2f", balance), "balance-value"));

        VBox page = new VBox(20, heading, sub, cards);
        page.getStyleClass().add("page");
        return page;
    }

    private VBox statCard(String title, String value, String valueStyle) {
        Label t = new Label(title);
        t.getStyleClass().add("stat-title");

        Label v = new Label(value);
        v.setMinWidth(Region.USE_PREF_SIZE); // never truncate the number
        v.getStyleClass().addAll("stat-value", valueStyle);

        VBox card = new VBox(10, t, v);
        card.getStyleClass().add("stat-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(0);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }
}