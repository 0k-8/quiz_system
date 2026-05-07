package com.kizlyak.view;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.kizlyak.entity.Quiz;
import com.kizlyak.entity.User;
import com.kizlyak.service.QuizService;

public class QuizSelectionView {

  public static void show(Stage stage, User user, MainApp mainApp, QuizService quizService) {
    stage.setTitle("⟨ Quiz System ⟩ Вибір тесту");

    VBox layout = new VBox(15);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(25));

    Label titleLabel = new Label("Оберіть тест");
    titleLabel.setFont(Font.font(22));

    // Список для відображення квізів
    ListView<Quiz> quizListView = new ListView<>();

    // Кастомне відображення (щоб показувати Title замість адреси об'єкта)
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

    // Завантаження даних
    List<Quiz> quizzes = quizService.getAllQuizzes();
    quizListView.getItems().addAll(quizzes);

    Button startBtn = new Button("Почати тест");
    startBtn.setPrefWidth(200);
    startBtn.setDisable(true); // Вимкнена, поки не обрано квіз

    // Активуємо кнопку при виборі
    quizListView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              startBtn.setDisable(newVal == null);
            });

    startBtn.setOnAction(
        e -> {
          Quiz selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
          mainApp.startQuizSession(user, selectedQuiz);
        });

    Button backBtn = new Button("Назад до меню");
    backBtn.setOnAction(e -> mainApp.showMainMenu(user));

    layout.getChildren().addAll(titleLabel, quizListView, startBtn, backBtn);

    Scene scene = new Scene(layout, 400, 500);
    stage.setScene(scene);
  }
}
