package com.kizlyak.service;

import java.util.List;
import java.util.UUID;

import com.kizlyak.dao.QuestionDao;
import com.kizlyak.dao.QuizDao;
import com.kizlyak.dao.ResultDao;
import com.kizlyak.entity.Question;
import com.kizlyak.entity.Quiz;
import com.kizlyak.entity.Result;

public class QuizService {
  private final QuizDao quizDao;
  private final QuestionDao questionDao;
  private final ResultDao resultDao;

  public QuizService(QuizDao quizDao, QuestionDao questionDao, ResultDao resultDao) {
    this.quizDao = quizDao;
    this.questionDao = questionDao;
    this.resultDao = resultDao;
  }

  public List<Quiz> getAllQuizzes() {
    return quizDao.findAll();
  }

  public List<Question> getQuestionsForQuiz(UUID quizId) {
    return questionDao.findByQuizId(quizId);
  }

  public void createQuiz(String title, int timeLimit, UUID creatorId) {
    Quiz quiz = new Quiz();
    quiz.setId(UUID.randomUUID());
    quiz.setTitle(title);
    quiz.setTimeLimitMinutes(timeLimit);
    quiz.setCreatedBy(creatorId);
    quizDao.saveQuiz(quiz);
  }

  public void saveResult(Result result) {
    resultDao.save(result);
  }

  public List<Result> getResultsForTeam(UUID teamId) {
    return resultDao.findByTeamId(teamId);
  }
}
