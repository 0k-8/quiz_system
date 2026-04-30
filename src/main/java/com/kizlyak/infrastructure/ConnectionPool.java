package com.kizlyak.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {
  private final String url;
  private final String user;
  private final String password;
  private final BlockingQueue<Connection> pool;
  private final List<Connection> allConnections;

  public ConnectionPool(String url, String user, String password, int size) throws SQLException {
    this.url = url;
    this.user = user;
    this.password = password;
    this.pool = new LinkedBlockingQueue<>(size);
    this.allConnections = new ArrayList<>(size);

    for (int i = 0; i < size; i++) {
      Connection connection = createConnection();
      pool.add(connection);
      allConnections.add(connection);
    }
  }

  private Connection createConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  public Connection getConnection() throws InterruptedException {
    return pool.take();
  }

  public void releaseConnection(Connection connection) {
    if (connection != null) {
      pool.offer(connection);
    }
  }

  public void shutdown() throws SQLException {
    for (Connection c : allConnections) {
      if (c != null && !c.isClosed()) {
        c.close();
      }
    }
  }
}
