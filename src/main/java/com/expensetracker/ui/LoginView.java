package com.expensetracker.ui;

import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView {

    private final HBox root = new HBox();
    private final UserDAO userDAO = new UserDAO();
    private boolean registerMode = false;

    private final Label title = new Label("Welcome back");
    private final Label subtitle = new Label("Log in to manage your money");
    private final TextField nameField = new TextField();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Button submitButton = new Button("Log In");
    private final Hyperlink toggleLink = new Hyperlink("New here? Create an account");
    private final Label messageLabel = new Label();

    public LoginView() {
        root.getChildren().addAll(buildBrandPanel(), buildFormPanel());
    }

    public HBox getRoot() {
        return root;
    }

    private VBox buildBrandPanel() {
        Label logo = new Label("₹");
        logo.getStyleClass().add("brand-logo");

        Label name = new Label("ExpenseTracker");
        name.getStyleClass().add("brand-title");

        Label tagline = new Label("Know where every rupee goes.");
        tagline.getStyleClass().add("brand-tagline");

        VBox brand = new VBox(14, logo, name, tagline);
        brand.setAlignment(Pos.CENTER);
        brand.getStyleClass().add("brand-panel");
        brand.setPrefWidth(480);
        HBox.setHgrow(brand, Priority.ALWAYS);
        return brand;
    }

    private StackPane buildFormPanel() {
        title.getStyleClass().add("form-title");
        subtitle.getStyleClass().add("form-subtitle");

        nameField.setPromptText("Full name");
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        nameField.getStyleClass().add("input");
        usernameField.getStyleClass().add("input");
        passwordField.getStyleClass().add("input");

        // Name field is hidden in login mode
        nameField.setVisible(false);
        nameField.setManaged(false);

        submitButton.getStyleClass().add("primary-button");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> handleSubmit());
        passwordField.setOnAction(e -> handleSubmit());

        toggleLink.getStyleClass().add("link");
        toggleLink.setOnAction(e -> toggleMode());

        messageLabel.getStyleClass().add("error-text");
        messageLabel.setWrapText(true);

        VBox card = new VBox(14, title, subtitle, nameField, usernameField,
                passwordField, submitButton, messageLabel, toggleLink);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(380);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setPadding(new Insets(40));

        StackPane holder = new StackPane(card);
        holder.getStyleClass().add("form-panel");
        HBox.setHgrow(holder, Priority.ALWAYS);
        holder.setPrefWidth(520);
        return holder;
    }

    private void setMode(boolean register) {
        registerMode = register;
        nameField.setVisible(register);
        nameField.setManaged(register);
        if (register) {
            title.setText("Create account");
            subtitle.setText("Start tracking in under a minute");
            submitButton.setText("Register");
            toggleLink.setText("Already have an account? Log in");
        } else {
            title.setText("Welcome back");
            subtitle.setText("Log in to manage your money");
            submitButton.setText("Log In");
            toggleLink.setText("New here? Create an account");
        }
    }

    private void toggleMode() {
        showMessage("", false);
        setMode(!registerMode);
    }

    private void showMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().removeAll("error-text", "success-text");
        messageLabel.getStyleClass().add(success ? "success-text" : "error-text");
    }

    private void handleSubmit() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String name = nameField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || (registerMode && name.isEmpty())) {
            showMessage("Please fill in all fields.", false);
            return;
        }

        if (registerMode) {
            User newUser = new User(name, username, password);
            if (userDAO.registerUser(newUser)) {
                setMode(false);
                passwordField.clear();
                showMessage("Account created! Please log in.", true);
            } else {
                showMessage("Username already taken, or a database error occurred.", false);
            }
        } else {
            User user = userDAO.loginUser(username, password);
            if (user != null) {
                Session.setUser(user);
                SceneManager.showDashboard();
            } else {
                showMessage("Wrong username or password.", false);
            }
        }
    }
}