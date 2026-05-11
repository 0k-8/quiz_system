package com.kizlyak.dao.impl;

import com.kizlyak.dao.TeamDao;
import com.kizlyak.entity.Team;
import com.kizlyak.infrastructure.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcTeamDao implements TeamDao {

  private final ConnectionPool pool;
  private final Map<UUID, Team> identityMap = new ConcurrentHashMap<>();

  public JdbcTeamDao(ConnectionPool pool) {
    this.pool = pool;
  }

  @Override
  public void saveTeam(Team team) {
    String sql = "INSERT INTO teams (id, team_name, created_at) VALUES (?, ?, ?)";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, team.getId());
        stmt.setString(2, team.getTeamName());
        stmt.setObject(3, team.getCreatedAt());
        stmt.executeUpdate();
        identityMap.put(team.getId(), team);
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Помилка зберігання команди", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  @Override
  public Optional<Team> getTeamById(UUID id) {
    if (identityMap.containsKey(id)) {
      return Optional.of(identityMap.get(id));
    }

    String sql = "SELECT * FROM teams WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            Team team = mapResultSetToTeam(rs);
            identityMap.put(team.getId(), team);
            return Optional.of(team);
          }
        }
      }

    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Помилка пошуку команди", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Team> findByName(String name) {
    String sql = "SELECT * FROM teams WHERE team_name = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, name);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            Team team = mapResultSetToTeam(rs);
            identityMap.put(team.getId(), team);
            return Optional.of(team);
          }
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding team by name", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return Optional.empty();
  }

  @Override
  public List<Team> getAllTeams() {
    String sql = "SELECT * FROM teams";
    List<Team> teams = new ArrayList<>();
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (Statement stmt = connection.createStatement();
          ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
          Team team = mapResultSetToTeam(rs);
          identityMap.put(team.getId(), team);
          teams.add(team);
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding all teams", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return teams;
  }

  @Override
  public void delete(UUID id) {
    String sql = "DELETE FROM teams WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        stmt.executeUpdate();
        identityMap.remove(id);
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error deleting team", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  private Team mapResultSetToTeam(ResultSet rs) throws SQLException {
    Team team = new Team();
    team.setId(rs.getObject("id", UUID.class));
    team.setTeamName(rs.getString("team_name"));

    // Перетворення Timestamp з БД у LocalDateTime для Java
    Timestamp ts = rs.getTimestamp("created_at");
    if (ts != null) {
      team.setCreatedAt(ts.toLocalDateTime());
    }
    return team;
  }
}
