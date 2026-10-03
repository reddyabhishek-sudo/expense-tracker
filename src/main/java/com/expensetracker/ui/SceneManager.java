package com.expensetracker.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage stage;

    public static void init(Stage s) {
        stage = s;
    }

    public static void show(Parent root) {
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(
                SceneManager.class.getResource("/styles/theme.css").toExternalForm());
        stage.setScene(scene);
    }

    public static void showLogin() {
        show(new LoginView().getRoot());
    }

    public static void showDashboard() {
        show(new DashboardView().getRoot());
    }
}