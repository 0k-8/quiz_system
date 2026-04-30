package com.kizlyak.entity;

import java.util.UUID;

public class Question {

  private UUID id;
  private UUID quizId;
  private String content;
  private String optionA;
  private String optionB;
  private String optionC;
  private String optionD;
  private String correctOption;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getQuizId() {
    return quizId;
  }

  public void setQuizId(UUID quizId) {
    this.quizId = quizId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getOptionA() {
    return optionA;
  }

  public void setOptionA(String optionA) {
    this.optionA = optionA;
  }

  public String getOptionB() {
    return optionB;
  }

  public void setOptionB(String optionB) {
    this.optionB = optionB;
  }

  public String getOptionC() {
    return optionC;
  }

  public void setOptionC(String optionC) {
    this.optionC = optionC;
  }

  public String getOptionD() {
    return optionD;
  }

  public void setOptionD(String optionD) {
    this.optionD = optionD;
  }

  public String getCorrectOption() {
    return correctOption;
  }

  public void setCorrectOption(String correctOption) {
    this.correctOption = correctOption;
  }
}
