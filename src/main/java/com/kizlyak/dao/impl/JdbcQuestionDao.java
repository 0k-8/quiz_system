package com.kizlyak.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.kizlyak.dao.QuestionDao;
import com.kizlyak.entity.Question;
import com.kizlyak.infrastructure.ConnectionPool;

public class JdbcQuestionDao implements QuestionDao {
  private final ConnectionPool pool;

  public JdbcQuestionDao(ConnectionPool pool) {
    this.pool = pool;
  }

  @Override
  public void saveQuestion(Question q) {
    String sql =
        "INSERT INTO questions (id, quiz_id, content, options_a, options_b, options_c, options_d,"
            + " correct_option) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, q.getId());
        stmt.setObject(2, q.getQuizId());
        stmt.setString(3, q.getContent());
        stmt.setString(4, q.getOptionA());
        stmt.setString(5, q.getOptionB());
        stmt.setString(6, q.getOptionC());
        stmt.setString(7, q.getOptionD());
        stmt.setString(
            8, String.valueOf(q.getCorrectOption())); // Конвертуємо char в String для бази
        stmt.executeUpdate();
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error saving question", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  @Override
  public List<Question> findByQuizId(UUID quizId) {
    String sql = "SELECT * FROM questions WHERE quiz_id = ?";
    List<Question> questions = new ArrayList<>();
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, quizId);
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            questions.add(mapResultSetToQuestion(rs));
          }
        }
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error finding questions by quiz id", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
    return questions;
  }

  @Override
  public void delete(UUID id) {
    String sql = "DELETE FROM questions WHERE id = ?";
    Connection connection = null;
    try {
      connection = pool.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setObject(1, id);
        stmt.executeUpdate();
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException("Error deleting question", e);
    } finally {
      if (connection != null) pool.releaseConnection(connection);
    }
  }

  private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
    Question q = new Question();
    q.setId(rs.getObject("id", UUID.class));
    q.setQuizId(rs.getObject("quiz_id", UUID.class));
    q.setContent(rs.getString("content"));
    q.setOptionA(rs.getString("options_a"));
    q.setOptionB(rs.getString("options_b"));
    q.setOptionC(rs.getString("options_c"));
    q.setOptionD(rs.getString("options_d"));
    String correct = rs.getString("correct_option");
    if (correct != null && !correct.isEmpty()) {
      q.setCorrectOption(rs.getString("correct_option"));
    }
    return q;
  }
}
