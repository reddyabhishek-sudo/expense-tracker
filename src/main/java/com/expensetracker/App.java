package com.expensetracker;

import com.expensetracker.ui.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Expense Tracker");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        SceneManager.init(stage);
        SceneManager.showLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}