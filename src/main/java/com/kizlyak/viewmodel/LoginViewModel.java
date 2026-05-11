package com.kizlyak.viewmodel;

import com.kizlyak.entity.User;
import com.kizlyak.service.AuthService;
import java.util.Optional;

public class LoginViewModel {
  private final AuthService authService;

  public LoginViewModel(AuthService authService) {
    this.authService = authService;
  }

  public Optional<User> login(String email, String password) {
    return authService.login(email, password);
  }
}
