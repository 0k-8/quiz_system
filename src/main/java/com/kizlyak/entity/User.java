package com.kizlyak.entity;

import java.util.UUID;
import java.util.function.Supplier;

public class User {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String passwordHash;
  private Role role;
  private UUID teamsId;

  private Team team;
  private Supplier<Team> teamLoader;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public UUID getTeamsId() {
    return teamsId;
  }

  public void setTeamsId(UUID teamsId) {
    this.teamsId = teamsId;
  }

  public Team getTeam() {
    if (team == null && teamLoader != null) {
      team = teamLoader.get();
    }
    return team;
  }

  public void setTeamLoader(Supplier<Team> teamLoader) {
    this.teamLoader = teamLoader;
  }
}
