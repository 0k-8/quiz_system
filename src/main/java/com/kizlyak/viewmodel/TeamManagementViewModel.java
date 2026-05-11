package com.kizlyak.viewmodel;

import com.kizlyak.entity.Team;
import com.kizlyak.entity.User;
import com.kizlyak.service.TeamService;
import com.kizlyak.service.UserService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamManagementViewModel {
  private final TeamService teamService;
  private final UserService userService;

  public TeamManagementViewModel(TeamService teamService, UserService userService) {
    this.teamService = teamService;
    this.userService = userService;
  }

  public void createTeam(String teamName, User creator) {
    if (teamName == null || teamName.isBlank()) {
      throw new IllegalArgumentException("Назва команди не може бути порожньою!");
    }
    if (teamService.findByName(teamName).isPresent()) {
      throw new RuntimeException("Команда з такою назвою вже існує!");
    }

    Team team = new Team();
    team.setId(UUID.randomUUID());
    team.setTeamName(teamName);
    team.setCreatedAt(java.time.LocalDateTime.now());
    teamService.save(team);

    creator.setTeamsId(team.getId());
    userService.save(creator);
  }

  public void joinTeam(String teamName, User user) {
    Team team =
        teamService
            .findByName(teamName)
            .orElseThrow(() -> new RuntimeException("Команду не знайдено!"));

    user.setTeamsId(team.getId());
    userService.save(user);
  }

  public void leaveTeam(User user) {
    user.setTeamsId(null);
    userService.save(user);
  }

  public List<User> getTeamMembers(UUID teamId) {
    return userService.findAll().stream()
        .filter(u -> teamId.equals(u.getTeamsId()))
        .collect(Collectors.toList());
  }

  public List<Team> getAllTeams() {
    return teamService.findAll();
  }
}
