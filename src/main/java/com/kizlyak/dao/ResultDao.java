package com.kizlyak.dao;

import com.kizlyak.entity.Result;
import java.util.List;
import java.util.UUID;

public interface ResultDao {
  void save(Result result);

  List<Result> findByTeamId(UUID teamId);

  List<Result> findByUserId(UUID userId);

  List<Result> findByQuizId(UUID quizId);

  List<Result> findTopScores(int limit);
}
