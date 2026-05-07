package com.kizlyak.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.kizlyak.dao.UserDao;
import com.kizlyak.entity.Role;
import com.kizlyak.entity.User;
import com.kizlyak.infrastructure.ConnectionPool;

public class JdbcUserDao implements UserDao {
  private final ConnectionPool pool;
  private final Map<UUID, User> identityMap = new ConcurrentHashMap<>();

  public JdbcUserDao(ConnectionPool pool) {
    this.pool = pool;
  }

  @Override
  public void saveUser(User user) {
    String sql =
        "MERGE INTO users (id, last_name, first_name, email, password_hash, role, team_id) KEY(id) VALUES"
            + " (?, ?, ?, ?, ?, ?, ?)";

    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, user.getId());
        stmt.setString(2, user.getLastName());
        stmt.setString(3, user.getFirstName());
        stmt.setString(4, user.getEmail());
        stmt.setString(5, user.getPasswordHash());
        stmt.setString(6, user.getRole() != null ? user.getRole().name() : null);
        stmt.setObject(7, user.getTeamsId());
        stmt.executeUpdate();
        identityMap.put(user.getId(), user);
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error saving/updating user", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  @Override
  public Optional<User> findById(UUID id) {
    if (identityMap.containsKey(id)) {
      return Optional.of(identityMap.get(id));
    }

    String sql = "SELECT * FROM users WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            User user = mapResultSetToUser(rs);
            identityMap.put(user.getId(), user);
            return Optional.of(user);
          }
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding user", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, email);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            User user = mapResultSetToUser(rs);
            identityMap.put(user.getId(), user);
            return Optional.of(user);
          }
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding user by email", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return Optional.empty();
  }

  @Override
  public List<User> findAll() {
    String sql = "SELECT * FROM users";
    List<User> users = new ArrayList<>();
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (Statement stmt = connection.createStatement();
          ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
          User user = mapResultSetToUser(rs);
          identityMap.put(user.getId(), user);
          users.add(user);
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding all users", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return users;
  }

  @Override
  public void delete(UUID id) {
    String sql = "DELETE FROM users WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        stmt.executeUpdate();
        identityMap.remove(id);
      }
    } catch (SQLException | InterruptedException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      throw new RuntimeException("Помилка при видаленні користувача", e);
    } finally {
      if (connection != null) {
        pool.releaseConnection(connection);
      }
    }
  }

  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setId(rs.getObject("id", UUID.class));
    user.setFirstName(rs.getString("first_name"));
    user.setLastName(rs.getString("last_name"));
    user.setEmail(rs.getString("email"));
    user.setPasswordHash(rs.getString("password_hash"));
    String roleStr = rs.getString("role");
    if (roleStr != null) {
      user.setRole(Role.valueOf(roleStr.toUpperCase()));
    }
    UUID teamId = rs.getObject("team_id", UUID.class);
    user.setTeamsId(teamId);

    if (teamId != null) {
      user.setTeamLoader(() -> new JdbcTeamDao(pool).getTeamById(teamId).orElse(null));
    }

    return user;
  }
}
