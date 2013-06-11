package fpatterns.db

import fpatterns._
import fpatterns.validation._
import java.sql.{ Connection, DriverManager }
import javax.sql.DataSource

trait ProvideConnection {
  def apply[A](db: DBResult[A]): DomainValidation[A]
}

trait ConnectionProviders {
  def makeUnManagedProvider(driver: String, url: String): ProvideConnection = new ProvideConnection {
    Class.forName(driver)

    def apply[A](db: DBResult[A]): DomainValidation[A] = closing(DriverManager.getConnection(url))(db.run)
  }

  def makeManagedProvider(dataSource: DataSource): ProvideConnection = new ProvideConnection {
    def apply[A](db: DBResult[A]): DomainValidation[A] = closing(dataSource.getConnection)(db.run)
  }
}

