package com.kizlyak.dao;

import com.kizlyak.entity.Question;
import java.util.List;
import java.util.UUID;

public interface QuestionDao {
  void saveQuestion(Question question);

  List<Question> findByQuizId(UUID quizId);

  void delete(UUID id);
}
