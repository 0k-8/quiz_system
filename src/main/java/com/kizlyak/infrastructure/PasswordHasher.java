package com.kizlyak.infrastructure;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

  public String hash(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public boolean check(String password, String hashed) {
    if (hashed == null || !hashed.startsWith("$2a$")) {
      return password.equals(hashed); // Fallback for plain text passwords already in DB
    }
    return BCrypt.checkpw(password, hashed);
  }
}
