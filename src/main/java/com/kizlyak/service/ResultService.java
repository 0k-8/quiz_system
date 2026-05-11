package com.kizlyak.service;

import com.kizlyak.dao.ResultDao;
import com.kizlyak.entity.Result;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResultService {
  private final ResultDao resultDao;

  public ResultService(ResultDao resultDao) {
    this.resultDao = resultDao;
  }

  public void save(Result result) {
    resultDao.save(result);
  }

  public List<Result> findByTeamId(UUID teamId) {
    return resultDao.findByTeamId(teamId);
  }

  public List<Result> findByUserId(UUID userId) {
    return resultDao.findByUserId(userId);
  }

  public List<Result> filterResults(UUID teamId, String quizTitle) {
    return resultDao.findByTeamId(teamId).stream()
        .filter(
            r ->
                r.getQuiz() != null
                    && r.getQuiz().getTitle().toLowerCase().contains(quizTitle.toLowerCase()))
        .collect(Collectors.toList());
  }

  public List<Result> searchResultsByTeam(String teamName) {
    // This is a simple implementation that filters in-memory.
    // In a real app, this would be a JOIN in the DAO.
    return resultDao.findTopScores(1000).stream() // Get a reasonable batch
        .filter(
            r ->
                r.getTeam() != null
                    && r.getTeam().getTeamName().toLowerCase().contains(teamName.toLowerCase()))
        .collect(Collectors.toList());
  }
}
