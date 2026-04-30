package com.kizlyak.entity;

import java.util.UUID;

public class Quiz {

  private UUID id;
  private String title;
  private int timeLimitMinutes;
  private UUID createdBy;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getTimeLimitMinutes() {
    return timeLimitMinutes;
  }

  public void setTimeLimitMinutes(int timeLimitMinutes) {
    this.timeLimitMinutes = timeLimitMinutes;
  }

  public UUID getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(UUID createdBy) {
    this.createdBy = createdBy;
  }
}
