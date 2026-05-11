package com.kizlyak.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Result {
  private UUID id;
  private UUID userId;
  private UUID teamId;
  private UUID quizId;
  private int score;
  private int correctAnswers;
  private int totalQuestions;
  private int timeSpentSeconds;
  private LocalDateTime completedAt;
  private Quiz quiz;
  private java.util.function.Supplier<Quiz> quizLoader;
  private Team team;
  private java.util.function.Supplier<Team> teamLoader;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getTeamId() {
    return teamId;
  }

  public void setTeamId(UUID teamId) {
    this.teamId = teamId;
  }

  public UUID getQuizId() {
    return quizId;
  }

  public void setQuizId(UUID quizId) {
    this.quizId = quizId;
  }

  public Quiz getQuiz() {
    if (quiz == null && quizLoader != null) {
      quiz = quizLoader.get();
    }
    return quiz;
  }

  public void setQuiz(Quiz quiz) {
    this.quiz = quiz;
  }

  public void setQuizLoader(java.util.function.Supplier<Quiz> quizLoader) {
    this.quizLoader = quizLoader;
  }

  public Team getTeam() {
    if (team == null && teamLoader != null) {
      team = teamLoader.get();
    }
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public void setTeamLoader(java.util.function.Supplier<Team> teamLoader) {
    this.teamLoader = teamLoader;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getCorrectAnswers() {
    return correctAnswers;
  }

  public void setCorrectAnswers(int correctAnswers) {
    this.correctAnswers = correctAnswers;
  }

  public int getTotalQuestions() {
    return totalQuestions;
  }

  public void setTotalQuestions(int totalQuestions) {
    this.totalQuestions = totalQuestions;
  }

  public int getTimeSpentSeconds() {
    return timeSpentSeconds;
  }

  public void setTimeSpentSeconds(int timeSpentSeconds) {
    this.timeSpentSeconds = timeSpentSeconds;
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(LocalDateTime completedAt) {
    this.completedAt = completedAt;
  }
}
