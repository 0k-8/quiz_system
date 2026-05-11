package com.kizlyak.view;

import com.kizlyak.entity.Team;
import com.kizlyak.entity.User;
import com.kizlyak.service.TeamService;
import com.kizlyak.service.UserService;
import com.kizlyak.viewmodel.TeamManagementViewModel;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TeamManagementView {

  public static void show(
      Stage stage, User user, MainApp mainApp, TeamService teamService, UserService userService) {
    TeamManagementViewModel viewModel = new TeamManagementViewModel(teamService, userService);
    stage.setTitle("⟨ Quiz System ⟩ Керування командою");

    VBox layout = new VBox(15);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(20));

    Label titleLabel = new Label("Керування командою");
    titleLabel.setFont(Font.font(22));

    VBox currentTeamBox = new VBox(10);
    currentTeamBox.setAlignment(Pos.CENTER);
    currentTeamBox.setStyle("-fx-border-color: #cccccc; -fx-padding: 10; -fx-border-radius: 5;");

    if (user.getTeamsId() != null) {
      String teamName = user.getTeam() != null ? user.getTeam().getTeamName() : "Команда";
      Label teamNameLabel = new Label("Ваша команда: " + teamName);
      teamNameLabel.setFont(Font.font(16));

      ListView<String> membersList = new ListView<>();
      membersList.setPrefHeight(120);
      List<User> members = viewModel.getTeamMembers(user.getTeamsId());
      for (User m : members) {
        membersList
            .getItems()
            .add(m.getFirstName() + " " + m.getLastName() + " (" + m.getEmail() + ")");
      }

      Button leaveBtn = new Button("Залишити команду");
      leaveBtn.setStyle("-fx-background-color: #e74c3c;");
      leaveBtn.setOnAction(
          e -> {
            viewModel.leaveTeam(user);
            show(stage, user, mainApp, teamService, userService);
          });

      currentTeamBox
          .getChildren()
          .addAll(teamNameLabel, new Label("Учасники:"), membersList, leaveBtn);
    } else {
      currentTeamBox.getChildren().add(new Label("Ви ще не в команді"));
    }

    VBox actionsBox = new VBox(10);
    actionsBox.setAlignment(Pos.CENTER);

    TextField teamNameField = new TextField();
    teamNameField.setPromptText("Введіть назву команди");

    Button createBtn = new Button("Створити нову команду");
    createBtn.setPrefWidth(200);
    createBtn.setOnAction(
        e -> {
          try {
            viewModel.createTeam(teamNameField.getText().trim(), user);
            show(stage, user, mainApp, teamService, userService);
          } catch (Exception ex) {
            showAlert(ex.getMessage());
          }
        });

    Label allTeamsLabel = new Label("Всі доступні команди (натисніть, щоб приєднатись):");
    ListView<Team> allTeamsList = new ListView<>();
    allTeamsList.setPrefHeight(150);
    allTeamsList.setCellFactory(
        lv ->
            new ListCell<Team>() {
              @Override
              protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTeamName());
              }
            });
    allTeamsList.getItems().addAll(viewModel.getAllTeams());

    allTeamsList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal != null) {
                try {
                  viewModel.joinTeam(newVal.getTeamName(), user);
                  show(stage, user, mainApp, teamService, userService);
                } catch (Exception ex) {
                  showAlert(ex.getMessage());
                }
              }
            });

    Button backBtn = new Button("Назад до меню");
    backBtn.setOnAction(e -> mainApp.showMainMenu(user));

    actionsBox
        .getChildren()
        .addAll(
            new Label("Створити команду:"), teamNameField, createBtn, allTeamsLabel, allTeamsList);

    layout.getChildren().addAll(titleLabel, currentTeamBox, actionsBox, backBtn);

    Scene scene = new Scene(layout, 500, 650);
    stage.setScene(scene);
  }

  private static void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
    alert.show();
  }
}
