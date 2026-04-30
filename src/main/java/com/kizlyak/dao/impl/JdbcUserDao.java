package com.kizlyak.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kizlyak.dao.UserDao;
import com.kizlyak.entity.Role;
import com.kizlyak.entity.User;
import com.kizlyak.infrastructure.ConnectionPool;

public class JdbcUserDao implements UserDao {
  private final ConnectionPool pool;

  public JdbcUserDao(ConnectionPool pool) {
    this.pool = pool;
  }

  @Override
  public void save(User user) {
    String sql =
        "INSERT INTO users (id, last_name, first_name, email, password_hash, role, team_id) VALUES"
            + " (?, ?, ?, ?, ?, ?, ?)";

    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, user.getId());
        stmt.setString(2, user.getFirstName());
        stmt.setString(3, user.getLastName());
        stmt.setString(4, user.getEmail());
        stmt.setString(5, user.getPasswordHash());
        stmt.setString(6, user.getRole() != null ? user.getRole().name() : null);
        stmt.setObject(7, user.getTeamsId());
        stmt.executeUpdate();
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error saving user", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  @Override
  public Optional<User> findById(UUID id) {
    String sql = "SELECT * FROM users WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            return Optional.of(mapResultSetToUser(rs));
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
  public List<User> findAll() {
    String sql = "SELECT * FROM users";
    List<User> users = new ArrayList<>();
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (Statement stmt = connection.createStatement();
          ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
          users.add(mapResultSetToUser(rs));
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
    String roleStr = rs.getString("role");
    if (roleStr != null) {
      user.setRole(Role.valueOf(roleStr.toUpperCase()));
    }
    user.setTeamsId(rs.getObject("team_id", UUID.class));
    return user;
  }
}
