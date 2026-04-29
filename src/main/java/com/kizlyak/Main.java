package com.kizlyak;

import org.flywaydb.core.Flyway;

public class Main {

  public static void main(String[] args) {
        Flyway flyway = Flyway.configure()
              .dataSource("jdbc:h2:./data/quiz_system", "root", "Denys.123")
              .load();
        flyway.migrate();
        System.out.println("Flyway Migrated");

    }
}
