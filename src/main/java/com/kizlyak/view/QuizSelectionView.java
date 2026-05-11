package com.kizlyak.view;

import com.kizlyak.entity.Quiz;
import com.kizlyak.entity.User;
import com.kizlyak.service.QuizService;
import com.kizlyak.viewmodel.QuizSelectionViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class QuizSelectionView {

  public static void show(Stage stage, User user, MainApp mainApp, QuizService quizService) {
    QuizSelectionViewModel viewModel = new QuizSelectionViewModel(quizService);
    stage.setTitle("⟨ Quiz System ⟩ Вибір тесту");

    VBox layout = new VBox(15);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(25));

    Label titleLabel = new Label("Оберіть тест");
    titleLabel.setFont(Font.font(22));

    HBox searchBox = new HBox(10);
    searchBox.setAlignment(Pos.CENTER);
    TextField searchField = new TextField();
    searchField.setPromptText("Пошук тестів...");
    Button searchBtn = new Button("Шукати");
    searchBox.getChildren().addAll(searchField, searchBtn);

    ListView<Quiz> quizListView = new ListView<>();
    quizListView.setCellFactory(
        param ->
            new ListCell<>() {
              @Override
              protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  setText(item.getTitle() + " (" + item.getTimeLimitMinutes() + " хв)");
                }
              }
            });

    quizListView.getItems().addAll(viewModel.getAllQuizzes());

    searchBtn.setOnAction(
        e -> {
          quizListView.getItems().clear();
          quizListView.getItems().addAll(viewModel.searchQuizzes(searchField.getText().trim()));
        });

    Button startBtn = new Button("Почати тест");
    startBtn.setPrefWidth(200);
    startBtn.setDisable(true);

    quizListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              startBtn.setDisable(newVal == null);
            });

    startBtn.setOnAction(
        e -> {
          if (user.getTeamsId() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження");
            alert.setHeaderText("Ви не в команді!");
            alert.setContentText("Для проходження тесту ви повинні бути учасником команди.");
            alert.showAndWait();
            return;
          }
          Quiz selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
          mainApp.startQuizSession(user, selectedQuiz);
        });

    Button backBtn = new Button("Назад до меню");
    backBtn.setOnAction(e -> mainApp.showMainMenu(user));

    layout.getChildren().addAll(titleLabel, searchBox, quizListView, startBtn, backBtn);

    Scene scene = new Scene(layout, 400, 550);
    stage.setScene(scene);
  }
}
