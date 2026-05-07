package com.kizlyak.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.kizlyak.entity.User;

public class MainMenuView {

  public static void show(Stage stage, User user, MainApp mainApp) {
    stage.setTitle("⟨ Quiz System ⟩ Головне меню");

    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(25, 25, 25, 25));

    Label welcomeLabel = new Label("Вітаємо, " + user.getFirstName() + "!");
    welcomeLabel.setFont(Font.font(24));

    Button startQuizBtn = new Button("Почати тестування");
    startQuizBtn.setPrefWidth(200);
    startQuizBtn.setOnAction(e -> mainApp.showQuizSelection(user));

    Button viewResultsBtn = new Button("Результати Команди");
    viewResultsBtn.setPrefWidth(200);
    viewResultsBtn.setOnAction(e -> mainApp.showResults(user));

    Button teamMgmtBtn = new Button("Моя Команда");
    teamMgmtBtn.setPrefWidth(200);
    teamMgmtBtn.setOnAction(e -> mainApp.showTeamManagement(user));

    Button logoutBtn = new Button("Вийти");
    logoutBtn.setPrefWidth(200);
    logoutBtn.setStyle("-fx-background-color: #ffcccc;");

    logoutBtn.setOnAction(e -> mainApp.showLogin());

    layout.getChildren().addAll(welcomeLabel, startQuizBtn, viewResultsBtn, teamMgmtBtn, logoutBtn);
    Scene scene = new Scene(layout, 400, 400);
    stage.setScene(scene);
  }
}
