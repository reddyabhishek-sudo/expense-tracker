package com.expensetracker.ui;

import com.expensetracker.dao.TransactionDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.Map;

public class SummaryView {

    private static final String[] COLORS = {
            "#6366f1", "#ec4899", "#f59e0b", "#10b981", "#3b82f6",
            "#ef4444", "#8b5cf6", "#14b8a6", "#f97316"
    };

    private final VBox root = new VBox(20);

    public SummaryView() {
        Label heading = new Label("Summary");
        heading.getStyleClass().add("page-title");
        Label sub = new Label("Where your money goes");
        sub.getStyleClass().add("page-subtitle");

        int uid = Session.getUser().getId();
        Map<String, Double> totals = new TransactionDAO().getTotalsByCategory(uid, "expense");

        root.getChildren().addAll(heading, sub);
        root.getStyleClass().add("page");

        if (totals.isEmpty()) {
            Label empty = new Label("No expenses yet. Add some to see your chart.");
            empty.getStyleClass().add("page-subtitle");
            root.getChildren().add(empty);
            return;
        }

        double sum = totals.values().stream().mapToDouble(Double::doubleValue).sum();

        PieChart chart = new PieChart();
        chart.setLegendVisible(false);
        chart.setLabelsVisible(false);
        chart.setStartAngle(90);
        chart.setMinSize(320, 320);
        chart.setPrefSize(380, 380);

        VBox legend = new VBox(12);
        legend.setAlignment(Pos.CENTER_LEFT);

        int i = 0;
        for (Map.Entry<String, Double> e : totals.entrySet()) {
            chart.getData().add(new PieChart.Data(e.getKey(), e.getValue()));
            legend.getChildren().add(legendRow(e.getKey(), e.getValue(), sum, COLORS[i % COLORS.length]));
            i++;
        }

        // Colour each slice after the chart builds its nodes
        int index = 0;
        for (PieChart.Data d : chart.getData()) {
            String color = COLORS[index % COLORS.length];
            d.getNode().setStyle("-fx-background-color: " + color + "; -fx-pie-color: " + color + ";");
            index++;
        }

        Label totalLabel = new Label(String.format("Total spent: ₹ %,.2f", sum));
        totalLabel.getStyleClass().add("stat-title");

        VBox legendCard = new VBox(16, totalLabel, legend);
        legendCard.getStyleClass().add("form-card");
        legendCard.setPadding(new Insets(24));
        legendCard.setMinWidth(300);
        legendCard.setMaxHeight(Region.USE_PREF_SIZE);

        HBox row = new HBox(30, chart, legendCard);
        row.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(row);
    }

    public Node getRoot() {
        return root;
    }

    private HBox legendRow(String name, double amount, double sum, String color) {
        Region dot = new Region();
        dot.setMinSize(12, 12);
        dot.setMaxSize(12, 12);
        dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 6;");

        Label n = new Label(name);
        n.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label a = new Label(String.format("₹ %,.2f  (%.0f%%)", amount, amount * 100 / sum));
        a.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");

        HBox row = new HBox(10, dot, n, spacer, a);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}