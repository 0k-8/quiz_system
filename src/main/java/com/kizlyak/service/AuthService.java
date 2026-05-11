package com.kizlyak.service;

import com.kizlyak.entity.Role;
import com.kizlyak.entity.User;
import com.kizlyak.infrastructure.PasswordHasher;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
  private final UserService userService;
  private final PasswordHasher passwordHasher;

  public AuthService(UserService userService, PasswordHasher passwordHasher) {
    this.userService = userService;
    this.passwordHasher = passwordHasher;
  }

  public void register(String firstName, String lastName, String email, String password) {
    validateRegistration(firstName, lastName, email, password);

    if (userService.findByEmail(email).isPresent()) {
      throw new RuntimeException("Користувач з такою поштою вже існує!");
    }

    User newUser = new User();
    newUser.setId(UUID.randomUUID());
    newUser.setFirstName(firstName);
    newUser.setLastName(lastName);
    newUser.setEmail(email);
    newUser.setPasswordHash(passwordHasher.hash(password));
    newUser.setRole(Role.USER);
    userService.save(newUser);
  }

  public Optional<User> login(String email, String password) {
    if (email == null || email.isBlank() || password == null || password.isBlank()) {
      return Optional.empty();
    }
    return userService
        .findByEmail(email)
        .filter(user -> passwordHasher.check(password, user.getPasswordHash()));
  }

  private void validateRegistration(
      String firstName, String lastName, String email, String password) {
    if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Ім'я та прізвище не можуть бути порожніми!");
    }

    String nameRegex = "^[A-ZА-ЩЬЮЯҐЄІЇ][a-zа-щьюяґєії' -]{1,50}$";
    if (!firstName.matches(nameRegex) || !lastName.matches(nameRegex)) {
      throw new IllegalArgumentException("Невірний формат імені або прізвища!");
    }

    if (email == null || !email.matches("[A-Za-z0-9._%+-]+@(.+)$")) {
      throw new IllegalArgumentException("Невірний формат пошти!");
    }

    if (password == null || password.length() < 6) {
      throw new IllegalArgumentException("Пароль повинен мати щонайменше 6 символів!");
    }
  }
}
