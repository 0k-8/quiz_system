package com.kizlyak.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Result {
  private UUID id;
  private UUID teamId;
  private UUID quizId;
  private int score;
  private int timeSpentSeconds;
  private LocalDateTime completedAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
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
