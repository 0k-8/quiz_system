package com.kizlyak.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kizlyak.entity.Quiz;

public interface QuizDao {
  void save(Quiz quiz);

  Optional<Quiz> findById(UUID id);

  List<Quiz> findAll();

  void delete(UUID id);
}
