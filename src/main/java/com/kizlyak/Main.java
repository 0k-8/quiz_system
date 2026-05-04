package com.kizlyak;

import com.kizlyak.dao.QuestionDao;
import com.kizlyak.dao.QuizDao;
import com.kizlyak.dao.ResultDao;
import com.kizlyak.dao.TeamDao;
import com.kizlyak.dao.UserDao;
import com.kizlyak.dao.impl.JdbcQuestionDao;
import com.kizlyak.dao.impl.JdbcQuizDao;
import com.kizlyak.dao.impl.JdbcResultDao;
import com.kizlyak.dao.impl.JdbcTeamDao;
import com.kizlyak.dao.impl.JdbcUserDao;
import com.kizlyak.entity.Question;
import com.kizlyak.entity.Quiz;
import com.kizlyak.entity.Result;
import com.kizlyak.entity.Role;
import com.kizlyak.entity.Team;
import com.kizlyak.entity.User;
import com.kizlyak.infrastructure.ConnectionPool;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException {
        ConnectionPool pool = new ConnectionPool();
        TeamDao teamDao = new JdbcTeamDao(pool);
        UserDao userDao = new JdbcUserDao(pool);
        QuizDao quizDao = new JdbcQuizDao(pool);
        QuestionDao questionDao = new JdbcQuestionDao(pool);
        ResultDao resultDao = new JdbcResultDao(pool);
        try {
            System.out.println("Запуск тестування");
            UUID teamId = UUID.randomUUID();
            Team team = new Team();
            team.setId(teamId);
            team.setTeamName("КОМАНДА ТЕСТУВАННЯ");
            team.setCreatedAt(LocalDateTime.now());
            teamDao.saveTeam(team);
            System.out.println("[OK] Таблиця Teams: Дані збережено.");

            // КРОК 2: Створюємо користувача
            UUID userId = UUID.randomUUID();
            User user = new User();
            user.setId(userId);
            user.setFirstName("Denys");
            user.setLastName("Kizlyak");
            user.setEmail("admin" + System.currentTimeMillis() + "@quiz.com");
            user.setPasswordHash("hash123");
            user.setRole(Role.USER);
            user.setTeamsId(teamId);
            userDao.saveUser(user);
            System.out.println("[OK] Таблиця Users: Дані збережено (зв'язок з Team перевірено).");

            // КРОК 3: Створюємо тест
            UUID quizId = UUID.randomUUID();
            Quiz quiz = new Quiz();
            quiz.setId(quizId);
            quiz.setTitle("Java Infrastructure Test");
            quiz.setTimeLimitMinutes(15);
            quiz.setCreatedBy(userId);
            quizDao.saveQuiz(quiz);
            System.out.println("[OK] Таблиця Quizzes: Дані збережено (зв'язок з User перевірено).");

            // КРОК 4: Створюємо Питання
            UUID qId = UUID.randomUUID();
            Question q = new Question();
            q.setId(qId);
            q.setQuizId(quizId);
            q.setContent("Яка анотація використовується для тестування в JUnit 5?");
            q.setOptionA("@Test");
            q.setOptionB("@Before");
            q.setOptionC("@Run");
            q.setOptionD("@TestCase");
            q.setCorrectOption("A");
            questionDao.saveQuestion(q);
            System.out.println("[OK] Таблиця Questions: Дані збережено (зв'язок з Quiz перевірено).");

            // КРОК 5: Створюємо Результат
            UUID resId = UUID.randomUUID();
            Result result = new Result();
            result.setId(resId);
            result.setTeamId(teamId);
            result.setQuizId(quizId);
            result.setScore(1); // 1 бал за правильну відповідь
            result.setTimeSpentSeconds(45);
            result.setCompletedAt(LocalDateTime.now());
            resultDao.save(result);
            System.out.println("[OK] Таблиця Results: Дані збережено.");

            // ФІНАЛЬНИЙ ВИВІД ДЛЯ ЗВІТУ
            System.out.println("\n--- РЕЗУЛЬТАТИ ПЕРЕВІРКИ  ---");
            User found = userDao.findById(userId).get();
            System.out.println("Знайдено користувача: " + found.getFirstName() + " " + found.getLastName());
            System.out.println("Знайдено питань у тесті: " + questionDao.findByQuizId(quizId).size());
            System.out.println("Найкращий результат у базі: " + resultDao.findTopScores(1).get(0).getScore());

            System.out.println("\n[SUCCESS] Всі таблиці протестовано. Infrastructure Layer працює коректно.");

        } catch (Exception e) {
            System.err.println("[ERROR] Тестування провалено!");
            e.printStackTrace();
        } finally {
            pool.shutdown();
            System.out.println("Пул закритий.");
        }
    }



}
