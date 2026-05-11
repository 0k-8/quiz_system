package com.kizlyak.service;

import com.kizlyak.dao.TeamDao;
import com.kizlyak.entity.Team;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TeamService {
  private final TeamDao teamDao;

  public TeamService(TeamDao teamDao) {
    this.teamDao = teamDao;
  }

  public void save(Team team) {
    teamDao.saveTeam(team);
  }

  public Optional<Team> findByName(String name) {
    return teamDao.findByName(name);
  }

  public Optional<Team> findById(UUID id) {
    return teamDao.getTeamById(id);
  }

  public List<Team> findAll() {
    return teamDao.getAllTeams();
  }
}
