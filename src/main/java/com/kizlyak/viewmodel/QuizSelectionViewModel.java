package com.kizlyak.viewmodel;

import com.kizlyak.entity.Quiz;
import com.kizlyak.service.QuizService;
import java.util.List;

public class QuizSelectionViewModel {
  private final QuizService quizService;

  public QuizSelectionViewModel(QuizService quizService) {
    this.quizService = quizService;
  }

  public List<Quiz> getAllQuizzes() {
    return quizService.getAllQuizzes();
  }

  public List<Quiz> searchQuizzes(String query) {
    return quizService.searchQuizzes(query);
  }
}
