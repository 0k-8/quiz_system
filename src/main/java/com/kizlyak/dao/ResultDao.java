package com.kizlyak.dao;

import java.util.List;
import java.util.UUID;

import com.kizlyak.entity.Result;

public interface ResultDao {
  void save(Result result);

  List<Result> findByTeamId(UUID teamId);

  List<Result> findByQuizId(UUID quizId);

  List<Result> findTopScores(int limit);
}
