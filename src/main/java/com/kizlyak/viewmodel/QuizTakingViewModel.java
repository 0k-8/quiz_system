package com.kizlyak.viewmodel;

import com.kizlyak.entity.Question;
import com.kizlyak.entity.Quiz;
import com.kizlyak.entity.Result;
import com.kizlyak.entity.User;
import com.kizlyak.service.QuizService;
import com.kizlyak.service.ResultService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class QuizTakingViewModel {
  private final QuizService quizService;
  private final ResultService resultService;
  private final Quiz quiz;
  private final User user;
  private List<Question> questions;
  private int currentQuestionIndex = 0;
  private int score = 0;

  public QuizTakingViewModel(
      QuizService quizService, ResultService resultService, Quiz quiz, User user) {
    this.quizService = quizService;
    this.resultService = resultService;
    this.quiz = quiz;
    this.user = user;
    this.questions = quizService.getQuestionsForQuiz(quiz.getId());
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public Question getCurrentQuestion() {
    if (currentQuestionIndex < questions.size()) {
      return questions.get(currentQuestionIndex);
    }
    return null;
  }

  public boolean nextQuestion(String selectedOption) {
    if (selectedOption.equalsIgnoreCase(getCurrentQuestion().getCorrectOption())) {
      score++;
    }
    currentQuestionIndex++;
    return currentQuestionIndex < questions.size();
  }

  public Result finishQuiz(int timeLeftSeconds) {
    int finalScore = (int) Math.round(((double) score / questions.size()) * 100);
    int timeSpent = (quiz.getTimeLimitMinutes() * 60) - timeLeftSeconds;

    Result result = new Result();
    result.setId(UUID.randomUUID());
    result.setQuizId(quiz.getId());
    result.setTeamId(user.getTeamsId());
    result.setUserId(user.getId());
    result.setScore(finalScore);
    result.setCorrectAnswers(score);
    result.setTotalQuestions(questions.size());
    result.setTimeSpentSeconds(timeSpent);
    result.setCompletedAt(LocalDateTime.now());

    resultService.save(result);
    return result;
  }

  public int getCurrentQuestionIndex() {
    return currentQuestionIndex;
  }

  public int getScore() {
    return score;
  }
}
