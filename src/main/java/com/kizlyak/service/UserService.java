package com.kizlyak.service;

import com.kizlyak.dao.UserDao;
import com.kizlyak.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public void save(User user) {
    userDao.saveUser(user);
  }

  public Optional<User> findByEmail(String email) {
    return userDao.findByEmail(email);
  }

  public Optional<User> findById(UUID id) {
    return userDao.findById(id);
  }

  public List<User> findAll() {
    return userDao.findAll();
  }

  public void delete(UUID id) {
    userDao.delete(id);
  }
}
