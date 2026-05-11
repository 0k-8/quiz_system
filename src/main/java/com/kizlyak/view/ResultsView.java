package com.kizlyak.view;

import com.kizlyak.entity.Result;
import com.kizlyak.entity.User;
import com.kizlyak.service.ResultService;
import com.kizlyak.viewmodel.ResultsViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ResultsView {

  public static void show(Stage stage, User user, MainApp mainApp, ResultService resultService) {
    ResultsViewModel viewModel = new ResultsViewModel(resultService, user);
    stage.setTitle("⟨ Quiz System ⟩ Результати");

    VBox layout = new VBox(15);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(25));

    Label titleLabel =
        new Label("Результати " + (user.getTeamsId() != null ? "команди" : "користувача"));
    titleLabel.setFont(Font.font(22));

    HBox searchBox = new HBox(10);
    searchBox.setAlignment(Pos.CENTER);
    TextField searchField = new TextField();
    searchField.setPromptText("Пошук за тестом...");
    Button searchBtn = new Button("🔍 Шукати");
    searchBox.getChildren().addAll(searchField, searchBtn);

    HBox filterBox = new HBox(10);
    filterBox.setAlignment(Pos.CENTER);
    Label sortLabel = new Label("Сортувати за:");
    ComboBox<String> sortCombo = new ComboBox<>();
    sortCombo
        .getItems()
        .addAll(
            "Датою (нові)", "Датою (старі)", "Балами (↑)", "Балами (↓)", "Часом (↑)", "Часом (↓)");
    sortCombo.getSelectionModel().select(0);
    filterBox.getChildren().addAll(sortLabel, sortCombo);

    ListView<Result> resultListView = new ListView<>();
    resultListView.setCellFactory(
        param ->
            new ListCell<>() {
              @Override
              protected void updateItem(Result item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  java.time.format.DateTimeFormatter formatter =
                      java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                  String dateStr =
                      item.getCompletedAt() != null
                          ? item.getCompletedAt().format(formatter)
                          : "N/A";
                  String quizTitle = item.getQuiz() != null ? item.getQuiz().getTitle() : "Тест";
                  String teamName =
                      item.getTeam() != null ? item.getTeam().getTeamName() : "Користувач";
                  setText(
                      "["
                          + teamName
                          + "] "
                          + quizTitle
                          + ": "
                          + item.getScore()
                          + "% | "
                          + dateStr);
                }
              }
            });

    resultListView.getItems().addAll(viewModel.getResults());

    sortCombo.setOnAction(
        e -> {
          String selected = sortCombo.getSelectionModel().getSelectedItem();
          if (selected == null) return;

          java.util.List<Result> currentList = new java.util.ArrayList<>(resultListView.getItems());
          switch (selected) {
            case "Датою (нові)":
              viewModel.sortByDate(currentList, false);
              break;
            case "Датою (старі)":
              viewModel.sortByDate(currentList, true);
              break;
            case "Балами (↑)":
              viewModel.sortByScore(currentList, true);
              break;
            case "Балами (↓)":
              viewModel.sortByScore(currentList, false);
              break;
            case "Часом (↑)":
              viewModel.sortByTime(currentList, true);
              break;
            case "Часом (↓)":
              viewModel.sortByTime(currentList, false);
              break;
          }
          resultListView.getItems().clear();
          resultListView.getItems().addAll(currentList);
        });

    searchBtn.setOnAction(
        e -> {
          resultListView.getItems().clear();
          resultListView.getItems().addAll(viewModel.searchResults(searchField.getText().trim()));
        });

    resultListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal != null) {
                showDetailPopup(newVal);
              }
            });

    Button backBtn = new Button("Назад до меню");
    backBtn.setOnAction(e -> mainApp.showMainMenu(user));

    layout.getChildren().addAll(titleLabel, searchBox, filterBox, resultListView, backBtn);

    Scene scene = new Scene(layout, 600, 550);
    stage.setScene(scene);
  }

  private static void showDetailPopup(Result result) {
    String quizTitle = result.getQuiz() != null ? result.getQuiz().getTitle() : "Невідомий тест";
    String teamName = result.getTeam() != null ? result.getTeam().getTeamName() : "N/A";

    java.time.format.DateTimeFormatter formatter =
        java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    String dateStr =
        result.getCompletedAt() != null ? result.getCompletedAt().format(formatter) : "N/A";

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Деталі результату");
    alert.setHeaderText("Тест: " + quizTitle + " (Команда: " + teamName + ")");

    String content =
        "📊 Успішність: "
            + result.getScore()
            + "%\n"
            + "✅ Правильних відповідей: "
            + result.getCorrectAnswers()
            + " з "
            + result.getTotalQuestions()
            + "\n"
            + "⏱ Витрачено часу: "
            + formatTime(result.getTimeSpentSeconds())
            + "\n"
            + "📅 Дата завершення: "
            + dateStr;

    alert.setContentText(content);
    alert.showAndWait();
  }

  private static String formatTime(int totalSeconds) {
    int mins = totalSeconds / 60;
    int secs = totalSeconds % 60;
    return String.format("%02d:%02d", mins, secs);
  }
}
