package com.kizlyak.dao;

import com.kizlyak.entity.Team;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamDao {
  void saveTeam(Team team);

  Optional<Team> getTeamById(UUID id);

  Optional<Team> findByName(String name);

  List<Team> getAllTeams();

  void delete(UUID id);
}
