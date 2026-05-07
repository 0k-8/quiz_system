package com.kizlyak.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

import com.kizlyak.entity.Question;
import com.kizlyak.entity.Quiz;
import com.kizlyak.entity.Result;
import com.kizlyak.entity.User;
import com.kizlyak.service.QuizService;

public class QuizTakingView {

    private static int currentQuestionIndex = 0;
    private static int score = 0;
    private static int timeLeftSeconds;
    private static Timeline timeline;

    public static void show(
          Stage stage, User user, MainApp mainApp, QuizService quizService, Quiz quiz) {
        List<Question> questions = quizService.getQuestionsForQuiz(quiz.getId());
        if (questions.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "У цьому тесті немає питань!");
            alert.showAndWait();
            mainApp.showQuizSelection(user);
            return;
        }

        currentQuestionIndex = 0;
        score = 0;
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

        // Timer logic
        timeline =
              new Timeline(
                    new KeyFrame(
                          Duration.seconds(1),
                          e -> {
                              timeLeftSeconds--;
                              timerLabel.setText("Залишилося часу: " + formatTime(timeLeftSeconds));
                              if (timeLeftSeconds <= 0) {
                                  timeline.stop();
                                  finishQuiz(stage, user, mainApp, quizService, quiz, questions);
                              }
                          }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Runnable updateQuestion =
              () -> {
                  Question q = questions.get(currentQuestionIndex);
                  questionLabel.setText(
                        "Питання "
                              + (currentQuestionIndex + 1)
                              + "/"
                              + questions.size()
                              + ":\n"
                              + q.getContent());
                  rbA.setText(q.getOptionA());
                  rbB.setText(q.getOptionB());
                  rbC.setText(q.getOptionC());
                  rbD.setText(q.getOptionD());
                  optionsGroup.selectToggle(null);

                  if (currentQuestionIndex == questions.size() - 1) {
                      nextBtn.setText("Завершити");
                  } else {
                      nextBtn.setText("Далі");
                  }
              };

        nextBtn.setOnAction(
              e -> {
                  RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
                  if (selected == null) {
                      Alert alert = new Alert(Alert.AlertType.INFORMATION, "Будь ласка, оберіть відповідь!");
                      alert.show();
                      return;
                  }

                  String answer = "";
                  if (selected == rbA) answer = "A";
                  else if (selected == rbB) answer = "B";
                  else if (selected == rbC) answer = "C";
                  else if (selected == rbD) answer = "D";

                  if (answer.equalsIgnoreCase(questions.get(currentQuestionIndex).getCorrectOption())) {
                      score++;
                  }

                  if (currentQuestionIndex < questions.size() - 1) {
                      currentQuestionIndex++;
                      updateQuestion.run();
                  } else {
                      timeline.stop();
                      finishQuiz(stage, user, mainApp, quizService, quiz, questions);
                  }
              });

        updateQuestion.run();

        layout.getChildren().addAll(timerLabel, questionLabel, rbA, rbB, rbC, rbD, nextBtn);

        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
    }

    private static void finishQuiz(
          Stage stage,
          User user,
          MainApp mainApp,
          QuizService quizService,
          Quiz quiz,
          List<Question> questions) {
        int finalScore = (int) Math.round(((double) score / questions.size()) * 100);
        int timeSpent = (quiz.getTimeLimitMinutes() * 60) - timeLeftSeconds;

        Result result = new Result();
        result.setId(UUID.randomUUID());
        result.setQuizId(quiz.getId());
        result.setTeamId(user.getTeamsId());
        result.setScore(finalScore);
        result.setCorrectAnswers(score);
        result.setTotalQuestions(questions.size());
        result.setTimeSpentSeconds(timeSpent);
        result.setCompletedAt(LocalDateTime.now());

        quizService.saveResult(result);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Тест завершено");
        alert.setHeaderText("Ваш результат: " + finalScore + "%");
        alert.setContentText(
              "Ви відповіли правильно на "
                    + score
                    + " з "
                    + questions.size()
                    + " питань.\n"
                    + "Витрачено часу: "
                    + formatTime(timeSpent));
        alert.showAndWait();

        mainApp.showMainMenu(user);
    }

    private static String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
