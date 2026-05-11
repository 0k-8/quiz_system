package com.kizlyak.view;

import com.kizlyak.entity.Question;
import com.kizlyak.entity.Quiz;
import com.kizlyak.entity.Result;
import com.kizlyak.entity.User;
import com.kizlyak.service.QuizService;
import com.kizlyak.service.ResultService;
import com.kizlyak.viewmodel.QuizTakingViewModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class QuizTakingView {

  private static int timeLeftSeconds;
  private static Timeline timeline;

  public static void show(
      Stage stage,
      User user,
      MainApp mainApp,
      QuizService quizService,
      ResultService resultService,
      Quiz quiz) {
    QuizTakingViewModel viewModel = new QuizTakingViewModel(quizService, resultService, quiz, user);

    if (viewModel.getQuestions().isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.WARNING, "У цьому тесті немає питань!");
      alert.showAndWait();
      mainApp.showQuizSelection(user);
      return;
    }

    timeLeftSeconds = quiz.getTimeLimitMinutes() * 60;

    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(30));

    Label timerLabel = new Label("Залишилося часу: " + formatTime(timeLeftSeconds));
    timerLabel.setFont(Font.font(18));
    timerLabel.setStyle("-fx-text-fill: red;");

    Label questionLabel = new Label();
    questionLabel.setFont(Font.font(20));
    questionLabel.setWrapText(true);

    ToggleGroup optionsGroup = new ToggleGroup();
    RadioButton rbA = new RadioButton();
    RadioButton rbB = new RadioButton();
    RadioButton rbC = new RadioButton();
    RadioButton rbD = new RadioButton();
    rbA.setToggleGroup(optionsGroup);
    rbB.setToggleGroup(optionsGroup);
    rbC.setToggleGroup(optionsGroup);
    rbD.setToggleGroup(optionsGroup);

    Button nextBtn = new Button("Далі");
    nextBtn.setPrefWidth(150);

    timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                e -> {
                  timeLeftSeconds--;
                  timerLabel.setText("Залишилося часу: " + formatTime(timeLeftSeconds));
                  if (timeLeftSeconds <= 0) {
                    timeline.stop();
                    finish(stage, user, mainApp, viewModel);
                  }
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

    Runnable updateQuestion =
        () -> {
          Question q = viewModel.getCurrentQuestion();
          questionLabel.setText(
              "Питання "
                  + (viewModel.getCurrentQuestionIndex() + 1)
                  + "/"
                  + viewModel.getQuestions().size()
                  + ":\n"
                  + q.getContent());
          rbA.setText(q.getOptionA());
          rbB.setText(q.getOptionB());
          rbC.setText(q.getOptionC());
          rbD.setText(q.getOptionD());
          optionsGroup.selectToggle(null);
          if (viewModel.getCurrentQuestionIndex() == viewModel.getQuestions().size() - 1) {
            nextBtn.setText("Завершити");
          }
        };

    nextBtn.setOnAction(
        e -> {
          RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
          if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Будь ласка, оберіть відповідь!").show();
            return;
          }

          String answer =
              selected == rbA ? "A" : selected == rbB ? "B" : selected == rbC ? "C" : "D";
          if (viewModel.nextQuestion(answer)) {
            updateQuestion.run();
          } else {
            timeline.stop();
            finish(stage, user, mainApp, viewModel);
          }
        });

    updateQuestion.run();
    layout.getChildren().addAll(timerLabel, questionLabel, rbA, rbB, rbC, rbD, nextBtn);
    stage.setScene(new Scene(layout, 600, 500));
  }

  private static void finish(
      Stage stage, User user, MainApp mainApp, QuizTakingViewModel viewModel) {
    Result result = viewModel.finishQuiz(timeLeftSeconds);
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Тест завершено");
    alert.setHeaderText("Ваш результат: " + result.getScore() + "%");
    alert.setContentText(
        "Ви відповіли правильно на "
            + result.getCorrectAnswers()
            + " з "
            + result.getTotalQuestions()
            + " питань.");
    alert.showAndWait();
    mainApp.showMainMenu(user);
  }

  private static String formatTime(int totalSeconds) {
    return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
  }
}
