package com.kizlyak.dao.impl;

import com.kizlyak.dao.ResultDao;
import com.kizlyak.entity.Result;
import com.kizlyak.infrastructure.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcResultDao implements ResultDao {
  private final ConnectionPool pool;
  private final Map<UUID, Result> identityMap = new ConcurrentHashMap<>();

  public JdbcResultDao(ConnectionPool pool) {
    this.pool = pool;
  }

  @Override
  public void save(Result result) {
    String sql =
        "INSERT INTO results (id, user_id, team_id, quiz_id, score, correct_answers,"
            + " total_questions, time_spent_seconds, completed_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?,"
            + " ?)";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, result.getId());
        stmt.setObject(2, result.getUserId());
        stmt.setObject(3, result.getTeamId());
        stmt.setObject(4, result.getQuizId());
        stmt.setInt(5, result.getScore());
        stmt.setInt(6, result.getCorrectAnswers());
        stmt.setInt(7, result.getTotalQuestions());
        stmt.setInt(8, result.getTimeSpentSeconds());
        stmt.setObject(9, result.getCompletedAt());
        stmt.executeUpdate();
        identityMap.put(result.getId(), result);
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error saving result", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  @Override
  public List<Result> findByTeamId(UUID teamId) {
    String sql = "SELECT * FROM results WHERE team_id = ? ORDER BY completed_at DESC";
    return findResultsByProperty(sql, teamId);
  }

  @Override
  public List<Result> findByUserId(UUID userId) {
    String sql = "SELECT * FROM results WHERE user_id = ? ORDER BY completed_at DESC";
    return findResultsByProperty(sql, userId);
  }

  @Override
  public List<Result> findByQuizId(UUID quizId) {
    String sql = "SELECT * FROM results WHERE quiz_id = ? ORDER BY score DESC";
    return findResultsByProperty(sql, quizId);
  }

  @Override
  public List<Result> findTopScores(int limit) {
    String sql = "SELECT * FROM results ORDER BY score DESC, time_spent_seconds ASC LIMIT ?";
    List<Result> results = new ArrayList<>();
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, limit);
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            Result result = mapResultSetToResult(rs);
            identityMap.put(result.getId(), result);
            results.add(result);
          }
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding top scores", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return results;
  }

  private List<Result> findResultsByProperty(String sql, UUID id) {
    List<Result> results = new ArrayList<>();
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            Result result = mapResultSetToResult(rs);
            identityMap.put(result.getId(), result);
            results.add(result);
          }
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding results", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return results;
  }

  private Result mapResultSetToResult(ResultSet rs) throws SQLException {
    Result result = new Result();
    result.setId(rs.getObject("id", UUID.class));
    result.setUserId(rs.getObject("user_id", UUID.class));
    result.setTeamId(rs.getObject("team_id", UUID.class));
    UUID quizId = rs.getObject("quiz_id", UUID.class);
    result.setQuizId(quizId);
    result.setScore(rs.getInt("score"));
    result.setCorrectAnswers(rs.getInt("correct_answers"));
    result.setTotalQuestions(rs.getInt("total_questions"));
    result.setTimeSpentSeconds(rs.getInt("time_spent_seconds"));

    if (quizId != null) {
      result.setQuizLoader(() -> new JdbcQuizDao(pool).findById(quizId).orElse(null));
    }

    UUID teamId = rs.getObject("team_id", UUID.class);
    if (teamId != null) {
      result.setTeamLoader(() -> new JdbcTeamDao(pool).getTeamById(teamId).orElse(null));
    }

    Timestamp ts = rs.getTimestamp("completed_at");
    if (ts != null) {
      result.setCompletedAt(ts.toLocalDateTime());
    }
    return result;
  }
}
