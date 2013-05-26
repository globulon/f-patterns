package fpatterns.db

import fpatterns._
import java.sql.{ Connection, DriverManager }
import javax.sql.DataSource

trait ConnectionProvider {
  def apply[A](db: DB[A]): A
}

trait ConnectionProviders {
  def commit[A](c: Connection)(f: Connection => A): A =
    try {
      f(c)
    } finally {
      c.commit()
    }

  def makeUnManagedProvider(driver: String, url: String): ConnectionProvider = new ConnectionProvider {
    Class.forName(driver)

    def apply[A](db: DB[A]) = closing(DriverManager.getConnection(url))(commit(_)(db.run))
  }

  def makeManagedProvider(dataSource: DataSource): ConnectionProvider = new ConnectionProvider {
    def apply[A](db: DB[A]) = closing(dataSource.getConnection)(commit(_)(db.run))
  }
}
