package com.kizlyak.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kizlyak.dao.QuizDao;
import com.kizlyak.entity.Quiz;
import com.kizlyak.infrastructure.ConnectionPool;

public class JdbcQuizDao implements QuizDao {
  private final ConnectionPool pool;

  public JdbcQuizDao(ConnectionPool pool) {
    this.pool = pool;
  }

  @Override
  public void saveQuiz(Quiz quiz) {
    String sql =
        "INSERT INTO quizzes (id, title, time_limit_minutes, created_by) VALUES (?, ?, ?, ?)";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, quiz.getId());
        stmt.setString(2, quiz.getTitle());
        stmt.setInt(3, quiz.getTimeLimitMinutes());
        stmt.setObject(4, quiz.getCreatedBy());
        stmt.executeUpdate();
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error saving quiz", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  @Override
  public Optional<Quiz> findById(UUID id) {
    String sql = "SELECT * FROM quizzes WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            return Optional.of(mapResultSetToQuiz(rs));
          }
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding quiz", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return Optional.empty();
  }

  @Override
  public List<Quiz> findAll() {
    String sql = "SELECT * FROM quizzes";
    List<Quiz> quizzes = new ArrayList<>();
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (Statement stmt = connection.createStatement();
          ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
          quizzes.add(mapResultSetToQuiz(rs));
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding all quizzes", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return quizzes;
  }

  @Override
  public void delete(UUID id) {
    String sql = "DELETE FROM quizzes WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        stmt.executeUpdate();
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error deleting quiz", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  private Quiz mapResultSetToQuiz(ResultSet rs) throws SQLException {
    Quiz quiz = new Quiz();
    quiz.setId(rs.getObject("id", UUID.class));
    quiz.setTitle(rs.getString("title"));
    quiz.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
    quiz.setCreatedBy(rs.getObject("created_by", UUID.class));
    return quiz;
  }
}
