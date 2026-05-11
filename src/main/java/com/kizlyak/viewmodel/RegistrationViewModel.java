package com.kizlyak.viewmodel;

import com.kizlyak.service.AuthService;

public class RegistrationViewModel {
  private final AuthService authService;

  public RegistrationViewModel(AuthService authService) {
    this.authService = authService;
  }

  public void register(String firstName, String lastName, String email, String password) {
    authService.register(firstName, lastName, email, password);
  }
}
