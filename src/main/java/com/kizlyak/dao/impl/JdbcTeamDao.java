package com.kizlyak.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kizlyak.dao.TeamDao;
import com.kizlyak.entity.Team;
import com.kizlyak.infrastructure.ConnectionPool;

public class JdbcTeamDao implements TeamDao {

  private final ConnectionPool pool;

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
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Помилка зберігання команди", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  @Override
  public Optional<Team> getTeamById(UUID id) {
    String sql = "SELECT * FROM teams WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            return Optional.of(mapResultSetToTeam(rs));
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
  public List<Team> getAllTeams() {
    String sql = "SELECT * FROM teams";
    List<Team> teams = new ArrayList<>();
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (Statement stmt = connection.createStatement();
          ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
          teams.add(mapResultSetToTeam(rs));
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
