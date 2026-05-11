package com.kizlyak.dao;

import com.kizlyak.entity.Quiz;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizDao {
  void saveQuiz(Quiz quiz);

  Optional<Quiz> findById(UUID id);

  List<Quiz> findAll();

  void delete(UUID id);
}
