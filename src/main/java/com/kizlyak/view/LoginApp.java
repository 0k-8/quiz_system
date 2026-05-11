package com.kizlyak.view;

import com.kizlyak.service.AuthService;
import com.kizlyak.viewmodel.LoginViewModel;
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

public class LoginApp {

  public static void show(Stage stage, AuthService authService, MainApp mainApp) {
    LoginViewModel viewModel = new LoginViewModel(authService);
    stage.setTitle("⟨ Quiz System ⟩ Вхід");

    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));

    Label sceneTitle = new Label("Авторизація");
    sceneTitle.setFont(Font.font(20));
    grid.add(sceneTitle, 0, 0, 2, 1);

    grid.add(new Label("Пошта:"), 0, 1);
    TextField emailField = new TextField();
    grid.add(emailField, 1, 1);

    grid.add(new Label("Пароль:"), 0, 2);
    PasswordField passwordField = new PasswordField();
    grid.add(passwordField, 1, 2);

    Button buttonLogin = new Button("Увійти");
    grid.add(buttonLogin, 1, 3);

    Button switchToReg = new Button("Немає акаунта? Створи його тут");
    switchToReg.setStyle(
        "-fx-background-color: transparent; -fx-text-fill: blue; -fx-underline: true");
    grid.add(switchToReg, 1, 4);

    Label messageLabel = new Label();
    grid.add(messageLabel, 1, 5);

    buttonLogin.setOnAction(
        e -> {
          String email = emailField.getText().trim();
          String password = passwordField.getText();

          if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Заповніть пошту та пароль!");
            return;
          }

          try {
            viewModel
                .login(email, password)
                .ifPresentOrElse(
                    user -> {
                      messageLabel.setStyle("-fx-text-fill: green;");
                      messageLabel.setText("Вітаємо, " + user.getFirstName());
                      mainApp.showMainMenu(user);
                    },
                    () -> {
                      messageLabel.setText("Невірний логін або пароль!");
                      messageLabel.setStyle("-fx-text-fill: red;");
                    });
          } catch (Exception ex) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Помилка: " + ex.getMessage());
          }
        });

    switchToReg.setOnAction(e -> mainApp.showRegistration());

    Scene scene = new Scene(grid, 400, 400);
    stage.setScene(scene);
  }
}
