package com.kizlyak.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kizlyak.entity.Team;

public interface TeamDao {
  void saveTeam(Team team);

  Optional<Team> getTeamById(UUID id);

  Optional<Team> findByName(String name);

  List<Team> getAllTeams();

  void delete(UUID id);
}
