package com.kizlyak.view;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.kizlyak.entity.Result;
import com.kizlyak.entity.User;
import com.kizlyak.entity.Quiz;
import com.kizlyak.service.QuizService;

public class ResultsView {

    public static void show(Stage stage, User user, MainApp mainApp, QuizService quizService) {
        stage.setTitle("⟨ Quiz System ⟩ Результати Команди");

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        Label titleLabel = new Label("Результати вашої команди");
        titleLabel.setFont(Font.font(22));

        ListView<Result> resultListView = new ListView<>();
        resultListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Result item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    String dateStr = item.getCompletedAt() != null ? item.getCompletedAt().format(formatter) : "N/A";
                    setText("Результат: " + item.getScore() + "% | Дата: " + dateStr + " (Натисніть для деталей)");
                }
            }
        });

        resultListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showDetailPopup(newVal, quizService);
            }
        });

        if (user.getTeamsId() != null) {
            List<Result> results = quizService.getResultsForTeam(user.getTeamsId());
            resultListView.getItems().addAll(results);
        } else {
            resultListView.setPlaceholder(new Label("Ви ще не проходили тестів або не належите до команди."));
        }

        Button backBtn = new Button("Назад до меню");
        backBtn.setOnAction(e -> mainApp.showMainMenu(user));

        layout.getChildren().addAll(titleLabel, resultListView, backBtn);

        Scene scene = new Scene(layout, 550, 500);
        stage.setScene(scene);
    }

    private static void showDetailPopup(Result result, QuizService quizService) {
        String quizTitle = quizService.getAllQuizzes().stream()
                .filter(q -> q.getId().equals(result.getQuizId()))
                .findFirst()
                .map(Quiz::getTitle)
                .orElse("Невідомий тест");

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String dateStr = result.getCompletedAt() != null ? result.getCompletedAt().format(formatter) : "N/A";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Деталі результату");
        alert.setHeaderText("Тест: " + quizTitle);
        
        String content = "📊 Успішність: " + result.getScore() + "%\n" +
                         "✅ Правильних відповідей: " + result.getCorrectAnswers() + " з " + result.getTotalQuestions() + "\n" +
                         "⏱ Витрачено часу: " + formatTime(result.getTimeSpentSeconds()) + "\n" +
                         "📅 Дата завершення: " + dateStr;
        
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
