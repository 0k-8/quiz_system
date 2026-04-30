package com.kizlyak.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kizlyak.entity.User;

public interface UserDao {
  void save(User user);

  Optional<User> findById(UUID id);

  List<User> findAll();

  void delete(UUID id);
}
