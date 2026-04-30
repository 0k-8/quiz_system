package com.kizlyak.dao;

import java.util.List;
import java.util.UUID;

import com.kizlyak.entity.Question;

public interface QuestionDao {
  void save(Question question);

  List<Question> findByQuizId(UUID quizId);

  void delete(UUID id);
}
