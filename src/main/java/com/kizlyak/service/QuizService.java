package com.kizlyak.service;

import com.kizlyak.dao.QuestionDao;
import com.kizlyak.dao.QuizDao;
import com.kizlyak.entity.Question;
import com.kizlyak.entity.Quiz;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuizService {
  private final QuizDao quizDao;
  private final QuestionDao questionDao;

  public QuizService(QuizDao quizDao, QuestionDao questionDao) {
    this.quizDao = quizDao;
    this.questionDao = questionDao;
  }

  public List<Quiz> getAllQuizzes() {
    return quizDao.findAll();
  }

  public Optional<Quiz> getQuizById(UUID id) {
    return quizDao.findById(id);
  }

  public List<Quiz> searchQuizzes(String query) {
    if (query == null || query.isBlank()) {
      return getAllQuizzes();
    }
    return quizDao.findAll().stream()
        .filter(q -> q.getTitle().toLowerCase().contains(query.toLowerCase()))
        .collect(Collectors.toList());
  }

  public List<Question> getQuestionsForQuiz(UUID quizId) {
    return questionDao.findByQuizId(quizId);
  }

  public void createQuiz(String title, int timeLimit, UUID creatorId) {
    validateQuiz(title, timeLimit);
    Quiz quiz = new Quiz();
    quiz.setId(UUID.randomUUID());
    quiz.setTitle(title);
    quiz.setTimeLimitMinutes(timeLimit);
    quiz.setCreatedBy(creatorId);
    quizDao.saveQuiz(quiz);
  }

  public void addQuestionToQuiz(
      UUID quizId, String content, String a, String b, String c, String d, String correct) {
    Question question = new Question();
    question.setId(UUID.randomUUID());
    question.setQuizId(quizId);
    question.setContent(content);
    question.setOptionA(a);
    question.setOptionB(b);
    question.setOptionC(c);
    question.setOptionD(d);
    question.setCorrectOption(correct);
    questionDao.saveQuestion(question);
  }

  public void deleteQuiz(UUID quizId) {
    quizDao.delete(quizId);
  }

  private void validateQuiz(String title, int timeLimit) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Назва тесту не може бути порожньою");
    }
    if (timeLimit <= 0) {
      throw new IllegalArgumentException("Ліміт часу повинен бути більшим за 0");
    }
  }
}
