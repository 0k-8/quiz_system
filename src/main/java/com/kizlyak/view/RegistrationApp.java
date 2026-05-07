package com.kizlyak.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.kizlyak.service.AuthService;

public class RegistrationApp {

  public static void show(Stage stage, AuthService authService, MainApp mainApp) {
    stage.setTitle("⟨ Quiz System ⟩ Реєстрація");

    GridPane gridPane = new GridPane();
    gridPane.setAlignment(Pos.CENTER);
    gridPane.setHgap(10);
    gridPane.setVgap(10);
    gridPane.setPadding(new Insets(25, 25, 25, 25));

    Label sceneTitle = new Label("Реєстрація");
    sceneTitle.setFont(Font.font(20));
    gridPane.add(sceneTitle, 0, 0, 2, 1);

    gridPane.add(new Label("Ім'я:"), 0, 1);
    TextField firstNameField = new TextField();
    gridPane.add(firstNameField, 1, 1);

    gridPane.add(new Label("Прізвище:"), 0, 2);
    TextField lastNameField = new TextField();
    gridPane.add(lastNameField, 1, 2);

    gridPane.add(new Label("Пошта:"), 0, 3);
    TextField emailField = new TextField();
    gridPane.add(emailField, 1, 3);

    gridPane.add(new Label("Пароль:"), 0, 4);
    PasswordField passwordField = new PasswordField();
    gridPane.add(passwordField, 1, 4);

    Button btn = new Button("Зареєструватись");
    gridPane.add(btn, 1, 5);

    Label messageLabel = new Label();
    gridPane.add(messageLabel, 1, 6);

    btn.setOnAction(
        e -> {
          String fName = firstNameField.getText().trim();
          String lName = lastNameField.getText().trim();
          String email = emailField.getText().trim();
          String pass = passwordField.getText();

          // ВАЛІДАЦІЯ
          if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red");
            messageLabel.setText("Всі поля мають бути заповнені!");
            return;
          }

          if (!email.contains("@") || !email.contains(".")) {
            messageLabel.setStyle("-fx-text-fill: red");
            messageLabel.setText("Невірний формат Email!");
            return;
          }

          if (pass.length() < 6) {
            messageLabel.setStyle("-fx-text-fill: red");
            messageLabel.setText("Пароль занадто короткий (мін. 6)!");
            return;
          }

          try {
            authService.register(fName, lName, email, pass);
            messageLabel.setStyle("-fx-text-fill: green");
            messageLabel.setText("Аккаунт створено! ");
          } catch (Exception ex) {
            messageLabel.setStyle("-fx-text-fill: red");
            messageLabel.setText(ex.getMessage());
          }
        });

    Button switchToLoginButton = new Button("Вже є акаунт? Увійти");
    switchToLoginButton.setStyle(
        "-fx-background-color: transparent; -fx-text-fill: blue; -fx-underline: true");
    gridPane.add(switchToLoginButton, 1, 7);

    switchToLoginButton.setOnAction(e -> mainApp.showLogin());

    Scene scene = new Scene(gridPane, 400, 450);
    stage.setScene(scene);
  }
}
