package com.kizlyak.dao;

import com.kizlyak.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {
  void saveUser(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);

  List<User> findAll();

  void delete(UUID id);
}
