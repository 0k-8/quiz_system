package com.kizlyak.view;

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
import com.kizlyak.entity.User;
import com.kizlyak.infrastructure.ConnectionPool;
import com.kizlyak.infrastructure.PasswordHasher;
import com.kizlyak.service.AuthService;
import com.kizlyak.service.QuizService;
import com.kizlyak.service.ResultService;
import com.kizlyak.service.TeamService;
import com.kizlyak.service.UserService;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
  private AuthService authService;
  private QuizService quizService;
  private UserService userService;
  private TeamService teamService;
  private ResultService resultService;
  private PasswordHasher passwordHasher;
  private Stage primaryStage;

  @Override
  public void init() throws SQLException {
    ConnectionPool pool = new ConnectionPool();

    UserDao userDao = new JdbcUserDao(pool);
    QuizDao quizDao = new JdbcQuizDao(pool);
    QuestionDao questionDao = new JdbcQuestionDao(pool);
    ResultDao resultDao = new JdbcResultDao(pool);
    TeamDao teamDao = new JdbcTeamDao(pool);

    this.passwordHasher = new PasswordHasher();
    this.userService = new UserService(userDao);
    this.teamService = new TeamService(teamDao);
    this.resultService = new ResultService(resultDao);
    this.quizService = new QuizService(quizDao, questionDao);
    this.authService = new AuthService(userService, passwordHasher);

    System.out.println("Система ініціалізована успішно.");
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    showLogin();
    primaryStage.show();
  }

  public void showLogin() {
    LoginApp.show(primaryStage, authService, this);
  }

  public void showRegistration() {
    RegistrationApp.show(primaryStage, authService, this);
  }

  public void showMainMenu(User user) {
    MainMenuView.show(primaryStage, user, this);
  }

  public void showQuizSelection(User user) {
    QuizSelectionView.show(primaryStage, user, this, quizService);
  }

  public void showResults(User user) {
    ResultsView.show(primaryStage, user, this, resultService);
  }

  public void showTeamManagement(User user) {
    TeamManagementView.show(primaryStage, user, this, teamService, userService);
  }

  public void startQuizSession(User user, com.kizlyak.entity.Quiz quiz) {
    QuizTakingView.show(primaryStage, user, this, quizService, resultService, quiz);
  }

  public AuthService getAuthService() {
    return authService;
  }

  public QuizService getQuizService() {
    return quizService;
  }

  public ResultService getResultService() {
    return resultService;
  }

  public TeamService getTeamService() {
    return teamService;
  }

  public UserService getUserService() {
    return userService;
  }
}
