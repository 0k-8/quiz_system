package com.kizlyak.viewmodel;

import com.kizlyak.entity.Result;
import com.kizlyak.entity.User;
import com.kizlyak.service.ResultService;
import java.util.List;

public class ResultsViewModel {
  private final ResultService resultService;
  private final User currentUser;

  public ResultsViewModel(ResultService resultService, User currentUser) {
    this.resultService = resultService;
    this.currentUser = currentUser;
  }

  public List<Result> getResults() {
    if (currentUser.getTeamsId() != null) {
      return resultService.findByTeamId(currentUser.getTeamsId());
    }
    return resultService.findByUserId(currentUser.getId());
  }

  public List<Result> searchResults(String quizTitle) {
    if (currentUser.getTeamsId() != null) {
      return resultService.filterResults(currentUser.getTeamsId(), quizTitle);
    }
    return resultService.findByUserId(currentUser.getId()).stream()
        .filter(
            r ->
                r.getQuiz() != null
                    && r.getQuiz().getTitle().toLowerCase().contains(quizTitle.toLowerCase()))
        .collect(java.util.stream.Collectors.toList());
  }

  public List<Result> searchByTeam(String teamName) {
    return resultService.searchResultsByTeam(teamName);
  }

  public void sortByScore(List<Result> results, boolean ascending) {
    results.sort(
        (r1, r2) -> {
          int cmp = Integer.compare(r1.getScore(), r2.getScore());
          return ascending ? cmp : -cmp;
        });
  }

  public void sortByTime(List<Result> results, boolean ascending) {
    results.sort(
        (r1, r2) -> {
          int cmp = Integer.compare(r1.getTimeSpentSeconds(), r2.getTimeSpentSeconds());
          return ascending ? cmp : -cmp;
        });
  }

  public void sortByDate(List<Result> results, boolean ascending) {
    results.sort(
        (r1, r2) -> {
          if (r1.getCompletedAt() == null || r2.getCompletedAt() == null) return 0;
          int cmp = r1.getCompletedAt().compareTo(r2.getCompletedAt());
          return ascending ? cmp : -cmp;
        });
  }
}
